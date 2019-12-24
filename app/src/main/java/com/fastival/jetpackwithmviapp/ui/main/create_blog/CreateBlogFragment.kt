package com.fastival.jetpackwithmviapp.ui.main.create_blog


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentCreateBlogBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
class CreateBlogFragment : BaseFragment<FragmentCreateBlogBinding, EmptyViewModel>() {

    override fun getBindingVariable() = BR.vm

    override fun initFunc() {
    }

    override fun getLayoutId(): Int = R.layout.fragment_create_blog

    override fun getViewModel(): Class<EmptyViewModel> = EmptyViewModel::class.java

    override fun subscribeObservers() {
    }
}
