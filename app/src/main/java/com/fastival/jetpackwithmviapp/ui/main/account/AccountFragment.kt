package com.fastival.jetpackwithmviapp.ui.main.account


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentAccountBinding
import com.fastival.jetpackwithmviapp.models.AccountProperties
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment
    : BaseMainFragment<FragmentAccountBinding, AccountViewModel>(R.layout.fragment_account) {

    override fun setTopLevelDesId(): Int = R.id.accountFragment

    override fun getBindingVariable(): Int {
        return BR.vm
    }

    override fun initFunc() {
    }

    override fun getViewModel(): Class<AccountViewModel> {
        return AccountViewModel::class.java
    }

    override fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState->
            if (dataState != null) {

                stateListener.onDataStateChange(dataState)
                dataState.data?.data?.let { event ->
                    event.getContentIfNotHandled()?.accountProperties?.let { accountProperties ->
                        Log.d(TAG, "AccountFragment, DataState: $accountProperties")
                        viewModel.setAccountPropertiesData(accountProperties)
                    }
                }

            }
        })

        // replace xml liveData
        /*viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState->
            if (viewState != null) {
                viewState.accountProperties?.let {
                    Log.d(TAG, "AccountFragment, ViewState: $it")
                    setAccountDataFields(it)
                }
            }
        })*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        change_password.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.setStateEvent(AccountStateEvent.GetAccountPropertiesEvent())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.edit -> {
                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
