package com.example.classnavigator.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classnavigator.R
import com.example.classnavigator.data.model.Course
import com.example.classnavigator.databinding.FragmentLessonDetailBinding

import com.example.classnavigator.ui.schedule.adapter.ScheduleAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LessonDetailFragment : Fragment() {
    private var _binding: FragmentLessonDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LessonDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLessonDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LessonDetailViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Получаем JSON из аргументов
        val coursesJson = arguments?.getString("courses_json")
        val courses: List<Course> = if (coursesJson != null) {
            val type = object : TypeToken<List<Course>>() {}.type
            Gson().fromJson(coursesJson, type)
        } else {
            emptyList()
        }
        // Отобразить список занятий (например, в RecyclerView)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerLessonDetail)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ScheduleAdapter().apply { submitList(courses) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 