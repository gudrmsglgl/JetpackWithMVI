package com.fastival.jetpackwithmviapp.extension.fragment

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
import androidx.annotation.IdRes
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.extension.addCompositeDisposable
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_FILTER_USERNAME
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_DESC
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogListAdapter
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.*
import com.fastival.jetpackwithmviapp.util.TopSpacingItemDecoration
import com.jakewharton.rxbinding3.recyclerview.scrollStateChanges
import com.jakewharton.rxbinding3.view.scrollChangeEvents
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogFragment.setupGlide(){

    requestOptions = RequestOptions
            .placeholderOf(R.drawable.default_image)
            .error(R.drawable.default_image)

    activity?.let {
        requestManager = Glide.with(it)
            .applyDefaultRequestOptions(requestOptions)
    }
}


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogFragment.fetchBlog(query: String? = null) =
    viewModel
        .searchBlog(query)
        .run {
            resetUI()
        }


@FlowPreview
@ExperimentalCoroutinesApi
private fun BlogFragment.resetUI(){
    blog_post_recyclerview.smoothScrollToPosition(0)
    uiCommunicationListener.hideSoftKeyboard()
    focusable_view.requestFocus()
}


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogFragment.initSearchView(menu: Menu){

    val searchManager: SearchManager =
        activity?.getSystemService(Context.SEARCH_SERVICE) as SearchManager

    val searchView =
        (menu.findItem(R.id.action_search).actionView as SearchView).apply{

            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            maxWidth = Integer.MAX_VALUE
            setIconifiedByDefault(true)
            isSubmitButtonEnabled = true

        }

    // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
    val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText

    searchPlate
        .setOnEditorActionListener { textView, actionId, _ ->

            actionSearchOnKeyboard(textView, actionId)
            true

        }


    // SEARCH BUTTON CLICKED (in toolbar)
    val searchButtonOnToolbar =
        searchView.findViewById(R.id.search_go_btn) as View

    searchButtonOnToolbar
        .setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            fetchBlog(searchQuery)
        }
}


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogFragment.initRecyclerView(){

    recyclerAdapter = BlogListAdapter(
        requestManager as RequestManager
    )

    blog_post_recyclerview.apply {

        val topSpacingItemDecoration = TopSpacingItemDecoration(30)

        removeItemDecoration(topSpacingItemDecoration)

        addItemDecoration(topSpacingItemDecoration)

        adapter = recyclerAdapter

        scrollStateChanges()
            .map { Pair(this, it) }
            .subscribe {
                val layoutManager = it.first.layoutManager as LinearLayoutManager
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition == it.first.adapter?.itemCount?.minus(1)){
                    viewModel.nextPage()
                }}
            .addCompositeDisposable(disposableBag)

    }

}


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogFragment.showFilterDialog(){
    activity?.let {

        val dialog = MaterialDialog(it)
            .noAutoDismiss()
            .customView(R.layout.layout_blog_filter)

        val view = dialog.getCustomView()

        checkBeforeFilterOption(
            view = view,
            savedFilter = viewModel.getFilter()
        )

        checkBeforeOrderOption(
            view = view,
            savedOrder = viewModel.getOrder()
        )

        onDialogPositiveBtn(view, dialog)

        onDialogNegativeBtn(view, dialog)

        dialog.show()

    }
}


private fun checkBeforeFilterOption(
    view: View,
    savedFilter: String
) = with(view)
{
    findRadioGroup(R.id.filter_group).apply {
        when (savedFilter) {
            BLOG_FILTER_DATE_UPDATED -> check(R.id.filter_date)
            BLOG_FILTER_USERNAME -> check(R.id.filter_author)
        }
    }
}


private fun checkBeforeOrderOption(
    view: View,
    savedOrder: String
) = with(view)
{
    findRadioGroup(R.id.order_group).apply {
        when(savedOrder){
            BLOG_ORDER_ASC -> check(R.id.filter_asc)
            BLOG_ORDER_DESC -> check(R.id.filter_desc)
        }
    }
}


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogFragment.saveLayoutManagerState() =
    blog_post_recyclerview.layoutManager?.onSaveInstanceState()?.let { lmState ->
        viewModel.setLayoutManagerState(lmState)
    }


private fun View.findRadioGroup(@IdRes radioGroupId: Int) =
    this.findViewById<RadioGroup>(radioGroupId)


@FlowPreview
@ExperimentalCoroutinesApi
private fun BlogFragment.actionSearchOnKeyboard(
    textView: TextView,
    actionId: Int
){
    if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_SEARCH)
    {

        val searchQuery = textView.text.toString()

        fetchBlog(searchQuery)

    }
}


@FlowPreview
@ExperimentalCoroutinesApi
private fun BlogFragment.onDialogPositiveBtn(
    view: View,
    dialog: MaterialDialog
){
    view.findViewById<TextView>(R.id.positive_button).setOnClickListener {

        val newFilter =
            when (view.findRadioGroup(R.id.filter_group).checkedRadioButtonId) {
                R.id.filter_author -> BLOG_FILTER_USERNAME
                R.id.filter_date -> BLOG_FILTER_DATE_UPDATED
                else -> BLOG_FILTER_DATE_UPDATED
            }

        val newOrder =
            when (view.findRadioGroup(R.id.order_group).checkedRadioButtonId){
                R.id.filter_desc -> "-"
                else -> ""
            }

        with(viewModel){

            saveFilterOptions(newFilter, newOrder)
            setBlogFilterOrder(newFilter, newOrder)

        }

        fetchBlog()

        dialog.dismiss()
    }
}


@FlowPreview
@ExperimentalCoroutinesApi
private fun BlogFragment.onDialogNegativeBtn(
    view: View,
    dialog: MaterialDialog
){
    view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
        dialog.dismiss()
    }
}