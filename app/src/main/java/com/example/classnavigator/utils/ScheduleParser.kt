package com.example.classnavigator.utils

import com.example.classnavigator.data.model.Course
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

/**
 * Парсер HTML для извлечения данных расписания.
 */
object ScheduleParser {
    
    /**
     * Парсинг HTML и извлечение списка занятий
     */
    fun parseHtmlToCourses(html: String): List<Course> {
        val courses = mutableListOf<Course>()
        
        try {
            val doc: Document = Jsoup.parse(html)
            val table = doc.select("table").firstOrNull()
            
            if (table != null) {
                val rows: Elements = table.select("tr")
                var currentDay = 1
                var currentDate = ""
                
                for (row in rows) {
                    val cells: Elements = row.select("td")
                    if (cells.size >= 4) {
                        val timeText = cells[0].text().trim()
                        val classroom = cells[1].text().trim()
                        val lessonInfo = cells[2].text().trim()
                        val teacher = cells[3].text().trim()
                        
                        // Извлекаем название предмета и тип занятия
                        val lessonParts = lessonInfo.split("**")
                        val name = lessonParts.getOrNull(1) ?: lessonInfo
                        val type = lessonParts.getOrNull(0)?.trim() ?: ""
                        
                        // Время начала и конца пары
                        val times = timeText.split("-")
                        val startTime = times.getOrNull(0)?.trim() ?: ""
                        val endTime = times.getOrNull(1)?.trim() ?: ""
                        
                        // Проверяем, не начался ли новый день недели и дата
                        val dayDateRegex = Regex("(Пн\\.|Вт\\.|Ср\\.|Чт\\.|Пт\\.|Сб\\.|Вс\\.),? ?(\\d{2}\\.\\d{2}\\.\\d{4})?")
                        val dayDateMatch = dayDateRegex.find(cells[0].text())
                        if (dayDateMatch != null) {
                            currentDay = when {
                                cells[0].text().contains("Пн.") -> 1
                                cells[0].text().contains("Вт.") -> 2
                                cells[0].text().contains("Ср.") -> 3
                                cells[0].text().contains("Чт.") -> 4
                                cells[0].text().contains("Пт.") -> 5
                                cells[0].text().contains("Сб.") -> 6
                                cells[0].text().contains("Вс.") -> 7
                                else -> currentDay
                            }
                            currentDate = dayDateMatch.groupValues.getOrNull(2) ?: ""
                        }
                        
                        if (name.isNotEmpty() && startTime.isNotEmpty()) {
                            courses.add(Course(
                                name = name,
                                teacher = teacher,
                                classroom = classroom,
                                weekDay = currentDay,
                                startTime = startTime,
                                endTime = endTime,
                                type = type,
                                date = currentDate
                            ))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Логирование ошибки парсинга
            println("Ошибка парсинга HTML: ${e.message}")
        }
        
        return courses
    }
} 