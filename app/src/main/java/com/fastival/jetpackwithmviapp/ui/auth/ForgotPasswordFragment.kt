package com.fastival.jetpackwithmviapp.ui.auth


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.DataStateChangeListener

/**
 * A simple [Fragment] subclass.
 */
class ForgotPasswordFragment : BaseAuthFragment() {

    lateinit var webView: WebView

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Main", "ForgotPasswordFragment: $viewModel")
        
        //loadPasswordResetWebView()

    }



}
