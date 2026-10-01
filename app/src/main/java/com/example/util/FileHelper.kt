package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import com.example.model.ImageMetadata
import com.example.model.OutputFormat
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

object FileHelper {

    fun getImageMetadata(context: Context, uri: Uri): ImageMetadata? {
        var name = "image_${System.currentTimeMillis()}"
        var sizeBytes = 0L

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    name = cursor.getString(nameIndex) ?: name
                }
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    sizeBytes = cursor.getLong(sizeIndex)
                }
            }
        }

        // If size wasn't in cursor, measure directly from stream
        if (sizeBytes <= 0) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    sizeBytes = stream.available().toLong()
                }
            } catch (_: Exception) {}
        }

        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"

        // Read dimensions with inJustDecodeBounds
        var width = 0
        var height = 0
        try {
            val options = android.graphics.BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { stream ->
                android.graphics.BitmapFactory.decodeStream(stream, null, options)
                width = options.outWidth
                height = options.outHeight
            }
        } catch (_: Exception) {}

        if (width <= 0 || height <= 0) {
            width = 800
            height = 800
        }

        return ImageMetadata(
            uri = uri,
            fileName = name,
            originalSizeBytes = sizeBytes,
            width = width,
            height = height,
            mimeType = mimeType
        )
    }

    fun getCacheDirForCompressed(context: Context): File {
        val dir = File(context.cacheDir, "compressed")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun saveToGallery(context: Context, sourceFile: File, displayName: String, mimeType: String): Uri? {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/PhotoSizePro")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        if (uri != null) {
            try {
                resolver.openOutputStream(uri)?.use { out ->
                    FileInputStream(sourceFile).use { input ->
                        input.copyTo(out)
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
                return uri
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    resolver.delete(uri, null, null)
                } catch (_: Exception) {}
            }
        }
        return null
    }

    fun shareFile(context: Context, file: File, mimeType: String, title: String = "Share Compressed File") {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, title).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareMultipleFiles(context: Context, files: List<File>, mimeType: String = "*/*") {
        try {
            val uris = ArrayList<Uri>()
            for (file in files) {
                uris.add(
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )
                )
            }
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                type = mimeType
                putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Share Compressed Files").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
