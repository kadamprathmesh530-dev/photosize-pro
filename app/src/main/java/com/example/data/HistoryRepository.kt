package com.example.data

import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val dao: CompressionHistoryDao) {
    val allHistory: Flow<List<CompressionHistoryEntity>> = dao.getAllHistory()

    suspend fun insert(entity: CompressionHistoryEntity): Long {
        return dao.insertHistory(entity)
    }

    suspend fun deleteById(id: Long) {
        dao.deleteHistoryById(id)
    }

    suspend fun clearAll() {
        dao.clearAllHistory()
    }
}
