package com.fastival.jetpackwithmviapp.di.auth

import com.fastival.jetpackwithmviapp.api.auth.OpenApiAuthService
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.persistence.AuthTokenDao
import com.fastival.jetpackwithmviapp.repository.auth.AuthRepository
import com.fastival.jetpackwithmviapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {

    @AuthScope
    @Provides
    fun provideOpenApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService{
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService
    ): AuthRepository {
        return AuthRepository(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager
        )
    }

}