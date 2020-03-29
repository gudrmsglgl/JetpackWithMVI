package com.fastival.jetpackwithmviapp.di

import android.app.Application
import androidx.databinding.ViewDataBinding
import com.fastival.jetpackwithmviapp.BaseApplication
import com.fastival.jetpackwithmviapp.di.auth.AuthComponent
import com.fastival.jetpackwithmviapp.di.main.MainComponent
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        SubComponentsModule::class
    ]
)
interface AppComponent{
    val sessionManager: SessionManager

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(baseActivity: BaseActivity<ViewDataBinding>)

    fun authComponent(): AuthComponent.Factory

    fun mainComponent(): MainComponent.Factory

}