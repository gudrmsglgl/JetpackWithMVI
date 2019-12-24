package com.fastival.jetpackwithmviapp.ui.auth


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentForgotPasswordBinding
import com.fastival.jetpackwithmviapp.ui.*
import com.fastival.jetpackwithmviapp.ui.base.BaseFragment
import com.fastival.jetpackwithmviapp.util.Constants
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : BaseFragment<FragmentForgotPasswordBinding, EmptyViewModel>() {

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

            val dataState = DataState.error<Any>(
                response = Response(errorMessage, ResponseType.Dialog())
            )
            stateListener.onDataStateChange(
                dataState = dataState
            )
        }

        override fun onLoading(isLoading: Boolean) {
            Log.e(TAG, "WebAppInterface.onLoading...")
            CoroutineScope(Dispatchers.Main).launch {
                stateListener.onDataStateChange(
                    DataState.loading(isLoading, null)
                )
            }
        }
    }

    override fun getBindingVariable(): Int {
        return BR.empty
    }

    override fun initFunc() {
        loadPasswordResetWebView()

        return_to_launcher_fragment.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadPasswordResetWebView() {

        stateListener.onDataStateChange(
            DataState.loading(true,null)
        )
        webView.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                stateListener.onDataStateChange(
                    DataState.loading(false, null)
                )
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

    override fun getLayoutId(): Int {
        return R.layout.fragment_forgot_password
    }

    override fun getViewModel(): Class<EmptyViewModel> {
        return EmptyViewModel::class.java
    }

    override fun subscribeObservers() {
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
