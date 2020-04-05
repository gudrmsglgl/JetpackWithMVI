package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentRegisterBinding
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.extension.editToString
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent.RegisterAttemptEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@FlowPreview
@ExperimentalCoroutinesApi
@AuthScope
class RegisterFragment
@Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseAuthFragment<FragmentRegisterBinding>(R.layout.fragment_register, viewModelFactory) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()

        binding.registerButton.setOnClickListener {
            register()
        }
    }

    private fun register() =
        viewModel.setStateEvent(
            RegisterAttemptEvent(
                binding.inputEmail.editToString(),
                binding.inputUsername.editToString(),
                binding.inputPassword.editToString(),
                binding.inputPasswordConfirm.editToString()
            )
        )


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
        viewModel
            .setViewStateRegistrationFields(
                RegistrationFields(
                    input_email.editToString(),
                    input_username.editToString(),
                    input_password.editToString(),
                    input_password_confirm.editToString()
                )
            )
    }
}
