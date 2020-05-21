package com.andreborud.thecoachtimer.ui.adapter

import androidx.databinding.ViewDataBinding
import com.andreborud.thecoachtimer.BR
import com.andreborud.thecoachtimer.data.model.FileItem

class FileBindingAdapter(private val binding: ViewDataBinding) : DataBindingViewHolder(binding) {

    /**
     * Bind fileItem values to R.layout.file_item_layout
     */
    fun bind(fileItem: FileItem) {
        binding.setVariable(BR.fileName, fileItem.file.name)
        binding.executePendingBindings()
    }
}