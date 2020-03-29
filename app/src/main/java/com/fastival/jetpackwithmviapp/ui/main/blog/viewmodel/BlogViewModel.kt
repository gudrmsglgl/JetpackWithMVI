package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import com.fastival.jetpackwithmviapp.di.SavedStateViewModelFactory
import com.fastival.jetpackwithmviapp.extension.parseRequestBody
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
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
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import javax.inject.Inject

class BlogViewModel
//@AssistedInject
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
    //@Assisted private val savedStateHandle: SavedStateHandle
): BaseViewModel<BlogStateEvent, BlogViewState>() {

    /*@AssistedInject.Factory
    interface Factory: SavedStateViewModelFactory<BlogViewModel>*/

    override val viewState: LiveData<BlogViewState>
        //get() = savedStateHandle.getLiveData("BlogViewModel")
        get() = super.viewState

    init {
        // viewState init filter & order
        setBlogFilterOrder(
            sharedPreferences.getString(BLOG_FILTER, BLOG_FILTER_DATE_UPDATED),
            sharedPreferences.getString(BLOG_ORDER, BLOG_ORDER_ASC)
        )
    }


    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when(stateEvent) {

            is BlogStateEvent.RestoreBlogListFromCache -> {
                blogRepository.restoreBlogListFromCache(
                    getSearchQuery(),
                    getOrder() + getFilter(),
                    getPage()
                )
            }

            is BlogStateEvent.BlogSearchEvent -> {
                clearLayoutManagerState()
                sessionManager.cachedToken.value?.let {authToken ->
                    blogRepository.searchBlogPosts(
                        authToken,
                        getSearchQuery(),
                        getOrder() + getFilter(),
                        getPage())
                }?: AbsentLiveData.create()
            }

            is BlogStateEvent.CheckAuthorOfBlogPost -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.isAuthorOfBlogPost(
                        authToken = authToken,
                        slug = getSlug()
                    )
                }?: AbsentLiveData.create()
            }

            is BlogStateEvent.DeleteBlogPostEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.deleteBlogPost(
                        authToken,
                        getBlogPost())
                }?: AbsentLiveData.create()
            }

            is BlogStateEvent.UpdateBlogPostEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->

                    val title = stateEvent.title.parseRequestBody()

                    val body = stateEvent.body.parseRequestBody()

                    blogRepository.updateBlogPost(
                        authToken = authToken,
                        slug = getSlug(),
                        title = title,
                        body = body,
                        image = stateEvent.image
                    )
                }?: AbsentLiveData.create()
            }

            is BlogStateEvent.None -> {
                return liveData {
                    emit(
                        DataState(
                        error = null,
                        loading = Loading(false),
                        data = null)
                    )
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

    /*override fun setViewState(viewState: BlogViewState) {
        savedStateHandle.set("BlogViewModel", viewState)
    }*/

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