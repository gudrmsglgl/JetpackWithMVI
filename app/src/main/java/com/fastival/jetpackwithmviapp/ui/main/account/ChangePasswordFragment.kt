package com.fastival.jetpackwithmviapp.ui.main.account


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentChangePasswordBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseFragment

/**
 * A simple [Fragment] subclass.
 */
class ChangePasswordFragment : BaseFragment<FragmentChangePasswordBinding, EmptyViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.vm
    }

    override fun initFunc() {
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_change_password
    }

    override fun getViewModel(): Class<EmptyViewModel> {
        return EmptyViewModel::class.java
    }

    override fun subscribeObservers() {
    }
}
