package com.sixtyninefourtwenty.vacationdaysreworked.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sixtyninefourtwenty.vacationdaysreworked.MyApplication
import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import com.sixtyninefourtwenty.vacationdaysreworked.data.repository.VacationsRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel(private val vacationsRepository: VacationsRepository) : ViewModel() {

    val allVacationsByTimeFromAscending = vacationsRepository.allVacationsByTimeFromAscending

    fun insertVacations(vararg vacations: Vacation) =
        viewModelScope.launch(Dispatchers.IO) { vacationsRepository.insertVacation(*vacations) }

    fun updateVacations(vararg vacations: Vacation) =
        viewModelScope.launch(Dispatchers.IO) { vacationsRepository.updateVacation(*vacations) }

    fun deleteVacation(vararg vacations: Vacation) =
        viewModelScope.launch(Dispatchers.IO) { vacationsRepository.deleteVacation(*vacations) }

    fun deleteVacation(vacations: List<Vacation>) =
        viewModelScope.launch(Dispatchers.IO) { vacationsRepository.deleteVacation(vacations) }

    fun findVacationsByNameAsync(name: String): Deferred<List<Vacation>> =
        viewModelScope.async(Dispatchers.IO) { vacationsRepository.findVacationsByName(name) }

    fun findVacationsByDescriptionAsync(description: String): Deferred<List<Vacation>> =
        viewModelScope.async(Dispatchers.IO) {
            vacationsRepository.findVacationsByDescription(description)
        }

    fun checkIfVacationExistsAsync(name: String): Deferred<Boolean> =
        viewModelScope.async(Dispatchers.IO) { vacationsRepository.checkIfVacationExists(name) }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo = (get(APPLICATION_KEY) as MyApplication).repository
                MainViewModel(repo)
            }
        }
    }

}
