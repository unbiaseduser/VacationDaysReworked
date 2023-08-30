package com.sixtyninefourtwenty.vacationdaysreworked.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.sixtyninefourtwenty.vacationdaysreworked.utils.obtainDeviceLocale
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Entity(tableName = "vacations")
@Parcelize
data class Vacation(
    @PrimaryKey val name: String,
    val description: String,
    @ColumnInfo(name = "is_sick") val isSickDay: Boolean,
    @ColumnInfo(name = "time_from") val timeFrom: Long,
    @ColumnInfo(name = "time_to") val timeTo: Long
) : Parcelable {
    companion object {
        private const val datePattern = "dd"
        private const val dateMonthPattern = "MMM dd"
        private const val dateMonthYearPattern = "MMM dd uuuu"
        private val localeToDateFormatterMap = mutableMapOf<Locale, DateTimeFormatter>()
        private val localeToDateMonthFormatterMap = mutableMapOf<Locale, DateTimeFormatter>()
        private val localeToDateMonthYearFormatterMap = mutableMapOf<Locale, DateTimeFormatter>()
    }

    @Ignore
    constructor(
        dateFrom: LocalDate,
        dateTo: LocalDate
    ) : this(
        "",
        "",
        false,
        dateFrom.atTime(LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli(),
        dateTo.atTime(LocalTime.of(0, 0)).toInstant(ZoneOffset.UTC).toEpochMilli()
    )

    @IgnoredOnParcel
    @Ignore
    private val dateFrom: LocalDate =
        Instant.ofEpochMilli(timeFrom).atZone(ZoneId.systemDefault()).toLocalDate()

    @IgnoredOnParcel
    @Ignore
    private val dateTo: LocalDate =
        Instant.ofEpochMilli(timeTo).atZone(ZoneId.systemDefault()).toLocalDate()

    @IgnoredOnParcel
    @Ignore
    private val period: Period = Period.between(dateFrom, dateTo.plusDays(1))

    @IgnoredOnParcel
    @Ignore
    private val isSpreadAcrossYears = dateFrom.year != dateTo.year

    @IgnoredOnParcel
    @Ignore
    val isSpreadAcrossMonths = if (isSpreadAcrossYears) true else dateFrom.month != dateTo.month

    enum class Status {
        PAST, PRESENT, FUTURE
    }

    fun calculateStatus(): Status {
        val today = LocalDate.now()
        return when {
            today.isBefore(dates.first()) -> Status.FUTURE
            today.isAfter(dates.last()) -> Status.PAST
            else -> Status.PRESENT
        }
    }

    fun calculateDaysUntilStartOrEnd(): Int {
        val status = calculateStatus()
        if (status == Status.PAST) return -1
        val targetDate = if (status == Status.FUTURE) dateFrom else dateTo
        var result = 0
        var refDate = LocalDate.now()
        while (refDate.isBefore(targetDate)) {
            result++
            refDate = refDate.plusDays(1)
        }
        return result
    }

    private fun getDateFormatter(locale: Locale): DateTimeFormatter {
        return when {
            isSpreadAcrossYears -> localeToDateMonthYearFormatterMap.computeIfAbsent(locale) { l -> DateTimeFormatter.ofPattern(dateMonthYearPattern, l) }
            isSpreadAcrossMonths -> localeToDateMonthFormatterMap.computeIfAbsent(locale) { l -> DateTimeFormatter.ofPattern(dateMonthPattern, l) }
            else -> localeToDateFormatterMap.computeIfAbsent(locale) { l -> DateTimeFormatter.ofPattern(datePattern, l) }
        }
    }

    fun formatDateFrom(locale: Locale = obtainDeviceLocale()): String =
        getDateFormatter(locale).format(dateFrom)

    fun formatDateTo(locale: Locale = obtainDeviceLocale()): String =
        getDateFormatter(locale).format(dateTo)

    infix fun hasSameMonthYearAs(other: Vacation): Boolean {
        return this.dateFrom.year == other.dateFrom.year &&
                this.dateFrom.month == other.dateFrom.month &&
                this.dateTo.year == other.dateTo.year &&
                this.dateTo.month == other.dateTo.month
    }

    @IgnoredOnParcel
    @Ignore
    val dates: List<LocalDate>

    @IgnoredOnParcel
    @Ignore
    val isSingleDay: Boolean

    init {
        require(timeFrom <= timeTo) { "Start time must be less than or equal to end time" }

        fun initListOfDates(): List<LocalDate> {
            var localDateFrom = dateFrom
            val list = mutableListOf(localDateFrom)
            while (localDateFrom.isBefore(dateTo)) {
                localDateFrom = localDateFrom.plusDays(1).also(list::add)
            }
            return list
        }

        dates = initListOfDates()
        isSingleDay = dates.size == 1
    }

}