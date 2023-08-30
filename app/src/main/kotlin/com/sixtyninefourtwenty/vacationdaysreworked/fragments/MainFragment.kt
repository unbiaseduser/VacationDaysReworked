package com.sixtyninefourtwenty.vacationdaysreworked.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.sixtyninefourtwenty.basefragments.BaseFragment
import com.sixtyninefourtwenty.vacationdaysreworked.R
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.FragmentMainBinding
import com.sixtyninefourtwenty.vacationdaysreworked.utils.navigate
import com.sixtyninefourtwenty.vacationdaysreworked.viewmodels.MainViewModel
import kotlinx.coroutines.launch

class MainFragment : BaseFragment<FragmentMainBinding>() {

    private val mainViewModel: MainViewModel by activityViewModels { MainViewModel.Factory }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMainBinding {
        return FragmentMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentMainBinding, savedInstanceState: Bundle?) {
        val menu = MainFragmentMenu()
        requireActivity().addMenuProvider(menu, viewLifecycleOwner)
        binding.pager.adapter = MainViewPagerAdapter(requireActivity())
        binding.pager.offscreenPageLimit = 2
        TabLayoutMediator(binding.tabs, binding.pager) { tab, pos ->
            when (pos) {
                0 -> tab.text = getString(R.string.timeline)
                1 -> tab.text = getString(R.string.calendar)
            }
        }.attach()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.allVacationsByTimeFromAscending.collect {
                    menu.reload(it.isEmpty())
                }
            }
        }
        binding.fab.setOnClickListener {
            navigate(MainFragmentDirections.actionMainFragmentToAddVacationDialog())
        }
    }

    private inner class MainFragmentMenu : MenuProvider {

        private var isNoVacationPresent = true

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) =
            menuInflater.inflate(R.menu.menu_main, menu)

        override fun onPrepareMenu(menu: Menu) {
            val summaryMenuItem = menu.findItem(R.id.action_summary)!!
            if (isNoVacationPresent) {
                summaryMenuItem.setEnabled(false).setVisible(false)
            } else {
                summaryMenuItem.setEnabled(true).setVisible(true)
            }
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_settings -> {
                    navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment())
                    true
                }

                R.id.action_summary -> {
                    navigate(MainFragmentDirections.actionMainFragmentToSummaryFragment())
                    true
                }

                else -> false
            }
        }

        fun reload(isNoVacationPresent: Boolean) {
            this.isNoVacationPresent = isNoVacationPresent
            requireActivity().invalidateMenu()
        }

    }

    private class MainViewPagerAdapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TimelineFragment()
                else -> CalendarFragment()
            }
        }

    }
}