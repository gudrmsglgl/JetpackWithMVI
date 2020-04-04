package com.fastival.jetpackwithmviapp.repository

import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.NETWORK_ERROR
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

@FlowPreview
open class NetworkBoundResource<NetworkObj, CacheObj, ViewState>(
    val dispatcher: CoroutineDispatcher,
    private val stateEvent: StateEvent,
    private val dbQuery: suspend () -> CacheObj?,
    private val fetchCacheData: (CacheObj, StateEvent?) -> DataState<ViewState>,
    private val apiCall: suspend () -> NetworkObj?,
    private val updateCache: suspend (NetworkObj, CoroutineDispatcher) -> Unit
)
{

    private val TAG: String = "AppDebug"

    val result: Flow<DataState<ViewState>> = flow {

        // STEP 1: VIEW CACHE
        emit(retCacheDataState(markJobComplete = false))


        // STEP 2: MAKE NETWORK CALL, SAVE RESULT TO CACHE OR Emit ERROR
        val apiResult = safeApiCall(dispatcher){apiCall.invoke()}

        resultSaveCacheOrEmitError(this, apiResult)

        // STEP 3: VIEW CACHE and MARK JOB COMPLETED
        emit(retCacheDataState(markJobComplete = true))

    }


    private suspend fun resultSaveCacheOrEmitError(
        flowCollector: FlowCollector<DataState<ViewState>>,
        apiResult: ApiResult<NetworkObj?>
    ) = when (apiResult){

            is ApiResult.Success -> {

                if (apiResult.value == null) {
                    flowCollector.emit(
                        buildError(
                            UNKNOWN_ERROR,
                            UIComponentType.Dialog,
                            stateEvent
                        )
                    )
                }

                else {
                    updateCache.invoke(apiResult.value, dispatcher)
                }

            }

            is ApiResult.GenericError -> {
                flowCollector.emit(
                    buildError(
                        apiResult.errorMessage?.let { it } ?: UNKNOWN_ERROR,
                        UIComponentType.Dialog,
                        stateEvent
                    )
                )
            }

            is ApiResult.NetworkError -> {
                flowCollector.emit(
                    buildError(
                        NETWORK_ERROR,
                        UIComponentType.Dialog,
                        stateEvent
                    )
                )
            }
    }



    private suspend fun retCacheDataState(markJobComplete: Boolean): DataState<ViewState>{

        val cacheResult = safeCacheCall(dispatcher){dbQuery?.invoke()}

        var jobCompleteMarker: StateEvent? = null
        if (markJobComplete) jobCompleteMarker = stateEvent

        return object: CacheResponseHandler<ViewState, CacheObj>(
            response = cacheResult,
            stateEvent = jobCompleteMarker
        ){
            override suspend fun cacheResultSuccess(
                cacheObj: CacheObj,
                stateEvent: StateEvent?
            ): DataState<ViewState> {
                return fetchCacheData.invoke(cacheObj, stateEvent)
            }
        }.getResult()

    }


}