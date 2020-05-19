package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.extension.parseRequestBody
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.fastival.jetpackwithmviapp.repository.main.BlogRepositoryImpl
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent.*
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.util.PreferenceKeys.Companion.BLOG_FILTER
import com.fastival.jetpackwithmviapp.util.PreferenceKeys.Companion.BLOG_ORDER
import com.fastival.jetpackwithmviapp.util.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class BlogViewModel
//@AssistedInject
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepositoryImpl,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
    //@Assisted private val savedStateHandle: SavedStateHandle
): BaseViewModel<BlogViewState>() {

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


    override fun setStateEvent(stateEvent: StateEvent) {
        if (!isJobAlreadyActive(stateEvent)) {
            sessionManager.getAuthToken()?.let { authToken ->

                launchJob(
                    stateEvent = stateEvent,
                    repositoryFunc = when(stateEvent){

                        is BlogSearchEvent -> {

                            if (stateEvent.clearLayoutManagerState)
                                clearLayoutManagerState()

                            /*val searchBlog = """
                                | query = ${getSearchQuery()}
                                | filterAndOder(order+filter) = ${getOrder()}${getFilter()}
                                | page = ${getPage()}
                            """.trimIndent()

                            Log.d(TAG, searchBlog)*/

                            blogRepository.searchBlogPosts(
                                authToken = authToken,
                                query = getSearchQuery(),
                                filterAndOrder = getOrder() + getFilter(),
                                page = getPage(),
                                stateEvent = stateEvent
                            )

                        }

                        is CheckAuthorOfBlogPost -> {
                            blogRepository.isAuthorOfBlogPost(
                                authToken = authToken,
                                slug = getSlug(),
                                stateEvent = stateEvent
                            )
                        }

                        is UpdateBlogPostEvent -> {
                            
                            val title = stateEvent.title.parseRequestBody()
                            
                            val body = stateEvent.body.parseRequestBody()
                            
                            blogRepository.updateBlogPost(
                                authToken = authToken,
                                slug = getSlug(),
                                title = title,
                                body = body,
                                image = stateEvent.image,
                                stateEvent = stateEvent
                            )
                            
                        }

                        is DeleteBlogPostEvent -> {
                            blogRepository.deleteBlogPost(
                                authToken = authToken,
                                blogPost = getBlogPost(),
                                stateEvent = stateEvent
                            )
                        }
                        
                        else -> {
                            flow {
                                emit(
                                    retInvalidEvent(stateEvent)
                                )
                            }
                        }

                    }
                )

            }
        }
    }


    override fun handleViewState(data: BlogViewState) {

        blogFragmentViewState(data)

        viewBlogFragmentViewState(data)

        updateBlogFragmentViewState(data)

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

    override fun initNewViewState(): BlogViewState = BlogViewState()

}