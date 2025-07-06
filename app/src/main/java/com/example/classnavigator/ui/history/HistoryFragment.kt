package com.example.classnavigator.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.classnavigator.R
import androidx.navigation.fragment.findNavController
import com.example.classnavigator.data.database.HistoryEntry
import com.example.classnavigator.ui.history.adapter.HistoryAdapter
import com.example.classnavigator.utils.HistoryUtils
import com.google.gson.Gson

/**
 * Фрагмент для отображения истории запросов расписания.
 * Показывает список истории, позволяет просматривать расписание из истории и удалять записи.
 */
class HistoryFragment : Fragment() {
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerHistory)
        val textEmpty = view.findViewById<View>(R.id.textEmpty)
        adapter = HistoryAdapter(
            onClick = { entry ->
                // Просмотр расписания из истории
                val courses = HistoryUtils.historyEntryToCourses(entry)
                val bundle = Bundle().apply {
                    putString("courses_json", Gson().toJson(courses))
                }
                findNavController().navigate(R.id.action_navigation_history_to_lessonDetailFragment, bundle)
            },
            onDelete = { entry ->
                viewModel.deleteEntry(entry)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.history.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
            textEmpty.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadHistory()
    }
} 