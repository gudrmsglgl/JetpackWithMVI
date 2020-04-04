package com.fastival.jetpackwithmviapp.ui.main.account


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.fastival.jetpackwithmviapp.BR

import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.databinding.FragmentChangePasswordBinding
import com.fastival.jetpackwithmviapp.extension.editToString
import com.fastival.jetpackwithmviapp.ui.EmptyViewModel
import com.fastival.jetpackwithmviapp.ui.base.BaseMainFragment
import com.fastival.jetpackwithmviapp.ui.base.account.BaseAccountFragment
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountStateEvent
import com.fastival.jetpackwithmviapp.util.StateMessageCallback
import com.fastival.jetpackwithmviapp.util.SuccessHandling.Companion.RESPONSE_PASSWORD_UPDATE_SUCCESS
import kotlinx.android.synthetic.main.fragment_change_password.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ChangePasswordFragment
@Inject
constructor(
    private val provider: ViewModelProvider.Factory
): BaseAccountFragment<FragmentChangePasswordBinding>(R.layout.fragment_change_password, provider)
{


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        update_password_button.setOnClickListener {

            viewModel.setStateEvent(AccountStateEvent.ChangePasswordEvent(
                input_current_password.editToString(),
                input_new_password.editToString(),
                input_confirm_new_password.editToString()
            ))

        }
    }

    override fun observeStateMessage() =
        viewModel
            .stateMessage
            .observe( viewLifecycleOwner, Observer { stateMessage ->

                stateMessage?.let {

                    if (it.response.message == RESPONSE_PASSWORD_UPDATE_SUCCESS){

                        uiCommunicationListener.hideSoftKeyboard()
                        findNavController().popBackStack()

                    }

                    uiCommunicationListener
                        .onResponseReceived(
                            response = it.response,
                            stateMessageCallback = object: StateMessageCallback{

                                override fun removeMessageFromStack() {
                                    viewModel.removeStateMessage()
                                }

                            }
                        )

                }

            })
}
