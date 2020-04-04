package com.fastival.jetpackwithmviapp.util

abstract class CacheResponseHandler<ViewState, Data>(
    private val response: CacheResult<Data?>,
    private val stateEvent: StateEvent?
) {
    suspend fun getResult(): DataState<ViewState>{

        return when(response) {

            is CacheResult.GenericError -> {
                DataState.error(
                    response = Response(
                        messageType = MessageType.Error,
                        uiComponentType = UIComponentType.Dialog,
                        message = "${stateEvent?.errorInfo()}\n\nReason: ${response.errorMessage}"
                    ),
                    stateEvent = stateEvent
                )
            }

            is CacheResult.Success -> {
                if (response.value == null) {
                    DataState.error(
                        response = Response(
                            message = "${stateEvent?.errorInfo()}\n\nReason: Data is NULL.",
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error
                        ),
                        stateEvent = stateEvent
                    )
                } else {
                    cacheResultSuccess(cacheObj = response.value, stateEvent = stateEvent)
                }
            }

        }
    }

    abstract suspend fun cacheResultSuccess(
        cacheObj: Data,
        stateEvent: StateEvent?
    ): DataState<ViewState>
}