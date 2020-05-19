package com.fastival.jetpackwithmviapp.ui

import android.content.SharedPreferences
import androidx.lifecycle.Observer
import com.fastival.jetpackwithmviapp.extension.parseRequestBody
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.repository.main.BlogRepositoryImpl
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.fastival.jetpackwithmviapp.util.*
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(InstantExecutorExtension::class)
class BlogViewModelTest {

    lateinit var repository: BlogRepositoryImpl
    lateinit var viewModel: BlogViewModel

    @BeforeEach
    fun init(){
        val authToken:AuthToken = mock()
        val sessionManager: SessionManager = mock{
            on { getAuthToken() } doReturn authToken
        }
        val sharedPreferences: SharedPreferences = mock()
        val editor: SharedPreferences.Editor = mock()
        repository = mock()
        viewModel = BlogViewModel(sessionManager, repository, sharedPreferences, editor)
    }

    @Test
    @DisplayName("SEARCH_BLOGPOST")
    fun searchBlogPost() = runBlocking {

        // given
        whenever(repository.searchBlogPosts(any(), any(), any(), any(), any()))
            .thenReturn( flow{
                emit(TestRepoResponse.searchBlogPostResponse())
            }.flowOn(Dispatchers.IO))

        // when
        viewModel.setStateEvent(TestUtil.getSearchEvent())

        // then
        verify(repository).searchBlogPosts(any(), any(), any(), any(), any())
        println("end test")
    }

    @Test
    @DisplayName("IS_AUTHOR_BLOGPOST_TEST")
    fun isAuthor() = runBlocking{

        // given
        val isAuthor: Boolean = true
        whenever(repository.isAuthorOfBlogPost(any(), any(), any()))
            .thenReturn(
                flow {
                    emit(TestRepoResponse.isAuthorBlogPostResponse(isAuthor))
                }
            )

        // when
        viewModel.setStateEvent(
            BlogStateEvent.CheckAuthorOfBlogPost()
        )

        // then
        verify(repository).isAuthorOfBlogPost(any(), any(), any())
        assertThat(
            viewModel.getCurrentViewStateOrNew().viewBlogFields.isAuthorOfBlogPost,
            `is`(isAuthor)
        )
    }

    @Test
    @DisplayName("DELETE_BLOGPOST_SUCCESS_TEST")
    fun deleteBlogPost() = runBlocking {
        // given
        whenever(repository.deleteBlogPost(any(), any(), any())).thenReturn(
            flow { emit(TestRepoResponse.deleteBlogPostResponse()) }
        )
        // when
        viewModel.setStateEvent(
            BlogStateEvent.DeleteBlogPostEvent()
        )
        // then
        verify(repository).deleteBlogPost(any(), any(), any())
        println("end test")
    }
}