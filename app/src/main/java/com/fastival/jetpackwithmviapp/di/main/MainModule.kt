package com.fastival.jetpackwithmviapp.di.main

import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.persistence.AppDatabase
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.repository.main.AccountRepository
import com.fastival.jetpackwithmviapp.repository.main.AccountRepositoryImpl
import com.fastival.jetpackwithmviapp.repository.main.BlogRepository
import com.fastival.jetpackwithmviapp.repository.main.CreateBlogRepository
import com.fastival.jetpackwithmviapp.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.create

@Module
object MainModule {

    @JvmStatic
    @MainScope
    @Provides
    fun provideOpenApiMainService(builder: Retrofit.Builder): OpenApiMainService {
        return builder.build().create(OpenApiMainService::class.java)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository =
        AccountRepositoryImpl(openApiMainService, accountPropertiesDao, sessionManager)


    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao{
        return db.getBlogPostDao()
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogPostRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository{
        return BlogRepository(openApiMainService, blogPostDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideCreateBlogPostRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CreateBlogRepository
    {
        return CreateBlogRepository(openApiMainService, blogPostDao, sessionManager)
    }
}