package com.andreborud.thecoachtimer.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.interfaces.ItemClickListener
import com.andreborud.thecoachtimer.data.interfaces.ListItem
import com.andreborud.thecoachtimer.data.model.FileItem
import com.andreborud.thecoachtimer.data.model.Lap
import com.andreborud.thecoachtimer.data.model.Leader
import com.andreborud.thecoachtimer.data.model.Player
import com.andreborud.thecoachtimer.databinding.FileItemLayoutBinding
import com.andreborud.thecoachtimer.databinding.LapItemLayoutBinding
import com.andreborud.thecoachtimer.databinding.LeaderboardItemLayoutBinding
import com.andreborud.thecoachtimer.databinding.PlayerItemLayoutBinding
import com.andreborud.thecoachtimer.ui.viewmodel.LeaderboardViewModel

/**
 * Created by andreborud on 15/07/16.
 *
 *
 * Uses RecyclerView for a more modern and memory effective way do load the list
 */
class RecyclerAdapter(private val itemClickListener: ItemClickListener?): ListAdapter<ListItem, DataBindingViewHolder>(object : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem.itemType == newItem.itemType
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
        return oldItem == newItem
    }
}) {

    var sortedBy: LeaderboardViewModel.Performance = LeaderboardViewModel.Performance.Explosiveness

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder =
        when (viewType) {
            R.layout.player_item_layout -> PlayerBindingAdapter(PlayerItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false), itemClickListener)
            R.layout.lap_item_layout -> LapBindingAdapter(LapItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            R.layout.leaderboard_item_layout -> LeaderBindingAdapter(LeaderboardItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            R.layout.file_item_layout -> FileBindingAdapter(FileItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("no supported item id")
        }

    override fun onBindViewHolder(holder: DataBindingViewHolder, position: Int) {
        when (holder) {
            is PlayerBindingAdapter -> holder.bind(getItem(position) as Player)
            is LapBindingAdapter -> holder.bind(getItem(position) as Lap)
            is LeaderBindingAdapter -> holder.bind(getItem(position) as Leader, sortedBy)
            is FileBindingAdapter -> holder.bind(getItem(position) as FileItem)
            else -> throw IllegalArgumentException("no supported item id")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    override fun submitList(list: List<ListItem>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}
