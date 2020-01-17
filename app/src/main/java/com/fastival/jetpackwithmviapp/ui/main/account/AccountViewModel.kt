package com.fastival.jetpackwithmviapp.ui.main.account

import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.models.AccountProperties
import com.fastival.jetpackwithmviapp.repository.main.AccountRepository
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.Loading
import com.fastival.jetpackwithmviapp.ui.base.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountStateEvent
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountViewState
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
): BaseViewModel<AccountStateEvent, AccountViewState>()
{
    // for xml val viewState.accountProperty.XXX
    override val viewState: LiveData<AccountViewState>
        get() = super.viewState


    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent) {
            is AccountStateEvent.GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                }?: AbsentLiveData.create()
            }

            is AccountStateEvent.UpdateAccountPropertiesEvent-> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        val newAccountProperties = AccountProperties(
                            pk,
                            stateEvent.email,
                            stateEvent.username
                        )
                        accountRepository.saveAccountProperties(
                            authToken,
                            newAccountProperties
                        )
                    }
                }?: AbsentLiveData.create()
            }

            is AccountStateEvent.ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmNewPassword)
                }?: AbsentLiveData.create()
            }

            is AccountStateEvent.None -> {
                return object: LiveData<DataState<AccountViewState>>(){
                    override fun onActive() {
                        super.onActive()
                        value = DataState(null, Loading(false), null)
                    }
                }
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties) {
        val update = getCurrentViewStateOrNew()
        if (update.accountProperties == accountProperties) {
            return
        }
        update.accountProperties = accountProperties
        setViewState(update)
    }

    fun logout(){
        sessionManager.logout()
    }

    override fun cancelActiveJobs() {
        accountRepository.cancelActiveJobs()
        handlePendingData() // hide progress bar
    }

    private fun handlePendingData(){
        setStateEvent(AccountStateEvent.None())
    }
}