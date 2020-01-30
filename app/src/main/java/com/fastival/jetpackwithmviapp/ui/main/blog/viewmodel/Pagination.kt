package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.util.Log
import com.fastival.jetpackwithmviapp.bvm
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState

fun bvm.refreshFromCache(){
    val update = getCurrentViewStateOrNew().apply {
        blogFields.apply {
            isQueryInProgress = true
            isQueryExhausted = false
        }
    }
    setViewState(update)
    setStateEvent(BlogStateEvent.RestoreBlogListFromCache())
}

fun bvm.loadFirstPage() {
    val update = getCurrentViewStateOrNew().apply {
        blogFields.apply {
            isQueryInProgress = true
            isQueryExhausted = false
            page = 1
        }
    }
    setViewState(update)
    setStateEvent(BlogStateEvent.BlogSearchEvent())
    Log.e(TAG, "BlogViewModel: loadFirstPage: ${getSearchQuery()}")
}

fun bvm.nextPage(){
    if (!getIsQueryInProgress() && !getIsQueryExhausted()) {
        Log.d(TAG, "BlogViewModel: Attempting to load next page...")

        val update = getCurrentViewStateOrNew().apply {
            val currentPage = this.copy().blogFields.page
            blogFields.apply {
                page = currentPage + 1
                isQueryInProgress = true
            }
        }

        setViewState(update)
        setStateEvent(BlogStateEvent.BlogSearchEvent())
    }
}

// handled ViewState on BlogRepository
fun bvm.handleIncomingBlogListData(handledVS: BlogViewState) {
    Log.d(TAG, "BlogViewModel, DataState: $handledVS")
    Log.d(TAG, "BlogViewModel, DataState: isQueryInProgress?: " +
            "${handledVS.blogFields.isQueryInProgress}")
    Log.d(TAG, "BlogViewModel, DataState: isQueryExhausted?: " +
            "${handledVS.blogFields.isQueryExhausted}")

    val update = getCurrentViewStateOrNew().apply {
        blogFields.apply {
            isQueryInProgress = handledVS.blogFields.isQueryInProgress
            isQueryExhausted = handledVS.blogFields.isQueryExhausted
            blogList = handledVS.blogFields.blogList
        }
    }

    setViewState(update)
}


