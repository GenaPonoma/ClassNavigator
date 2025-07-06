package com.example.classnavigator.data.model

// 课程数据类
// weekDay: 1-7 代表周一到周日
// startTime, endTime 格式如 "08:00"
data class Course(
    val name: String,
    val teacher: String,
    val classroom: String,
    val weekDay: Int,
    val startTime: String,
    val endTime: String,
    val type: String, // тип занятия
    val date: String  // дата занятия
) 