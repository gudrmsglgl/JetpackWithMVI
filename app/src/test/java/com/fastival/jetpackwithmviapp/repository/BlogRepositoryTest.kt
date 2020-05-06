package com.fastival.jetpackwithmviapp.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.fastival.jetpackwithmviapp.api.GenericResponse
import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.api.main.response.BlogCreateUpdateResponse
import com.fastival.jetpackwithmviapp.api.main.response.BlogListSearchResponse
import com.fastival.jetpackwithmviapp.extension.parseRequestBody
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.persistence.returnOrderedBlogQuery
import com.fastival.jetpackwithmviapp.repository.main.BlogRepository
import com.fastival.jetpackwithmviapp.repository.main.BlogRepositoryImpl
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogStateEvent
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import com.fastival.jetpackwithmviapp.util.DataState
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.NOT_FOUND
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.UNKNOWN_ERROR
import com.fastival.jetpackwithmviapp.util.InstantExecutorExtension
import com.fastival.jetpackwithmviapp.util.SuccessHandling
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_NO_PERMISSION_TO_EDIT
import com.fastival.jetpackwithmviapp.util.TestUtil
import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import retrofit2.HttpException


@FlowPreview
@ExtendWith(InstantExecutorExtension::class)
class BlogRepositoryTest {

    lateinit var blogRepository: BlogRepository
    lateinit var blogPostDao: BlogPostDao
    lateinit var openApiMainService: OpenApiMainService
    lateinit var sessionManager: SessionManager

    @BeforeEach
    fun initEach(){
        blogPostDao = mock()
        openApiMainService = mock()
        sessionManager = mock()
        blogRepository = BlogRepositoryImpl(
            openApiMainService, blogPostDao, sessionManager
        )
    }

    @Test
    @DisplayName("NoMoreResult_HttpException")
    fun searchBlogPostNoMoreResult() = runBlocking {
        // given
        var emitCount: Int = 1

        val repositoryBlogListData = MutableLiveData<List<BlogPost>>()
        var repositoryStateMessage: String? = null
        val observer: Observer<List<BlogPost>> = mock()
        repositoryBlogListData.observeForever(observer)

        val testAuthToken = TestUtil.createAuthToken()
        val testNonQuery = ""
        val testFilterAndOrder = "-date_updated"
        val testPage = 1

        val cacheResponse: List<BlogPost> = (0 until 10).map { TestUtil.createBlogPost(it) }
        val httpExceptionNoMoreBlogPost: HttpException = mock {
            on { code() } doReturn 404
            on { message() } doReturn NOT_FOUND
        }

        whenever(openApiMainService
            .searchListBlogPosts(
                testAuthToken.transHeaderAuthorization(),
                testNonQuery,
                testFilterAndOrder,
                testPage))
            .thenThrow(httpExceptionNoMoreBlogPost)

        whenever(blogPostDao
            .returnOrderedBlogQuery(
                query = testNonQuery,
                filterAndOrder = testFilterAndOrder,
                page = testPage
            ))
            .thenReturn(cacheResponse)

        // when
        blogRepository.searchBlogPosts(
            testAuthToken,
            testNonQuery,
            testFilterAndOrder,
            testPage,
            TestUtil.getSearchEvent()
        ).collect {
            println("stateMessage[$emitCount]: ${it.stateMessage?.response?.message}")
            repositoryStateMessage = it.stateMessage?.response?.message
            repositoryBlogListData.value = it.data?.blogFields?.blogList
            emitCount++
        }

        // then
        inOrder(openApiMainService, blogPostDao) {

            verify(blogPostDao, times(1))
                .returnOrderedBlogQuery(
                    query = testNonQuery,
                    filterAndOrder = testFilterAndOrder,
                    page = testPage
                )

            verify(openApiMainService, times(1))
                .searchListBlogPosts(testAuthToken.transHeaderAuthorization(),
                    testNonQuery,
                    testFilterAndOrder,
                    testPage)

            verify(blogPostDao, never()).insert(any())

        }

        verify(observer, times(1)).onChanged(cacheResponse)

        assertThat(
            repositoryStateMessage,
            `is`(endsWith(NOT_FOUND))
        )
    }

