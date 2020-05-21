package com.andreborud.thecoachtimer.data.model

import android.os.Parcelable
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.interfaces.ListItem
import kotlinx.android.parcel.Parcelize

// Player parcelable to be able to sent the player from the MainActivity to the SessionActivity
@Parcelize
data class Player(var name: Name, var picture: Picture): ListItem, Parcelable {
    override val itemType: Int
        get() = R.layout.player_item_layout
}

@Parcelize
data class Name(val title: String, val first: String, val last: String): Parcelable {
    val fullName: String
        get() = "$title $first $last"
}

@Parcelize
data class Picture(val large: String, val medium: String, val thumbnail: String): Parcelable
