package com.example.classnavigator.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.classnavigator.data.database.AppDatabase
import com.example.classnavigator.data.database.HistoryEntry
import com.example.classnavigator.utils.HistoryUtils
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана истории запросов расписания.
 * Позволяет загружать историю из Room, удалять записи, наблюдать за изменениями.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val _history = MutableLiveData<List<HistoryEntry>>()
    val history: LiveData<List<HistoryEntry>> = _history

    /**
     * Загрузить всю историю из базы данных
     */
    fun loadHistory() {
        viewModelScope.launch {
            val list = HistoryUtils.getAllHistory(getApplication())
            _history.postValue(list)
        }
    }

    /**
     * Удалить запись из истории
     */
    fun deleteEntry(entry: HistoryEntry) {
        viewModelScope.launch {
            val db = Room.databaseBuilder(
                getApplication(),
                AppDatabase::class.java,
                "app-db"
            ).build()
            db.historyDao().delete(entry)
            // После удаления — обновить список
            loadHistory()
        }
    }
} 