    @Test
    fun searchBlogPostWithQuery() = runBlocking {
        // given
        var emitCount: Int = 1

        val repositoryBlogListData = MutableLiveData<List<BlogPost>>()
        val observer: Observer<List<BlogPost>> = mock()
        repositoryBlogListData.observeForever(observer)

        val testAuthToken = TestUtil.createAuthToken()
        val testNonQuery = "test#4"
        val testFilterAndOrder = "-date_updated"
        val testPage = 1

        val cacheResponse: List<BlogPost> = List(1){
            TestUtil.createBlogPost(4)
        }

        val blogListWithQueryResponse = BlogListSearchResponse(
            results = TestUtil.createBlogListResponse(0, 1).apply {
                this[0].title = "test#4_updated"
            },
            detail = "testBlogListResponse#4"
        )


        whenever(openApiMainService
            .searchListBlogPosts(
                testAuthToken.transHeaderAuthorization(),
                testNonQuery,
                testFilterAndOrder,
                testPage))
            .thenReturn(blogListWithQueryResponse)

        whenever(blogPostDao
            .returnOrderedBlogQuery(
                query = testNonQuery,
                filterAndOrder = testFilterAndOrder,
                page = testPage
            ))
            .thenReturn(cacheResponse)
            .thenReturn(blogListWithQueryResponse.toList())


        // when
        blogRepository.searchBlogPosts(
            testAuthToken,
            testNonQuery,
            testFilterAndOrder,
            testPage,
            TestUtil.getSearchEvent()
        ).collect {
            println("stateMessage[$emitCount]: ${it.stateMessage?.response?.message}")
            repositoryBlogListData.value = it.data?.blogFields?.blogList
            emitCount++
        }

        // then
        inOrder(openApiMainService, blogPostDao) {

            verify(blogPostDao)
                .returnOrderedBlogQuery(
                    query = testNonQuery,
                    filterAndOrder = testFilterAndOrder,
                    page = testPage
                )


            verify(openApiMainService, times(1))
                .searchListBlogPosts(testAuthToken.transHeaderAuthorization(),
                    testNonQuery,
                    testFilterAndOrder,
                    testPage)

            blogListWithQueryResponse
                .toList()
                .forEach {
                    verify(blogPostDao).insert(it)
                }

            verify(blogPostDao)
                .returnOrderedBlogQuery(
                    query = testNonQuery,
                    filterAndOrder = testFilterAndOrder,
                    page = testPage
                )
        }

        verify(observer).onChanged(cacheResponse)

        verify(observer).onChanged(blogListWithQueryResponse.toList())

        assertThat(
            repositoryBlogListData.value,
            `is`(blogListWithQueryResponse.toList())
        )

        assertThat(
            repositoryBlogListData.value,
            hasSize(1)
        )
    }

    @Test
    fun searchBlogPostNonQuery() = runBlocking {

        // given
        val repositoryBlogListData = MutableLiveData<List<BlogPost>>()
        val observer: Observer<List<BlogPost>> = mock()

        repositoryBlogListData
            .observeForever(observer)

        val testAuthToken = TestUtil.createAuthToken()
        val testNonQuery = ""
        val testFilterAndOrder = "-date_updated"
        val testPage = 1

        val existResponse = BlogListSearchResponse(
            results = TestUtil.createBlogListResponse(end = 10),
            detail = "testBlogListResponse#1"
        )

        val updateResponse = BlogListSearchResponse(
            results = TestUtil.createBlogListResponse(10, 20),
            detail = "testBlogListResponse#2"
        )

        whenever(openApiMainService
            .searchListBlogPosts(
                testAuthToken.transHeaderAuthorization(),
                testNonQuery,
                testFilterAndOrder,
                testPage))
            .thenReturn(updateResponse)


        whenever(blogPostDao
            .returnOrderedBlogQuery(
                query = testNonQuery,
                filterAndOrder = testFilterAndOrder,
                page = testPage
            ))
            .thenReturn(existResponse.toList())
            .thenReturn(updateResponse.toList())

        // when
        blogRepository
            .searchBlogPosts(
                testAuthToken,
                testNonQuery,
                testFilterAndOrder,
                testPage,
                TestUtil.getSearchEvent()
            ).collect {
                repositoryBlogListData.value = it.data?.blogFields?.blogList
            }

        // then
        // order verify
        inOrder(openApiMainService, blogPostDao) {

            verify(blogPostDao)
                .returnOrderedBlogQuery(
                    query = testNonQuery,
                    filterAndOrder = testFilterAndOrder,
                    page = testPage
                )


            verify(openApiMainService, times(1))
                .searchListBlogPosts(testAuthToken.transHeaderAuthorization(),
                    testNonQuery,
                    testFilterAndOrder,
                    testPage)

           updateResponse.toList()
               .forEach {
                   verify(blogPostDao).insert(it)
               }

           verify(blogPostDao)
                .returnOrderedBlogQuery(
                    query = testNonQuery,
                    filterAndOrder = testFilterAndOrder,
                    page = testPage
                )


        }

        // value changed verify
        verify(observer).onChanged(existResponse.toList())

        verify(observer).onChanged(updateResponse.toList())

        assertThat(
            repositoryBlogListData.value,
            hasSize(10)
        )

        assertThat(
            repositoryBlogListData.value,
            `is`(updateResponse.toList())
        )
    }

