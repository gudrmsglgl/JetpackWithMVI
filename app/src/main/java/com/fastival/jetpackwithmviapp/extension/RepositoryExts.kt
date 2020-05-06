package com.fastival.jetpackwithmviapp.extension

import android.util.Log
import com.fastival.jetpackwithmviapp.models.AuthToken
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.Constants.Companion.CACHE_TIMEOUT
import com.fastival.jetpackwithmviapp.util.Constants.Companion.NETWORK_TIMEOUT
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.CACHE_ERROR_TIMEOUT
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.NETWORK_ERROR_TIMEOUT
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.UNKNOWN_ERROR
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException

/**
 * Reference: https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
 */

private val TAG: String = "AppDebug"

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): ApiResult<T?> {
    return withContext(dispatcher) {
        try{
            // throws TimeoutcacellationException
            withTimeout(NETWORK_TIMEOUT) {
                ApiResult.Success(apiCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                }

                is IOException -> {
                    ApiResult.NetworkError
                }

                is HttpException -> {
                    val code = throwable.code()
                    val throwableMessage = throwable.message()

                    //Log.e(TAG,"throwableMessage: $throwableMessage")
                    ApiResult.GenericError(
                        code,
                        throwableMessage
                    )
                }

                else -> {
                    ApiResult.GenericError(
                        null,
                        UNKNOWN_ERROR
                    )
                }
            }
        }
    }
}

suspend fun <T> safeCacheCall(
    dispatcher: CoroutineDispatcher,
    cacheCall: suspend () -> T?
): CacheResult<T?> {
    return withContext(dispatcher) {
        try{
            // throws TimeoutCancellationException
            withTimeout(CACHE_TIMEOUT) {
                CacheResult.Success(cacheCall.invoke())
            }
        }catch (throwable: Throwable){
            when(throwable) {
                is TimeoutCancellationException -> {
                    CacheResult.GenericError(CACHE_ERROR_TIMEOUT)
                }
                else -> {
                    CacheResult.GenericError(UNKNOWN_ERROR)
                }
            }
        }
    }
}

fun <ViewState> buildError(
    message: String,
    uiComponentType: UIComponentType,
    stateEvent: StateEvent?
): DataState<ViewState>{
    return DataState.error(
        response = Response(
            message = "${stateEvent?.errorInfo()}\n\nReason: $message",
            uiComponentType = uiComponentType,
            messageType = MessageType.Error
        ),
        stateEvent = stateEvent
    )
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try{
        throwable.response()?.errorBody()?.toString()
    } catch (exception: Exception) {
        UNKNOWN_ERROR
    }
}