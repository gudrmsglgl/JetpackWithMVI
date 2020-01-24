package com.fastival.jetpackwithmviapp.extension

import android.app.SearchManager
import android.content.Context
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.fastival.jetpackwithmviapp.BF
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_FILTER_USERNAME
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogListAdapter
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import com.fastival.jetpackwithmviapp.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*



fun BF.onBlogSearchOrFilter(){
    viewModel.loadFirstPage().run {
        resetUI()
    }
}

private fun BF.resetUI(){
    blog_post_recyclerview.smoothScrollToPosition(0)
    stateListener.hideSoftKeyboard()
    focusable_view.requestFocus()
}


fun BF.initRecyclerView(){
    val context = this.context

    blog_post_recyclerview.also { rcv ->

        rcv.layoutManager = LinearLayoutManager(context)
        val topSpacingDecorator = TopSpacingItemDecoration(30)
        rcv.removeItemDecoration(topSpacingDecorator)
        rcv.addItemDecoration(topSpacingDecorator)

        recyclerAdapter = BlogListAdapter(requestManager, this)
        rcv.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()

                if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                    Log.d(TAG, "BlogFragment: attempting to load next page...")
                    viewModel.nextPage()
                }
            }
        })

        rcv.adapter = recyclerAdapter
    }

}

fun BF.initSearchView(menu: Menu){
    val searchManager: SearchManager =
        activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

    val searchView = (menu.findItem(R.id.action_search).actionView as SearchView).apply {
        setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        maxWidth = Integer.MAX_VALUE
        setIconifiedByDefault(true)
        isSubmitButtonEnabled = true
    }

    // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
    val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
    searchPlate.setOnEditorActionListener { textView, actionId, keyEvent ->

        if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
            || actionId == EditorInfo.IME_ACTION_SEARCH) {

            val searchQuery = textView.text.toString()
            Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: $searchQuery")
            viewModel.setQuery(searchQuery).run {
                onBlogSearchOrFilter()
            }
        }
        true
    }

    // SEARCH BUTTON CLICKED (in toolbar)
    val searchButton = searchView.findViewById(R.id.search_go_btn) as View
    searchButton.setOnClickListener {
        val searchQuery = searchPlate.text.toString()
        Log.e(TAG, "SearchView: (button) executing search...: $searchQuery")
        viewModel.setQuery(searchQuery).run {
            onBlogSearchOrFilter()
        }
    }
}


fun BF.showFilterDialog(){

    activity?.let {

        val dialog = MaterialDialog(it)
            .noAutoDismiss()
            .customView(R.layout.layout_blog_filter)

        val view = dialog.getCustomView()

        val filter = viewModel.getFilter()
        val order = viewModel.getOrder()

        if (filter.equals(BLOG_FILTER_DATE_UPDATED)) {
            view.findViewById<RadioGroup>(R.id.filter_group).check(R.id.filter_date)
        }
        else {
            view.findViewById<RadioGroup>(R.id.filter_group).check(R.id.filter_author)
        }

        if (order.equals(BLOG_ORDER_ASC)) {
            view.findViewById<RadioGroup>(R.id.order_group).check(R.id.filter_asc)
        }
        else {
            view.findViewById<RadioGroup>(R.id.order_group).check(R.id.filter_desc)
        }

        view.findViewById<TextView>(R.id.positive_button).setOnClickListener {
            Log.d(TAG, "FilterDialog: apply filter.")

            val selectedFilter = dialog.getCustomView().findViewById<RadioButton>(
                dialog.getCustomView().findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId
            )

            val selectedOrder = dialog.getCustomView().findViewById<RadioButton>(
                dialog.getCustomView().findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId
            )

            var filter = BLOG_FILTER_DATE_UPDATED
            if (selectedFilter.text.toString().equals(getString(R.string.filter_author))) {
                filter = BLOG_FILTER_USERNAME
            }

            var order = ""
            if (selectedOrder.text.toString().equals(getString(R.string.filter_desc))) {
                order = "-"
            }

            viewModel.saveFilterOptions(filter,order).let {
                with(viewModel) {
                    setBlogFilter(filter)
                    setBlogOrder(order)
                }
                this.onBlogSearchOrFilter()
            }

            dialog.dismiss()
        }

        view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
            Log.d(TAG, "FilterDialog: cancelling filter.")
            dialog.dismiss()
        }

        dialog.show()
    }

}