    @Test
    fun updateBlogPost() = runBlocking {
        // given
        val testAuthToken = TestUtil.createAuthToken()
        val testBlogPost = TestUtil.createBlogPost(identifier = 1)

        val updatedBlogPost = BlogCreateUpdateResponse("updatedTestBlogPost",
            testBlogPost.pk,
            testBlogPost.title+"Updated",
            testBlogPost.slug,
            testBlogPost.body,
            testBlogPost.image,
            "2020-05-03T04:46:10.227131Z",
            testBlogPost.username+"updated")

        val updatedTitle = updatedBlogPost.title.parseRequestBody()
        val updatedBody = updatedBlogPost.body.parseRequestBody()

        val updateEvent = BlogStateEvent.UpdateBlogPostEvent(
            body = updatedBlogPost.body, title = updatedBlogPost.title, image = null)

        whenever(openApiMainService
            .updateBlog(
                testAuthToken.transHeaderAuthorization(),
                testBlogPost.slug,
                updatedTitle,
                updatedBody,
                null))
            .thenReturn(updatedBlogPost)

        // when
        blogRepository
            .updateBlogPost(
                testAuthToken,
                testBlogPost.slug,
                updatedTitle,
                updatedBody,
                null,
                updateEvent)
            .collect { repositoryResponse ->
                assertThat(
                    repositoryResponse.data?.viewBlogFields?.blogPost,
                    `is`(updatedBlogPost.toBlogPost())
                    )
            }

        // then
        verify(openApiMainService)
            .updateBlog(
                testAuthToken.transHeaderAuthorization(),
                testBlogPost.slug,
                updatedTitle,
                updatedBody,
                null)

        verify(blogPostDao)
            .updateBlogPost(
                updatedBlogPost.pk,
                updatedBlogPost.title,
                updatedBlogPost.body,
                updatedBlogPost.image)

    }

    @Test
    @DisplayName("updatePost_fail_exception")
    fun updateBlogPostFail() = runBlocking {
        // given
        val testAuthToken = TestUtil.createAuthToken()
        val testBlogPost = TestUtil.createBlogPost(identifier = 1)

        val updatedBlogPost = BlogCreateUpdateResponse("updatedTestBlogPost",
            testBlogPost.pk,
            testBlogPost.title+"Updated",
            testBlogPost.slug,
            testBlogPost.body,
            testBlogPost.image,
            "2020-05-03T04:46:10.227131Z",
            testBlogPost.username+"updated")

        val httpException: HttpException = mock {
            on { code() } doReturn 404
            on { message() } doReturn NOT_FOUND
        }

        var repositoryValue: DataState<BlogViewState>? = null

        val updatedTitle = updatedBlogPost.title.parseRequestBody()
        val updatedBody = updatedBlogPost.body.parseRequestBody()

        val updateEvent = BlogStateEvent.UpdateBlogPostEvent(
            body = updatedBlogPost.body, title = updatedBlogPost.title, image = null)

        whenever(openApiMainService
            .updateBlog(
                testAuthToken.transHeaderAuthorization(),
                testBlogPost.slug,
                updatedTitle,
                updatedBody,
                null))
            .thenThrow(httpException)

        // when
        blogRepository.updateBlogPost(
            testAuthToken,
            updatedBlogPost.slug,
            updatedTitle,
            updatedBody,
            null,
            updateEvent
        ).collect {
            repositoryValue = it
        }

        /*
        *   then
        *   1. order verify
        *   2. cache not update verify
        *   3. assert updateField null
        * */
        inOrder(openApiMainService, blogPostDao) {

            verify(openApiMainService, times(1))
                .updateBlog(testAuthToken.transHeaderAuthorization(),
                    testBlogPost.slug,
                    updatedTitle,
                    updatedBody,
                    null)

            verify(blogPostDao, never())
                .updateBlogPost(
                    updatedBlogPost.pk,
                    updatedBlogPost.title,
                    updatedBlogPost.body,
                    updatedBlogPost.image)
        }

        assertThat(
            repositoryValue?.data?.updatedBlogFields?.updatedBlogBody,
            nullValue()
        )

        assertThat(
            repositoryValue?.data?.updatedBlogFields?.updatedBlogTitle,
            nullValue()
        )
    }

