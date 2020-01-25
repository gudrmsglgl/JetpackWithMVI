package com.fastival.jetpackwithmviapp.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NavigationRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.di.Injectable
import com.fastival.jetpackwithmviapp.ui.DataStateChangeListener
import com.fastival.jetpackwithmviapp.ui.UICommunicationListener
import com.fastival.jetpackwithmviapp.viewmodels.ViewModelProviderFactory
import com.wada811.databinding.dataBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseMainFragment<vb: ViewDataBinding, vm: BaseViewModel<*,*>>
    (@LayoutRes contentLayoutId: Int)
    : Fragment(contentLayoutId), Injectable {

    val TAG = "AppDebug"

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var provider: ViewModelProviderFactory

    internal lateinit var stateListener: DataStateChangeListener
    internal lateinit var uiCommunicationListener: UICommunicationListener

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

        // You can use binding ( data_binding_ktx)
        binding.setVariable(getBindingVariable(), viewModel)

        cancelActiveJobs()

        initFunc()
        subscribeObservers()

        setupActionBarWithNavController(setTopLevelDesId(), activity as AppCompatActivity)
    }

    fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateListener = context as DataStateChangeListener
        }catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }

        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }
    }

    private fun setupActionBarWithNavController(@IdRes fragmentId: Int, activity: AppCompatActivity)  {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

    @IdRes
    protected abstract fun setTopLevelDesId(): Int

    protected abstract fun getBindingVariable(): Int

    protected abstract fun initFunc()

    protected abstract fun getViewModel(): Class<vm>

    protected abstract fun subscribeObservers()
}