package com.sixtyninefourtwenty.vacationdaysreworked.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sixtyninefourtwenty.basefragments.BaseFragment
import com.sixtyninefourtwenty.stuff.asApplication
import com.sixtyninefourtwenty.vacationdaysreworked.MyApplication
import com.sixtyninefourtwenty.vacationdaysreworked.R
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.FragmentSummaryBinding
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.ListItemVacationPerMonthBinding
import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import com.sixtyninefourtwenty.vacationdaysreworked.data.VacationDaysPerMonth
import com.sixtyninefourtwenty.vacationdaysreworked.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

class SummaryFragment : BaseFragment<FragmentSummaryBinding>() {

    private lateinit var binding: FragmentSummaryBinding
    private var trackSickDays = false
    private val mainViewModel: MainViewModel by activityViewModels { MainViewModel.Factory }
    private val adapter = VacationDaysPerMonthAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSummaryBinding {
        return FragmentSummaryBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentSummaryBinding, savedInstanceState: Bundle?) {
        this.binding = binding
        with(binding.list) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SummaryFragment.adapter
        }
        trackSickDays = requireContext().asApplication<MyApplication>().prefs.trackSickDays
        lifecycleScope.launch {
            mainViewModel.allVacationsByTimeFromAscending.collect { list ->
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, getYearsVacationsAreIn(list)).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.yearPicker.adapter = it
                }
                binding.yearPicker.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val year = parent!!.selectedItem as Int
                            binding.summaryYear.text = getString(
                                R.string.summary_year,
                                year,
                                getNumOfVacationDaysForYear(year, list)
                            )
                            adapter.submitList(getVacationDaysPerMonth(list, year))
                            adapter.setYear(year)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {/*unused*/
                        }
                    }
            }
        }
    }

    private fun getYearsVacationsAreIn(vacations: List<Vacation>): List<Int> =
        vacations.flatMap { it.dates }.map { it.year }.distinct()

    private fun getNumOfVacationDaysForMonth(month: Month, year: Int, vacations: List<Vacation>): Int {
        var copyOfVacations = vacations
        if (!trackSickDays) {
            copyOfVacations = copyOfVacations.filter { !it.isSickDay }
        }
        return copyOfVacations.flatMap { it.dates }
            .filter { it.month == month && it.year == year }
            .distinct()
            .size
    }

    private fun getVacationDaysPerMonth(vacations: List<Vacation>, year: Int): List<VacationDaysPerMonth> {
        val result = mutableListOf<VacationDaysPerMonth>()
        var copyOfVacations = vacations
        if (!trackSickDays) {
            copyOfVacations = copyOfVacations.filter { !it.isSickDay }
        }
        val datesInYear = copyOfVacations.flatMap { it.dates }
            .filter { it.year == year }
            .distinct()
        for (month in Month.values()) {
            val numOfDates = datesInYear.filter { it.month == month }.size
            if (numOfDates > 0) {
                result.add(VacationDaysPerMonth(month, numOfDates))
            }
        }
        return result
    }

    private fun getNumOfVacationDaysForYear(year: Int, vacations: List<Vacation>): Int {
        var total = 0
        for (month in Month.values())
            total += getNumOfVacationDaysForMonth(month, year, vacations)
        return total
    }

    private class VacationDaysPerMonthAdapter : ListAdapter<VacationDaysPerMonth, VacationDaysPerMonthAdapter.ViewHolder>(DIFFER) {

        private var isLeapYear = false

        @SuppressLint("NotifyDataSetChanged")
        fun setYear(year: Int) {
            isLeapYear = LocalDate.of(year, 1, 1).isLeapYear
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ListItemVacationPerMonthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getItem(position)
            val context = holder.binding.root.context
            val daysInMonth = item.month.length(isLeapYear)
            holder.binding.month.text = item.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
            holder.binding.summary.text = context.getString(R.string.summary_month, item.numOfVacationDaysForMonth, daysInMonth)
            holder.binding.bar.progress =
                ((item.numOfVacationDaysForMonth / daysInMonth.toFloat()) * 100).roundToInt()
        }

        companion object {
            private val DIFFER = object : DiffUtil.ItemCallback<VacationDaysPerMonth>() {
                override fun areItemsTheSame(
                    oldItem: VacationDaysPerMonth,
                    newItem: VacationDaysPerMonth
                ): Boolean = oldItem.month == newItem.month

                override fun areContentsTheSame(
                    oldItem: VacationDaysPerMonth,
                    newItem: VacationDaysPerMonth
                ): Boolean = oldItem == newItem

            }
        }

        private class ViewHolder(val binding: ListItemVacationPerMonthBinding) : RecyclerView.ViewHolder(binding.root) {

        }
    }

}