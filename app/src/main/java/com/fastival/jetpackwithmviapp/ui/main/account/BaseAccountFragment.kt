package com.fastival.jetpackwithmviapp.ui.main.account

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.main.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountViewState
import com.wada811.databinding.dataBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseAccountFragment<vb: ViewDataBinding>(
    @LayoutRes layoutRes: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): BaseMainFragment(layoutRes)
{

    val viewModel: AccountViewModel by viewModels { viewModelFactory }

    internal val binding: vb by dataBinding()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChannel()

        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }

        setHasOptionsMenu(true)

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


    override fun setTopLevelDesId()= R.id.accountFragment


    abstract fun observeStateMessage()
}
