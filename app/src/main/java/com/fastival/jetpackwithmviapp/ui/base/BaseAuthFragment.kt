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
import com.fastival.jetpackwithmviapp.ui.auth.AuthViewModel
import com.fastival.jetpackwithmviapp.ui.auth.state.AUTH_VIEW_STATE_BUNDLE_KEY
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthViewState
import com.fastival.jetpackwithmviapp.viewmodels.InjectingSavedStateViewModelFactory
import com.fastival.jetpackwithmviapp.viewmodels.ViewModelProviderFactory
import com.wada811.databinding.dataBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseAuthFragment<vb: ViewDataBinding, vm: BaseViewModel<*,*>>
    (@LayoutRes contentId: Int)
    : Fragment(contentId), Injectable {

    val TAG = "AppDebug"

    /*@Inject
    lateinit var provider: ViewModelProviderFactory*/
    @Inject
    lateinit var defaultFactory: InjectingSavedStateViewModelFactory

    protected lateinit var stateListener: DataStateChangeListener

    internal val binding: vb by dataBinding()
    internal lateinit var viewModel: vm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {

            val factory =
                defaultFactory.create(this, arguments)

            viewModel = if (isViewModelInitialized()) {
                viewModel
            } else {
                ViewModelProvider(this, factory).get(getViewModel())
            }

        }?:throw Exception("Invalid Activity")

        cancelActiveJobs()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.setVariable(getBindingVariable(), viewModel)

        initFunc()
        subscribeObservers()

    }

    fun isViewModelInitialized() = ::viewModel.isInitialized

    private fun cancelActiveJobs(){
        if (isViewModelInitialized()) {
            viewModel.cancelActiveJobs()
        }
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