package com.fastival.jetpackwithmviapp.di

import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class EmptyViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(EmptyViewModel::class)
    abstract fun bindAuthViewModel(viewModel: EmptyViewModel): ViewModel
}