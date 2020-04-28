package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentRegisterBinding
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.extension.addCompositeDisposable
import com.fastival.jetpackwithmviapp.extension.editToString
import com.fastival.jetpackwithmviapp.extension.singleClick
import com.fastival.jetpackwithmviapp.extension.tvToString
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent.RegisterAttemptEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import com.jakewharton.rxbinding3.widget.TextViewAfterTextChangeEvent
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.regex.Pattern
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

        observeViewState()

        observeRegisterForm()

        observeRegButton()
    }

    private fun observeRegButton() = binding.registerButton
        .singleClick()
        .subscribe { register() }
        .addCompositeDisposable(disposableBag)

    private fun observeRegisterForm() = Observable.combineLatest(
        isValidIdAndName(),
        isEqualPwdConfirmPwd(),
        BiFunction { t1: Boolean, t2: Boolean ->
            t1 && t2
        })
        .subscribe { isAllInputField ->
            binding.registerButton.apply {

                isEnabled = if (isAllInputField) {
                    setBackgroundResource(R.drawable.main_button_drawable)
                    true
                } else {
                    setBackgroundResource(R.color.grey2)
                    false
                }

            }}
        .addCompositeDisposable(disposableBag)

    private fun isValidIdAndName() = Observable.combineLatest(
        isValidID(),
        isNotEmptyName(),
        BiFunction { isValidId: Boolean, notEmptyName: Boolean ->
            isValidId && notEmptyName
        })

    private fun isValidID() = binding.inputEmail
        .textChanges()
        .doOnNext { Log.d(TAG, "input ID: $it") }
        .map { checkInputIdField(it) }

    private fun checkInputIdField(
        input: CharSequence
    ): Boolean = if (input.isEmpty()) {
        binding.inputEmailLayout.error = null
        false
    } else {
        if (validateEmail(input)) {
            binding.inputEmailLayout.error = null
            true
        }
        else {
            binding.inputEmailLayout.error = getString(R.string.reg_not_email)
            false
        }
    }

    private fun isNotEmptyName() = binding.inputUsername
        .textChanges()
        .map { it.isNotEmpty() }

    private fun isEqualPwdConfirmPwd() = Observable.combineLatest(

        binding.inputPassword
            .textChanges(),

        binding.inputPasswordConfirm
            .textChanges(),

        BiFunction { pwd: CharSequence, confirm: CharSequence ->

            if (pwd.toString().isEmpty() && confirm.toString().isEmpty()) {
                binding.inputPasswordConfirmLayout.error = null
                false
            }

            if (pwd.toString() == confirm.toString()) {
                binding.inputPasswordConfirmLayout.error = null
                true
            } else {
                if (pwd.toString().isNotEmpty() && confirm.toString().isEmpty()) {
                    binding.inputPasswordConfirmLayout.error = null
                } else {
                    binding.inputPasswordConfirmLayout.error = getString(R.string.reg_pwd_confirm_notEqual)
                }
                false
            }

        })

    private fun register() = viewModel.setStateEvent(
        RegisterAttemptEvent(
            binding.inputEmail.editToString(),
            binding.inputUsername.editToString(),
            binding.inputPassword.editToString(),
            binding.inputPasswordConfirm.editToString()
        ))

    private fun observeViewState() = viewModel.viewState
        .observe(viewLifecycleOwner,
            Observer {viewState->
                viewState.registrationFields?.let {regField->
                    regField.registration_email?.let { input_email.setText(it) }
                    regField.registration_username?.let { input_username.setText(it) }
                    regField.registration_password?.let { input_password.setText(it) }
                    regField.registration_confirm_password?.let { input_password_confirm.setText(it) }
                }
            })

    override fun onDestroyView() {
        super.onDestroyView()
        saveRegFields()
    }

    private fun validEmailAddressRegex() =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE)

    private fun validateEmail(id: CharSequence) =
        validEmailAddressRegex().matcher(id).find()

    private fun saveRegFields() = viewModel.setViewStateRegistrationFields(
        RegistrationFields(
            input_email.editToString(),
            input_username.editToString(),
            input_password.editToString(),
            input_password_confirm.editToString()
        ))

}
