package com.example.classnavigator.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.classnavigator.utils.AppPreferences
import com.example.classnavigator.utils.HistoryUtils
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    
    private val _currentLanguage = MutableLiveData<String>()
    val currentLanguage: LiveData<String> = _currentLanguage
    
    private val _clearHistoryResult = MutableLiveData<Boolean>()
    val clearHistoryResult: LiveData<Boolean> = _clearHistoryResult

    // Загрузка настроек
    fun loadSettings(context: Context) {
        _currentLanguage.value = AppPreferences.getLanguage(context)
    }

    // Переключение языка
    fun toggleLanguage(context: Context) {
        val currentLang = _currentLanguage.value ?: "ru"
        val newLang = if (currentLang == "ru") "en" else "ru"
        
        AppPreferences.setLanguage(context, newLang)
        _currentLanguage.value = newLang
        
        // Применяем язык
        applyLanguage(context, newLang)
        
        // Перезапускаем активность для применения языка
        (context as? androidx.appcompat.app.AppCompatActivity)?.recreate()
    }
    
    private fun applyLanguage(context: Context, language: String) {
        val locale = when (language) {
            "en" -> java.util.Locale.ENGLISH
            else -> java.util.Locale("ru")
        }
        
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    // Очистка истории
    fun clearHistory(context: Context) {
        viewModelScope.launch {
            try {
                HistoryUtils.clearAllHistory(context)
                _clearHistoryResult.value = true
            } catch (e: Exception) {
                _clearHistoryResult.value = false
            }
        }
    }

    // Получение версии приложения
    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: Exception) {
            "1.0.0"
        }
    }
} 