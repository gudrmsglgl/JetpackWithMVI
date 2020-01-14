package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_DESC
import com.fastival.jetpackwithmviapp.repository.main.BlogRepository
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.Loading
import com.fastival.jetpackwithmviapp.ui.base.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import com.fastival.jetpackwithmviapp.util.PreferenceKeys.Companion.BLOG_FILTER
import com.fastival.jetpackwithmviapp.util.PreferenceKeys.Companion.BLOG_ORDER
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
): BaseViewModel<BlogStateEvent, BlogViewState>() {

    init {
        // viewState init filter & order
        setBlogFilter(
            sharedPreferences.getString(BLOG_FILTER, BLOG_FILTER_DATE_UPDATED)
        )

        setBlogOrder(
            sharedPreferences.getString(BLOG_ORDER, BLOG_ORDER_ASC)
        )
    }


    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when(stateEvent) {

            is BlogStateEvent.BlogSearchEvent -> {
                sessionManager.cachedToken.value?.let {authToken ->
                    blogRepository.searchBlogPosts(
                        authToken,
                        getSearchQuery(),
                        getOrder() + getFilter(),
                        getPage())
                }?: AbsentLiveData.create()
            }

            is BlogStateEvent.CheckAuthorOfBlogPost -> {
                AbsentLiveData.create()
            }

            is BlogStateEvent.None -> {
                return object : LiveData<DataState<BlogViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    fun saveFilterOptions(filter: String, order: String) = with(editor)
    {
        putString(BLOG_FILTER, filter)
        apply()

        putString(BLOG_ORDER, order)
        apply()
    }



    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }


    override fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData(){
        setStateEvent(BlogStateEvent.None())
    }
}