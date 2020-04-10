import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.api.main.response.BlogCreateUpdateResponse
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.repository.main.CreateBlogRepository
import com.fastival.jetpackwithmviapp.extension.safeApiCall
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_MEMBER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@MainScope
@FlowPreview
class CreateBlogRepositoryImpl
@Inject
constructor(
    private val openApiMainService: OpenApiMainService,
    private val blogPostDao: BlogPostDao,
    private val sessionManager: SessionManager
): CreateBlogRepository
{

    override fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?,
        stateEvent: StateEvent
    ): Flow<DataState<CreateBlogViewState>> = flow{

        val resCreateBlog =
            safeApiCall(Dispatchers.IO) {
                openApiMainService.createBlog(
                    authToken.transHeaderAuthorization(),
                    title,
                    body,
                    image
                )
            }

        emit(
            resCreateBlog2DataState(blogPostDao, resCreateBlog, stateEvent)
        )

    }


    private suspend fun resCreateBlog2DataState(
        blogPostDao: BlogPostDao,
        response: ApiResult<BlogCreateUpdateResponse?>,
        stateEvent: StateEvent
    ): DataState<CreateBlogViewState> =

        object: ApiResponseHandler<CreateBlogViewState, BlogCreateUpdateResponse>(
            response = response,
            stateEvent = stateEvent
        ) {

            override suspend fun handleApiResultSuccess(
                networkObj: BlogCreateUpdateResponse
            ): DataState<CreateBlogViewState>
            {

                // If they don't have a paid membership account it will still return a 200
                // Need to account for that
                if (networkObj.response != RESPONSE_MUST_BECOME_MEMBER){

                    withContext(Dispatchers.IO){
                        blogPostDao.insert(
                            networkObj.toBlogPost()
                        )
                    }

                }

                return DataState.data(
                    response = Response(
                        message = networkObj.response,
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Success
                    ),
                    data = null,
                    stateEvent = stateEvent
                )

            }

        }.getResult()

}