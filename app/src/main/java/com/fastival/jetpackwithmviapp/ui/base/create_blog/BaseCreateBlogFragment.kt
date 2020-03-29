package com.fastival.jetpackwithmviapp.ui.base.create_blog

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel.CreateBlogViewModel
import com.wada811.databinding.dataBinding

abstract class BaseCreateBlogFragment<vb: ViewDataBinding>(
    @LayoutRes layoutId: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): BaseMainFragment(layoutId)
{

    val viewModel: CreateBlogViewModel by viewModels { viewModelFactory }

    internal val binding: vb by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // You can use binding ( data_binding_ktx)
        binding.setVariable(getVariableId(), viewModel)
    }

    fun cancelActiveJobs() = viewModel.cancelActiveJobs()

    // nav_create_blog_startDes_id
    override fun setTopLevelDesId(): Int = R.id.createBlogFragment

    @IdRes
    abstract fun getVariableId(): Int
}