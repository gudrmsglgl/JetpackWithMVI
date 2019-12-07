package com.fastival.jetpackwithmviapp.ui.auth

import android.util.Log
import androidx.lifecycle.Observer
import com.fastival.jetpackwithmviapp.BR
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.ActivityAuthBinding
import com.fastival.jetpackwithmviapp.extension.navActivity
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity
import com.fastival.jetpackwithmviapp.ui.main.MainActivity

class AuthActivity : BaseActivity<ActivityAuthBinding, AuthViewModel>() {

    override fun getLayoutId() = R.layout.activity_auth

    override fun getViewModel(): Class<AuthViewModel> {
        return AuthViewModel::class.java
    }

    override fun getBindingVariable(): Int {
        return BR.authViewModel
    }

    override fun subscribeObservers() {
        Log.d(TAG, "observe__ viewModel: $viewModel")

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

}
