package com.sixtyninefourtwenty.vacationdaysreworked.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class Preferences(context: Context) {

    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    var trackSickDays: Boolean
        get() = prefs.getBoolean("track_sick", true)
        set(value) { prefs.edit { putBoolean("track_sick", value) } }

}