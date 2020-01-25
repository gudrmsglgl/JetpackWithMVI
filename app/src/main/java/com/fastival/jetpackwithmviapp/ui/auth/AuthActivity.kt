package com.fastival.jetpackwithmviapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.fastival.jetpackwithmviapp.BR
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.ActivityAuthBinding
import com.fastival.jetpackwithmviapp.extension.activity.navActivity
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity
import com.fastival.jetpackwithmviapp.ui.main.MainActivity
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : BaseActivity<ActivityAuthBinding, AuthViewModel>(),
    NavController.OnDestinationChangedListener {

    override fun getLayoutId() = R.layout.activity_auth

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getBindingVariable(): Int {
        return BR.authViewModel
    }

    override fun initVariables() {
        super.initVariables()
        findNavController(R.id.auth_nav_host_fragment).addOnDestinationChangedListener(this)
    }

    override fun onResume() {
        super.onResume()
        emitCheckPreviousAuthUser()
    }

    override fun subscribeObservers() {
        Log.d(TAG, "AuthActivityObserve__ viewModel: $viewModel")

        viewModel.dataState.observe(this, Observer {dataState->

            onDataStateChange(dataState)

            dataState.data?.let { data ->

                data.data?.getContentIfNotHandled()?.let { authViewState ->
                    authViewState.authToken?.let {
                        Log.d(TAG, "AuthActivity, DataState: $it")
                        viewModel.setAuthToken(it)
                    }
                }

                data.response?.let { event->
                    event.peekContent().let { response ->
                        response.message?.let { message ->
                            if (message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                                onFinishCheckPreviousAuthUser()
                            }
                        }
                    }
                }

            }


        })


        viewModel.viewState.observe(this, Observer {viewState->
            viewState.authToken?.let { authToken ->
                sessionManager.login(authToken)
            }
        })

        sessionManager.cachedToken.observe(this, Observer {authToken->
            authToken.let {
                if (it != null && it.account_pk != -1 && it.token != null){
                    navActivity<MainActivity>(true){}
                }
            }
        })
    }

    private fun emitCheckPreviousAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    // Navigation Changed -> JobCancel()
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        viewModel.cancelActiveJobs()
    }

    override fun expandAppBar() {
        // ignore
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) progress_bar.visibility = View.VISIBLE
        else progress_bar.visibility = View.GONE
    }

    private fun onFinishCheckPreviousAuthUser(){
        fragment_container.visibility = View.VISIBLE
    }
}
