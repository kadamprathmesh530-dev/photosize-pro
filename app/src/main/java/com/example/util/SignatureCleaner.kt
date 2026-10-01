package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

object SignatureCleaner {

    enum class InkColor(val colorInt: Int) {
        BLACK(Color.BLACK),
        BLUE(Color.rgb(26, 54, 138))
    }

    suspend fun cleanSignature(
        inputBitmap: Bitmap,
        threshold: Int = 180, // 0 to 255
        inkColor: InkColor = InkColor.BLACK,
        autoTrim: Boolean = true
    ): Bitmap = withContext(Dispatchers.Default) {
        val width = inputBitmap.width
        val height = inputBitmap.height
        val pixels = IntArray(width * height)
        inputBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var minX = width
        var minY = height
        var maxX = 0
        var maxY = 0
        var hasInk = false

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                val pixel = pixels[index]
                val r = Color.red(pixel)
                val g = Color.green(pixel)
                val b = Color.blue(pixel)

                // Luminance calculation
                val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

                if (lum >= threshold) {
                    // Paper background -> Pure White
                    pixels[index] = Color.WHITE
                } else {
                    // Ink stroke
                    hasInk = true
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y

                    // Apply ink color with slight anti-aliasing gradient
                    if (inkColor == InkColor.BLUE) {
                        val factor = (lum.toFloat() / threshold.toFloat()).coerceIn(0f, 1f)
                        val blueR = (20 + factor * 40).toInt()
                        val blueG = (40 + factor * 60).toInt()
                        val blueB = (150 + factor * 70).toInt().coerceAtMost(255)
                        pixels[index] = Color.rgb(blueR, blueG, blueB)
                    } else {
                        val factor = (lum.toFloat() / threshold.toFloat()).coerceIn(0f, 1f)
                        val darkVal = (factor * 40).toInt()
                        pixels[index] = Color.rgb(darkVal, darkVal, darkVal)
                    }
                }
            }
        }

        val cleaned = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)

        if (autoTrim && hasInk && maxX > minX && maxY > minY) {
            val padding = 16
            val cropLeft = max(0, minX - padding)
            val cropTop = max(0, minY - padding)
            val cropWidth = min(width - cropLeft, (maxX - minX) + padding * 2)
            val cropHeight = min(height - cropTop, (maxY - minY) + padding * 2)

            if (cropWidth > 10 && cropHeight > 10) {
                val trimmed = Bitmap.createBitmap(cleaned, cropLeft, cropTop, cropWidth, cropHeight)
                if (cleaned != trimmed) cleaned.recycle()
                return@withContext trimmed
            }
        }

        cleaned
    }
}
