package com.fastival.jetpackwithmviapp.ui.base.account

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.account.AccountViewModel
import com.wada811.databinding.dataBinding

abstract class BaseAccountFragment<vb: ViewDataBinding>
    (@LayoutRes contentLayoutId: Int): BaseMainFragment<AccountViewModel>(contentLayoutId)
{

    internal val binding: vb by dataBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // You can use binding ( data_binding_ktx)
        binding.setVariable(getBindingVariable(), viewModel)
    }

    override fun setTopLevelDesId()= R.id.accountFragment

    override fun getViewModel(): Class<AccountViewModel> = AccountViewModel::class.java

}