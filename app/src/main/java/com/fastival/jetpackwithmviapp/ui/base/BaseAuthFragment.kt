package com.fastival.jetpackwithmviapp.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.ui.DataStateChangeListener
import com.fastival.jetpackwithmviapp.ui.auth.AuthViewModel
import com.fastival.jetpackwithmviapp.viewmodels.InjectingSavedStateViewModelFactory
import com.wada811.databinding.dataBinding
import javax.inject.Inject

abstract class BaseAuthFragment<vb: ViewDataBinding>(
    @LayoutRes contentId: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): Fragment(contentId)
{

    val TAG = "AppDebug"

    protected lateinit var stateListener: DataStateChangeListener

    internal val binding: vb by dataBinding()

    val viewModel: AuthViewModel by viewModels { viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setVariable(getBindingVariable(), viewModel)

        initFunc()
        subscribeObservers()

    }


    private fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateListener = context as DataStateChangeListener
        }catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }


    protected abstract fun getBindingVariable(): Int

    protected abstract fun initFunc()

    protected abstract fun subscribeObservers()
}