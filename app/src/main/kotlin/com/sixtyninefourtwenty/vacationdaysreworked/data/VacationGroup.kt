package com.sixtyninefourtwenty.vacationdaysreworked.data

import android.content.Context
import android.os.Parcelable
import com.sixtyninefourtwenty.vacationdaysreworked.R
import com.sixtyninefourtwenty.vacationdaysreworked.utils.obtainDeviceLocale
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Parcelize
data class VacationGroup(val vacations: List<Vacation>) : Parcelable {

    companion object {
        private val localeToMonthYearFormatterMap = mutableMapOf<Locale, DateTimeFormatter>()
        private const val monthYearPattern = "MMM uuuu"
        fun buildListOfVacationGroups(
            vacations: List<Vacation>
        ): List<VacationGroup> {
            var copyOfVacations = vacations
            val finalList = mutableListOf<VacationGroup>()
            while (copyOfVacations.isNotEmpty()) {
                val reference = copyOfVacations[0]
                val filteredVacations =
                    copyOfVacations.filter { reference hasSameMonthYearAs it }
                finalList.add(VacationGroup(filteredVacations))
                copyOfVacations = copyOfVacations.filter { !filteredVacations.contains(it) }
            }
            return finalList
        }
    }

    fun getTitle(context: Context, locale: Locale = obtainDeviceLocale()): String {
        val formatter = localeToMonthYearFormatterMap.computeIfAbsent(locale) { l -> DateTimeFormatter.ofPattern(monthYearPattern, l) }
        val dateFrom =
            Instant.ofEpochMilli(vacations[0].timeFrom).atZone(ZoneId.systemDefault()).toLocalDate()
        return if (!vacations[0].isSpreadAcrossMonths) {
            formatter.format(dateFrom)
        } else {
            val dateTo = Instant.ofEpochMilli(vacations[0].timeTo).atZone(ZoneId.systemDefault())
                .toLocalDate()
            context.getString(
                R.string.date_display,
                formatter.format(dateFrom),
                formatter.format(dateTo)
            )
        }
    }

    init {
        require(vacations.isNotEmpty()) { "Vacation group must not be empty" }
    }

}