package com.fastival.jetpackwithmviapp.repository.auth

import android.content.SharedPreferences
import android.util.Log
import com.fastival.jetpackwithmviapp.api.auth.OpenApiAuthService
import com.fastival.jetpackwithmviapp.api.auth.network_response.LoginResponse
import com.fastival.jetpackwithmviapp.api.auth.network_response.RegistrationResponse
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.models.AccountProperties
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.persistence.AuthTokenDao
import com.fastival.jetpackwithmviapp.repository.buildError
import com.fastival.jetpackwithmviapp.repository.safeApiCall
import com.fastival.jetpackwithmviapp.repository.safeCacheCall
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthViewState
import com.fastival.jetpackwithmviapp.ui.auth.state.LoginFields
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SAVE_ACCOUNT_PROPERTIES
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.INVALID_CREDENTIALS
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FlowPreview
@AuthScope
class AuthRepositoryImpl
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val editor: SharedPreferences.Editor
): AuthRepository
{

    private val TAG: String = "AppDebug"


    override fun attemptLogin(
        stateEvent: StateEvent,
        email: String,
        password: String
    ): Flow<DataState<AuthViewState>> = flow {

        val loginFieldErrors = LoginFields(email, password).isValidForLogin()

        if (loginFieldErrors == LoginFields.LoginError.none()) {

            val apiResult = safeApiCall(Dispatchers.IO){
                openApiAuthService.login(email, password)
            }

            emit(
                loginResponseToDataState(apiResult, stateEvent, email)
            )

        }
        else {
            Log.d(TAG, "emitting error: ${loginFieldErrors}")
            emit(
                buildError(
                    loginFieldErrors,
                    UIComponentType.Dialog,
                    stateEvent
                )
            )
        }

    }


    private suspend fun loginResponseToDataState(
        apiResult: ApiResult<LoginResponse?>,
        stateEvent: StateEvent,
        email: String
    ): DataState<AuthViewState> {

        return object: ApiResponseHandler<AuthViewState, LoginResponse>(
            response = apiResult,
            stateEvent = stateEvent
        ) {

            override suspend fun handleSuccess(resultObj: LoginResponse): DataState<AuthViewState> {

                // Incorrect login credentials counts as a 200 response from server, so need to handle that
                if (resultObj.response == GENERIC_AUTH_ERROR)
                    return invalidUser(stateEvent, null)

                withContext(Dispatchers.IO) {
                    accountPropertiesDao.insertOrIgnore(
                        AccountProperties(
                            resultObj.pk,
                            resultObj.email,
                            ""
                        )
                    )
                }

                // will return -1 if failure
                val authToken = AuthToken(
                    resultObj.pk,
                    resultObj.token
                )

                var tokenResult: Long = 0L

                withContext(Dispatchers.IO) {
                    tokenResult = authTokenDao.insert(authToken)
                }

                if (tokenResult < 0) return failSaveToken(stateEvent)

                saveAuthenticatedUserToPrefs(email)

                return validAuthTokenData(authToken, stateEvent)
            }
        }.getResult()
    }


    private fun invalidUser(
        stateEvent: StateEvent,
        registrationResponse: RegistrationResponse?
    ): DataState<AuthViewState> =
        DataState.error(
            response = Response(
                message = registrationResponse?.let { it.errorMessage } ?: INVALID_CREDENTIALS,
                uiComponentType = UIComponentType.Dialog,
                messageType = MessageType.Error
            ),
            stateEvent = stateEvent
        )


    private fun failSaveToken(
        stateEvent: StateEvent
    ): DataState<AuthViewState> =
        DataState.error(
            response = Response(
                ERROR_SAVE_AUTH_TOKEN,
                UIComponentType.Dialog,
                MessageType.Error
            ),
            stateEvent = stateEvent
        )


    private fun validAuthTokenData(
        authToken: AuthToken,
        stateEvent: StateEvent
    ): DataState<AuthViewState> =
        DataState.data(
            data = AuthViewState(
                authToken = authToken
            ),
            stateEvent = stateEvent,
            response = null
        )



    override fun attemptRegistration(
        stateEvent: StateEvent,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Flow<DataState<AuthViewState>> = flow {

        val registrationFieldError = RegistrationFields(
            email,
            username,
            password,
            confirmPassword
        ).isValidForRegistration()

        if (registrationFieldError == RegistrationFields.RegistrationError.none()) {

            val apiResult = safeApiCall(Dispatchers.IO) {
                openApiAuthService.register(
                    email,
                    username,
                    password,
                    confirmPassword
                )
            }

            emit(
                registrationToDataState(apiResult, stateEvent, email)
            )

        }
        else {
            emit(
                buildError(
                    registrationFieldError,
                    UIComponentType.Dialog,
                    stateEvent
                )
            )
        }
    }


    private suspend fun registrationToDataState(
        apiResult: ApiResult<RegistrationResponse?>,
        stateEvent: StateEvent,
        email: String
    ): DataState<AuthViewState> = object: ApiResponseHandler<AuthViewState, RegistrationResponse>(
        response = apiResult,
        stateEvent = stateEvent
    ){
        override suspend fun handleSuccess(resultObj: RegistrationResponse): DataState<AuthViewState> {

            if (resultObj.response == GENERIC_AUTH_ERROR)
                return invalidUser(stateEvent, resultObj)

            var resultSaveAccount = 0L
            withContext(Dispatchers.IO){
                resultSaveAccount = accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        resultObj.pk,
                        resultObj.email,
                        resultObj.username
                    )
                )
            }

            if (resultSaveAccount < 0)
                return failSaveAccount(stateEvent)

            val authtoken = AuthToken(
                resultObj.pk,
                resultObj.token
            )

            var resultSaveToken = 0L

            withContext(Dispatchers.IO) {
                resultSaveToken = authTokenDao.insert(authtoken)
            }

            if (resultSaveToken < 0)
                return failSaveToken(stateEvent)

            saveAuthenticatedUserToPrefs(email)

            return validAuthTokenData(authtoken, stateEvent)
        }
    }.getResult()


    private fun failSaveAccount(stateEvent: StateEvent): DataState<AuthViewState> =
        DataState.error(
            response = Response(
                ERROR_SAVE_ACCOUNT_PROPERTIES,
                UIComponentType.Dialog,
                MessageType.Error
            ),
            stateEvent = stateEvent
        )


    override fun checkPreviousAuthUser(
        stateEvent: StateEvent
    ): Flow<DataState<AuthViewState>> = flow {

        val previousAuthUserEmail: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail.isNullOrBlank()){
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found.")
            emit(returnNoTokenFound(stateEvent))
        }

        else {

            val cacheResult = safeCacheCall(Dispatchers.IO) {
                accountPropertiesDao.searchByEmail(previousAuthUserEmail)
            }

            emit(retCacheResultPreviousUser(cacheResult, stateEvent))

        }
    }


    private suspend fun retCacheResultPreviousUser(
        cacheResult: CacheResult<AccountProperties?>,
        stateEvent: StateEvent
    ): DataState<AuthViewState> = object: CacheResponseHandler<AuthViewState, AccountProperties>(
        response = cacheResult,
        stateEvent = stateEvent
    ){
        override suspend fun handleSuccess(resultObj: AccountProperties): DataState<AuthViewState> {

            if (resultObj.pk > -1) {
                authTokenDao.searchByPk(resultObj.pk).let { loadedToken ->
                    if (loadedToken != null)
                        return validAuthTokenData(loadedToken, stateEvent)
                }
            }

            Log.d(TAG, "createCacheRequestAndReturn: AuthToken not found...")
            return notFoundPreviousUser(stateEvent)

        }
    }.getResult()


    private fun notFoundPreviousUser(
        stateEvent: StateEvent
    ): DataState<AuthViewState> =
        DataState.error(
            response = Response(
                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                UIComponentType.None,
                MessageType.Error
            ),
            stateEvent = stateEvent
        )


    override fun saveAuthenticatedUserToPrefs(email: String) = with(editor) {
        putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        apply()
    }


    override fun returnNoTokenFound(stateEvent: StateEvent): DataState<AuthViewState> =
        DataState.error(
            response = Response(
                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                UIComponentType.None,
                MessageType.Error
            ),
            stateEvent = stateEvent
        )


}