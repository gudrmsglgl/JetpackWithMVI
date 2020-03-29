package com.fastival.jetpackwithmviapp.ui.base.account

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.account.AccountViewModel
import com.wada811.databinding.dataBinding

abstract class BaseAccountFragment<vb: ViewDataBinding>(
    @LayoutRes contentLayoutId: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): BaseMainFragment(contentLayoutId)
{

    val viewModel: AccountViewModel by viewModels { viewModelFactory }

    internal val binding: vb by dataBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
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
