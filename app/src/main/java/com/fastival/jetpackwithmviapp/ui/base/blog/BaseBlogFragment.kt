package com.fastival.jetpackwithmviapp.ui.base.blog

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.wada811.databinding.dataBinding

abstract class BaseBlogFragment<vb: ViewDataBinding>(@LayoutRes layoutId: Int)
    : BaseMainFragment<BlogViewModel>(layoutId)
{
    internal val binding: vb by dataBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // You can use binding ( data_binding_ktx)
        binding.setVariable(getBindingVariable(), viewModel)
    }

    // nav_blog_startDes_id
    override fun setTopLevelDesId(): Int = R.id.blogFragment

    override fun getViewModel(): Class<BlogViewModel> = BlogViewModel::class.java
}