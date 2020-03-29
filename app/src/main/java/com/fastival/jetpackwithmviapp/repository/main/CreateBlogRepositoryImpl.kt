/*
package com.fastival.jetpackwithmviapp.repository.main

import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.api.main.response.BlogCreateUpdateResponse
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.repository.NetworkBoundResource2
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_MEMBER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@FlowPreview
class CreateBlogRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): CreateBlogRepository2
{
    override fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
        stateEvent: StateEvent
    ): Flow<DataState<CreateBlogViewState>> {
        return object: NetworkBoundResource2<BlogCreateUpdateResponse, BlogPost, CreateBlogViewState>(
            dispatcher = Dispatchers.IO,
            stateEvent = stateEvent,
            dbQuery = null,
            fetchCacheData = null,
            apiCall = {
                openApiMainService.createBlog(
                    "Token ${authToken.token!!}",
                    title,
                    body,
                    image)},
            updateCache = { networkObj ->
                if (networkObj.response != RESPONSE_MUST_BECOME_MEMBER) {
                    val updatedBlogPost = networkObj.toBlogPost()
                    blogPostDao.insert(updatedBlogPost)
                }
            },
            retApiResult = {
                 DataState.data(
                    response = Response(
                        message = it.response,
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Success
                    ),
                    data = null,
                    stateEvent = stateEvent
                )
            }){}.result
    }
}*/
