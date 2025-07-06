package com.example.classnavigator.data.network

import retrofit2.http.GET
import retrofit2.http.Url

// Интерфейс для Retrofit, чтобы получать HTML-страницу расписания
interface ScheduleApi {
    @GET
    suspend fun getScheduleHtml(@Url url: String): String
} 