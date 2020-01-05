package com.fastival.jetpackwithmviapp.ui.main.account


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentChangePasswordBinding
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountStateEvent
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_PASSWORD_UPDATE_SUCCESS
import kotlinx.android.synthetic.main.fragment_change_password.*

/**
 * A simple [Fragment] subclass.
 */
class ChangePasswordFragment : BaseMainFragment<FragmentChangePasswordBinding, AccountViewModel>() {

    override fun setTopLevelDesId(): Int = R.id.accountFragment

    override fun getBindingVariable(): Int {
        return BR.vm
    }

    override fun initFunc() {

        update_password_button.setOnClickListener {
            viewModel.setStateEvent(AccountStateEvent.ChangePasswordEvent(
                input_current_password.text.toString(),
                input_new_password.text.toString(),
                input_confirm_new_password.text.toString()
            ))
        }

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_change_password
    }

    override fun getViewModel(): Class<AccountViewModel> {
        return AccountViewModel::class.java
    }

    override fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateListener.onDataStateChange(dataState)
            Log.d(TAG, "ChangePasswordFragment, DataState: $dataState")
            if (dataState != null) {
                dataState.data?.response?.let { event ->
                    if (event.peekContent().message.equals(RESPONSE_PASSWORD_UPDATE_SUCCESS)) {
                        stateListener.hideSoftKeyboard()
                        findNavController().popBackStack()
                    }
                }
            }
        })
    }
}
