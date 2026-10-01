package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

enum class PdfPageSize(val label: String, val widthPts: Int, val heightPts: Int) {
    A4("A4 (210 x 297 mm)", 595, 842),
    LETTER("Letter (8.5 x 11 in)", 612, 792),
    FIT_IMAGE("Fit Image", 0, 0)
}

object PdfExporter {

    suspend fun createPdfFromImages(
        context: Context,
        imageUris: List<Uri>,
        pageSize: PdfPageSize = PdfPageSize.A4,
        marginPts: Int = 20,
        pdfName: String = "PhotoSizePro_Doc_${System.currentTimeMillis()}"
    ): File = withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()

        for (i in imageUris.indices) {
            val uri = imageUris[i]
            val bitmap = loadBitmapForPdf(context, uri) ?: continue

            val pageWidth = if (pageSize == PdfPageSize.FIT_IMAGE) bitmap.width else pageSize.widthPts
            val pageHeight = if (pageSize == PdfPageSize.FIT_IMAGE) bitmap.height else pageSize.heightPts

            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Fill white background
            canvas.drawColor(Color.WHITE)

            if (pageSize == PdfPageSize.FIT_IMAGE) {
                canvas.drawBitmap(bitmap, 0f, 0f, null)
            } else {
                val availW = (pageWidth - marginPts * 2).toFloat().coerceAtLeast(50f)
                val availH = (pageHeight - marginPts * 2).toFloat().coerceAtLeast(50f)

                val imgW = bitmap.width.toFloat()
                val imgH = bitmap.height.toFloat()

                val scale = Math.min(availW / imgW, availH / imgH)
                val destW = imgW * scale
                val destH = imgH * scale

                val destX = marginPts + (availW - destW) / 2f
                val destY = marginPts + (availH - destH) / 2f

                val destRect = RectF(destX, destY, destX + destW, destY + destH)
                val srcRect = Rect(0, 0, bitmap.width, bitmap.height)
                val paint = Paint(Paint.FILTER_BITMAP_FLAG)

                canvas.drawBitmap(bitmap, srcRect, destRect, paint)
            }

            pdfDocument.finishPage(page)
            bitmap.recycle()
        }

        val pdfDir = File(context.cacheDir, "pdfs")
        if (!pdfDir.exists()) pdfDir.mkdirs()

        val cleanName = pdfName.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val outFile = File(pdfDir, "$cleanName.pdf")

        FileOutputStream(outFile).use { fos ->
            pdfDocument.writeTo(fos)
        }
        pdfDocument.close()

        outFile
    }

    private fun loadBitmapForPdf(context: Context, uri: Uri): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }

            val maxDimension = 1800
            var sampleSize = 1
            val maxSide = Math.max(options.outWidth, options.outHeight)
            if (maxSide > maxDimension) {
                sampleSize = Math.round(maxSide.toFloat() / maxDimension.toFloat()).coerceAtLeast(1)
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, decodeOptions)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
