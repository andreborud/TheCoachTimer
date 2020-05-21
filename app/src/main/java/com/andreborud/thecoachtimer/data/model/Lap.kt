package com.andreborud.thecoachtimer.data.model

import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.interfaces.ListItem

data class Lap(val id: Int, val time: Long, val readableTime: String, val timestamp: Long): ListItem {

    // Layout to be used in the RecyclerAdapter
    override val itemType: Int
        get() = R.layout.lap_item_layout
}