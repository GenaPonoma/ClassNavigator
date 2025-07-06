package com.example.classnavigator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.classnavigator.databinding.ActivityMainBinding
import com.example.classnavigator.utils.AppPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Применяем сохраненный язык
        applyLanguage()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation.bottomNavigation
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)

        // Проверяем, первый ли это запуск
        if (AppPreferences.isFirstLaunch(this)) {
            // Показываем стартовый экран при первом запуске
            navController.navigate(R.id.startFragment)
            AppPreferences.setFirstLaunch(this, false)
        } else {
            // При повторных запусках показываем экран расписания
            navController.navigate(R.id.scheduleMainFragment)
        }
    }
    
    private fun applyLanguage() {
        val language = AppPreferences.getLanguage(this)
        val locale = when (language) {
            "en" -> java.util.Locale.ENGLISH
            else -> java.util.Locale("ru")
        }
        
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}