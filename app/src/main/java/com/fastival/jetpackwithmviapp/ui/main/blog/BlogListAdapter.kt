package com.fastival.jetpackwithmviapp.ui.main.blog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.*
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.LayoutBlogListItemBinding
import com.fastival.jetpackwithmviapp.extension.convertLongToStringDate
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.util.GenericViewHolder
import kotlinx.android.synthetic.main.layout_blog_list_item.view.*

class BlogListAdapter(
    private val requestManager: RequestManager,
    private val interaction: Interaction? = null
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    val DIFF_CALLBACK = object: DiffUtil.ItemCallback<BlogPost>(){
        override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem.pk == newItem.pk
        }

        override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =
        AsyncListDiffer(
            BlogRecyclerChangeCallback(this),
            AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
        )

    fun submitList(
        blogList: List<BlogPost>?,
        isQueryExhausted: Boolean
    ){
      val newList = blogList?.toMutableList()
      if (isQueryExhausted) newList?.add((NO_MORE_RESULTS_BLOG_MARKER))
      differ.submitList(newList)
    }

    internal inner class BlogRecyclerChangeCallback(
        private val adapter: BlogListAdapter
    ): ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (differ.currentList[position].pk > -1){
            return BLOG_ITEM
        }
        return differ.currentList[position].pk
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            NO_MORE_RESULTS -> {
                Log.e(TAG, "onCreateViewHolder: No More results...")
                return GenericViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.layout_no_more_results,
                        parent,
                        false
                    )
                )
            }

            BLOG_ITEM -> {
                return BlogViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.layout_blog_list_item,
                        parent,
                        false
                    ),
                    requestManager,
                    interaction
                )
            }

            else -> {
                return BlogViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.layout_blog_list_item,
                        parent,
                        false
                    ),
                    requestManager,
                    interaction
                )
            }
        }
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is BlogViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }


    class BlogViewHolder(
        val binding: LayoutBlogListItemBinding,
        val requestManager: RequestManager,
        private val interaction: Interaction?
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(item: BlogPost) {
            binding.item = item
            binding.requestManager = requestManager
            binding.blogContainer.setOnClickListener{
                interaction?.onItemSelected(adapterPosition, item)
            }
        }

        companion object{
            @JvmStatic
            @BindingAdapter(value = ["url", "requestManager"])
            fun bindingImage(view: ImageView, url: String, requestManager: RequestManager) {
                requestManager
                    .load(url)
                    .transition(withCrossFade())
                    .into(view)
            }

        }


    }

    interface Interaction{
        fun onItemSelected(position: Int, item: BlogPost)
    }
}