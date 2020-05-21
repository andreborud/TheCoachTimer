package com.andreborud.thecoachtimer.data.model

import android.os.Parcelable
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.interfaces.ListItem
import kotlinx.android.parcel.Parcelize

data class Leader(val player: Player, val explosiveness: Double, val endurance: Int): ListItem {

    // Layout to be used in the RecyclerAdapter
    override val itemType: Int
        get() = R.layout.leaderboard_item_layout
}
