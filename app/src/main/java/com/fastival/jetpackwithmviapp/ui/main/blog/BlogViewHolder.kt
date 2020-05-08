package com.fastival.jetpackwithmviapp.ui.main.blog

import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.databinding.LayoutBlogListItemBinding
import com.fastival.jetpackwithmviapp.extension.singleClick
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.BaseViewHolder
import io.reactivex.subjects.PublishSubject

class BlogViewHolder(
    val binding: LayoutBlogListItemBinding,
    val requestManager: RequestManager
): BaseViewHolder<BlogPost>(binding)
{

    override fun bind(item: BlogPost, clickSubject: PublishSubject<BlogPost>) {

        binding.root
            .singleClick()
            .map { item }
            .subscribe(clickSubject)

        binding.item = item
        binding.requestManager = requestManager

    }

}