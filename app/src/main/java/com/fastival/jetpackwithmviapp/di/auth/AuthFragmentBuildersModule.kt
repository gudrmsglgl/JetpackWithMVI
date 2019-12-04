package com.fastival.jetpackwithmviapp.di.auth

import com.fastival.jetpackwithmviapp.ui.auth.ForgotPasswordFragment
import com.fastival.jetpackwithmviapp.ui.auth.LauncherFragment
import com.fastival.jetpackwithmviapp.ui.auth.LoginFragment
import com.fastival.jetpackwithmviapp.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment
}