package com.example.classnavigator.data.database

import androidx.room.Database
import androidx.room.RoomDatabase


/**
 * Главная база данных приложения для хранения истории запросов расписания.
 */
@Database(entities = [HistoryEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
} 