    @Test
    @DisplayName("isAuthor_true")
    fun isAuthorHasPermission_fromServer() = runBlocking {
        // given
        val testAuthToken = TestUtil.createAuthToken()
        val testBlogPost = TestUtil.createBlogPost(identifier = 1)
        val hasPermissionIsAuthor = GenericResponse(RESPONSE_HAS_PERMISSION_TO_EDIT)

        var repositoryResponse: DataState<BlogViewState>? = null

        whenever(openApiMainService
            .isAuthorOfBlogPost(testAuthToken.transHeaderAuthorization(), testBlogPost.slug)
        ).thenReturn(hasPermissionIsAuthor)

        // when
        blogRepository
            .isAuthorOfBlogPost(
                testAuthToken, testBlogPost.slug, TestUtil.getIsAuthorEvent())
            .collect {
                repositoryResponse = it
            }

        // then
        verify(openApiMainService)
            .isAuthorOfBlogPost(testAuthToken.transHeaderAuthorization(), testBlogPost.slug)

        assertThat(
            repositoryResponse?.data?.viewBlogFields?.isAuthorOfBlogPost,
            `is`(true)
        )

    }

    @Test
    @DisplayName("isAuthor_false")
    fun isAuthorNoPermission_fromServer() = runBlocking {

        // given
        val testAuthToken = TestUtil.createAuthToken()
        val testBlogPost = TestUtil.createBlogPost(identifier = 1)
        val noPermissionIsAuthor = GenericResponse(RESPONSE_NO_PERMISSION_TO_EDIT)

        var repositoryResponse: DataState<BlogViewState>? = null

        whenever(openApiMainService
            .isAuthorOfBlogPost(testAuthToken.transHeaderAuthorization(), testBlogPost.slug)
        ).thenReturn(noPermissionIsAuthor)

        // when
        blogRepository
            .isAuthorOfBlogPost(
                testAuthToken, testBlogPost.slug, TestUtil.getIsAuthorEvent())
            .collect{
                repositoryResponse = it
            }

        // then
        verify(openApiMainService)
            .isAuthorOfBlogPost(testAuthToken.transHeaderAuthorization(), testBlogPost.slug)

        assertThat(
            repositoryResponse?.data?.viewBlogFields?.isAuthorOfBlogPost,
            `is`(false)
        )
    }

    @Test
    @DisplayName("delete_success_Test")
    fun deletePostSuccess_fromServer(): Unit = runBlocking {

        // given
        val testAuthToken = TestUtil.createAuthToken()
        val testBlogPost = TestUtil.createBlogPost(identifier = 1)
        val successDeletePost =  GenericResponse(SuccessHandling.SUCCESS_BLOG_DELETED)
        var repositoryResponse: String? = null

        whenever(openApiMainService
            .deleteBlogPost(testAuthToken.transHeaderAuthorization(), testBlogPost.slug)
        ).thenReturn(successDeletePost)

        // when
        blogRepository.deleteBlogPost(
            testAuthToken, testBlogPost, TestUtil.getDeleteEvent()
        ).collect{
            repositoryResponse = it.stateMessage?.response?.message
        }

        // then
        verify(openApiMainService)
            .deleteBlogPost(testAuthToken.transHeaderAuthorization(), testBlogPost.slug)
        verify(blogPostDao)
            .deleteBlogPost(testBlogPost)

        verifyNoMoreInteractions(openApiMainService)

        assertThat(SuccessHandling.SUCCESS_BLOG_DELETED, `is`(repositoryResponse))
    }

    @Test
    @DisplayName("delete_fail_Test")
    fun deletePostFail_fromServer() = runBlocking {
        // given
        val testAuthToken = TestUtil.createAuthToken()
        val testBlogPost = TestUtil.createBlogPost(1)
        val failDeletePost = GenericResponse(ERROR_UNKNOWN)
        var repositoryResponse: String? = null

        whenever(openApiMainService
            .deleteBlogPost(testAuthToken.transHeaderAuthorization(), testBlogPost.slug)
        ).thenReturn(failDeletePost)

        // when
        blogRepository.deleteBlogPost(
            testAuthToken, testBlogPost, TestUtil.getDeleteEvent()
        ).collect{
            repositoryResponse = it.stateMessage?.response?.message
        }

        verify(openApiMainService)
            .deleteBlogPost(testAuthToken.transHeaderAuthorization(), testBlogPost.slug)

        verify(blogPostDao, never())
            .deleteBlogPost(testBlogPost)

        assertThat(repositoryResponse, endsWith(UNKNOWN_ERROR))
    }
}