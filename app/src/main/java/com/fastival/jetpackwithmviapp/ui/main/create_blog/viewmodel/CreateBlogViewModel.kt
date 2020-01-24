package com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fastival.jetpackwithmviapp.extension.parseRequestBody
import com.fastival.jetpackwithmviapp.repository.main.CreateBlogRepository
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.Loading
import com.fastival.jetpackwithmviapp.ui.base.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val repository: CreateBlogRepository
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {

    override val viewState: LiveData<CreateBlogViewState>
        get() = super.viewState

    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        return when(stateEvent){
            is CreateBlogStateEvent.CreateNewBlogEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->

                    val title: RequestBody = stateEvent.title.parseRequestBody()

                    val body: RequestBody = stateEvent.body.parseRequestBody()

                    repository.createNewBlogPost(
                        authToken,
                        title,
                        body,
                        stateEvent.image
                    )
                }?: AbsentLiveData.create()
            }

            is CreateBlogStateEvent.None -> {
                liveData {
                    emit(
                        DataState(
                            data = null,
                            loading = Loading(false),
                            error = null
                        )
                    )
                }
            }
        }

    }

    override fun initNewViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    override fun cancelActiveJobs() {
        repository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData(){
        setStateEvent(CreateBlogStateEvent.None())
    }
}