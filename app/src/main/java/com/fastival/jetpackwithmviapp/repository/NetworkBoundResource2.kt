package com.fastival.jetpackwithmviapp.repository

import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.NETWORK_ERROR
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

@FlowPreview
abstract class NetworkBoundResource2<NetworkObj, CacheObj, ViewState>(
    private val dispatcher: CoroutineDispatcher,
    private val stateEvent: StateEvent,
    private val dbQuery: (suspend () -> CacheObj?)?,
    private val fetchCacheData: ((CacheObj) -> DataState<ViewState>)?,
    private val apiCall: suspend () -> NetworkObj?,
    private val updateCache: suspend (NetworkObj) -> Unit,
    private val retApiResult: ((NetworkObj) -> DataState<ViewState>)?
)
{

    private val TAG: String = "AppDebug"

    val result: Flow<DataState<ViewState>> = flow {

        // STEP 1: VIEW CACHE
        dbQuery?.run {
            emit(returnCache(markJobComplete = false))
        }

        // STEP 2: MAKE NETWORK CALL, SAVE RESULT TO CACHE OR Emit ERROR
        val apiResult = safeApiCall(dispatcher){apiCall.invoke()}

        resultSaveCacheOrEmitError(this, apiResult)

        // STEP 3: VIEW CACHE and MARK JOB COMPLETED
        dbQuery?.run {
            emit(returnCache(markJobComplete = true))
        }
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
                    if (dbQuery != null)
                        updateCache.invoke(apiResult.value as NetworkObj)
                    else
                        flowCollector.emit(retApiResult!!.invoke(apiResult.value))
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



    private suspend fun returnCache(markJobComplete: Boolean): DataState<ViewState>{

        val cacheResult = safeCacheCall(dispatcher){dbQuery?.invoke()}

        var jobCompleteMarker: StateEvent? = null
        if (markJobComplete) jobCompleteMarker = stateEvent

        return object: CacheResponseHandler<ViewState, CacheObj>(
            response = cacheResult,
            stateEvent = jobCompleteMarker
        ){
            override suspend fun handleSuccess(resultObj: CacheObj): DataState<ViewState> {
                return fetchCacheData!!.invoke(resultObj)
            }
        }.getResult()

    }


   /* abstract fun updateCache(networkObj: NetworkObj)

    abstract fun handleCacheSuccess(resultObj: CacheObj): DataState<ViewState>*/

}