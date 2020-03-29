package com.fastival.jetpackwithmviapp.ui.auth


import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentLoginBinding
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent.LoginAttemptEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.LoginFields
import com.fastival.jetpackwithmviapp.ui.base.BaseAuthFragment
import com.fastival.jetpackwithmviapp.util.ApiEmptyResponse
import com.fastival.jetpackwithmviapp.util.ApiErrorResponse
import com.fastival.jetpackwithmviapp.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseAuthFragment<FragmentLoginBinding>(R.layout.fragment_login, viewModelFactory) {

    override fun getBindingVariable(): Int {
        return BR.authViewModel
    }

    override fun initFunc() {
        login_button.setOnClickListener {
            viewModel.setStateEvent(LoginAttemptEvent(
                input_email.text.toString(),
                input_password.text.toString()
            ))
        }
    }

    override fun subscribeObservers(){
        viewModel.viewState.observe(viewLifecycleOwner, Observer {viewState->
            viewState.loginFields?.let { loginFields ->
                loginFields.login_email?.let { input_email.setText(it) }
                loginFields.login_password?.let { input_password.setText(it) }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }
}
