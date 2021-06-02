package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentLoginBinding
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.extension.addCompositeDisposable
import com.fastival.jetpackwithmviapp.extension.singleClick
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent.LoginAttemptEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.LoginFields
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@FlowPreview
@ExperimentalCoroutinesApi
@AuthScope
class LoginFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseAuthFragment<FragmentLoginBinding>(R.layout.fragment_login, viewModelFactory) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewState()
        observeInputIdPwd()
        observeLoginBtn()
    }

    private fun observeViewState() = viewModel.viewState
        .observe(viewLifecycleOwner,
            Observer { viewState->
                viewState.loginFields?.let { loginFields ->
                    loginFields.login_email?.let { input_email.setText(it) }
                    loginFields.login_password?.let { input_password.setText(it) }
                }
            })

    private fun observeInputIdPwd() = Observable.combineLatest(
        binding.inputEmail.textChanges(),
        binding.inputPassword.textChanges(),
        BiFunction { inputId: CharSequence , inputPwd: CharSequence ->
            inputId.isNotEmpty() && inputPwd.isNotEmpty() })
        .subscribe { isNotEmpty ->
            if (isNotEmpty) {
                binding.loginButton.setBackgroundResource(R.drawable.main_button_drawable)
                binding.loginButton.isEnabled = true
            } else {
                binding.loginButton.setBackgroundResource(R.color.grey2)
                binding.loginButton.isEnabled = false
            }}
        .addCompositeDisposable(disposableBag)

    private fun observeLoginBtn() = login_button.singleClick()
        .filter { binding.loginButton.isEnabled }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe{ login() }
        .addCompositeDisposable(disposableBag)

    private fun login() = with(viewModel) {
        saveLoginFields(this)

        setStateEvent(LoginAttemptEvent(
            binding.inputEmail.text.toString(),
            binding.inputPassword.text.toString()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveLoginFields(viewModel)
    }

    private fun saveLoginFields(
        viewModel: AuthViewModel
    ) = viewModel.setViewStateLoginFields(
        LoginFields(
            binding.inputEmail.text.toString(),
            binding.inputPassword.text.toString()
        ))

}
