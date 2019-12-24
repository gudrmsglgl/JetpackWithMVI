package com.fastival.jetpackwithmviapp.ui.main.blog

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentBlogBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseFragment


class BlogFragment : BaseFragment<FragmentBlogBinding, EmptyViewModel>() {

    override fun getBindingVariable(): Int = BR.vm

    override fun initFunc() {
    }

    override fun getLayoutId(): Int = R.layout.fragment_blog

    override fun getViewModel(): Class<EmptyViewModel> = EmptyViewModel::class.java

    override fun subscribeObservers() {
    }
}
