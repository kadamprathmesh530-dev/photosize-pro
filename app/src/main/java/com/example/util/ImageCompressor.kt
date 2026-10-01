package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import com.example.model.CompressionResult
import com.example.model.ImageMetadata
import com.example.model.OutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.roundToInt

object ImageCompressor {

    suspend fun compressImage(
        context: Context,
        metadata: ImageMetadata,
        targetKb: Int,
        targetWidth: Int? = null,
        targetHeight: Int? = null,
        outputFormat: OutputFormat = OutputFormat.JPEG,
        backgroundColor: Int = Color.WHITE,
        onProgress: (Float, String) -> Unit = { _, _ -> }
    ): CompressionResult = withContext(Dispatchers.IO) {
        onProgress(0.1f, "Loading image...")

        val originalBitmap = loadRotatedBitmap(context, metadata.uri)
            ?: throw IllegalStateException("Failed to decode image")

        onProgress(0.25f, "Preparing dimensions...")

        // Step 1: Initial dimension scaling if requested
        var currentBitmap: Bitmap = if (targetWidth != null && targetHeight != null && targetWidth > 0 && targetHeight > 0) {
            Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
        } else if (targetWidth != null && targetWidth > 0) {
            val aspect = originalBitmap.height.toFloat() / originalBitmap.width.toFloat()
            val newHeight = (targetWidth * aspect).roundToInt().coerceAtLeast(1)
            Bitmap.createScaledBitmap(originalBitmap, targetWidth, newHeight, true)
        } else if (targetHeight != null && targetHeight > 0) {
            val aspect = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
            val newWidth = (targetHeight * aspect).roundToInt().coerceAtLeast(1)
            Bitmap.createScaledBitmap(originalBitmap, newWidth, targetHeight, true)
        } else {
            originalBitmap
        }

        // Apply background color if format does not support transparency or user requested solid background
        if (outputFormat == OutputFormat.JPEG || backgroundColor != Color.TRANSPARENT) {
            val composited = Bitmap.createBitmap(currentBitmap.width, currentBitmap.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(composited)
            canvas.drawColor(backgroundColor)
            canvas.drawBitmap(currentBitmap, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))
            if (currentBitmap != originalBitmap) {
                currentBitmap.recycle()
            }
            currentBitmap = composited
        }

        val targetBytes = targetKb * 1024L
        val compressFormat = when (outputFormat) {
            OutputFormat.JPEG -> Bitmap.CompressFormat.JPEG
            OutputFormat.PNG -> Bitmap.CompressFormat.PNG
            OutputFormat.WEBP -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bitmap.CompressFormat.WEBP_LOSSY
                } else {
                    @Suppress("DEPRECATION")
                    Bitmap.CompressFormat.WEBP
                }
            }
        }

        // If format is PNG (lossless), quality parameter is ignored by Android for PNG.
        // For PNG or if target KB cannot be reached by quality alone, we downscale dimensions iteratively.
        var bestBytes: ByteArray? = null
        var bestQuality = 85
        var currentWidth = currentBitmap.width
        var currentHeight = currentBitmap.height

        var attemptCount = 0
        val maxAttempts = 18

        while (attemptCount < maxAttempts) {
            attemptCount++
            onProgress(0.3f + (attemptCount * 0.035f).coerceAtMost(0.55f), "Optimizing size (Attempt $attemptCount)...")

            if (outputFormat == OutputFormat.PNG) {
                val stream = ByteArrayOutputStream()
                currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val bytes = stream.toByteArray()
                if (bytes.size <= targetBytes) {
                    bestBytes = bytes
                    break
                } else {
                    // Downscale dimensions by ratio
                    val ratio = Math.sqrt(targetBytes.toDouble() / bytes.size.toDouble()) * 0.92
                    val newW = (currentBitmap.width * ratio).roundToInt().coerceAtLeast(20)
                    val newH = (currentBitmap.height * ratio).roundToInt().coerceAtLeast(20)
                    if (newW >= currentBitmap.width || newH >= currentBitmap.height) {
                        bestBytes = bytes
                        break
                    }
                    val scaled = Bitmap.createScaledBitmap(currentBitmap, newW, newH, true)
                    currentBitmap.recycle()
                    currentBitmap = scaled
                    currentWidth = newW
                    currentHeight = newH
                }
            } else {
                // Binary search for JPEG / WEBP quality
                var lowQ = 5
                var highQ = 100
                var foundQ = -1
                var localBest: ByteArray? = null

                while (lowQ <= highQ) {
                    val midQ = (lowQ + highQ) / 2
                    val stream = ByteArrayOutputStream()
                    currentBitmap.compress(compressFormat, midQ, stream)
                    val bytes = stream.toByteArray()

                    if (bytes.size <= targetBytes) {
                        // Meets target! Try higher quality
                        foundQ = midQ
                        localBest = bytes
                        lowQ = midQ + 1
                    } else {
                        // Exceeds target, try lower quality
                        highQ = midQ - 1
                    }
                }

                if (foundQ != -1 && localBest != null) {
                    bestBytes = localBest
                    bestQuality = foundQ
                    break
                } else {
                    // Even quality 5 exceeds target KB! Must downscale dimensions
                    val streamMin = ByteArrayOutputStream()
                    currentBitmap.compress(compressFormat, 5, streamMin)
                    val bytesMin = streamMin.toByteArray()

                    val scaleFactor = Math.sqrt(targetBytes.toDouble() / bytesMin.size.toDouble()) * 0.90
                    val nextW = (currentBitmap.width * scaleFactor).roundToInt().coerceAtLeast(40)
                    val nextH = (currentBitmap.height * scaleFactor).roundToInt().coerceAtLeast(40)

                    if (nextW >= currentBitmap.width && nextH >= currentBitmap.height) {
                        // Cannot downscale further
                        bestBytes = bytesMin
                        bestQuality = 5
                        break
                    }

                    val scaled = Bitmap.createScaledBitmap(currentBitmap, nextW, nextH, true)
                    currentBitmap.recycle()
                    currentBitmap = scaled
                    currentWidth = nextW
                    currentHeight = nextH
                }
            }
        }

        // Final fallback if somehow null
        if (bestBytes == null) {
            val fallbackStream = ByteArrayOutputStream()
            currentBitmap.compress(compressFormat, 10, fallbackStream)
            bestBytes = fallbackStream.toByteArray()
            bestQuality = 10
        }

        onProgress(0.9f, "Saving result...")

        // Save output to cache directory
        val cacheDir = FileHelper.getCacheDirForCompressed(context)
        val cleanBaseName = metadata.fileName.substringBeforeLast(".")
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val finalSizeKb = (bestBytes.size / 1024L).toInt()
        val outFileName = "${cleanBaseName}_compressed_${finalSizeKb}kb.${outputFormat.extension}"
        val outFile = File(cacheDir, outFileName)

        FileOutputStream(outFile).use { fos ->
            fos.write(bestBytes)
            fos.flush()
        }

        val outUri = Uri.fromFile(outFile)
        val isWithinTarget = bestBytes.size <= targetBytes

        onProgress(1.0f, "Completed!")

        CompressionResult(
            originalMetadata = metadata,
            compressedSizeBytes = bestBytes.size.toLong(),
            targetKb = targetKb,
            resultWidth = currentWidth,
            resultHeight = currentHeight,
            qualityUsed = bestQuality,
            outputFormat = outputFormat,
            outputUri = outUri,
            outputFilePath = outFile.absolutePath,
            isWithinTarget = isWithinTarget
        )
    }

    private fun loadRotatedBitmap(context: Context, uri: Uri): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeStream(inputStream, null, boundsOptions)
        inputStream.close()

        val maxDimension = 2400
        var sampleSize = 1
        val maxSide = max(boundsOptions.outWidth, boundsOptions.outHeight)
        if (maxSide > maxDimension) {
            sampleSize = (maxSide.toFloat() / maxDimension.toFloat()).roundToInt().coerceAtLeast(1)
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val readStream = context.contentResolver.openInputStream(uri) ?: return null
        val decoded = BitmapFactory.decodeStream(readStream, null, decodeOptions) ?: return null
        readStream.close()

        // Check EXIF rotation
        var orientation = ExifInterface.ORIENTATION_NORMAL
        try {
            context.contentResolver.openInputStream(uri)?.use { exifStream ->
                val exif = ExifInterface(exifStream)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            }
        } catch (_: Exception) {}

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        }

        return if (!matrix.isIdentity) {
            val rotated = Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
            if (rotated != decoded) decoded.recycle()
            rotated
        } else {
            decoded
        }
    }
}
