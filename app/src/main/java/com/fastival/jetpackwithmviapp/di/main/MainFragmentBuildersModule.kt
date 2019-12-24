package com.fastival.jetpackwithmviapp.di.main

import com.fastival.jetpackwithmviapp.ui.main.account.AccountFragment
import com.fastival.jetpackwithmviapp.ui.main.account.ChangePasswordFragment
import com.fastival.jetpackwithmviapp.ui.main.account.UpdateAccountFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.BlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.UpdateBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.ViewBlogFragment
import com.fastival.jetpackwithmviapp.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment

    @ContributesAndroidInjector
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment
}