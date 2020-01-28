package com.fastival.jetpackwithmviapp.ui.base

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.di.Injectable
import com.fastival.jetpackwithmviapp.ui.DataStateChangeListener
import com.fastival.jetpackwithmviapp.ui.UICommunicationListener
import com.fastival.jetpackwithmviapp.viewmodels.InjectingSavedStateViewModelFactory
import javax.inject.Inject

abstract class BaseMainFragment<vm: BaseViewModel<*,*>>
    (@LayoutRes contentLayoutId: Int)
    : Fragment(contentLayoutId), Injectable {

    val TAG = "AppDebug"

    @Inject
    lateinit var requestManager: RequestManager

    /*@Inject
    lateinit var provider: ViewModelProviderFactory*/
    @Inject
    lateinit var defaultViewModelFactory: InjectingSavedStateViewModelFactory

    internal lateinit var stateListener: DataStateChangeListener
    internal lateinit var uiCommunicationListener: UICommunicationListener

    internal lateinit var viewModel: vm


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.run {

            val factory =
                defaultViewModelFactory.create(this, arguments)

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

        subscribeObservers()

        setupActionBarWithNavController(setTopLevelDesId(), activity as AppCompatActivity)
    }

    fun cancelActiveJobs() {
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

    fun isViewModelInitialized() = ::viewModel.isInitialized


    @IdRes
    protected abstract fun setTopLevelDesId(): Int

    protected abstract fun getBindingVariable(): Int

    protected abstract fun getViewModel(): Class<vm>

    protected abstract fun subscribeObservers()
}