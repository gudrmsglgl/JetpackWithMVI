package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import com.fastival.jetpackwithmviapp.util.ApiEmptyResponse
import com.fastival.jetpackwithmviapp.util.ApiErrorResponse
import com.fastival.jetpackwithmviapp.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Main", "RegisterFragment: $viewModel")

        subscribeObservers()
    }

    private fun subscribeObservers(){
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
                input_password_confirm.toString()
            )
        )
    }
}
