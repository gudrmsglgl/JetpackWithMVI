package com.fastival.jetpackwithmviapp.di.main

import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
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


}