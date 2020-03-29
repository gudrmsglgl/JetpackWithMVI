package com.fastival.jetpackwithmviapp.di

import com.fastival.jetpackwithmviapp.di.auth.AuthComponent
import com.fastival.jetpackwithmviapp.di.main.MainComponent
import dagger.Module

@Module(
    subcomponents = [
    AuthComponent::class,
    MainComponent::class
    ])
class SubComponentsModule