package com.sixtyninefourtwenty.vacationdaysreworked.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import kotlinx.coroutines.flow.Flow

@Dao
interface VacationsDao {

    @Insert
    suspend fun insertVacation(vararg vacations: Vacation)

    @Update
    suspend fun updateVacation(vararg vacations: Vacation)

    @Delete
    suspend fun deleteVacation(vararg vacations: Vacation)

    @Delete
    suspend fun deleteVacation(vacations: List<Vacation>)

    @Query("SELECT * FROM vacations ORDER BY time_from ASC")
    fun getAllVacationsByTimeFromAscending(): Flow<List<Vacation>>

    @Query("SELECT * FROM vacations WHERE name LIKE :name")
    suspend fun findVacationsByName(name: String): List<Vacation>

    @Query("SELECT * FROM vacations WHERE description LIKE :description")
    suspend fun findVacationsByDescription(description: String): List<Vacation>

    @Query("SELECT EXISTS (SELECT name FROM vacations WHERE name = :name)")
    suspend fun checkIfVacationExists(name: String): Boolean
}