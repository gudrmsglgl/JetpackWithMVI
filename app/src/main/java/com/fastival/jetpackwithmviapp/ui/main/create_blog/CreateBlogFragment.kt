package com.fastival.jetpackwithmviapp.ui.main.create_blog


import androidx.fragment.app.Fragment
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentCreateBlogBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment

/**
 * A simple [Fragment] subclass.
 */
class CreateBlogFragment : BaseMainFragment<FragmentCreateBlogBinding, CreateBlogViewModel>() {

    override fun setTopLevelDesId(): Int = R.id.createBlogFragment

    override fun getBindingVariable() = BR.vm

    override fun initFunc() {
    }

    override fun getLayoutId(): Int = R.layout.fragment_create_blog

    override fun getViewModel(): Class<CreateBlogViewModel> = CreateBlogViewModel::class.java

    override fun subscribeObservers() {
    }
}
