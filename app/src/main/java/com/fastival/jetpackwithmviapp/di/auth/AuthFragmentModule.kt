package com.fastival.jetpackwithmviapp.di.auth

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.fragments.auth.AuthFragmentFactory
import dagger.Module
import dagger.Provides

@Module
object AuthFragmentModule {

    @JvmStatic
    @AuthScope
    @Provides
    fun provideFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory
    ): FragmentFactory {
        return AuthFragmentFactory(viewModelFactory)
    }

}