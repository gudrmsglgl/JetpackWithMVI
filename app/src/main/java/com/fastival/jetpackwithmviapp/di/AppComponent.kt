package com.fastival.jetpackwithmviapp.di

import android.app.Application
import com.fastival.jetpackwithmviapp.BaseApplication
import com.fastival.jetpackwithmviapp.session.SessionManager
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidInjectionModule::class, AppModule::class,
        ActivityBuildersModule::class, ViewModelFactoryModule::class, EmptyViewModelModule::class]
)
interface AppComponent: AndroidInjector<BaseApplication>{
    val sessionManager: SessionManager

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}