package com.sixtyninefourtwenty.vacationdaysreworked.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kennyc.view.MultiStateView
import com.sixtyninefourtwenty.basefragments.BaseFragment
import com.sixtyninefourtwenty.vacationdaysreworked.R
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.ListItemVacationBinding
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.ListItemVacationGroupBinding
import com.sixtyninefourtwenty.vacationdaysreworked.data.Vacation
import com.sixtyninefourtwenty.vacationdaysreworked.data.VacationGroup
import com.sixtyninefourtwenty.vacationdaysreworked.databinding.FragmentTimelineBinding
import com.sixtyninefourtwenty.vacationdaysreworked.utils.navigate
import com.sixtyninefourtwenty.vacationdaysreworked.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import net.cachapa.expandablelayout.ExpandableLayout
import splitties.views.appcompat.contentDescAsTooltip

class TimelineFragment : BaseFragment<FragmentTimelineBinding>() {

    private val mainViewModel: MainViewModel by activityViewModels { MainViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTimelineBinding {
        return FragmentTimelineBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(binding: FragmentTimelineBinding, savedInstanceState: Bundle?) {
        val vacationsAdapter = VacationGroupAdapter(requireContext(), onDeleteGroupButtonClickListener = {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_vacations_in_group)
                .setMessage(getString(R.string.delete_vacations_in_group_confirmation, it.getTitle(requireContext())))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    mainViewModel.deleteVacation(it.vacations)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }, onDeleteButtonClickListener = {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_vacation)
                .setMessage(getString(R.string.delete_vacation_confirmation, it.name))
                .setPositiveButton(R.string.delete) { _, _ ->
                    mainViewModel.deleteVacation(it)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }, onEditButtonClickListener = {
            navigate(MainFragmentDirections.actionMainFragmentToEditVacationDialog(it))
        })
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = vacationsAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.allVacationsByTimeFromAscending.collect {
                    vacationsAdapter.submitList(VacationGroup.buildListOfVacationGroups(it)) {
                        binding.root.viewState = MultiStateView.ViewState.CONTENT
                    }
                }
            }
        }
    }

    private class VacationGroupAdapter(
        context: Context,
        private val onDeleteGroupButtonClickListener: (VacationGroup) -> Unit,
        private val onDeleteButtonClickListener: (Vacation) -> Unit,
        private val onEditButtonClickListener: (Vacation) -> Unit
    ) : ListAdapter<VacationGroup, VacationGroupAdapter.VacationGroupViewHolder>(
        VacationGroupDiffer(context)
    ) {
        private val recycledViewPool = RecycledViewPool()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacationGroupViewHolder {
            val binding = ListItemVacationGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VacationGroupViewHolder(binding, recycledViewPool) {
                onDeleteGroupButtonClickListener(getItem(it))
            }
        }

        override fun onBindViewHolder(holder: VacationGroupViewHolder, position: Int) {
            val context = holder.itemView.context
            val current = getItem(position)
            val vacations = current.vacations
            holder.groupTitle.text = current.getTitle(context)

            val existingLayoutManager = holder.listOfVacation.layoutManager
            if (existingLayoutManager == null) {
                holder.listOfVacation.layoutManager =
                    LinearLayoutManager(context).apply {
                        initialPrefetchItemCount = vacations.size
                    }
            } else if (existingLayoutManager is LinearLayoutManager) {
                existingLayoutManager.initialPrefetchItemCount = vacations.size
            }

            val existingAdapter = holder.listOfVacation.adapter
            if (existingAdapter == null) {
                val adapter = VacationAdapter(onDeleteButtonClickListener, onEditButtonClickListener).apply {
                    submitList(vacations)
                }
                holder.listOfVacation.adapter = adapter
            } else if (existingAdapter is VacationAdapter) {
                existingAdapter.submitList(vacations)
            }

        }

        private class VacationGroupViewHolder(
            binding: ListItemVacationGroupBinding,
            pool: RecycledViewPool,
            onDeleteGroupButtonClickListener: (Int) -> Unit
        ) : RecyclerView.ViewHolder(binding.root) {
            val groupTitle = binding.groupTitle
            val listOfVacation = binding.vacationsInGroup

            init {
                listOfVacation.setRecycledViewPool(pool)
                binding.deleteGroup.setOnClickListener {
                    onDeleteGroupButtonClickListener(bindingAdapterPosition)
                }
                binding.expandVacations.setOnExpansionUpdateListener { _, state ->
                    when (state) {
                        ExpandableLayout.State.EXPANDING -> {
                            with(binding.expandCollapseGroup) {
                                setImageResource(R.drawable.expand_less)
                                contentDescription = context.getString(R.string.collapse)
                                contentDescAsTooltip()
                            }
                        }

                        ExpandableLayout.State.COLLAPSING -> {
                            with(binding.expandCollapseGroup) {
                                setImageResource(R.drawable.expand_more)
                                contentDescription = context.getString(R.string.expand)
                                contentDescAsTooltip()
                            }
                        }
                    }
                }
                binding.expandCollapseGroup.setOnClickListener {
                    binding.expandVacations.toggle()
                }
            }
        }

        class VacationGroupDiffer(private val context: Context) :
            DiffUtil.ItemCallback<VacationGroup>() {

            override fun areItemsTheSame(oldItem: VacationGroup, newItem: VacationGroup): Boolean =
                oldItem.getTitle(context) == newItem.getTitle(context)

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: VacationGroup,
                newItem: VacationGroup
            ): Boolean =
                oldItem.vacations == newItem.vacations

        }

        private class VacationAdapter(
            private val onDeleteButtonClickListener: (Vacation) -> Unit,
            private val onEditButtonClickListener: (Vacation) -> Unit
        ) : ListAdapter<Vacation, VacationAdapter.VacationViewHolder>(VacationDiffer()) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VacationViewHolder {
                val binding = ListItemVacationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return VacationViewHolder(binding,
                    { onDeleteButtonClickListener(getItem(it)) },
                    { onEditButtonClickListener(getItem(it)) })
            }

            override fun onBindViewHolder(holder: VacationViewHolder, position: Int) {
                val context = holder.itemView.context
                val current = getItem(position)
                val dateFromString = current.formatDateFrom()
                val range = if (current.isSingleDay) {
                    dateFromString
                } else {
                    context.getString(R.string.date_display, dateFromString, current.formatDateTo())
                }
                holder.vacationRange.text = range
                holder.vacationName.text = current.name
                if (!current.isSickDay) {
                    holder.sickDayNotice.visibility = View.GONE
                }
                if (current.description.isEmpty()) {
                    holder.vacationDescription.visibility = View.GONE
                } else {
                    holder.vacationDescription.text = current.description
                }

                val numOfDaysUntilStartOrEnd = current.calculateDaysUntilStartOrEnd()
                when (current.calculateStatus()) {
                    Vacation.Status.PRESENT -> holder.daysUntil.text =
                        if (numOfDaysUntilStartOrEnd > 1) {
                            context.getString(R.string.days_until_end, numOfDaysUntilStartOrEnd)
                        } else {
                            context.getString(R.string.ends_after_today)
                        }

                    Vacation.Status.FUTURE -> holder.daysUntil.text =
                        if (numOfDaysUntilStartOrEnd > 1) {
                            context.getString(R.string.days_until_begin, numOfDaysUntilStartOrEnd)
                        } else {
                            context.getString(R.string.begins_after_today)
                        }

                    Vacation.Status.PAST -> holder.daysUntil.visibility = View.GONE
                }
            }

            class VacationViewHolder(
                binding: ListItemVacationBinding,
                onDeleteButtonClickListener: (Int) -> Unit,
                onEditButtonClickListener: (Int) -> Unit
            ) : RecyclerView.ViewHolder(binding.root) {
                val vacationRange = binding.vacationRange
                val vacationName = binding.vacationName
                val sickDayNotice = binding.sickDayNotice
                val vacationDescription = binding.vacationDescription
                val daysUntil = binding.daysUntil

                init {
                    binding.btnDelete.setOnClickListener {
                        onDeleteButtonClickListener(bindingAdapterPosition)
                    }
                    binding.btnEdit.setOnClickListener {
                        onEditButtonClickListener(bindingAdapterPosition)
                    }
                    binding.expandVacation.setOnExpansionUpdateListener { _, state ->
                        when (state) {
                            ExpandableLayout.State.EXPANDING -> {
                                with(binding.btnShowHide) {
                                    setImageResource(R.drawable.visibility_off)
                                    contentDescription = context.getString(R.string.hide)
                                    contentDescAsTooltip()
                                }
                            }

                            ExpandableLayout.State.COLLAPSING -> {
                                with(binding.btnShowHide) {
                                    setImageResource(R.drawable.visibility)
                                    contentDescription = context.getString(R.string.show)
                                    contentDescAsTooltip()
                                }
                            }
                        }
                    }
                    binding.btnShowHide.setOnClickListener {
                        binding.expandVacation.toggle()
                    }
                }
            }

            private class VacationDiffer : DiffUtil.ItemCallback<Vacation>() {
                override fun areItemsTheSame(oldItem: Vacation, newItem: Vacation): Boolean =
                    oldItem.name == newItem.name

                override fun areContentsTheSame(oldItem: Vacation, newItem: Vacation): Boolean =
                    oldItem == newItem

            }

        }
    }
}