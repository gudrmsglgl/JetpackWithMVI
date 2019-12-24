package com.fastival.jetpackwithmviapp.ui.main.blog


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentViewBlogBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
class ViewBlogFragment : BaseFragment<FragmentViewBlogBinding, EmptyViewModel>() {

    override fun getBindingVariable(): Int = BR.vm

    override fun initFunc() {
    }

    override fun getLayoutId(): Int = R.layout.fragment_view_blog

    override fun getViewModel(): Class<EmptyViewModel> = EmptyViewModel::class.java

    override fun subscribeObservers() {
    }




}
