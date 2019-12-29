package com.fastival.jetpackwithmviapp.ui.main.account


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentUpdateAccountBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment

/**
 * A simple [Fragment] subclass.
 */
class UpdateAccountFragment : BaseMainFragment<FragmentUpdateAccountBinding, EmptyViewModel>() {

    override fun setTopLevelDesId(): Int = R.id.accountFragment

    override fun getBindingVariable(): Int {
        return BR.vm
    }

    override fun initFunc() {
    }

    override fun getLayoutId() = R.layout.fragment_update_account

    override fun getViewModel(): Class<EmptyViewModel> {
        return EmptyViewModel::class.java
    }

    override fun subscribeObservers() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(false)
    }
}
