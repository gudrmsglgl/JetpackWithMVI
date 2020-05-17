package com.fastival.jetpackwithmviapp.ui.auth

import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.repository.auth.AuthRepository
import com.fastival.jetpackwithmviapp.ui.BaseViewModel
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthStateEvent.*
import com.fastival.jetpackwithmviapp.ui.auth.state.AuthViewState
import com.fastival.jetpackwithmviapp.ui.auth.state.LoginFields
import com.fastival.jetpackwithmviapp.ui.auth.state.RegistrationFields
import com.fastival.jetpackwithmviapp.util.StateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@AuthScope
class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
): BaseViewModel<AuthViewState>()
{

    override fun setStateEvent(stateEvent: StateEvent) =
        launchJob(
            stateEvent = stateEvent,
            repositoryFunc = when (stateEvent){

                is LoginAttemptEvent -> {
                    authRepository.attemptLogin(
                        stateEvent = stateEvent,
                        email = stateEvent.email,
                        password = stateEvent.password
                    )
                }

                is RegisterAttemptEvent -> {
                    authRepository.attemptRegistration(
                        stateEvent = stateEvent,
                        email = stateEvent.email,
                        username = stateEvent.username,
                        password = stateEvent.password,
                        confirmPassword = stateEvent.confirm_password
                    )
                }

                is CheckPreviousAuthEvent -> {
                    authRepository.checkPreviousAuthUser(stateEvent)
                }

                else -> {
                    flow {
                        emit(retInvalidEvent(stateEvent))
                    }
                }

            }
        )

    override fun handleViewState(data: AuthViewState) {
        data.authToken?.let { authToken ->
            setViewStateAuthToken(authToken)
        }
    }

    override fun initNewViewState(): AuthViewState = AuthViewState()

    fun setViewStateRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        setViewState(update)
    }

    fun setViewStateLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.loginFields == loginFields) {
            return
        }
        update.loginFields = loginFields
        setViewState(update)
    }

    fun setViewStateAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) return
        update.authToken = authToken
        setViewState(update)
    }

}