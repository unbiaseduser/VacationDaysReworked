package com.sixtyninefourtwenty.vacationdaysreworked

import android.app.Application
import com.sixtyninefourtwenty.vacationdaysreworked.data.db.VacationsDatabase
import com.sixtyninefourtwenty.vacationdaysreworked.data.repository.Preferences
import com.sixtyninefourtwenty.vacationdaysreworked.data.repository.VacationsRepository

class MyApplication : Application() {
    private val database by lazy { VacationsDatabase.getDatabase(this) }
    val repository by lazy { VacationsRepository(database.vacationsDao()) }
    val prefs by lazy { Preferences(this) }
}
