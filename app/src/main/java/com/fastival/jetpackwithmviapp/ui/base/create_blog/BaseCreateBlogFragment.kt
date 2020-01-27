package com.fastival.jetpackwithmviapp.ui.base.create_blog

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel.CreateBlogViewModel
import com.wada811.databinding.dataBinding

abstract class BaseCreateBlogFragment<vb: ViewDataBinding>(@LayoutRes layoutId: Int)
    : BaseMainFragment<CreateBlogViewModel>(layoutId)
{
    internal val binding: vb by dataBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // You can use binding ( data_binding_ktx)
        binding.setVariable(getBindingVariable(), viewModel)
    }

    // nav_create_blog_startDes_id
    override fun setTopLevelDesId(): Int = R.id.createBlogFragment

    override fun getViewModel(): Class<CreateBlogViewModel> = CreateBlogViewModel::class.java
}