package com.example.classnavigator.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

/**
 * DAO для доступа к истории запросов расписания в базе данных Room.
 */
@Dao
interface HistoryDao {
    /**
     * Вставить новую запись в историю
     */
    @Insert
    suspend fun insert(entry: HistoryEntry)

    /**
     * Получить всю историю (по убыванию даты)
     */
    @Query("SELECT * FROM history ORDER BY date DESC")
    suspend fun getAll(): List<HistoryEntry>

    /**
     * Удалить запись
     */
    @Delete
    suspend fun delete(entry: HistoryEntry)

    /**
     * Удалить запись по ID
     */
    @Query("DELETE FROM history WHERE id = :entryId")
    suspend fun deleteById(entryId: Long)

    /**
     * Удалить все записи из истории
     */
    @Query("DELETE FROM history")
    suspend fun deleteAll()

    /**
     * Найти по типу поиска и значению
     */
    @Query("SELECT * FROM history WHERE search_type = :type AND search_value = :value ORDER BY date DESC")
    suspend fun findByTypeAndValue(type: String, value: String): List<HistoryEntry>
} 