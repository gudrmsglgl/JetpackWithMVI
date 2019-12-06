package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.auth.state.LoginFields
import com.fastival.jetpackwithmviapp.util.ApiEmptyResponse
import com.fastival.jetpackwithmviapp.util.ApiErrorResponse
import com.fastival.jetpackwithmviapp.util.ApiSuccessResponse
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : BaseAuthFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "LoginFragment: $viewModel")

        subscribeObservers()
    }

    private fun subscribeObservers(){
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
