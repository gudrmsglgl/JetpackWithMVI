package com.fastival.jetpackwithmviapp.ui.base

import android.Manifest.*
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.extension.activity.*
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.*
import com.fastival.jetpackwithmviapp.util.Constants.Companion.PERMISSIONS_REQUEST_READ_STORAGE
import com.fastival.jetpackwithmviapp.viewmodels.InjectingSavedStateViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity<vb: ViewDataBinding>
    : AppCompatActivity(), DataStateChangeListener, UICommunicationListener
{

    val TAG: String = "AppDebug"

    abstract fun inject()

    @Inject
    lateinit var sessionManager: SessionManager

    protected lateinit var binding: vb

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        initVariables()
        subscribeObservers()
    }

    protected open fun initVariables(){
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        Log.d(TAG, "baseActivity_initBinding()")
    }


    override fun onUIMessageReceived(uiMessage: UIMessage) {
        when(uiMessage.uiMessageType){

            is UIMessageType.AreYouSureDialog -> {
                areYouSureDialog(
                    message = uiMessage.message,
                    callback = uiMessage.uiMessageType.callback
                )
            }

            is UIMessageType.Dialog -> {
                displayInfoDialog(uiMessage.message)
            }

            is UIMessageType.Toast -> {
                displayToast(uiMessage.message)
            }

            is UIMessageType.None -> {
                Log.i(TAG, "onUIMessageReceived: ${uiMessage.message}")
            }
        }
    }

    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let {
            GlobalScope.launch(Dispatchers.Main){
                displayProgressBar(it.loading.isLoading)

                it.error?.let { errorEvent ->
                    handleStateError(errorEvent)
                }

                it.data?.response?.let {
                    handleStateResponse(it)
                }

            }
        }
    }

    private fun handleStateError(event: Event<StateError>){
        event.getContentIfNotHandled()?.let {
            when(it.response.responseType) {
                is ResponseType.Dialog -> {
                    it.response.message?.let {msg-> displayErrorDialog(msg) }
                }

                is ResponseType.Toast -> {
                    it.response.message?.let { msg -> displayToast(msg) }
                }

                is ResponseType.None -> {
                    Log.i(TAG, "handleStateError: ${it.response.message}")
                }

            }
        }
    }

    private fun handleStateResponse(event: Event<Response>) {
        event.getContentIfNotHandled()?.let {

            when(it.responseType) {
                is ResponseType.Toast -> {
                    it.message?.let { msg -> displayToast(msg) }
                }

                is ResponseType.Dialog -> {
                    it.message?.let { msg -> displaySuccessDialog(msg) }
                }

                is ResponseType.None -> {
                    Log.i(TAG, "handleStateResponse: ${it.message}")
                }
            }
        }
    }

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun subscribeObservers()

    abstract fun displayProgressBar(bool: Boolean)

    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager
                .hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun isStoragePermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                this,
                permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_READ_STORAGE
            )

            return false
        } else {
            // Permission has already been granted
            return true
        }
    }


}