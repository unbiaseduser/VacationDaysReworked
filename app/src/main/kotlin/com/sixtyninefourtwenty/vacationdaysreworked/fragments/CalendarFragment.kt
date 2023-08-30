package com.sixtyninefourtwenty.vacationdaysreworked.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import com.sixtyninefourtwenty.basefragments.BaseFragment
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.FragmentCalendarBinding
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.SimpleCalendarDayBinding
import com.sixtyninefourtwenty.vacationdaysreworked.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    private val mainViewModel: MainViewModel by activityViewModels { MainViewModel.Factory }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCalendarBinding {
        return FragmentCalendarBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentCalendarBinding, savedInstanceState: Bundle?) {
        val dayViewBinder = SimpleDayViewBinder()
        with(binding.calendar) {
            setup(
                startMonth = YearMonth.of(YearMonth.now().minusYears(100).year, Month.JANUARY),
                endMonth = YearMonth.of(YearMonth.now().plusYears(100).year, Month.DECEMBER),
                firstDayOfWeek = firstDayOfWeekFromLocale()
            )
            scrollToDate(LocalDate.now())
            dayBinder = dayViewBinder
            monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    container.textView.text = data.yearMonth.format(YEAR_MONTH_FORMATTER)
                }

                override fun create(view: View): MonthViewContainer = MonthViewContainer(view)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.allVacationsByTimeFromAscending.collect { vacations ->
                    val dates = vacations.flatMap { it.dates }.distinct()
                    dayViewBinder.datesToHighlight = dates
                    binding.calendar.notifyCalendarChanged()
                }
            }
        }
    }

    private class SimpleDayViewContainer(view: View) : ViewContainer(view) {
        private val binding = SimpleCalendarDayBinding.bind(view)
        lateinit var day: CalendarDay
        val textView = binding.root
    }

    private class SimpleDayViewBinder : MonthDayBinder<SimpleDayViewContainer> {
        var datesToHighlight: List<LocalDate> = listOf()

        override fun bind(container: SimpleDayViewContainer, data: CalendarDay) {
            container.day = data
            container.textView.text = data.date.dayOfMonth.toString()
            if (data.position == DayPosition.OutDate || data.position == DayPosition.InDate) {
                container.textView.setTextColor(Color.TRANSPARENT)
            } else {
                if (datesToHighlight.contains(data.date)) {
                    container.textView.setTextColor(Color.RED)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }
            }
        }

        override fun create(view: View): SimpleDayViewContainer = SimpleDayViewContainer(view)
    }

    private class MonthViewContainer(view: View) : ViewContainer(view) {
        val textView = view as TextView
    }

    companion object {
        private val YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM uuuu")
    }

}