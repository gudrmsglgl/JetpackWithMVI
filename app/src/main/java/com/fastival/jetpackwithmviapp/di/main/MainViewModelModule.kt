package com.fastival.jetpackwithmviapp.di.main

import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.di.SavedStateViewModelFactory
import com.fastival.jetpackwithmviapp.di.ViewModelKey
import com.fastival.jetpackwithmviapp.ui.main.account.AccountViewModel
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel.CreateBlogViewModel
import com.squareup.inject.assisted.AssistedInject
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@AssistedModule
@Module(includes = [AssistedInject_MainViewModelModule::class])
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accountViewModel: AccountViewModel): ViewModel

   /* @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindBlogViewModel(blogViewModel: BlogViewModel): ViewModel*/

    @Binds
    @IntoMap
    @ViewModelKey(CreateBlogViewModel::class)
    abstract fun bindCreateBlogViewModel(
        createBlogViewModel: CreateBlogViewModel
    ): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BlogViewModel::class)
    abstract fun bindSavedStateBlogViewModel(
        factory: BlogViewModel.Factory
    ): SavedStateViewModelFactory<out ViewModel>
}