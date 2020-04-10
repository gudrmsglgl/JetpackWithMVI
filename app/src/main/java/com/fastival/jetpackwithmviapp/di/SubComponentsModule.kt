package com.fastival.jetpackwithmviapp.di

import com.fastival.jetpackwithmviapp.di.auth.AuthComponent
import com.fastival.jetpackwithmviapp.di.main.MainComponent
import dagger.Module
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
@ExperimentalCoroutinesApi
@FlowPreview
@Module(
    subcomponents = [
        AuthComponent::class,
        MainComponent::class
    ]
)
class SubComponentsModule