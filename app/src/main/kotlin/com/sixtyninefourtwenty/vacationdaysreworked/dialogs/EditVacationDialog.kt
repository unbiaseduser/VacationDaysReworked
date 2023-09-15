package com.sixtyninefourtwenty.vacationdaysreworked.dialogs

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.sixtyninefourtwenty.stuff.makeToast
import com.sixtyninefourtwenty.vacationdaysreworked.R
import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import kotlinx.coroutines.launch

class EditVacationDialog : AbstractAddEditVacationDialog() {

    private val args: EditVacationDialogArgs by navArgs()

    override val existingVacation: Vacation
        get() = args.vacation

    override val title: Int = R.string.edit_vacation

    override val subclassFragmentManager: FragmentManager
        get() = childFragmentManager

    override fun onVacationCreated(newVacation: Vacation) {
        mainViewModel.updateVacations(newVacation).invokeOnCompletion {
            lifecycleScope.launch {
                requireContext().makeToast(R.string.edit_successful).show()
                dismiss()
            }
        }
    }

}