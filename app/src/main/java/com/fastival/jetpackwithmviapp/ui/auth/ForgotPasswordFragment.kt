package com.fastival.jetpackwithmviapp.ui.auth


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentForgotPasswordBinding
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.Response
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@FlowPreview
@ExperimentalCoroutinesApi
@AuthScope
class ForgotPasswordFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : BaseAuthFragment<FragmentForgotPasswordBinding>(R.layout.fragment_forgot_password, viewModelFactory)
{

    private val webView: WebView by lazy {
        binding.root.findViewById<WebView>(R.id.webview)
    }

    private val webInteractionCallback = object : WebAppInterface.OnWebInteractionCallback{

        override fun onSuccess(email: String) {
            Log.e(TAG, "WebAppInterface.onSuccess: a reset link will be sent to $email.")
            onPasswordResetLinkSent()
        }

        override fun onError(errorMessage: String) {
            Log.e(TAG, "WebAppInterface.onError: $errorMessage")

            uiCommunicationListener
                .onResponseReceived(
                    response = Response(
                        message = errorMessage,
                        uiComponentType = UIComponentType.Dialog,
                        messageType = MessageType.Error
                    ),
                    stateMessageCallback = object : StateMessageCallback{
                        override fun removeMessageFromStack() {
                            viewModel.removeStateMessage()
                        }
                    })
        }

        override fun onLoading(isLoading: Boolean) {
            Log.e(TAG, "WebAppInterface.onLoading...")
            uiCommunicationListener.displayProgressBar(isLoading)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPasswordResetWebView()

        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPasswordResetWebView() {

        uiCommunicationListener.displayProgressBar(true)

        webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                uiCommunicationListener.displayProgressBar(false)
            }
        }

        webView.loadUrl(Constants.PASSWORD_RESET_URL)
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebAppInterface(webInteractionCallback), "AndroidTextListener")

    }

    fun onPasswordResetLinkSent(){
        CoroutineScope(Dispatchers.Main).launch {
            parent_view.removeView(webView)
            webView.destroy()

            val anim = TranslateAnimation(
                password_reset_done_container.width.toFloat(),
                0f,
                0f,
                0f
            )
            anim.duration = 500
            password_reset_done_container.startAnimation(anim)
            password_reset_done_container.visibility = View.VISIBLE
        }
    }


    class WebAppInterface
    constructor(private val callback: OnWebInteractionCallback){

        private val TAG: String = "AppDebug"

        @JavascriptInterface
        fun onSuccess(email: String) {
            callback.onSuccess(email)
        }

        @JavascriptInterface
        fun onError(errorMessage: String) {
            callback.onError(errorMessage)
        }

        @JavascriptInterface
        fun onLoading(isLoading: Boolean) {
            callback.onLoading(isLoading)
        }

        interface OnWebInteractionCallback{
            fun onSuccess(email: String)
            fun onError(errorMessage: String)
            fun onLoading(isLoading: Boolean)
        }
    }


}
