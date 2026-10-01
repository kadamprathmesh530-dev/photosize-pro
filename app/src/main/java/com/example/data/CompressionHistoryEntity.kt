package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compression_history")
data class CompressionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val targetKb: Int,
    val width: Int,
    val height: Int,
    val format: String,
    val filePath: String,
    val presetId: String,
    val timestamp: Long = System.currentTimeMillis()
)
