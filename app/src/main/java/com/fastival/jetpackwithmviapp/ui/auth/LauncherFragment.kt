package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentLauncherBinding
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import kotlinx.android.synthetic.main.fragment_launcher.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@FlowPreview
@ExperimentalCoroutinesApi
@AuthScope
class LauncherFragment
@Inject constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseAuthFragment<FragmentLauncherBinding>(R.layout.fragment_launcher, viewModelFactory) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        register.setOnClickListener {
            findNavController().navigate(R.id.action_launcherFragment_to_registerFragment)
        }

        login.setOnClickListener {
            findNavController().navigate(R.id.action_launcherFragment_to_loginFragment)
        }

        forgot_password.setOnClickListener {
            findNavController().navigate(R.id.action_launcherFragment_to_forgotPasswordFragment)
        }

        focusable_view.requestFocus() // reset focus
    }

}
