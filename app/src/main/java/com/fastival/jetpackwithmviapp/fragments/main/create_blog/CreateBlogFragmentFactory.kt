package com.fastival.jetpackwithmviapp.fragments.main.create_blog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
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
import com.fastival.jetpackwithmviapp.ui.main.create_blog.CreateBlogFragment
import javax.inject.Inject

@MainScope
class CreateBlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): FragmentFactory()
{
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =

        when (className) {

            CreateBlogFragment::class.java.name -> {
                CreateBlogFragment(viewModelFactory, requestManager)
            }

            else -> {
                CreateBlogFragment(viewModelFactory, requestManager)
            }

        }

}