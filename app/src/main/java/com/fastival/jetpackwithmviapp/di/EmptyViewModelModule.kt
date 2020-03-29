package com.fastival.jetpackwithmviapp.di

import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.di.auth.key.AuthViewModelKey
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EmptyViewModelModule {

    @Binds
    @IntoMap
    @AuthViewModelKey(EmptyViewModel::class)
    abstract fun bindAuthViewModel(viewModel: EmptyViewModel): ViewModel
}