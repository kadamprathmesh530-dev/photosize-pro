package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CompressionHistoryDao {
    @Query("SELECT * FROM compression_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<CompressionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: CompressionHistoryEntity): Long

    @Query("DELETE FROM compression_history WHERE id = :id")
    suspend fun deleteHistoryById(id: Long)

    @Query("DELETE FROM compression_history")
    suspend fun clearAllHistory()
}
