package com.sixtyninefourtwenty.vacationdaysreworked.fragments

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sixtyninefourtwenty.vacationdaysreworked.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        with(findPreference<Preference>("navigation_appearance_prefs")!!) {
            setOnPreferenceClickListener {
                findNavController().navigate(SettingsFragmentDirections.actionNavigationSettingsToNavigationAppearanceSettings())
                true
            }
        }
    }

}