package com.andreborud.thecoachtimer.data.model

import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.interfaces.ListItem
import java.io.File

data class FileItem(var file: File): ListItem {

    // Layout to be used in the RecyclerAdapter
    override val itemType: Int
        get() = R.layout.file_item_layout
}