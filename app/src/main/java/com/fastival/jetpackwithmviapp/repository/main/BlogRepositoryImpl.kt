package com.fastival.jetpackwithmviapp.repository.main

import android.util.Log
import com.fastival.jetpackwithmviapp.api.GenericResponse
import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.api.main.response.BlogCreateUpdateResponse
import com.fastival.jetpackwithmviapp.api.main.response.BlogListSearchResponse
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.extension.buildError
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.extension.safeApiCall
import com.fastival.jetpackwithmviapp.persistence.returnOrderedBlogQuery
import com.fastival.jetpackwithmviapp.repository.NetworkBoundResource
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@FlowPreview
@MainScope
class BlogRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): BlogRepository
{

    private val TAG: String = "AppDebug"

    override fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        filterAndOrder: String,
        page: Int,
        stateEvent: StateEvent
    ): Flow<DataState<BlogViewState>> =

            object: NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
                dispatcher = Dispatchers.IO,
                stateEvent = stateEvent,
                dbQuery = {
                    blogPostDao.returnOrderedBlogQuery(
                        query = query,
                        filterAndOrder = filterAndOrder,
                        page = page)
                },
                fetchCacheData = { cacheObj, stateEvent ->

                    val viewState = BlogViewState(
                        blogFields = BlogViewState.BlogFields(
                            blogList = cacheObj
                        )
                    )

                    DataState.data(
                        response = null,
                        data = viewState,
                        stateEvent = stateEvent
                    )

                },
                apiCall = {

                  openApiMainService.searchListBlogPosts(
                      authToken.transHeaderAuthorization(),
                      query = query,
                      ordering = filterAndOrder,
                      page = page
                  )

                },
                updateCache = { networkObj, dispatcher ->

                    val updatedBlogList = networkObj.toList()

                    withContext(dispatcher) {

                        for (blogPost in updatedBlogList){
                            try{
                                blogPostDao.insert(blogPost)
                            }catch (e: Exception){
                                Log.e(TAG, "updateLocalDb: error updating cache data on blog post with slug: ${blogPost.slug}. " +
                                        "${e.message}")
                                // Could send an error report here or something but I don't think you should throw an error to the UI
                                // Since there could be many blog posts being inserted/updated.
                            }
                        }

                    }

                }
            ){}.result


    override fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String,
        stateEvent: StateEvent
    ): Flow<DataState<BlogViewState>> = flow {

        val apiResult = safeApiCall(Dispatchers.IO) {
            openApiMainService.isAuthorOfBlogPost(
                authToken.transHeaderAuthorization(),
                slug
            )
        }

        emit(
            resIsAuthorBlogPost(apiResult, stateEvent)
        )
    }


    private suspend fun resIsAuthorBlogPost(
        result: ApiResult<GenericResponse?>,
        stateEvent: StateEvent
    ): DataState<BlogViewState> =

        object: ApiResponseHandler<BlogViewState, GenericResponse>(
            response = result,
            stateEvent = stateEvent
        ){

            override suspend fun handleApiResultSuccess(networkObj: GenericResponse): DataState<BlogViewState> {

                val viewState = BlogViewState(
                    viewBlogFields = BlogViewState.ViewBlogFields(
                        isAuthorOfBlogPost = false
                    )
                )

                return when (networkObj.response) {

                    RESPONSE_NO_PERMISSION_TO_EDIT -> {

                        DataState.data(
                            response = null,
                            data = viewState,
                            stateEvent = stateEvent
                        )

                    }

                    RESPONSE_HAS_PERMISSION_TO_EDIT -> {

                        viewState.viewBlogFields.isAuthorOfBlogPost = true

                        DataState.data(
                            response = null,
                            data = viewState,
                            stateEvent = stateEvent
                        )

                    }

                    else -> {
                        buildError(
                            ERROR_UNKNOWN,
                            UIComponentType.None,
                            stateEvent
                        )
                    }
                }

            }
        }.getResult()


    override fun deleteBlogPost(
        authToken: AuthToken,
        blogPost: BlogPost,
        stateEvent: StateEvent
    ): Flow<DataState<BlogViewState>> = flow {

        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.deleteBlogPost(
                authToken.transHeaderAuthorization(),
                blogPost.slug
            )
        }

        emit(
            resDeleteBlogPost(apiResult, blogPostDao, blogPost, stateEvent)
        )
    }


    private suspend fun resDeleteBlogPost(
        apiResult: ApiResult<GenericResponse?>,
        blogPostDao: BlogPostDao,
        blogPost: BlogPost,
        stateEvent: StateEvent
    ): DataState<BlogViewState> =

        object: ApiResponseHandler<BlogViewState, GenericResponse>(
            apiResult,
            stateEvent
        ){
            override suspend fun handleApiResultSuccess(networkObj: GenericResponse): DataState<BlogViewState> {

                if (networkObj.response == SUCCESS_BLOG_DELETED){

                    withContext(Dispatchers.IO){
                        blogPostDao.deleteBlogPost(blogPost)
                    }

                    return DataState.data(
                        response = Response(
                            message = SUCCESS_BLOG_DELETED,
                            uiComponentType = UIComponentType.Toast,
                            messageType = MessageType.Success
                        ),
                        stateEvent = stateEvent
                    )
                }
                else {

                    return buildError(
                        ERROR_UNKNOWN,
                        UIComponentType.Dialog,
                        stateEvent
                    )

                }

            }
        }.getResult()


    override fun updateBlogPost(
        authToken: AuthToken,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
        stateEvent: StateEvent
    ): Flow<DataState<BlogViewState>> = flow {

        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.updateBlog(
                authToken.transHeaderAuthorization(),
                slug, title, body, image
            )
        }

        emit(
            resUpdateBlog(apiResult, blogPostDao, stateEvent)
        )

    }


    private suspend fun resUpdateBlog(
        apiResult: ApiResult<BlogCreateUpdateResponse?>,
        blogPostDao: BlogPostDao,
        stateEvent: StateEvent
    ): DataState<BlogViewState> =

        object : ApiResponseHandler<BlogViewState, BlogCreateUpdateResponse>(
            apiResult,
            stateEvent
        ){
            override suspend fun handleApiResultSuccess(networkObj: BlogCreateUpdateResponse): DataState<BlogViewState> {

                val updatedBlogPost: BlogPost = networkObj.toBlogPost()

                withContext(Dispatchers.IO){
                    with(updatedBlogPost) {

                        blogPostDao.updateBlogPost(
                            pk, title, body, image
                        )

                    }
                }

                return retViewStateUpdatedBlog(
                    networkObj.response,
                    updatedBlogPost,
                    stateEvent)

            }
        }.getResult()


    private fun retViewStateUpdatedBlog(
        message: String,
        updatedBlogPost: BlogPost,
        stateEvent: StateEvent
    ): DataState<BlogViewState> = DataState.data(
        response = Response(
            message = message,
            uiComponentType = UIComponentType.Toast,
            messageType = MessageType.Success
        ),
        data = BlogViewState(
            viewBlogFields = BlogViewState.ViewBlogFields(
                blogPost = updatedBlogPost
            ),
            updatedBlogFields = BlogViewState.UpdatedBlogFields(
                updatedBlogTitle = updatedBlogPost.title,
                updatedBlogBody = updatedBlogPost.body,
                updatedImageUri = null
            )
        ),
        stateEvent = stateEvent
    )

}