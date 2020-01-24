package com.fastival.jetpackwithmviapp.repository.main

import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.api.main.response.BlogCreateUpdateResponse
import com.fastival.jetpackwithmviapp.extension.convertServerStringDateToLong
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.repository.JobManager
import com.fastival.jetpackwithmviapp.repository.NetworkBoundResource
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.Response
import com.fastival.jetpackwithmviapp.ui.ResponseType
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import com.fastival.jetpackwithmviapp.util.ApiSuccessResponse
import com.fastival.jetpackwithmviapp.util.GenericApiResponse
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_MEMBER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("CreateBlogRepository")
{
    private val TAG = "AppDebug"

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<CreateBlogViewState>>
    {
        return object:
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, CreateBlogViewState>(
                sessionManager.isConnectedToTheInternet(),
                isNetworkRequest = true,
                shouldCancelIfNoInternet = true,
                shouldLoadFromCache = false
            ) {

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {
                if (response.body.response != RESPONSE_MUST_BECOME_MEMBER) {

                    val resBody = response.body
                    val newBlogPost = BlogPost(
                        resBody.pk, resBody.title,
                        resBody.slug, resBody.body,
                        resBody.image, resBody.date_updated.convertServerStringDateToLong(),
                        resBody.username
                    )

                    updateLocalDb(newBlogPost)

                }

                withContext(Dispatchers.Main) {
                    onCompleteJob(DataState.data(
                        data = null,
                        response = Response(response.body.response, ResponseType.Dialog())
                    ))
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.createBlog(
                    "Token ${authToken.token!!}",
                    title, body, image
                )
            }

            // not used in this case
            override fun loadFromCache(): LiveData<CreateBlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let {
                    blogPostDao.insert(it)
                }
            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {
            }

            override fun setJob(job: Job) {
                addJob("createNewBlogPost", job)
            }
        }.asLiveData()

    }
}