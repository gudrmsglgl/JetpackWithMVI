package com.fastival.jetpackwithmviapp.util

import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState

object TestRepoResponse {

    fun searchBlogPostResponse(): DataState<BlogViewState> = DataState.data(
        response = null,
        data = BlogViewState(
            blogFields = BlogViewState.BlogFields(
                blogList = TestUtil.createBlogListResponse(0,10).map {
                    it.toBlogPost()
                }
            )
        ),
        stateEvent = TestUtil.getSearchEvent()
    )

    fun isAuthorBlogPostResponse(
        isAuthor: Boolean
    ): DataState<BlogViewState> = DataState.data(
        response = null,
        data = BlogViewState(
            viewBlogFields = BlogViewState.ViewBlogFields(isAuthorOfBlogPost = isAuthor)
        ),
        stateEvent = TestUtil.getIsAuthorEvent()
    )

    fun deleteBlogPostResponse(): DataState<BlogViewState> = DataState.data(
        response = Response(
            message = SuccessHandling.SUCCESS_BLOG_DELETED,
            uiComponentType = UIComponentType.Toast,
            messageType = MessageType.Success
        ),
        stateEvent = TestUtil.getDeleteEvent()
    )


    fun updateBlogPostResponse(): DataState<BlogViewState> = DataState.data(
        response = Response(
            message = SuccessHandling.SUCCESS_BLOG_UPDATED,
            uiComponentType = UIComponentType.Toast,
            messageType = MessageType.Success
        ),
        data = BlogViewState(
            viewBlogFields = BlogViewState.ViewBlogFields(
                blogPost = TestUtil.createBlogPost(1).apply {
                    title = TestUtil.UPDATED_TITLE
                    body = TestUtil.UPDATED_BODY
                }
            ),
            updatedBlogFields = BlogViewState.UpdatedBlogFields(
                updatedBlogTitle = TestUtil.UPDATED_TITLE,
                updatedBlogBody = TestUtil.UPDATED_BODY,
                updatedImageUri = null
            )
        ),
        stateEvent = BlogStateEvent.UpdateBlogPostEvent(TestUtil.UPDATED_TITLE, TestUtil.UPDATED_BODY, null)
    )
}