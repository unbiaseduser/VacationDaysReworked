package com.sixtyninefourtwenty.vacationdaysreworked.data.repository

import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import com.sixtyninefourtwenty.vacationdaysreworked.data.db.VacationsDao
import kotlinx.coroutines.flow.distinctUntilChanged

class VacationsRepository(private val dao: VacationsDao) {

    val allVacationsByTimeFromAscending =
        dao.getAllVacationsByTimeFromAscending().distinctUntilChanged()

    suspend fun insertVacation(vararg vacations: Vacation) = dao.insertVacation(*vacations)

    suspend fun updateVacation(vararg vacations: Vacation) = dao.updateVacation(*vacations)

    suspend fun deleteVacation(vararg vacations: Vacation) = dao.deleteVacation(*vacations)

    suspend fun deleteVacation(vacations: List<Vacation>) = dao.deleteVacation(vacations)

    suspend fun findVacationsByName(name: String) = dao.findVacationsByName(name)

    suspend fun findVacationsByDescription(description: String) =
        dao.findVacationsByDescription(description)

    suspend fun checkIfVacationExists(name: String) =
        dao.checkIfVacationExists(name)

}
