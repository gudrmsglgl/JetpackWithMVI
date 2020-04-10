package com.fastival.jetpackwithmviapp.fragments.auth

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.ui.auth.ForgotPasswordFragment
import com.fastival.jetpackwithmviapp.ui.auth.LauncherFragment
import com.fastival.jetpackwithmviapp.ui.auth.LoginFragment
import com.fastival.jetpackwithmviapp.ui.auth.RegisterFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject
@ExperimentalCoroutinesApi
@FlowPreview
@AuthScope
class AuthFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): FragmentFactory()
{
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =

        when (className) {

            LauncherFragment::class.java.name -> {
                LauncherFragment(viewModelFactory)
            }

            LoginFragment::class.java.name -> {
                LoginFragment(viewModelFactory)
            }

            RegisterFragment::class.java.name -> {
                RegisterFragment(viewModelFactory)
            }

            ForgotPasswordFragment::class.java.name -> {
                ForgotPasswordFragment(viewModelFactory)
            }

            else -> {
                LauncherFragment(viewModelFactory)
            }

        }

}