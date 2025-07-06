package com.example.classnavigator.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Утилита для работы с настройками приложения
 */
object AppPreferences {
    private const val PREF_NAME = "app_preferences"
    private const val KEY_FIRST_LAUNCH = "first_launch"
    private const val KEY_LANGUAGE = "language"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Проверить, является ли это первым запуском приложения
     */
    fun isFirstLaunch(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_FIRST_LAUNCH, true)
    }

    /**
     * Отметить, что приложение уже запускалось
     */
    fun setFirstLaunch(context: Context, isFirst: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_FIRST_LAUNCH, isFirst).apply()
    }

    /**
     * Получить текущий язык приложения
     */
    fun getLanguage(context: Context): String {
        return getPreferences(context).getString(KEY_LANGUAGE, "ru") ?: "ru"
    }

    /**
     * Установить язык приложения
     */
    fun setLanguage(context: Context, language: String) {
        getPreferences(context).edit().putString(KEY_LANGUAGE, language).apply()
    }
} 