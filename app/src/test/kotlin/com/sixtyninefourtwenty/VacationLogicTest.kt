package com.sixtyninefourtwenty

import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class VacationLogicTest {

    companion object {
        private lateinit var presentVacation: Vacation
        private lateinit var pastVacation: Vacation
        private lateinit var futureVacation: Vacation
        @BeforeAll
        @JvmStatic
        fun setupAll() {
            presentVacation = Vacation(
                LocalDate.now().minusDays(4),
                LocalDate.now().plusDays(3)
            )
            pastVacation = Vacation(
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(4)
            )
            futureVacation = Vacation(
                LocalDate.now().plusDays(6),
                LocalDate.now().plusDays(12)
            )
        }
    }

    @Test
    fun failOnInvalidVacation() {
        assertThrows<IllegalArgumentException> { Vacation(LocalDate.now(), LocalDate.now().minusDays(1)) }
    }

    @Test
    fun testListOfDates() {
        assertIterableEquals(listOf(
            LocalDate.now().minusDays(4),
            LocalDate.now().minusDays(3),
            LocalDate.now().minusDays(2),
            LocalDate.now().minusDays(1),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2),
            LocalDate.now().plusDays(3)
        ), presentVacation.dates)
        val oneDayVacation = Vacation(
            LocalDate.now(),
            LocalDate.now()
        )
        assertIterableEquals(listOf(
            LocalDate.now()
        ), oneDayVacation.dates)
    }

    @Test
    fun testGetStatus() {
        assertEquals(Vacation.Status.PRESENT, presentVacation.calculateStatus())
        assertEquals(Vacation.Status.PAST, pastVacation.calculateStatus())
        assertEquals(Vacation.Status.FUTURE, futureVacation.calculateStatus())
    }

    @Test
    fun testGetNumOfDaysUntilStartOrEnd() {
        assertEquals(3, presentVacation.calculateDaysUntilStartOrEnd())
        assertEquals(6, futureVacation.calculateDaysUntilStartOrEnd())
        assertEquals(-1, pastVacation.calculateDaysUntilStartOrEnd())
    }

}