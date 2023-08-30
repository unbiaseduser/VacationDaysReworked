package com.sixtyninefourtwenty.vacationdaysreworked

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.PreferenceManager
import com.sixtyninefourtwenty.theming.applyTheming
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)
        applyTheming(
            material2ThemeStyleRes = R.style.Theme_VacationDaysReworked,
            material3CustomColorsThemeStyleRes = R.style.Theme_VacationDaysReworked_Material3_Android11,
            material3DynamicColorsThemeStyleRes = R.style.Theme_VacationDaysReworked_Material3
        )
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}