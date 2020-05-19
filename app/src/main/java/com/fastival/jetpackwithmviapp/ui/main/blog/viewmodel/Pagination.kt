package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.util.Log
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.NOT_FOUND
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
    return errorResponse?.contains(NOT_FOUND)?: false
}


@FlowPreview
@ExperimentalCoroutinesApi
fun paginationDone(viewModel: BlogViewModel) = with(viewModel)
{
    setQueryExhausted(true)
    removeStateMessage()
}