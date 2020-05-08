package com.fastival.jetpackwithmviapp.api

import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.util.Constants
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
    fun getBlogList() = runBlocking {
        enqueueResponse(fileName = "blogposts.json")

        val testQuery = ""
        val testOrdering = "-date_updated"
        val testPage = 1

        val blogList = service.searchListBlogPosts(
            "TestToken",
            testQuery,
            testOrdering,
            testPage)

        val request = mockWebServer.takeRequest()

        assertThat(
            request.headers.get("Authorization"),
            `is`("TestToken")
        )

        assertThat(request.path , allOf(
            containsString(testQuery), containsString(testOrdering), containsString(testPage.toString())
        ))

        assertThat(blogList.results.size, `is`(10))

        (0 until 10).forEach {
            assertThat(blogList.results[it].username, `is`("Tester#${it+1}"))
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
}