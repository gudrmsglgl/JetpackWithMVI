package com.fastival.jetpackwithmviapp.repository.auth

import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.api.auth.OpenApiAuthService
import com.fastival.jetpackwithmviapp.api.auth.network_response.LoginResponse
import com.fastival.jetpackwithmviapp.api.auth.network_response.RegistrationResponse
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.persistence.AuthTokenDao
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.util.GenericApiResponse
import javax.inject.Inject

class AuthRepository
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
)
{

    fun testLoginRequest(email: String, password: String): LiveData<GenericApiResponse<LoginResponse>>{
        return openApiAuthService.login(email, password)
    }

    fun testRegistrationRequest(
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>{
        return openApiAuthService.register(email, username, password, confirmPassword)
    }
}