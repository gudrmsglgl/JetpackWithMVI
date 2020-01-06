package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentLauncherBinding
import com.fastival.jetpackwithmviapp.ui.base.BaseAuthFragment
import kotlinx.android.synthetic.main.fragment_launcher.*

/**
 * A simple [Fragment] subclass.
 */
class LauncherFragment : BaseAuthFragment<FragmentLauncherBinding, AuthViewModel>() {

    override fun getBindingVariable() = BR.vm

    override fun initFunc() {
        register.setOnClickListener {
            navRegistration()
        }

        login.setOnClickListener {
            navLogin()
        }

        forgot_password.setOnClickListener {
            navForgotPassword()
        }

        focusable_view.requestFocus() // reset focus

        Log.d(TAG, "LauncherFragment: $viewModel")
    }

    override fun getLayoutId() = R.layout.fragment_launcher

    override fun getViewModel(): Class<AuthViewModel> = AuthViewModel::class.java

    override fun subscribeObservers() {
    }

    private fun navLogin(){
        findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
    }

    private fun navRegistration(){
        findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
    }

    private fun navForgotPassword(){
        findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
    }
}
