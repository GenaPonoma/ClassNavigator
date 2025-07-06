package com.example.classnavigator.ui.schedule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.classnavigator.R
import com.example.classnavigator.data.model.Course

/**
 * Адаптер для отображения списка занятий в RecyclerView.
 */
class ScheduleAdapter : ListAdapter<Course, ScheduleAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textName: TextView = itemView.findViewById(R.id.textCourseName)
        private val textTime: TextView = itemView.findViewById(R.id.textTime)
        private val textTeacher: TextView = itemView.findViewById(R.id.textTeacher)
        private val textRoom: TextView = itemView.findViewById(R.id.textClassroom)
        private val textType: TextView = itemView.findViewById(R.id.textLessonType)

        fun bind(course: Course) {
            textName.text = course.name
            textTime.text = "${course.startTime} - ${course.endTime}"
            textTeacher.text = "Преподаватель: ${course.teacher}"
            textRoom.text = "Аудитория: ${course.classroom}"
            textType.text = course.type
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.name == newItem.name && oldItem.startTime == newItem.startTime
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
} 