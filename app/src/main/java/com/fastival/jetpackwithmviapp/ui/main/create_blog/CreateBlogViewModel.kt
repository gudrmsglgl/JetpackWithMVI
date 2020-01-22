package com.fastival.jetpackwithmviapp.ui.main.create_blog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fastival.jetpackwithmviapp.repository.main.CreateBlogRepository
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.Loading
import com.fastival.jetpackwithmviapp.ui.base.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val repository: CreateBlogRepository
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {

    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        return when(stateEvent){
            is CreateBlogStateEvent.CreateNewBlogEvent -> {
                AbsentLiveData.create()
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

    fun setNewBlogFields(title: String?, body: String?, uri: Uri?) {
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.blogFields
        title?.let { newBlogFields.newBlogTitle = it }
        body?.let { newBlogFields.newBlogBody = it }
        uri?.let { newBlogFields.newImageUri = it }
        update.blogFields = newBlogFields
        setViewState(update)
    }

    fun clearnewBlogFields(){
        val update = getCurrentViewStateOrNew()
        update.blogFields = CreateBlogViewState.NewBlogFields()
        setViewState(update)
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