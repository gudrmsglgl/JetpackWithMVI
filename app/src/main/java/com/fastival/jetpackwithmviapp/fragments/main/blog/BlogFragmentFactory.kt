package com.fastival.jetpackwithmviapp.fragments.main.blog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.ui.auth.ForgotPasswordFragment
import com.fastival.jetpackwithmviapp.ui.auth.LauncherFragment
import com.fastival.jetpackwithmviapp.ui.auth.LoginFragment
import com.fastival.jetpackwithmviapp.ui.auth.RegisterFragment
import com.fastival.jetpackwithmviapp.ui.main.account.AccountFragment
import com.fastival.jetpackwithmviapp.ui.main.account.ChangePasswordFragment
import com.fastival.jetpackwithmviapp.ui.main.account.UpdateAccountFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.UpdateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.ViewBlogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class BlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestOptions: RequestOptions,
    private val requestManager: RequestManager
): FragmentFactory()
{
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =

        when (className) {

            BlogFragment::class.java.name -> {
                BlogFragment(viewModelFactory, requestOptions)
            }

            UpdateBlogFragment::class.java.name -> {
                UpdateBlogFragment(viewModelFactory, requestManager)
            }

            ViewBlogFragment::class.java.name -> {
                ViewBlogFragment(viewModelFactory, requestManager)
            }

            else -> {
                BlogFragment(viewModelFactory, requestOptions)
            }

        }

}