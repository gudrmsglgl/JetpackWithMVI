package com.fastival.jetpackwithmviapp.di.auth

import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.di.ViewModelKey
import com.fastival.jetpackwithmviapp.ui.auth.AuthViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AuthViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AuthViewModel::class)
    abstract fun bindAuthViewModel(authViewModel: AuthViewModel): ViewModel

}