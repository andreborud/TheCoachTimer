package com.andreborud.thecoachtimer.ui.adapter

import androidx.databinding.ViewDataBinding
import com.andreborud.thecoachtimer.BR
import com.andreborud.thecoachtimer.data.model.Lap

class LapBindingAdapter(private val binding: ViewDataBinding) : DataBindingViewHolder(binding) {

    /**
     * Bind lap values to R.layout.lap_item_layout
     */
    fun bind(lap: Lap) {
        binding.setVariable(BR.lapName, "${lap.id}")
        binding.setVariable(BR.lapTime, lap.readableTime)
        binding.executePendingBindings()
    }
}