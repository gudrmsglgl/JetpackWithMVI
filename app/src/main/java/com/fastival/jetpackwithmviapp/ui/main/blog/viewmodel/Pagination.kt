package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.util.Log
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.INVALID_PAGE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.refreshFromCache(){

    if (!isJobAlreadyActive(BlogStateEvent.BlogSearchEvent())){

        setQueryExhausted(false)
        setStateEvent(
            BlogStateEvent.BlogSearchEvent(
                clearLayoutManagerState = false
            )
        )

    }

}


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.searchBlog(query: String? = null) {
    if (!isJobAlreadyActive(BlogStateEvent.BlogSearchEvent())) {

        getCurrentViewStateOrNew()
            .apply {

                blogFields.apply {
                    query?.let { searchQuery = it }
                    page = 1
                    isQueryExhausted = false
                }

            }.run {
                setViewState(this)
                setStateEvent(BlogStateEvent.BlogSearchEvent())
            }
    }
}




@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.nextPage(){

    if ( !isJobAlreadyActive(BlogStateEvent.BlogSearchEvent()) &&
         !getIsQueryExhausted())
    {
        Log.d(TAG, "BlogViewModel: Attempting to load next page...")

        getCurrentViewStateOrNew()
            .apply {

                val currentPage = this.copy().blogFields.page ?: 1

                blogFields.apply {
                    page = currentPage.plus(1)
                }

            }.run {
                setViewState(this)
                setStateEvent(BlogStateEvent.BlogSearchEvent())
            }
    }
}


fun isPaginationDone(errorResponse: String?): Boolean{
    // if error response = '{"detail":"Invalid page."}' then pagination is finished
    return errorResponse?.contains(INVALID_PAGE)?: false
}


@FlowPreview
@ExperimentalCoroutinesApi
fun paginationDone(viewModel: BlogViewModel) = with(viewModel)
{
    setQueryExhausted(true)
    removeStateMessage()
}