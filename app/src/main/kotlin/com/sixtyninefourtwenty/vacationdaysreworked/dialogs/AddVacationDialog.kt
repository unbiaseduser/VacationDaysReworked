package com.sixtyninefourtwenty.vacationdaysreworked.dialogs

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.sixtyninefourtwenty.stuff.makeToast
import com.sixtyninefourtwenty.vacationdaysreworked.R
import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import kotlinx.coroutines.launch

class AddVacationDialog : AbstractAddEditVacationDialog() {

    override val existingVacation: Vacation? = null

    override val title: Int = R.string.add_vacation

    override val subclassFragmentManager: FragmentManager
        get() = childFragmentManager

    override fun onVacationCreated(newVacation: Vacation) {
        mainViewModel.insertVacations(newVacation).invokeOnCompletion {
            lifecycleScope.launch {
                requireContext().makeToast(R.string.add_successful).show()
                dismiss()
            }
        }
    }

}