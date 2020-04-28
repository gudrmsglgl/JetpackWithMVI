package com.fastival.jetpackwithmviapp.ui.main.account


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentUpdateAccountBinding
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountStateEvent
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import kotlinx.android.synthetic.main.fragment_update_account.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
@FlowPreview
@ExperimentalCoroutinesApi
@MainScope
class UpdateAccountFragment
@Inject
constructor(
    private val provider: ViewModelProvider.Factory
): BaseAccountFragment<FragmentUpdateAccountBinding>(R.layout.fragment_update_account, provider)
{


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel

    }

    override fun observeStateMessage() = viewModel.stateMessage
        .observe(viewLifecycleOwner,
            Observer { stateMessage ->
                stateMessage?.let {

                    uiCommunicationListener.onResponseReceived(
                        response = it.response,
                        stateMessageCallback = object: StateMessageCallback{

                            override fun removeMessageFromStack() {
                                viewModel.removeStateMessage()
                            }

                        }
                    )

                }
            })

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.update_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.save -> {
                saveChange()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveChange(){
        viewModel.setStateEvent(
            AccountStateEvent.UpdateAccountPropertiesEvent(
                input_email.text.toString(),
                input_username.text.toString()
            )
        )
        uiCommunicationListener.hideSoftKeyboard()
    }

}
