package com.example.classnavigator.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

/**
 * Сущность для хранения истории запросов расписания в базе данных Room.
 * Позволяет сохранять все параметры поиска и результат (список занятий) в виде JSON.
 *
 * @property id Уникальный идентификатор записи
 * @property searchType Тип поиска ("group" или "teacher")
 * @property searchValue Значение поиска (группа или ФИО преподавателя)
 * @property date Дата запроса (yyyy-MM-dd HH:mm:ss)
 * @property weekType Тип недели ("числитель"/"знаменатель"/иное)
 * @property coursesJson Список занятий в виде JSON
 */
@Entity(tableName = "history")
data class HistoryEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "search_type")
    val searchType: String,
    @ColumnInfo(name = "search_value")
    val searchValue: String,
    @ColumnInfo(name = "date")
    val date: String,
    @ColumnInfo(name = "week_type")
    val weekType: String,
    @ColumnInfo(name = "courses_json")
    val coursesJson: String
) 