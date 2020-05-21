package com.andreborud.thecoachtimer.ui.adapter

import android.graphics.Typeface
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.andreborud.thecoachtimer.App
import com.andreborud.thecoachtimer.BR
import com.andreborud.thecoachtimer.data.model.Leader
import com.andreborud.thecoachtimer.ui.viewmodel.LeaderboardViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class LeaderBindingAdapter(private val binding: ViewDataBinding) : DataBindingViewHolder(binding) {

    /**
     * Bind leader values to R.layout.leaderboard_item_layout
     */
    fun bind(leader: Leader, performance: LeaderboardViewModel.Performance /* send the current sorting to make values of current sorting bold*/) {
        binding.setVariable(BR.leaderFullName, leader.player.name.fullName)
        binding.setVariable(BR.leaderImage, leader.player.picture.thumbnail)
        binding.setVariable(BR.leaderExplosive, leader.explosiveness.toString())
        binding.setVariable(BR.leaderEndurance, leader.endurance.toString())
        binding.setVariable(BR.sortByExplosiveness, performance == LeaderboardViewModel.Performance.Explosiveness)
        binding.setVariable(BR.sortByEndurance, performance == LeaderboardViewModel.Performance.Endurance)
        binding.executePendingBindings()
    }

    companion object {

        /**
         * Custom binding to load image from url straight into an imageview
         */
        @BindingAdapter("leaderImageSrc")
        @JvmStatic
        fun loadLeaderImage(view: ImageView, imageUrl: String?) {
            imageUrl?.let {
                Glide.with(App.context)
                    .load(imageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(view)
            }
        }

        /**
         * Custom binding to set font style of textView
         */
        @BindingAdapter("isBold")
        @JvmStatic
        fun setBold(view: TextView, isBold: Boolean) {
            view.setTypeface(null, if (isBold) Typeface.BOLD else Typeface.NORMAL)
        }
    }
}