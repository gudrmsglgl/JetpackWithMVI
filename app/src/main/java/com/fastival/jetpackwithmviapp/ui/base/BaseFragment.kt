package com.fastival.jetpackwithmviapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<vb: ViewDataBinding, vm: BaseViewModel<*,*>>: DaggerFragment() {

    val TAG = "AppDebug"

    @Inject
    lateinit var provider: ViewModelProviderFactory

    protected lateinit var binding: vb
    protected lateinit var viewModel: vm

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activity?.let {
            ViewModelProvider(it, provider).get(getViewModel())
        }?:throw Exception("Invalid Activity")

        binding.setVariable(getBindingVariable(), viewModel)

        initFunc()
        subscribeObservers()
    }

    protected abstract fun getBindingVariable(): Int

    protected abstract fun initFunc()

    @LayoutRes
    protected abstract fun getLayoutId(): Int

    protected abstract fun getViewModel(): Class<vm>

    protected abstract fun subscribeObservers()
}