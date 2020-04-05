package com.fastival.jetpackwithmviapp.repository.main

import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.DataState
import com.fastival.jetpackwithmviapp.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

@FlowPreview
interface CreateBlogRepository {

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
        stateEvent: StateEvent
    ): Flow<DataState<CreateBlogViewState>>
}