package com.fastival.jetpackwithmviapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.api.auth.network_response.LoginResponse
import com.fastival.jetpackwithmviapp.api.auth.network_response.RegistrationResponse
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.repository.auth.AuthRepository
import com.fastival.jetpackwithmviapp.ui.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent.*
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthViewState
import com.fastival.jetpackwithmviapp.ui.auth.state.LoginFields
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import com.fastival.jetpackwithmviapp.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject constructor(val authRepository: AuthRepository): BaseViewModel<AuthStateEvent, AuthViewState>()
{

    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        return when(stateEvent) {
            is LoginAttempEvent -> {
                AbsentLiveData.create()
            }

            is RegisterAttemptEvent -> {
                AbsentLiveData.create()
            }

            is CheckPreviousAuthEvent -> {
                AbsentLiveData.create()
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
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) return
        update.authToken = authToken
        _viewState.value = update
    }

}