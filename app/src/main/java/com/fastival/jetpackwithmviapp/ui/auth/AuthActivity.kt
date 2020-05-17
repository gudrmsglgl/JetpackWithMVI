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
import com.fastival.jetpackwithmviapp.extension.activity.navActivity
import com.fastival.jetpackwithmviapp.fragments.auth.AuthNavHostFragment
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent
import com.fastival.jetpackwithmviapp.ui.BaseActivity
import com.fastival.jetpackwithmviapp.ui.main.MainActivity
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AuthActivity : BaseActivity()
{

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel: AuthViewModel by viewModels { viewModelFactory }


    override fun inject() {
        (application as BaseApplication).authComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        subscribeObservers()
        onRestoreInstanceState()
    }

    override fun onResume() {
        super.onResume()
        emitCheckPreviousAuthUser()
    }

    private fun onRestoreInstanceState(){
        val host = supportFragmentManager.findFragmentById(R.id.auth_fragments_container)
        host?.let {  }?: createNavHost()
    }

    private fun createNavHost() {
        val navHost =
            AuthNavHostFragment.create(R.navigation.auth_nav_graph)

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.auth_fragments_container,
                navHost,
                getString(R.string.AuthNavHost)
            )
            .setPrimaryNavigationFragment(navHost)
            .commit()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(this, Observer { viewState ->
            Log.d(TAG, "AuthActivity, subscribeObservers: AuthViewState: $viewState")
            viewState.authToken?.let {
                sessionManager.login(it)
            }
        })

        viewModel.totalActiveEvents.observe(this, Observer {
            displayProgressBar(viewModel.areAnyJobActive())
        })

        viewModel.stateMessage.observe(this, Observer { stateMessage ->
            stateMessage?.let {

                if (stateMessage.response.message == RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)
                    splashGone()

                onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.removeStateMessage()
                        }
                    }
                )

            }
        })

        sessionManager.cachedToken.observe(this, Observer {authToken->
            authToken.let {
                if (it != null && it.account_pk != -1 && it.token != null){
                    navActivity<MainActivity>(true){}
                    (application as BaseApplication).releaseAuthComponent()
                } else {
                    splashGone()
                }
            }
        })

    }
    override fun expandAppBar() {
        // ignore
    }
    override fun displayProgressBar(isAnyActiveJob: Boolean) {
        if (isAnyActiveJob) progress_bar.visibility = View.VISIBLE
        else progress_bar.visibility = View.GONE
    }
    private fun splashGone(){
        fragment_container.visibility = View.VISIBLE
        splash_logo.visibility = View.GONE
    }
    private fun emitCheckPreviousAuthUser() = viewModel.setStateEvent(AuthStateEvent.CheckPreviousAuthEvent())
}
