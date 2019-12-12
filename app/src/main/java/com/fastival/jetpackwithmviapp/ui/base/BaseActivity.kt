package com.fastival.jetpackwithmviapp.ui.base

import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.extension.displayErrorDialog
import com.fastival.jetpackwithmviapp.extension.displaySuccessDialog
import com.fastival.jetpackwithmviapp.extension.displayToast
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.*
import com.fastival.jetpackwithmviapp.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity<vb: ViewDataBinding, vm: BaseViewModel<*, *>>: DaggerAppCompatActivity(),
DataStateChangeListener{

    val TAG: String = "AppDebug"

    @Inject
    lateinit var provider: ViewModelProviderFactory

    @Inject
    lateinit var sessionManager: SessionManager

    protected lateinit var viewModel: vm
    protected lateinit var binding: vb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        this.viewModel = if(::viewModel.isInitialized){
            Log.d(TAG, "reuse viewModel")
            viewModel
        } else {
            Log.d(TAG, "init viewModel")
            ViewModelProvider(this, provider).get(getViewModel())
        }

        Log.d(TAG, "viewModel: $viewModel")
        initVariables()
        initFunc()
        subscribeObservers()
    }

    protected open fun initVariables(){
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        binding.setVariable(getBindingVariable(), viewModel)
        binding.executePendingBindings()
        Log.d(TAG, "baseActivity_initBinding()")
    }

    protected open fun initFunc(){

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

    protected abstract fun getBindingVariable(): Int

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun getViewModel(): Class<vm>

    protected abstract fun subscribeObservers()

    abstract fun displayProgressBar(bool: Boolean)
}