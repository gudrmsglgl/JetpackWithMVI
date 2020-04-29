package com.fastival.jetpackwithmviapp.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<ITEM>(
    binding: ViewBinding
): RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(item: ITEM)
}