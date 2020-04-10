package com.fastival.jetpackwithmviapp.ui.auth

import com.fastival.jetpackwithmviapp.ui.UICommunicationListener
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.wada811.databinding.dataBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseAuthFragment<vb: ViewDataBinding>(
    @LayoutRes layoutRes: Int,
    private val viewModelFactory: ViewModelProvider.Factory
): Fragment(layoutRes)
{

    val TAG = "AppDebug"

    internal val binding: vb by dataBinding()

    val viewModel: AuthViewModel by viewModels { viewModelFactory }

    lateinit var uiCommunicationListener: UICommunicationListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChannel()
    }

    private fun setupChannel() = viewModel.setUpChannel()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement com.fastival.jetpackwithmviapp.ui.UICommunicationListener" )
        }
    }


}