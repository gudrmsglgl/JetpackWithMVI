package com.fastival.jetpackwithmviapp.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.di.Injectable
import com.fastival.jetpackwithmviapp.ui.DataStateChangeListener
import com.fastival.jetpackwithmviapp.viewmodels.ViewModelProviderFactory
import com.wada811.databinding.dataBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseAuthFragment<vb: ViewDataBinding, vm: BaseViewModel<*,*>>
    (@LayoutRes contentId: Int)
    : Fragment(contentId), Injectable {

    val TAG = "AppDebug"

    @Inject
    lateinit var provider: ViewModelProviderFactory

    protected lateinit var stateListener: DataStateChangeListener

    internal val binding: vb by dataBinding()
    internal lateinit var viewModel: vm

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = activity?.let {
            ViewModelProvider(it, provider).get(getViewModel())
        }?:throw Exception("Invalid Activity")

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setVariable(getBindingVariable(), viewModel)

        cancelActiveJobs()

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

    protected abstract fun getViewModel(): Class<vm>

    protected abstract fun subscribeObservers()
}