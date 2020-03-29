package com.fastival.jetpackwithmviapp.ui.base.blog

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.wada811.databinding.dataBinding

abstract class BaseBlogFragment<vb: ViewDataBinding>(
    @LayoutRes layoutId: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): BaseMainFragment(layoutId)
{

    val viewModel: BlogViewModel by viewModels { viewModelFactory }

    internal val binding: vb by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
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