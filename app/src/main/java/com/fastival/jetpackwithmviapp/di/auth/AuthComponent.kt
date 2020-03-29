package com.fastival.jetpackwithmviapp.di.auth

import com.fastival.jetpackwithmviapp.ui.auth.AuthActivity
import dagger.Subcomponent

@AuthScope
@Subcomponent(
    modules = [
        AuthModule::class,
        AuthViewModelModule::class,
        AuthFragmentModule::class
    ]
)
interface AuthComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthComponent
    }

    fun inject(authActivity: AuthActivity)
}