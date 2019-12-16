package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentRegisterBinding
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent.RegisterAttemptEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import com.fastival.jetpackwithmviapp.ui.base.BaseFragment
import com.fastival.jetpackwithmviapp.util.ApiEmptyResponse
import com.fastival.jetpackwithmviapp.util.ApiErrorResponse
import com.fastival.jetpackwithmviapp.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : BaseFragment<FragmentRegisterBinding, AuthViewModel>() {

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }


    override fun initFunc() {
        register_button.setOnClickListener {
            viewModel.setStateEvent(
                RegisterAttemptEvent(
                    input_email.text.toString(),
                    input_username.text.toString(),
                    input_password.text.toString(),
                    input_password_confirm.text.toString()
                )
            )
        }
    }


    override fun getLayoutId()= R.layout.fragment_register


    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState->
            viewState.registrationFields?.let {regField->
                regField.registration_email?.let { input_email.setText(it) }
                regField.registration_username?.let { input_username.setText(it) }
                regField.registration_password?.let { input_password.setText(it) }
                regField.registration_confirm_password?.let { input_password_confirm.setText(it) }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setRegistrationFields(
            RegistrationFields(
                input_email.text.toString(),
                input_username.text.toString(),
                input_password.text.toString(),
                input_password_confirm.text.toString()
            )
        )
    }
}
