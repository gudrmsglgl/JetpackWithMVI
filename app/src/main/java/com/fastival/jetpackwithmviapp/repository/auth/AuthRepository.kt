package com.fastival.jetpackwithmviapp.repository.auth

import com.fastival.jetpackwithmviapp.api.auth.OpenApiAuthService
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.persistence.AuthTokenDao
import com.fastival.jetpackwithmviapp.session.SessionManager
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
}