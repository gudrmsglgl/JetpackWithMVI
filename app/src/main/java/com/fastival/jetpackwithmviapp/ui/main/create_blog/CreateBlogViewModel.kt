package com.fastival.jetpackwithmviapp.ui.main.create_blog

import CreateBlogRepositoryImpl
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.extension.parseRequestBody
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class CreateBlogViewModel
@Inject
constructor(
    val repository: CreateBlogRepositoryImpl,
    val sessionManager: SessionManager
) : BaseViewModel<CreateBlogViewState>() {

    override val viewState: LiveData<CreateBlogViewState>
        get() = super.viewState


    override fun setStateEvent(stateEvent: StateEvent) =
        sessionManager.cachedToken.value?.let { authToken ->

            launchJob(
                stateEvent = stateEvent,
                jobFunc = when(stateEvent) {

                    is CreateBlogStateEvent.CreateNewBlogEvent -> {

                        val title = stateEvent.title.parseRequestBody()

                        val body = stateEvent.body.parseRequestBody()

                        repository.createNewBlogPost(
                            authToken = authToken,
                            title = title,
                            body = body,
                            image = stateEvent.image,
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

        }?: sessionManager.logout()

    override fun handleViewState(data: CreateBlogViewState) {
        // not in
        Log.d(TAG, "CreateBlogViewModel_handleViewState")
    }

    override fun initNewViewState() = CreateBlogViewState()


    fun clearNewBlogFields(){
        val update = getCurrentViewStateOrNew().apply {
            blogFields = CreateBlogViewState.NewBlogFields()
        }
        setViewState(update)
    }


    fun setNewBlogFields(title: String?, body: String?, uri: Uri?){
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.blogFields
        title?.let { newBlogFields.newBlogTitle = it }
        body?.let { newBlogFields.newBlogBody = it }
        uri?.let { newBlogFields.newImageUri = it }
        update.blogFields = newBlogFields
        setViewState(update)
    }

}