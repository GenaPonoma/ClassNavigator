package com.example.classnavigator.ui.schedule

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classnavigator.data.model.Course
import com.example.classnavigator.data.database.HistoryEntry
import com.example.classnavigator.utils.HistoryUtils
import com.example.classnavigator.data.network.ScheduleApi
import com.example.classnavigator.utils.ScheduleParser
import kotlinx.coroutines.launch

/**
 * ViewModel для главного экрана расписания
 */
class ScheduleMainViewModel : ViewModel() {
    // Список занятий
    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    // Состояние загрузки
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // Сообщение об ошибке
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // История запросов
    private val _history = MutableLiveData<List<HistoryEntry>>()
    val history: LiveData<List<HistoryEntry>> = _history

    // Состояние поиска
    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    // Загрузка расписания по ссылке
    fun loadSchedule(url: String, api: ScheduleApi) {
        _loading.value = true
        _error.value = null
        viewModelScope.launch {
            try {
                val html = api.getScheduleHtml(url)
                val result = ScheduleParser.parseHtmlToCourses(html)
                _courses.value = result
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    // Сохранение в историю
    fun saveToHistory(context: Context, searchType: String, searchValue: String, weekType: String, date: String) {
        viewModelScope.launch {
            try {
                val emptyCourses = emptyList<Course>()
                HistoryUtils.saveHistory(
                    context = context,
                    searchType = searchType,
                    searchValue = searchValue,
                    weekType = weekType,
                    courses = emptyCourses
                )
                loadHistory(context)
            } catch (e: Exception) {
                // Игнорируем ошибки сохранения истории
            }
        }
    }

    // Удаление из истории
    fun deleteFromHistory(context: Context, entry: HistoryEntry) {
        viewModelScope.launch {
            try {
                HistoryUtils.deleteHistory(context, entry.id.toLong())
                loadHistory(context)
            } catch (e: Exception) {
                _error.value = "Ошибка удаления из истории"
            }
        }
    }

    // Загрузка истории
    fun loadHistory(context: Context) {
        viewModelScope.launch {
            try {
                val historyList = HistoryUtils.getAllHistory(context)
                _history.value = historyList
            } catch (e: Exception) {
                _history.value = emptyList()
            }
        }
    }

    // Обработка поиска по группе
    fun searchByGroup(group: String, weekType: String, date: String, api: ScheduleApi, context: Context): Boolean {
        if (group.isEmpty()) {
            _error.value = "Введите группу"
            return false
        }
        
        val url = "https://cchgeu.ru/studentu/onlayn-raspisanie/${group}/$date/$weekType"
        loadSchedule(url, api)
        saveToHistory(context, "group", group, weekType, date)
        return true
    }

    // Обработка поиска по преподавателю
    fun searchByTeacher(teacher: String, weekType: String, date: String, api: ScheduleApi, context: Context): Boolean {
        if (teacher.isEmpty()) {
            _error.value = "Введите преподавателя"
            return false
        }
        
        val url = "https://cchgeu.ru/studentu/onlayn-raspisanie/prepodavatel/${teacher}/$date/$weekType"
        loadSchedule(url, api)
        saveToHistory(context, "teacher", teacher, weekType, date)
        return true
    }

    // Обработка поиска по дате
    fun searchByDate(searchDate: String, group: String, weekType: String, api: ScheduleApi, context: Context): Boolean {
        if (searchDate.isEmpty() || group.isEmpty()) {
            _error.value = "Введите дату и группу"
            return false
        }
        
        val url = "https://cchgeu.ru/studentu/onlayn-raspisanie/${group}/$searchDate/$weekType"
        loadSchedule(url, api)
        saveToHistory(context, "group", group, weekType, searchDate)
        return true
    }

    // Обработка повторного поиска из истории
    fun repeatSearchFromHistory(entry: HistoryEntry, api: ScheduleApi, context: Context) {
        val date = getCurrentDateString()
        val url = when (entry.searchType) {
            "group" -> "https://cchgeu.ru/studentu/onlayn-raspisanie/${entry.searchValue}/$date/${entry.weekType}"
            "teacher" -> "https://cchgeu.ru/studentu/onlayn-raspisanie/prepodavatel/${entry.searchValue}/$date/${entry.weekType}"
            else -> return
        }
        loadSchedule(url, api)
        saveToHistory(context, entry.searchType, entry.searchValue, entry.weekType, date)
    }

    // Получение текущей даты
    private fun getCurrentDateString(): String {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        return String.format("%04d-%02d-%02d", year, month, day)
    }

    // Очистка ошибки
    fun clearError() {
        _error.value = null
    }

    // Состояние поиска
    data class SearchState(
        val searchType: String = "group",
        val group: String = "",
        val teacher: String = "",
        val date: String = "",
        val dateGroup: String = "",
        val weekType: String = "числитель"
    )
} 