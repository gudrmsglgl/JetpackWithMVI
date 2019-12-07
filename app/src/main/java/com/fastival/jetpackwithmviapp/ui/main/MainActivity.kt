package com.fastival.jetpackwithmviapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.fastival.jetpackwithmviapp.BR
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.ActivityMainBinding
import com.fastival.jetpackwithmviapp.extension.navActivity
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.auth.AuthActivity
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding, EmptyViewModel>() {

    override fun initBinding() {
        super.initBinding()
        Log.d(TAG, "MainActivity_ sessionManager: ${sessionManager.hashCode()}")
        binding.smr = sessionManager
    }

    override fun getBindingVariable(): Int {
        return BR.emptyViewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun getViewModel(): Class<EmptyViewModel> {
        return EmptyViewModel::class.java
    }

    override fun subscribeObservers() {
        sessionManager.cachedToken.observe(this, Observer {authToken->
            Log.d(TAG, "MainActivity, subscribeObservers: ViewState: $authToken")

            if (authToken == null ) {
                navActivity<AuthActivity>(true){}
            }

            authToken?.let {
                if (it.token == null || it.account_pk == -1) {
                    navActivity<AuthActivity>(true){}
                }
            }

        })
    }
}