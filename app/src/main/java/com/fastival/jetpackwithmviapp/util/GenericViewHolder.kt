package com.fastival.jetpackwithmviapp.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.fastival.jetpackwithmviapp.databinding.LayoutNoMoreResultsBinding
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.BaseViewHolder

class GenericViewHolder(
    binding: LayoutNoMoreResultsBinding
): BaseViewHolder<BlogPost>(binding){
    override fun bind(item: BlogPost) {
        // ignore
    }
}