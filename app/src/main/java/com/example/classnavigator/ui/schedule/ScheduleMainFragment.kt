package com.example.classnavigator.ui.schedule

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.classnavigator.R
import com.example.classnavigator.databinding.FragmentScheduleMainBinding
import com.example.classnavigator.databinding.SearchPanelContentBinding
import com.example.classnavigator.ui.schedule.adapter.ScheduleAdapter
import com.example.classnavigator.data.network.ScheduleApi
import com.example.classnavigator.ui.history.adapter.HistoryAdapter
import com.example.classnavigator.data.database.HistoryEntry
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import android.widget.EditText

/**
 * Фрагмент для отображения главного расписания
 */
class ScheduleMainFragment : Fragment() {
    private var _binding: FragmentScheduleMainBinding? = null
    private val binding get() = _binding!!
    private var _searchPanelBinding: SearchPanelContentBinding? = null
    private val searchPanelBinding get() = _searchPanelBinding!!
    private val viewModel: ScheduleMainViewModel by viewModels()
    private lateinit var adapter: ScheduleAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var api: ScheduleApi
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var searchHandler: SearchHandler

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleMainBinding.inflate(inflater, container, false)
        
        // Исправлено: биндимся к первому дочернему элементу searchPanel
        val searchPanelContent = binding.searchPanel.getChildAt(0)
        _searchPanelBinding = SearchPanelContentBinding.bind(searchPanelContent)
        
        setupRecyclerView()
        setupBottomSheet()
        setupTabLayout()
        setupApi()
        setupSearchHandler()
        setupObservers()
        setupClickListeners()
        setupKeyboardListeners()
        loadHistory()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Загружаем историю при запуске
        loadHistory()
    }

    private fun setupRecyclerView() {
        adapter = ScheduleAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        historyAdapter = HistoryAdapter(
            onClick = { entry ->
                viewModel.repeatSearchFromHistory(entry, api, requireContext())
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            },
            onDelete = { entry ->
                viewModel.deleteFromHistory(requireContext(), entry)
            }
        )
        binding.recyclerRecentQueries.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerRecentQueries.adapter = historyAdapter
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.searchPanel)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.isHideable = true
    }

    private fun setupTabLayout() {
        searchPanelBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> showGroupSearch()
                    1 -> showTeacherSearch()
                    2 -> showDateSearch()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showGroupSearch() {
        searchPanelBinding.layoutGroupSearch.visibility = View.VISIBLE
        searchPanelBinding.layoutTeacherSearch.visibility = View.GONE
        searchPanelBinding.layoutDateSearch.visibility = View.GONE
    }

    private fun showTeacherSearch() {
        searchPanelBinding.layoutGroupSearch.visibility = View.GONE
        searchPanelBinding.layoutTeacherSearch.visibility = View.VISIBLE
        searchPanelBinding.layoutDateSearch.visibility = View.GONE
    }

    private fun showDateSearch() {
        searchPanelBinding.layoutGroupSearch.visibility = View.GONE
        searchPanelBinding.layoutTeacherSearch.visibility = View.GONE
        searchPanelBinding.layoutDateSearch.visibility = View.VISIBLE
    }

    private fun setupApi() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://cchgeu.ru/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        api = retrofit.create(ScheduleApi::class.java)
    }

    private fun setupSearchHandler() {
        searchHandler = SearchHandler()
    }

    private inner class SearchHandler {
        fun performSearch() {
            val currentTab = searchPanelBinding.tabLayout.selectedTabPosition
            
            when (currentTab) {
                0 -> searchByGroup()
                1 -> searchByTeacher()
                2 -> searchByDate()
            }
        }

        private fun searchByGroup() {
            val group = searchPanelBinding.editGroup.text.toString().trim()
            if (group.isNotEmpty()) {
                viewModel.searchByGroup(group, "числитель", getCurrentDateString(), api, requireContext())
            }
        }

        private fun searchByTeacher() {
            val teacher = searchPanelBinding.editTeacher.text.toString().trim()
            if (teacher.isNotEmpty()) {
                viewModel.searchByTeacher(teacher, "числитель", getCurrentDateString(), api, requireContext())
            }
        }

        private fun searchByDate() {
            val group = searchPanelBinding.editDateGroup.text.toString().trim()
            val date = searchPanelBinding.editDate.text.toString().trim()
            if (group.isNotEmpty() && date.isNotEmpty()) {
                viewModel.searchByDate(date, group, "числитель", api, requireContext())
            }
        }

        private fun getCurrentDateString(): String {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH) + 1
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            return String.format("%04d-%02d-%02d", year, month, day)
        }
    }

    private fun setupObservers() {
        // Подписка на список занятий
        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            adapter.submitList(courses)
            if (courses.isNullOrEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.layoutEmpty.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }
        
        // Подписка на состояние загрузки
        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading == true) View.VISIBLE else View.GONE
        }
        
        // Подписка на ошибки
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { 
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        // Подписка на историю
        viewModel.history.observe(viewLifecycleOwner) { history ->
            if (history.isNotEmpty()) {
                binding.textRecentQueries.visibility = View.VISIBLE
                binding.recyclerRecentQueries.visibility = View.VISIBLE
                historyAdapter.submitList(history.take(5))
            } else {
                binding.textRecentQueries.visibility = View.GONE
                binding.recyclerRecentQueries.visibility = View.GONE
            }
        }
    }

    private fun setupClickListeners() {
        // FAB для показа панели поиска
        binding.fabShowSearch.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Кнопка закрытия панели поиска
        searchPanelBinding.buttonCloseSearch.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // Обработка поиска по кнопке
        searchPanelBinding.buttonSearch.setOnClickListener {
            searchHandler.performSearch()
        }
    }

    private fun setupKeyboardListeners() {
        // Обработка Enter на клавиатуре для поиска
        searchPanelBinding.editGroup.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchHandler.performSearch()
                true
            } else {
                false
            }
        }

        searchPanelBinding.editTeacher.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchHandler.performSearch()
                true
            } else {
                false
            }
        }

        searchPanelBinding.editDateGroup.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchHandler.performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun loadHistory() {
        viewModel.loadHistory(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _searchPanelBinding = null
    }
} 