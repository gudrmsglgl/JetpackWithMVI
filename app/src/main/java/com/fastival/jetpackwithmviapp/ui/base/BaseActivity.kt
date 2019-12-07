package com.fastival.jetpackwithmviapp.ui.base

import android.os.Bundle
import android.util.Log
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity<vb: ViewDataBinding, vm: BaseViewModel<*, *>>: DaggerAppCompatActivity(){

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
        initBinding()
        subscribeObservers()
    }

    protected open fun initBinding(){
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        binding.lifecycleOwner = this
        binding.setVariable(getBindingVariable(), viewModel)
        binding.executePendingBindings()
        Log.d(TAG, "baseActivity_initBinding()")
    }

    protected abstract fun getBindingVariable(): Int

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun getViewModel(): Class<vm>

    protected abstract fun subscribeObservers()
}