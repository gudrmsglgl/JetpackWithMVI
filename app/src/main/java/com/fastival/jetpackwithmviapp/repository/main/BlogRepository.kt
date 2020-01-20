package com.fastival.jetpackwithmviapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.fastival.jetpackwithmviapp.api.GenericResponse
import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.api.main.response.BlogListSearchResponse
import com.fastival.jetpackwithmviapp.extension.convertServerStringDateToLong
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.persistence.returnOrderedBlogQuery
import com.fastival.jetpackwithmviapp.repository.JobManager
import com.fastival.jetpackwithmviapp.repository.NetworkBoundResource
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.Data
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.Response
import com.fastival.jetpackwithmviapp.ui.ResponseType
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import com.fastival.jetpackwithmviapp.util.ApiSuccessResponse
import com.fastival.jetpackwithmviapp.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.fastival.jetpackwithmviapp.util.GenericApiResponse
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("BlogRepository")
{

    private val TAG: String = "AppDebug"

    fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true)
        {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogListSearchResponse>) {

                val blogPostList: ArrayList<BlogPost> = ArrayList()
                for (blogPostResponse in response.body.results) {
                    blogPostList.add(
                        BlogPost(
                        pk = blogPostResponse.pk,
                        title = blogPostResponse.title,
                        slug = blogPostResponse.slug,
                        body = blogPostResponse.body,
                        image = blogPostResponse.image,
                        date_updated = blogPostResponse.date_updated.convertServerStringDateToLong(),
                        username = blogPostResponse.username
                        )
                    )
                }

                updateLocalDb(blogPostList)

                createCacheRequestAndReturn()

            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openApiMainService.searchListBlogPosts(
                    "Token ${authToken.token!!}",
                    query,
                    filterAndOrder,
                    page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.returnOrderedBlogQuery(query, filterAndOrder, page)
                    .switchMap { list ->
                        object : LiveData<BlogViewState>(){
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    BlogViewState.BlogFields(
                                        blogList = list,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                // loop through list and update the local db
                if (cacheObject != null) {
                    withContext(Dispatchers.IO) {
                        for (blogPost in cacheObject) {
                            try {
                                // Launch each insert as a separate job to be executed in parallel
                                val j = launch {
                                    Log.d(TAG, "updateLocalDb: inserting blog: $blogPost")
                                    blogPostDao.insert(blogPost)
                                }
                                j.join() // wait for completion before proceeding to next
                            }catch (e: Exception) {
                                Log.e(TAG, "updateLocalDb: error updating cache data on blog post with slug: ${blogPost.slug}. " +
                                        "${e.message}")
                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                // Since there could be many blog posts being inserted/updated.
                            }
                        }
                    }
                }
                else {
                    Log.d(TAG, "updateLocalDb: blog post list is null")
                }

            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Dispatchers.Main) {

                    // finishing by viewing db cache
                    result.addSource(loadFromCache()){ viewState ->

                        viewState.blogFields.isQueryInProgress = false
                        if (page * PAGINATION_PAGE_SIZE > viewState.blogFields.blogList.size) {
                            viewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(DataState.data(viewState, null))

                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }
        }.asLiveData()

    }

    fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<GenericResponse, Any, BlogViewState>
            (sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                withContext(Dispatchers.Main) {

                    Log.d(TAG, "handleApiSuccessResponse: ${response.body.response}")
                    if (response.body.response == RESPONSE_NO_PERMISSION_TO_EDIT) {
                        onCompleteJob(DataState.data(
                            data = BlogViewState(
                                viewBlogFields = BlogViewState.ViewBlogFields(
                                    isAuthorOfBlogPost = false
                                )
                            ),
                            response = null
                        ))
                    }
                    else if (response.body.response == RESPONSE_HAS_PERMISSION_TO_EDIT) {
                        onCompleteJob(DataState.data(
                            data = BlogViewState(
                                viewBlogFields = BlogViewState.ViewBlogFields(
                                    isAuthorOfBlogPost = true
                                )
                            ),
                            response = null
                        ))
                    }
                    else {
                        onErrorReturn(ERROR_UNKNOWN, false, false)
                    }
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.isAuthorOfBlogPost(
                    "Token ${authToken.token!!}",
                    slug
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {

            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            override fun setJob(job: Job) {
                addJob("isAuthorOfBlogPost",job)
            }
        }.asLiveData()
    }

    fun deleteBlogPost(
        authToken: AuthToken,
        blogPost: BlogPost
    ): LiveData<DataState<BlogViewState>> {
        return object: NetworkBoundResource<GenericResponse, BlogPost, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<GenericResponse>) {
                if (response.body.response == SUCCESS_BLOG_DELETED) {
                    updateLocalDb(blogPost)
                }
                else {
                    onCompleteJob(
                        DataState.error(
                            Response(
                                message = ERROR_UNKNOWN,
                                responseType = ResponseType.Dialog()
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.deleteBlogPost(
                    "Token ${authToken.token!!}",
                    blogPost.slug
                )
            }

            // not used in this case
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
               cacheObject?.let { blogPost ->  
                   blogPostDao.deleteBlogPost(blogPost)
                   onCompleteJob(DataState.data(
                       null,
                       Response(SUCCESS_BLOG_DELETED, ResponseType.Toast())
                   ))
               }
            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            override fun setJob(job: Job) {
                addJob("deleteBlogPost",job)
            }
        }.asLiveData()
    }
}