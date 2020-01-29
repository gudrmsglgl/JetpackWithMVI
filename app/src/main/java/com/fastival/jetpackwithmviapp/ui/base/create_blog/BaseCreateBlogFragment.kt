package com.fastival.jetpackwithmviapp.ui.base.create_blog

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel.CreateBlogViewModel
import com.fastival.jetpackwithmviapp.viewmodels.InjectingSavedStateViewModelFactory
import com.wada811.databinding.dataBinding
import javax.inject.Inject

abstract class BaseCreateBlogFragment<vb: ViewDataBinding>(@LayoutRes layoutId: Int)
    : BaseMainFragment(layoutId)
{
    @Inject
    lateinit var defaultProvider: InjectingSavedStateViewModelFactory
    lateinit var viewModel: CreateBlogViewModel
    internal val binding: vb by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {
            val factory = defaultProvider.create(this, arguments)
            viewModel = if (::viewModel.isInitialized){
                viewModel
            } else {
                ViewModelProvider(this, factory).get(CreateBlogViewModel::class.java)
            }

            cancelActiveJobs()
        }
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