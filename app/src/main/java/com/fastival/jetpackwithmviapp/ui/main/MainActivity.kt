package com.fastival.jetpackwithmviapp.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.fastival.jetpackwithmviapp.BR
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.ActivityMainBinding
import com.fastival.jetpackwithmviapp.extension.navActivity
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.auth.AuthActivity
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*

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


            if (authToken == null) {
                Log.d(TAG, "authToken == null")
                navActivity<AuthActivity>(true){}
            }

            authToken?.let {
                if (it.token == null || it.account_pk == -1) {
                    Log.d(TAG, "authToken.token == null || authToken.account_pk == -1")
                    navActivity<AuthActivity>(true){}
                }
            }

        })
    }

    override fun displayProgressBar(bool: Boolean) {
        if (bool) progress_bar.visibility = View.VISIBLE
        else progress_bar.visibility = View.GONE
    }
}
