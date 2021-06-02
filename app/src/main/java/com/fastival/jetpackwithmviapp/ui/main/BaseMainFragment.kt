package com.fastival.jetpackwithmviapp.ui.main

import com.fastival.jetpackwithmviapp.ui.UICommunicationListener
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.fastival.jetpackwithmviapp.ui.BaseViewModel
import com.fastival.jetpackwithmviapp.util.StateMessage
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseMainFragment(
    @LayoutRes contentLayoutId: Int
): Fragment(contentLayoutId)
{

    val TAG = "AppDebug"

    internal lateinit var uiCommunicationListener: UICommunicationListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(setTopLevelDesId(), activity as AppCompatActivity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement com.fastival.jetpackwithmviapp.ui.UICommunicationListener" )
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

    fun showStateMessage(
        stateMessage: StateMessage,
        viewModel: BaseViewModel<*>
    ) = uiCommunicationListener.onResponseReceived(
        response = stateMessage.response,
        stateMessageCallback = object: StateMessageCallback{
            override fun removeMessageFromStack() {
                viewModel.removeStateMessage()
            }
        }
    )

    @IdRes
    protected abstract fun setTopLevelDesId(): Int
}