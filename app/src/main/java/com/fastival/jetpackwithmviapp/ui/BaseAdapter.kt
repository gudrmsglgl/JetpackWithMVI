package com.fastival.jetpackwithmviapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.*


abstract class BaseAdapter<ITEM>(
    diffCallback: DiffUtil.ItemCallback<ITEM>
): RecyclerView.Adapter<BaseViewHolder<ITEM>>() {

    val differ = AsyncListDiffer(
        BlogRecyclerChangeCallback(),
        AsyncDifferConfig.Builder(diffCallback).build()
    )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: BaseViewHolder<ITEM>, position: Int) {
        holder.bind(differ.currentList[position])
    }

    inner class BlogRecyclerChangeCallback: ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            this@BaseAdapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            this@BaseAdapter.notifyDataSetChanged()
        }

        override fun onInserted(position: Int, count: Int) {
            this@BaseAdapter.notifyItemRangeChanged(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            this@BaseAdapter.notifyDataSetChanged()
        }
    }

    fun <vb: ViewDataBinding> generateBinding(
        parent: ViewGroup,
        @LayoutRes layoutRes: Int
    ): vb = DataBindingUtil.inflate(
        LayoutInflater.from(parent.context),
        layoutRes,
        parent,
        false
    )

}