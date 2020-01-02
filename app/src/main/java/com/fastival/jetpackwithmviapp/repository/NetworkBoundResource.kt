package com.fastival.jetpackwithmviapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.fastival.jetpackwithmviapp.ui.DataState
import com.fastival.jetpackwithmviapp.ui.Response
import com.fastival.jetpackwithmviapp.ui.ResponseType
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.Constants.Companion.NETWORK_TIMEOUT
import com.fastival.jetpackwithmviapp.util.Constants.Companion.TESTING_CACHE_DELAY
import com.fastival.jetpackwithmviapp.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>(
    isNetworkAvailable: Boolean,
    isNetworkRequest: Boolean,
    shouldLoadFromCache: Boolean)
{

    private val TAG: String = "AppDebug"

    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope

    init {
        setJob(initNewJob())
        setValue(DataState.loading(isLoading = true, cachedData = null))

        if (shouldLoadFromCache) {
            // view cache to start
            val dbSource = this.loadFromCache()
            result.addSource(dbSource){
                result.removeSource(dbSource)
                setValue(DataState.loading(isLoading = true, cachedData = it))
            }
        }

        if (isNetworkRequest){
            handleNetworkRequest(isNetworkAvailable)
        }

        else {
            coroutineScope.launch {
                delay(TESTING_CACHE_DELAY)
                // View data from cache only and return
                createCacheRequestAndReturn()
            }
        }

    }

    private fun handleNetworkRequest(isNetworkAvailable: Boolean){

        if (isNetworkAvailable) {
            coroutineScope.launch {

                // simulate a network delay for testing
                delay(TESTING_NETWORK_DELAY)

                withContext(Main) {

                    // make network call
                    val apiResponse = createCall()
                    result.addSource(apiResponse){ response ->
                        result.removeSource(apiResponse)

                        coroutineScope.launch {
                            handleGenericResponse(response)
                        }
                    }
                }

                GlobalScope.launch(IO){
                    delay(NETWORK_TIMEOUT)

                    if (!job.isCompleted) {
                        Log.e(TAG, "NetworkBoundResource: JOB NETWORK TIMEOUT." )
                        job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
                    }
                }

            }
        }
        else {
            onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, shouldUseDialog = true, shouldUseToast = false)
        }
    }

    private suspend fun handleGenericResponse(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> handleApiSuccessResponse(response)
            is ApiErrorResponse -> {
                Log.e(TAG, "NetworkBoundResource: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "NetworkBoundResource: Request returned NOTING (HTTP 204)")
                onErrorReturn("HTTP 204. Returned noting.", true, false)
            }
        }
    }


    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()

        if (msg == null) msg = ERROR_UNKNOWN
        else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false
        }
        if (shouldUseToast) responseType = ResponseType.Toast()
        if (useDialog) responseType = ResponseType.Dialog()

        onCompleteJob(DataState.error(
            response = Response(
                message = msg,
                responseType = responseType
            )
        ))
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called...")
        job = Job()
        job.invokeOnCompletion(onCancelling = true, invokeImmediately = true, handler = object : CompletionHandler{
            override fun invoke(cause: Throwable?) {
                if (job.isCancelled) {
                    Log.e(TAG, "NetworkBoundResource: Job has been cancelled.")
                    cause?.let {
                        onErrorReturn(it.message, false, true)
                    }?: onErrorReturn(ERROR_UNKNOWN, false, true)
                }
                else if(job.isCompleted) {
                    Log.e(TAG, "NetworkBoundResource: Job has been completed...")
                }
            }
        })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    abstract fun loadFromCache(): LiveData<ViewStateType>

    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)

    abstract suspend fun createCacheRequestAndReturn()

    abstract fun setJob(job: Job)
}