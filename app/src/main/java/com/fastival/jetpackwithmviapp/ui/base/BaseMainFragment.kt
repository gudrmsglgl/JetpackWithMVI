package com.fastival.jetpackwithmviapp.ui.base

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
import com.bumptech.glide.RequestManager
import com.fastival.jetpackwithmviapp.ui.DataStateChangeListener
import com.fastival.jetpackwithmviapp.ui.UICommunicationListener
import javax.inject.Inject

abstract class BaseMainFragment(
    @LayoutRes contentLayoutId: Int
): Fragment(contentLayoutId)
{

    val TAG = "AppDebug"

    internal lateinit var stateListener: DataStateChangeListener
    internal lateinit var uiCommunicationListener: UICommunicationListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBarWithNavController(setTopLevelDesId(), activity as AppCompatActivity)
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

}