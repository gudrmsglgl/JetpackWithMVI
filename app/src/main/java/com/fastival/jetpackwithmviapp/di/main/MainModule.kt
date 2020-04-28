package com.fastival.jetpackwithmviapp.di.main

import CreateBlogRepositoryImpl
import android.app.Application
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.databinding.FragmentBlogBinding
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.persistence.AppDatabase
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.repository.main.*
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.main.MainActivity
import com.fastival.jetpackwithmviapp.util.TopSpacingItemDecoration
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit

@ExperimentalCoroutinesApi
@FlowPreview
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
    ): BlogRepository =
        BlogRepositoryImpl(openApiMainService, blogPostDao, sessionManager)


    @JvmStatic
    @MainScope
    @Provides
    fun provideCreateBlogPostRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CreateBlogRepository =
        CreateBlogRepositoryImpl(openApiMainService, blogPostDao, sessionManager)


}