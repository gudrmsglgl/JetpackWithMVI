package com.fastival.jetpackwithmviapp.ui.main.account

import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.models.AccountProperties
import com.fastival.jetpackwithmviapp.repository.main.AccountRepositoryImpl
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountStateEvent
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountViewState
import com.fastival.jetpackwithmviapp.util.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepositoryImpl
): BaseViewModel<AccountViewState>()
{
    // for xml val viewState.accountProperty.XXX
    override val viewState: LiveData<AccountViewState>
        get() = super.viewState


    override fun setStateEvent(stateEvent: StateEvent) {

        sessionManager.cachedToken.value?.let { authToken ->

            launchJob(
                stateEvent = stateEvent,
                jobFunc = when(stateEvent) {

                    is AccountStateEvent.GetAccountPropertiesEvent -> {
                        accountRepository
                            .getAccountProperties(stateEvent = stateEvent, authToken = authToken)
                    }

                    is AccountStateEvent.UpdateAccountPropertiesEvent -> {
                        accountRepository
                            .saveAccountProperties(
                                stateEvent = stateEvent,
                                authToken = authToken,
                                email = stateEvent.email,
                                username = stateEvent.username
                            )
                    }

                    is AccountStateEvent.ChangePasswordEvent -> {
                        accountRepository
                            .updatePassword(
                                stateEvent = stateEvent,
                                authToken = authToken,
                                currentPassword = stateEvent.currentPassword,
                                newPassword = stateEvent.newPassword,
                                confirmNewPassword = stateEvent.confirmNewPassword
                            )
                    }

                    else -> {
                        flow{
                            emit(retInvalidEvent(stateEvent))
                        }
                    }
                }
            )

        }

    }

    override fun handleViewState(data: AccountViewState) {
        data.accountProperties?.let {
            setAccountPropertiesData(it)
        }
    }

    override fun initNewViewState(): AccountViewState = AccountViewState()

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        setViewState(update)
    }

    fun logout() = sessionManager.logout()

}