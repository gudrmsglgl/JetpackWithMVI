package com.fastival.jetpackwithmviapp.ui.main.blog

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.main.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel.BlogViewModel
import com.wada811.databinding.dataBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseBlogFragment<vb: ViewDataBinding>(
    @LayoutRes layoutId: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): BaseMainFragment(layoutId)
{

    val viewModel: BlogViewModel by viewModels { viewModelFactory }

    internal val binding: vb by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        setHasOptionsMenu(true)

        setupChannel()

        observeProceedJob()

        observeStateMessage()
    }


    private fun observeProceedJob() =
        viewModel
            .numActiveJobs
            .observe( viewLifecycleOwner, Observer {
                uiCommunicationListener.displayProgressBar(
                    viewModel.areAnyJobActive()
                )
            })


    private fun setupChannel() = viewModel.setUpChannel()


    // nav_blog_startDes_id
    override fun setTopLevelDesId(): Int = R.id.blogFragment


    abstract fun observeStateMessage()
}