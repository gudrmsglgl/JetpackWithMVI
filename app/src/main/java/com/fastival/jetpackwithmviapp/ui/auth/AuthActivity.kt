package com.fastival.jetpackwithmviapp.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.BaseApplication
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.ActivityAuthBinding
import com.fastival.jetpackwithmviapp.extension.activity.navActivity
import com.fastival.jetpackwithmviapp.fragments.auth.AuthNavHostFragment
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity
import com.fastival.jetpackwithmviapp.ui.main.MainActivity
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity<ActivityAuthBinding>()
{

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: AuthViewModel by viewModels { viewModelFactory }

    override fun getLayoutId() = R.layout.activity_auth

    override fun inject() {
        (application as BaseApplication).authComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onRestoreInstanceState()
    }

    override fun onResume() {
        super.onResume()
        emitCheckPreviousAuthUser()
    }

    private fun onRestoreInstanceState(){
        val host = supportFragmentManager.findFragmentById(R.id.auth_nav_host_fragment)
        host?.let {  }?: createNavHost()
    }

    private fun createNavHost() {
        val navHost = AuthNavHostFragment.create(R.navigation.auth_nav_graph)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.auth_nav_host_fragment,
                navHost,
                getString(R.string.AuthNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    override fun subscribeObservers() {
        Log.d(TAG, "AuthActivityObserve__ viewModel: $viewModel")

        viewModel.dataState.observe(this, Observer {dataState->
            if (dataState != null) {

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
                    (application as BaseApplication).releaseAuthComponent()
                }
            }
        })
    }

    private fun emitCheckPreviousAuthUser(){
        viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
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
