package com.fastival.jetpackwithmviapp.util

import com.fastival.jetpackwithmviapp.api.main.response.BlogSearchResponse
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent

object TestUtil {

    const val UPDATED_TITLE = "TEST_UPDATED_TITLE"
    const val UPDATED_BODY = "TEST_UPDATED_BODY"

    fun createAuthToken() = AuthToken(
        account_pk = 1,
        token = "Token"
    )

    fun createBlogPost(identifier: Int) = BlogPost(
        pk = identifier,
        title = "title#$identifier",
        slug = "tester#${identifier}slug",
        body = "body#$identifier",
        image = "img#$identifier",
        date_updated = identifier.toLong(),
        username = "user#$identifier"
    )

    private fun createBlogSearchResponse(identifier: Int) = BlogSearchResponse(
        pk = identifier,
        title = "title#$identifier",
        slug = "tester#${identifier}slug",
        body = "body#$identifier",
        image = "img#$identifier",
        date_updated = "2020-05-03T04:$identifier:10.227131Z",
        username = "user#$identifier"
    )

    fun createBlogListResponse(
        start:Int = 0,
        end: Int
    ): List<BlogSearchResponse> = (start until end).map {
        createBlogSearchResponse(it)
    }

    fun getSearchEvent() = BlogStateEvent.BlogSearchEvent()

    fun getDeleteEvent() = BlogStateEvent.DeleteBlogPostEvent()

    fun getIsAuthorEvent() = BlogStateEvent.CheckAuthorOfBlogPost()
}