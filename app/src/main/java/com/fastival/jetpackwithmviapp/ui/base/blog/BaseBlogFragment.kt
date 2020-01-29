package com.fastival.jetpackwithmviapp.ui.base.blog

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.fastival.jetpackwithmviapp.viewmodels.InjectingSavedStateViewModelFactory
import com.wada811.databinding.dataBinding
import javax.inject.Inject

abstract class BaseBlogFragment<vb: ViewDataBinding>(@LayoutRes layoutId: Int)
    : BaseMainFragment(layoutId)
{

    @Inject
    lateinit var defaultViewModelFactory: InjectingSavedStateViewModelFactory
    lateinit var viewModel: BlogViewModel
    internal val binding: vb by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.run {
            val factory =
                defaultViewModelFactory.create(this, arguments)

            viewModel = if(::viewModel.isInitialized) {
                viewModel
            } else {
                ViewModelProvider(this, factory)[BlogViewModel::class.java]
            }

            cancelActiveJobs()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.setVariable(getVariableId(), viewModel)
    }


    // nav_blog_startDes_id
    override fun setTopLevelDesId(): Int = R.id.blogFragment

    @IdRes
    abstract fun getVariableId(): Int

    fun cancelActiveJobs() = viewModel.cancelActiveJobs()

}