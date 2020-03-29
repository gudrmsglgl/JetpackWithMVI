package com.fastival.jetpackwithmviapp.repository.auth

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.fastival.jetpackwithmviapp.api.auth.OpenApiAuthService
import com.fastival.jetpackwithmviapp.api.auth.network_response.LoginResponse
import com.fastival.jetpackwithmviapp.api.auth.network_response.RegistrationResponse
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.models.AccountProperties
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.persistence.AuthTokenDao
import com.fastival.jetpackwithmviapp.repository.JobManager
import com.fastival.jetpackwithmviapp.repository.NetworkBoundResource
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.Response
import com.fastival.jetpackwithmviapp.ui.ResponseType
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthViewState
import com.fastival.jetpackwithmviapp.ui.auth.state.LoginFields
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SAVE_ACCOUNT_PROPERTIES
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Job
import javax.inject.Inject

@AuthScope
class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val editor: SharedPreferences.Editor
): JobManager("AuthRepository")
{

    private val TAG = "AppDebug"

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        val loginFieldError = LoginFields(email, password).isValidForLogin()
        if (!loginFieldError.equals(LoginFields.LoginError.none())) {
            return retErrorResponse(loginFieldError, ResponseType.Dialog())
        }

        return object : NetworkBoundResource<LoginResponse, Any, AuthViewState>
            (sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false) {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {

                Log.d(TAG, "handleApiSuccessResponse: $response")

                // Incorrect login credentials counts as a 200 response from server, so need to handle that
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                // Don't care about result here. Just insert if it doesn't exist b/c of foreign key relationship
                // with AuthToken
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        "")
                )

                val result = authTokenDao.insert(
                    AuthToken(response.body.pk, response.body.token)
                )

                if (result < 0) {
                    return onCompleteJob(DataState.error(
                        Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                    ))
                }

                saveAuthenticatedUserToPrefs(email)

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(response.body.pk, response.body.token)
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                addJob("attemptLogin", job)
            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    fun attemptRegistration(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<DataState<AuthViewState>> {

        val registrationFieldsErrors =
            RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()

        if (!registrationFieldsErrors.equals(RegistrationFields.RegistrationError.none()))
            return retErrorResponse(registrationFieldsErrors, ResponseType.Dialog())

        return object : NetworkBoundResource<RegistrationResponse, Any, AuthViewState>
            (sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false){
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<RegistrationResponse>) {

                Log.d(TAG, "handleApiSuccessResponse: $response")

                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }

                val result1 = accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        response.body.username
                    )
                )

                if (result1 < 0) {
                    onCompleteJob(DataState.error(
                        Response(ERROR_SAVE_ACCOUNT_PROPERTIES, ResponseType.Dialog())
                    ))
                    return
                }

                val result2 = authTokenDao.insert(
                    AuthToken(response.body.pk, response.body.token)
                )

                if (result2 < 0) {
                    onCompleteJob(DataState.error(
                        Response(ERROR_SAVE_AUTH_TOKEN, ResponseType.Dialog())
                    ))
                    return
                }

                saveAuthenticatedUserToPrefs(email)
                
                onCompleteJob(DataState.data(
                    data = AuthViewState(
                        authToken = AuthToken(response.body.pk, response.body.token)
                    )
                ))
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, confirmPassword)
            }

            override fun setJob(job: Job) {
                addJob("attemptRegistration", job)
            }

            // not used in this case
            override suspend fun createCacheRequestAndReturn() {

            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>>{

        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if (previousAuthUserEmail.isNullOrEmpty()) {
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found.")
            return retNoTokenFound()
        }

        return object : NetworkBoundResource<Void, Any, AuthViewState>
            (sessionManager.isConnectedToTheInternet(),
            false,
            true,
            false)
        {
            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<Void>) {
            }

            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }

            override suspend fun createCacheRequestAndReturn() {
                // pref email key -> Properties(pk) -> auth token
                accountPropertiesDao.searchByEmail(previousAuthUserEmail).let {accountProperties->
                    Log.d(TAG, "createCacheRequestAndReturn: searching for token... account properties: ${accountProperties}")

                    accountProperties?.let {
                        if (accountProperties.pk > -1) {
                            authTokenDao.searchByPk(accountProperties.pk)?.let { authToken ->
                                if (authToken.token != null) {
                                    onCompleteJob(
                                        DataState.data(
                                            AuthViewState(authToken = authToken)
                                        )
                                    )
                                    return
                                }
                            }
                        }
                    }
                    Log.d(TAG, "createCacheRequestAndReturn: AuthToken not found...")
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )

                }
            }

            override fun setJob(job: Job) {
                addJob("checkPreviousAuthUser", job)
            }

            // not used in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }

            // not used in this case
            override suspend fun updateLocalDb(cacheObject: Any?) {
            }

        }.asLiveData()
    }

    private fun retNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                value = DataState.data(
                    null, Response(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE, ResponseType.None()))
            }
        }
    }

    private fun retErrorResponse(
        errorMessage: String,
        responseType: ResponseType): LiveData<DataState<AuthViewState>> {

        Log.d(TAG, "returnErrorResponse: $errorMessage")
        return object : LiveData<DataState<AuthViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    Response(errorMessage, responseType)
                )
            }
        }
    }

    private fun saveAuthenticatedUserToPrefs(email: String) {
        editor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        editor.apply()
    }

}