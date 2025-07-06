package com.example.classnavigator.utils

import android.content.Context
import androidx.room.Room
import com.example.classnavigator.data.database.AppDatabase
import com.example.classnavigator.data.database.HistoryEntry
import com.example.classnavigator.data.model.Course
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Утилиты для работы с историей запросов: сериализация/десериализация, сохранение в Room.
 */
object HistoryUtils {
    private val gson = Gson()

    /**
     * Преобразовать список занятий в JSON
     */
    fun coursesToJson(courses: List<Course>): String = gson.toJson(courses)

    /**
     * Преобразовать JSON обратно в список занятий
     */
    fun jsonToCourses(json: String): List<Course> {
        val type = object : TypeToken<List<Course>>() {}.type
        return gson.fromJson(json, type)
    }

    /**
     * Сохранить историю поиска в базу данных Room
     * @param context Context приложения
     * @param searchType Тип поиска ("group"/"teacher")
     * @param searchValue Значение поиска (группа или ФИО)
     * @param weekType Тип недели ("числитель"/"знаменатель")
     * @param courses Список занятий (результат поиска)
     */
    suspend fun saveHistory(
        context: Context,
        searchType: String,
        searchValue: String,
        weekType: String,
        courses: List<Course>
    ) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app-db"
        ).build()
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val entry = HistoryEntry(
            searchType = searchType,
            searchValue = searchValue,
            date = date,
            weekType = weekType,
            coursesJson = coursesToJson(courses)
        )
        withContext(Dispatchers.IO) {
            db.historyDao().insert(entry)
        }
    }

    /**
     * Получить всю историю запросов из базы данных Room
     * @param context Context приложения
     * @return Список записей истории
     */
    suspend fun getAllHistory(context: Context): List<HistoryEntry> {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app-db"
        ).build()
        return withContext(Dispatchers.IO) {
            db.historyDao().getAll()
        }
    }

    /**
     * Удалить запись из истории по ID
     * @param context Context приложения
     * @param entryId ID записи для удаления
     */
    suspend fun deleteHistory(context: Context, entryId: Long) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app-db"
        ).build()
        withContext(Dispatchers.IO) {
            db.historyDao().deleteById(entryId)
        }
    }

    /**
     * Очистить всю историю запросов
     * @param context Context приложения
     */
    suspend fun clearAllHistory(context: Context) {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app-db"
        ).build()
        withContext(Dispatchers.IO) {
            db.historyDao().deleteAll()
        }
    }

    /**
     * Преобразовать запись истории в список занятий
     */
    fun historyEntryToCourses(entry: HistoryEntry): List<Course> = jsonToCourses(entry.coursesJson)

    /**
     * Пример: получить историю и вывести названия всех найденных групп
     *
     * suspend fun printHistory(context: Context) {
     *     val history = HistoryUtils.getAllHistory(context)
     *     for (entry in history) {
     *         println("Тип: ${entry.searchType}, Значение: ${entry.searchValue}, Дата: ${entry.date}")
     *         val courses = HistoryUtils.historyEntryToCourses(entry)
     *         println("Найдено занятий: ${courses.size}")
     *     }
     * }
     */
} 