package com.fastival.jetpackwithmviapp.ui.main.blog

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.BaseAdapter
import com.fastival.jetpackwithmviapp.ui.BaseViewHolder
import com.fastival.jetpackwithmviapp.util.GenericViewHolder
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.subjects.PublishSubject

class BlogListAdapter(
    private val requestManager: RequestManager,
    private val diffCallback: DiffUtil.ItemCallback<BlogPost> = object: DiffUtil.ItemCallback<BlogPost>(){
        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }
    }
): BaseAdapter<BlogPost>(diffCallback)
{

    private val TAG: String = "AppDebug"

    private val NO_MORE_RESULTS = -1
    private val BLOG_ITEM = 0
    private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(
        NO_MORE_RESULTS,
        "",
        "",
        "",
        "",
        0,
        ""
    )

    private val _restoreListPosSubject: PublishSubject<Unit> = PublishSubject.create()
    val restoreListPosSubject
        get() = _restoreListPosSubject

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position].pk > -1){
            return BLOG_ITEM
        }
        return differ.currentList[position].pk
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<BlogPost> = when (viewType){

        NO_MORE_RESULTS -> {
            GenericViewHolder(
                binding = generateBinding(parent, R.layout.layout_no_more_results)
            )
        }

        else -> {
            BlogViewHolder(
                binding = generateBinding(parent, R.layout.layout_blog_list_item),
                requestManager = requestManager
            )
        }
    }

    fun submitList(
        blogList: List<BlogPost>?,
        isQueryExhausted: Boolean
    ){
        val newList = blogList?.toMutableList()

        if (isQueryExhausted)
            newList?.add((NO_MORE_RESULTS_BLOG_MARKER))

        val commitCallback = Runnable {
            _restoreListPosSubject.onNext(Unit)
        }

        differ.submitList(newList, commitCallback)
    }

    // Prepare the image that will be displayed in the RecyclerView.
    // This also ensures if the network connection is lost, they will be in the cache
    fun preloadGlideImages(
        requestManager: RequestManager,
        list: List<BlogPost>
    ){
        for (blogPost in list) {
            requestManager
                .load(blogPost.image)
                .preload()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _restoreListPosSubject.onComplete()
    }

}