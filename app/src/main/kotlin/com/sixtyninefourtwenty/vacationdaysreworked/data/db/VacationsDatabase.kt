package com.sixtyninefourtwenty.vacationdaysreworked.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation

@Database(entities = [Vacation::class], version = 1)
abstract class VacationsDatabase : RoomDatabase() {
    abstract fun vacationsDao(): VacationsDao

    companion object {
        @Volatile
        private var INSTANCE: VacationsDatabase? = null

        fun getDatabase(context: Context): VacationsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VacationsDatabase::class.java,
                    "vacations"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}