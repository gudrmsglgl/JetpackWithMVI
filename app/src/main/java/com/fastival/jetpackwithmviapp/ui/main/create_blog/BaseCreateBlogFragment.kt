package com.fastival.jetpackwithmviapp.ui.main.create_blog

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.main.BaseMainFragment
import com.wada811.databinding.dataBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
abstract class BaseCreateBlogFragment<vb: ViewDataBinding>(
    @LayoutRes layoutId: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): BaseMainFragment(layoutId)
{

    val viewModel: CreateBlogViewModel by viewModels { viewModelFactory }

    internal val binding: vb by dataBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChannel()

        setHasOptionsMenu(true)

        observeProceedJob()

        observeStateMessage()
    }

    private fun observeProceedJob() = viewModel.numActiveJobs
        .observe(viewLifecycleOwner,
            Observer {

                uiCommunicationListener.displayProgressBar(
                    viewModel.areAnyJobActive()
                )

            })

    // nav_create_blog_startDes_id
    override fun setTopLevelDesId(): Int = R.id.createBlogFragment

    private fun setupChannel() = viewModel.setUpChannel()

    abstract fun observeStateMessage()

}