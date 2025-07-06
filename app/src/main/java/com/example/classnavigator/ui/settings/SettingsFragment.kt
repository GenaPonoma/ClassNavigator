package com.example.classnavigator.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.classnavigator.R
import com.example.classnavigator.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Загружаем настройки
        viewModel.loadSettings(requireContext())
        
        // Устанавливаем версию приложения
        binding.textVersion.text = viewModel.getAppVersion(requireContext())
        
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        // Наблюдаем за текущим языком
        viewModel.currentLanguage.observe(viewLifecycleOwner) { language ->
            updateLanguageButton(language)
        }
        
        // Результат очистки истории
        viewModel.clearHistoryResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), getString(R.string.history_cleared), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_clearing_history), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        // Кнопка смены языка
        binding.buttonLanguage.setOnClickListener {
            viewModel.toggleLanguage(requireContext())
        }
        
        // Кнопка очистки истории
        binding.buttonClearHistory.setOnClickListener {
            // Показываем диалог подтверждения
            showClearHistoryDialog()
        }
        
        // Кнопка обратной связи
        binding.buttonFeedback.setOnClickListener {
            // Здесь можно добавить логику отправки обратной связи
            Toast.makeText(requireContext(), getString(R.string.feedback_development), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateLanguageButton(language: String) {
        when (language) {
            "en" -> {
                binding.buttonLanguage.text = getString(R.string.english)
                binding.buttonLanguage.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
            }
            "ru" -> {
                binding.buttonLanguage.text = getString(R.string.russian)
                binding.buttonLanguage.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
            }
        }
    }

    private fun showClearHistoryDialog() {
        // Простой диалог подтверждения
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.clear_history_title))
            .setMessage(getString(R.string.clear_history_message))
            .setPositiveButton(getString(R.string.clear)) { _, _ ->
                viewModel.clearHistory(requireContext())
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 