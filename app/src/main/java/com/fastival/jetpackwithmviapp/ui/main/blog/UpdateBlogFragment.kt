package com.fastival.jetpackwithmviapp.ui.main.blog


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentUpdateBlogBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment

/**
 * A simple [Fragment] subclass.
 */
class UpdateBlogFragment : BaseMainFragment<FragmentUpdateBlogBinding, EmptyViewModel>() {

    override fun setTopLevelDesId(): Int = R.id.blogFragment

    override fun getBindingVariable(): Int = BR.vm

    override fun initFunc() {
    }

    override fun getLayoutId(): Int = R.layout.fragment_update_blog

    override fun getViewModel(): Class<EmptyViewModel> = EmptyViewModel::class.java

    override fun subscribeObservers() {
    }
}
