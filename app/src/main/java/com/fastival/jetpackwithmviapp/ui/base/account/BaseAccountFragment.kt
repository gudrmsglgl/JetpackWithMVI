package com.fastival.jetpackwithmviapp.ui.base.account

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.account.AccountViewModel
import com.fastival.jetpackwithmviapp.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountViewState
import com.fastival.jetpackwithmviapp.viewmodels.InjectingSavedStateViewModelFactory
import com.wada811.databinding.dataBinding
import javax.inject.Inject

abstract class BaseAccountFragment<vb: ViewDataBinding>
    (@LayoutRes contentLayoutId: Int)
    : BaseMainFragment(contentLayoutId)
{

    @Inject
    lateinit var defaultProvider: InjectingSavedStateViewModelFactory
    lateinit var viewModel: AccountViewModel
    internal val binding: vb by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.run{
            val factory = defaultProvider.create(this, arguments)
            viewModel = if (::viewModel.isInitialized){
                viewModel
            } else {
                ViewModelProvider(this, factory).get(AccountViewModel::class.java)
            }
            cancelActiveJobs()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setVariable(getVariableId(), viewModel)
    }

    fun cancelActiveJobs() = viewModel.cancelActiveJobs()

    override fun setTopLevelDesId()= R.id.accountFragment

    @IdRes
    abstract fun getVariableId(): Int
}
