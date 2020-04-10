package com.fastival.jetpackwithmviapp.repository.main

import android.util.Log
import com.fastival.jetpackwithmviapp.api.GenericResponse
import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.di.main.MainScope
import com.fastival.jetpackwithmviapp.models.AccountProperties
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.repository.NetworkBoundResource
import com.fastival.jetpackwithmviapp.extension.safeApiCall
import com.fastival.jetpackwithmviapp.session.SessionManager
import com.fastival.jetpackwithmviapp.ui.main.account.state.AccountViewState
import com.fastival.jetpackwithmviapp.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@FlowPreview
@MainScope
class AccountRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
): AccountRepository
{

    private val TAG: String = "AppDebug"

    override fun getAccountProperties(
        authToken: AuthToken,
        stateEvent: StateEvent
    ): Flow<DataState<AccountViewState>> =
        object : NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
            dispatcher = Dispatchers.IO,
            stateEvent = stateEvent,
            dbQuery = {
                accountPropertiesDao.searchByPk(authToken.account_pk!!)
            },
            apiCall = {
                openApiMainService.getAccountProperties(authToken.transHeaderAuthorization())
            },
            updateCache = { networkData, dispatcher ->
                Log.d(TAG, "updateCache: $networkData")

                withContext(dispatcher) {
                    accountPropertiesDao.updateAccountProperties(
                        networkData.pk,
                        networkData.email,
                        networkData.username
                    )
                }
            },
            fetchCacheData = { cacheData, event ->
                DataState.data(
                    response = null,
                    data = AccountViewState(
                        accountProperties = cacheData
                    ),
                    stateEvent = event
                )
            }
        ){}.result


    override fun saveAccountProperties(
        authToken: AuthToken,
        email: String,
        username: String,
        stateEvent: StateEvent
    ): Flow<DataState<AccountViewState>> = flow{

        val responseUpdateProperties =
            safeApiCall(Dispatchers.IO) {
                openApiMainService.saveAccountProperties(
                    authToken.transHeaderAuthorization(),
                    email,
                    username
                )
            }

        emit(
            resUpdateProperties2DataState(responseUpdateProperties, stateEvent, authToken)
        )

    }


    private suspend fun resUpdateProperties2DataState(
        apiResult: ApiResult<GenericResponse?>,
        stateEvent: StateEvent,
        authToken: AuthToken
    ): DataState<AccountViewState> =
        object: ApiResponseHandler<AccountViewState, GenericResponse>(
            response = apiResult,
            stateEvent = stateEvent
        ){

            override suspend fun handleApiResultSuccess(
                networkObj: GenericResponse
            ): DataState<AccountViewState> {

                withContext(Dispatchers.IO) {

                    val updatedAccountProperties =
                        openApiMainService.getAccountProperties(authToken.transHeaderAuthorization())

                    accountPropertiesDao.updateAccountProperties(
                        pk = updatedAccountProperties.pk,
                        email = updatedAccountProperties.email,
                        username = updatedAccountProperties.username
                    )

                }

                return DataState.data(
                    data = null,
                    response = Response(
                        message = networkObj.response,
                        uiComponentType = UIComponentType.Toast,
                        messageType = MessageType.Success
                    ),
                    stateEvent = stateEvent
                )
            }

        }.getResult()


    override fun updatePassword(
        authToken: AuthToken,
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
        stateEvent: StateEvent
    ): Flow<DataState<AccountViewState>> = flow {

        val responseUpdatePwd =
            safeApiCall(Dispatchers.IO) {
                openApiMainService.updatePassword(
                    authToken.transHeaderAuthorization(),
                    currentPassword,
                    newPassword,
                    confirmNewPassword
                )
            }

        emit(
            resUpdatePwd2DataState(responseUpdatePwd, stateEvent)
        )

    }

    private suspend fun resUpdatePwd2DataState(
        apiResult: ApiResult<GenericResponse?>,
        stateEvent: StateEvent
    ): DataState<AccountViewState> = object: ApiResponseHandler<AccountViewState, GenericResponse>(
        response = apiResult,
        stateEvent = stateEvent
    ){

        override suspend fun handleApiResultSuccess(
            networkObj: GenericResponse
        ): DataState<AccountViewState> =
            
            DataState.data(
                data = null,
                response = Response(
                    message = networkObj.response,
                    uiComponentType = UIComponentType.Toast,
                    messageType = MessageType.Success
                ),
                stateEvent = stateEvent
            )

    }.getResult()


}