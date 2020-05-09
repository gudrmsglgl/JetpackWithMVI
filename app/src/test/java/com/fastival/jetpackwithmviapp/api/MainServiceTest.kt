package com.fastival.jetpackwithmviapp.api

import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.api.main.response.BlogCreateUpdateResponse
import com.fastival.jetpackwithmviapp.extension.parseRequestBody
import com.fastival.jetpackwithmviapp.util.Constants
import com.fastival.jetpackwithmviapp.util.SuccessHandling
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.Okio
import okio.buffer
import okio.source
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainServiceTest {

    private lateinit var service: OpenApiMainService

    private lateinit var mockWebServer: MockWebServer

    @BeforeEach
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenApiMainService::class.java)

    }

    @AfterEach
    fun stopService() = mockWebServer.shutdown()

    @Test
    @DisplayName("API_TEST_SEARCH_BLOG_POSTS")
    fun searchBlogList() = runBlocking {

        val nonQuery = ""
        val descOrdering = "-date_updated"
        val validPage = 1
        val invalidPage = 2

        val resCode: Int = 404
        val resMessage = "Not Found"

        mockWebServer.dispatcher = object : Dispatcher(){
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {

                    "/blog/list?search=$nonQuery&ordering=$descOrdering&page=$validPage" -> {
                        getMockResponse(fileName = "blogposts.json")
                    }

                    "/blog/list?search=$nonQuery&ordering=$descOrdering&page=$invalidPage" -> {
                        MockResponse().apply {
                            setStatus("HTTP/1.1 $resCode $resMessage")
                            setBody("{\"detail\":\"Invalid page.\"}")
                        }
                    }

                    else -> {
                        MockResponse().apply {
                            setStatus("HTTP/1.1 $resCode $resMessage")
                            setBody("{\"detail\":\"Invalid page.\"}")
                        }
                    }
                }
            }
        }

        val blogList = service.searchListBlogPosts(
            "TestToken",
            nonQuery,
            descOrdering,
            validPage)

        val request = mockWebServer.takeRequest()

        assertThat(
            request.headers.get("Authorization"),
            `is`("TestToken")
        )

        assertThat(request.path , allOf(
            containsString(nonQuery), containsString(descOrdering), containsString(validPage.toString())
        ))

        assertThat(blogList.results.size, `is`(10))

        (0 until 10).forEach {
            assertThat(blogList.results[it].username, `is`("Tester#${it+1}"))
        }


        try {
            val notFoundBlogList = service.searchListBlogPosts(
                "TestToken",
                nonQuery,
                descOrdering,
                invalidPage)
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    assertThat(throwable.code(), `is`(resCode))
                    assertThat(throwable.message(), `is`(resMessage))
                }
            }
        }

    }

    @Test
    @DisplayName("API_TEST_IS_AUTHOR")
    fun isAuthor() = runBlocking {

        mockWebServer.dispatcher = object : Dispatcher(){
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {

                    "/blog/true/is_author" -> {
                        MockResponse().apply {
                            setBody("{\"response\":\"You have permission to edit that.\"}")
                        }
                    }

                    else -> {
                        MockResponse().apply {
                            setBody("{\"response\":\"You don't have permission to edit that.\"}")
                        }
                    }

                }
            }
        }

        val authorResponse: GenericResponse = service.isAuthorOfBlogPost(
            "TestToken",
            "true"
        )

        val request: RecordedRequest = mockWebServer.takeRequest()

        assertThat(
            request.path,
            `is`( "/blog/true/is_author")
        )

        assertThat(
            authorResponse.response,
            `is`("You have permission to edit that.")
        )

        val notAuthorResponse: GenericResponse = service.isAuthorOfBlogPost(
            "TestToken",
            "false"
        )

        assertThat(
            notAuthorResponse.response,
            `is`("You don't have permission to edit that.")
        )

    }

    @Test
    @DisplayName("API_TEST_DELETE_BLOG_POST")
    fun deletePost() = runBlocking {

        val validSlug = "validSlug"
        val invalidSlug = "invalidSlug"
        mockWebServer.dispatcher = object : Dispatcher(){
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when (request.path) {

                    "/blog/$validSlug/delete" -> {
                        MockResponse().apply {
                            setBody("{\"response\":\"${SuccessHandling.SUCCESS_BLOG_DELETED}\"}")
                        }
                    }

                    else -> {
                        MockResponse().apply {
                            setBody("{\"response\":\"You don't have permission to delete that.\"}")
                        }
                    }

                }
            }
        }

        val deleteResponse: GenericResponse = service.deleteBlogPost(
            "TestToken",
            validSlug)

        assertThat(
            deleteResponse.response,
            `is`(SuccessHandling.SUCCESS_BLOG_DELETED)
        )

        val deleteResponse2: GenericResponse = service.deleteBlogPost(
            "TestToken",
            invalidSlug
        )

        assertThat(
            deleteResponse2.response,
            `is`("You don't have permission to delete that.")
        )

    }

    @Test
    @DisplayName("API_TEST_UPDATE_BLOG_POST")
    fun updatePost() = runBlocking {

        val validSlug = "validSlug"
        val invalidSlug = "invalidSlug"
        val updatedTitle = "updatedTitle"
        val updatedBody = "I've watched those eyes light up with a smile\n" +
                "미소를 띄며 두 눈이 반짝이는 걸 보았지.\n" +
                "River in the not good times\n" +
                "좋지 않은 순간에 ...\n" +
                "Oh, you taught me all that I know (I know)\n" +
                "오, 너는 내게 모든 걸 가르쳐줬어.\n" +
                "I've seen your soul grow just like a rose\n" +
                "난 네 영혼이 꼭 장미처럼 자라나는 것을 보았지.\n" +
                "Made it through all of those thorns\n" +
                "그 모든 가시들을 뚫고 말이야.\n" +
                "Girl into the woman I know\n" +
                "소녀에서, 내가 지금 아는 여자가 되었어."

        val lessThanTitle = "less"
        val lessThanBody = "less than 50"

        val badRequestMessageTitle = "{\"response\":[\"Enter a title longer than 5 characters.\"]}"
        val badRequestMessageBody = "{\"response\":[\"Enter a body longer than 50 characters.\"]}"

        mockWebServer.dispatcher = object : Dispatcher(){
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when {

                    (updatedTitle.length <= 5) -> {
                        MockResponse().apply {
                            setStatus("HTTP/1.1 400 Bad Request")
                            setBody(badRequestMessageTitle)
                        }
                    }

                    (updatedBody.length <= 50) -> {
                        MockResponse().apply {
                            setStatus("HTTP/1.1 400 Bad Request")
                            setBody(badRequestMessageBody)
                        }
                    }

                    (request.path == "/blog/$invalidSlug/update") -> {
                        MockResponse().apply {
                            setBody("{\"response\":\"You don't have permission to edit that.\"}")
                        }
                    }

                    else -> {
                        getMockResponse("updatedBlogPost.json")
                    }

                }
            }
        }

        val updatedBlogPost: BlogCreateUpdateResponse = service.updateBlog(
            "TestToken",
            slug = validSlug,
            title = updatedTitle.parseRequestBody(),
            body = updatedBody.parseRequestBody(),
            image = null
        )

        assertThat(
            updatedBlogPost.body,
            `is`(updatedBody)
        )

        try {
            val errorBadRequest = service.updateBlog(
                "TestToken",
                slug = validSlug,
                title = lessThanTitle.parseRequestBody(),
                body = updatedBody.parseRequestBody(),
                image = null
            )
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    assertThat(throwable.code() , `is`(400))
                    assertThat(throwable.message(), `is`("Bad Request"))
                }
            }
        }

    }


    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val bufferedSource = javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")
            .source()
            .buffer()
       val mockResponse = MockResponse()
       for ( (key, value) in headers) {
           mockResponse.addHeader(key, value)
       }
        mockWebServer.enqueue(
            mockResponse
                .setBody(bufferedSource.readString(Charsets.UTF_8))
        )
    }

    private fun getMockResponse(
        fileName: String,
        headers: Map<String, String> = emptyMap()
    ): MockResponse = MockResponse().apply {

        val bufferedSource = javaClass.classLoader!!
            .getResourceAsStream("api-response/$fileName")
            .source()
            .buffer()

        for ( (key, value) in headers) {
            addHeader(key, value)
        }

        setBody(bufferedSource.readString(Charsets.UTF_8))
    }


}