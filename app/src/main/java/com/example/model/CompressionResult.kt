package com.example.model

import android.net.Uri

enum class OutputFormat(val extension: String, val mimeType: String) {
    JPEG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    WEBP("webp", "image/webp")
}

data class ImageMetadata(
    val uri: Uri,
    val fileName: String,
    val originalSizeBytes: Long,
    val width: Int,
    val height: Int,
    val mimeType: String
) {
    val originalSizeKb: Double
        get() = originalSizeBytes / 1024.0

    val formattedOriginalSize: String
        get() = if (originalSizeKb >= 1024) {
            String.format("%.2f MB", originalSizeKb / 1024.0)
        } else {
            String.format("%.1f KB", originalSizeKb)
        }
}

data class CompressionResult(
    val originalMetadata: ImageMetadata,
    val compressedSizeBytes: Long,
    val targetKb: Int,
    val resultWidth: Int,
    val resultHeight: Int,
    val qualityUsed: Int,
    val outputFormat: OutputFormat,
    val outputUri: Uri,
    val outputFilePath: String,
    val isWithinTarget: Boolean
) {
    val compressedSizeKb: Double
        get() = compressedSizeBytes / 1024.0

    val formattedCompressedSize: String
        get() = String.format("%.1f KB", compressedSizeKb)

    val percentSaved: Int
        get() {
            if (originalMetadata.originalSizeBytes <= 0) return 0
            val saved = (originalMetadata.originalSizeBytes - compressedSizeBytes).toDouble()
            val percent = (saved / originalMetadata.originalSizeBytes.toDouble()) * 100.0
            return percent.coerceIn(0.0, 100.0).toInt()
        }
}
