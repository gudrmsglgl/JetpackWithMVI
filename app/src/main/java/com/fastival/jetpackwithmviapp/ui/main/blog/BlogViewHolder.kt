package com.fastival.jetpackwithmviapp.ui.main.blog

import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.databinding.LayoutBlogListItemBinding
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.BaseViewHolder

class BlogViewHolder(
    val binding: LayoutBlogListItemBinding,
    val requestManager: RequestManager
): BaseViewHolder<BlogPost>(binding)
{

    override fun bind(item: BlogPost) {
        binding.item = item
        binding.requestManager = requestManager
    }

}