package com.fastival.jetpackwithmviapp.di.main

import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.repository.main.AccountRepository
import com.fastival.jetpackwithmviapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create

@Module
class MainModule {

    @MainScope
    @Provides
    fun provideOpenApiMainService(builder: Retrofit.Builder): OpenApiMainService {
        return builder.build().create(OpenApiMainService::class.java)
    }

    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository{
        return AccountRepository(openApiMainService, accountPropertiesDao, sessionManager)
    }
}