package com.example.classnavigator.ui.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.classnavigator.R
import com.example.classnavigator.databinding.FragmentStartBinding

/**
 * Стартовый экран приложения.
 * Показывается только при первом запуске приложения.
 */
class StartFragment : Fragment() {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StartViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Анимация появления элементов
        setupAnimations()
        
        // Настройка кнопок
        setupButtons()
        
        // Загрузка данных
        // viewModel.loadWelcomeData()
    }

    private fun setupAnimations() {
        // Анимация появления иконки
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        binding.imageViewIcon.startAnimation(fadeIn)
        
        // Анимация появления заголовка
        val slideUp = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left)
        binding.textViewTitle.startAnimation(slideUp)
        
        // Анимация появления подзаголовка
        val slideUpDelayed = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left)
        slideUpDelayed.startOffset = 300
        binding.textViewSubtitle.startAnimation(slideUpDelayed)
        
        // Анимация появления кнопок
        val scaleIn = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        scaleIn.startOffset = 600
        binding.buttonFindSchedule.startAnimation(scaleIn)
        
        val scaleInDelayed = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        scaleInDelayed.startOffset = 800
        binding.buttonViewHistory.startAnimation(scaleInDelayed)
        
        val scaleInDelayed2 = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        scaleInDelayed2.startOffset = 1000
        binding.buttonSettings.startAnimation(scaleInDelayed2)
    }

    private fun setupButtons() {
        // Кнопка поиска расписания
        binding.buttonFindSchedule.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_scheduleMainFragment)
        }
        
        // Кнопка истории
        binding.buttonViewHistory.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_navigation_history)
        }
        
        // Кнопка настроек
        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_settingsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 