package com.fastival.jetpackwithmviapp.di

import com.fastival.jetpackwithmviapp.di.auth.AuthFragmentBuildersModule
import com.fastival.jetpackwithmviapp.di.auth.AuthModule
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.di.auth.AuthViewModelModule
import com.fastival.jetpackwithmviapp.ui.auth.AuthActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity
}