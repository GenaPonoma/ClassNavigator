package com.example.classnavigator.data.repository

import com.example.classnavigator.data.model.Course
import com.example.classnavigator.data.network.ScheduleApi
import com.example.classnavigator.utils.ScheduleParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Репозиторий для работы с расписанием.
 * Связывает сетевые запросы с парсингом данных.
 */
class ScheduleRepository(private val api: ScheduleApi) {
    
    /**
     * Загрузить расписание по URL
     */
    suspend fun loadSchedule(url: String): List<Course> {
        return withContext(Dispatchers.IO) {
            try {
                val html = api.getScheduleHtml(url)
                ScheduleParser.parseHtmlToCourses(html)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
} 