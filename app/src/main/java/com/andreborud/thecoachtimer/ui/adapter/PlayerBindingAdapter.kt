package com.andreborud.thecoachtimer.ui.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import com.andreborud.thecoachtimer.App
import com.andreborud.thecoachtimer.BR
import com.andreborud.thecoachtimer.data.interfaces.ItemClickListener
import com.andreborud.thecoachtimer.data.model.Player
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PlayerBindingAdapter(private val binding: ViewDataBinding, private val itemClickListener: ItemClickListener?) : DataBindingViewHolder(binding) {

    /**
     * Bind player values to R.layout.player_item_layout
     */
    fun bind(player: Player) {
        binding.setVariable(BR.playerName, player.name.fullName)
        binding.setVariable(BR.playerImage, player.picture.thumbnail)

        // Add click listener to root view
        binding.root.setOnClickListener {
            itemClickListener?.onItemClick(player)
        }

        binding.executePendingBindings()
    }

    companion object {

        /**
         * Custom binding to load image from url straight into an imageview
         */
        @BindingAdapter("playerImageSrc")
        @JvmStatic
        fun loadPlayerImage(view: ImageView, imageUrl: String?) {
            imageUrl?.let {
                Glide.with(App.context)
                    .load(imageUrl)
                    .apply(RequestOptions().circleCrop())
                    .into(view)
            }
        }
    }
}