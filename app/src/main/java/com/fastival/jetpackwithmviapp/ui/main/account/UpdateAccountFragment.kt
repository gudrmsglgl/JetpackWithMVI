package com.fastival.jetpackwithmviapp.ui.main.account


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentUpdateAccountBinding
import com.fastival.jetpackwithmviapp.models.AccountProperties
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.base.account.BaseAccountFragment
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_update_account.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class UpdateAccountFragment
@Inject
constructor(
    private val provider: ViewModelProvider.Factory
): BaseAccountFragment<FragmentUpdateAccountBinding>(R.layout.fragment_update_account, provider)
{

    fun subscribeObservers() {

        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            if (dataState != null) {

                Log.d(TAG, "UpdateAccountFragment, DataState: ${dataState}")
                stateListener.onDataStateChange(dataState)

            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeObservers()
    }

    private fun saveChange(){
        viewModel.setStateEvent(
            AccountStateEvent.UpdateAccountPropertiesEvent(
                input_email.text.toString(),
                input_username.text.toString()
            )
        )
        stateListener.hideSoftKeyboard()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save -> {
                saveChange()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getVariableId(): Int = BR.vm

}
