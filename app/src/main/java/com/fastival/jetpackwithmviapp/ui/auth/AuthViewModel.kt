package com.fastival.jetpackwithmviapp.ui.auth

import android.util.Log
import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.repository.auth.AuthRepository
import com.fastival.jetpackwithmviapp.ui.base.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent.*
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthViewState
import com.fastival.jetpackwithmviapp.ui.auth.state.LoginFields
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import javax.inject.Inject

class AuthViewModel
@Inject constructor(val authRepository: AuthRepository): BaseViewModel<AuthStateEvent, AuthViewState>()
{

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        return when(stateEvent) {
            is LoginAttemptEvent -> {
               authRepository.attemptLogin(stateEvent.email, stateEvent.password)
            }

            is RegisterAttemptEvent -> {
               authRepository.attemptRegistration(
                   stateEvent.email,
                   stateEvent.username,
                   stateEvent.password,
                   stateEvent.confirm_password)
            }

            is CheckPreviousAuthEvent -> {
                authRepository.checkPreviousAuthUser()
            }
        }
    }

    override fun initNewViewState(): AuthViewState = AuthViewState()

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        _viewState.value = update
    }

    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        _viewState.value = update
    }

    fun setAuthToken(authToken: AuthToken) {
        Log.d(TAG, "authToken: $authToken")
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) return
        update.authToken = authToken
        _viewState.value = update
    }

    fun cancelActiveJobs(){
        authRepository.cancelActiveJobs()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

}