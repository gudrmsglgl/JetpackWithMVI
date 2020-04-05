package com.fastival.jetpackwithmviapp.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.di.main.key.MainViewModelKey
import com.fastival.jetpackwithmviapp.ui.main.account.AccountViewModel
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.fastival.jetpackwithmviapp.ui.main.create_blog.CreateBlogViewModel
import com.fastival.jetpackwithmviapp.viewmodels.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/*@AssistedModule
@Module(includes = [AssistedInject_MainViewModelModule::class])*/
@Module
abstract class MainViewModelModule {

    @MainScope
    @Binds
    abstract fun provideViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel

    @MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(
        createBlogViewModel: CreateBlogViewModel
    ): ViewModel

    /*@MainScope
    @Binds
    @IntoMap
    @MainViewModelKey(BlogViewModel::class)
    abstract fun bindSavedStateBlogViewModel(
        factory: BlogViewModel.Factory
    ): SavedStateViewModelFactory<out ViewModel>*/
}