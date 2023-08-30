package com.sixtyninefourtwenty.vacationdaysreworked.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.sixtyninefourtwenty.bottomsheetalertdialog.BottomSheetAlertDialogFragmentViewBuilder
import com.sixtyninefourtwenty.bottomsheetalertdialog.DialogButtonProperties
import com.sixtyninefourtwenty.vacationdaysreworked.R
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.DialogAddEditVacationBinding
import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import com.sixtyninefourtwenty.vacationdaysreworked.utils.getInput
import com.sixtyninefourtwenty.vacationdaysreworked.utils.isBlank
import com.sixtyninefourtwenty.vacationdaysreworked.viewmodels.MainViewModel
import kotlinx.coroutines.launch

abstract class AbstractAddEditVacationDialog : BottomSheetDialogFragment() {

    abstract val existingVacation: Vacation?

    @get:StringRes
    abstract val title: Int

    abstract fun onVacationCreated(newVacation: Vacation)

    abstract val subclassFragmentManager: FragmentManager

    private var nullableRoot: View? = null
    private val root get() = nullableRoot!!
    private lateinit var binding: DialogAddEditVacationBinding

    protected val mainViewModel: MainViewModel by activityViewModels { MainViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        fun openSingleDatePicker() {
            MaterialDatePicker.Builder.datePicker().build().apply {
                addOnPositiveButtonClickListener {
                    onVacationCreated(
                        Vacation(
                            name = existingVacation?.name ?: binding.nameInput.getInput(),
                            description = binding.descInput.getInput(),
                            isSickDay = binding.isSick.isChecked,
                            timeFrom = it,
                            timeTo = it
                        )
                    )
                }
                show(subclassFragmentManager, null)
            }
        }

        fun openDateRangePicker() {
            MaterialDatePicker.Builder.dateRangePicker().build().apply {
                addOnPositiveButtonClickListener {
                    onVacationCreated(
                        Vacation(
                            name = existingVacation?.name ?: binding.nameInput.getInput(),
                            description = binding.descInput.getInput(),
                            isSickDay = binding.isSick.isChecked,
                            timeFrom = it.first,
                            timeTo = it.second
                        )
                    )
                }
                show(subclassFragmentManager, null)
            }
        }

        binding = DialogAddEditVacationBinding.inflate(inflater, container, false)
        nullableRoot = BottomSheetAlertDialogFragmentViewBuilder(binding.root, this)
            .setTitle(title)
            .setPositiveButton(DialogButtonProperties(
                textRes = android.R.string.ok,
                listener = {
                    if (existingVacation == null && binding.nameInput.isBlank()) {
                        binding.nameInputLayout.error = getString(R.string.empty_name_error)
                    } else if (binding.nameInput.error != null) {

                    } else {
                        when {
                            binding.singleDate.isChecked -> openSingleDatePicker()
                            binding.dateRange.isChecked -> openDateRangePicker()
                            binding.dontUpdateDates.isChecked -> {
                                //If this one is checked, that means we're editing a vacation.
                                val vacation = existingVacation!!
                                onVacationCreated(
                                    Vacation(
                                        name = vacation.name,
                                        description = binding.descInput.getInput(),
                                        isSickDay = binding.isSick.isChecked,
                                        timeFrom = vacation.timeFrom,
                                        timeTo = vacation.timeTo
                                    )
                                )
                            }
                        }
                    }
                },
                dismissAfterClick = false
            ))
            .setNegativeButton(DialogButtonProperties(textRes = android.R.string.cancel))
            .rootView
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vacation = existingVacation
        if (vacation != null) {
            binding.descInput.setText(vacation.description)
            with(binding.nameInput) {
                setText(vacation.name)
                visibility = View.GONE
            }
            binding.isSick.isChecked = vacation.isSickDay
            with(binding.dontUpdateDates) {
                isChecked = true
                visibility = View.VISIBLE
            }
        } else {
            binding.nameInput.addTextChangedListener(onTextChanged = { s, _, _, _ ->
                if (s != null) {
                    lifecycleScope.launch {
                        val isVacationPresent =
                            mainViewModel.checkIfVacationExistsAsync(s.toString()).await()
                        binding.nameInput.error =
                            if (isVacationPresent) getString(R.string.vacation_already_exists_error) else null
                    }
                }
            })
            binding.dontUpdateDates.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        nullableRoot = null
    }


}