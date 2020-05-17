package com.fastival.jetpackwithmviapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseViewModel<ViewState>: ViewModel() {

    val TAG = "AppDebug"

    private val liveActiveStateEvents: MutableLiveData<HashSet<String>> = MutableLiveData()
    private val hashActiveStateEvents: HashSet<String> = HashSet()

    val totalActiveEvents: LiveData<Int> =
        Transformations
            .map(liveActiveStateEvents){ activeEvents ->
                activeEvents.size
            }

    private var channelScope: CoroutineScope? = null

    private val messageStack = MessageStack()
    val stateMessage: LiveData<StateMessage?>
        get() = messageStack.stateMessage

    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()
    open val viewState: LiveData<ViewState>
        get() = _viewState

    fun launchJob(
        stateEvent: StateEvent,
        repositoryFunc: Flow<DataState<ViewState>>
    ){
        if (!isStateEventActive(stateEvent) && messageStack.size == 0) {
            addStateEvent(stateEvent)
            repositoryFunc
                .onEach { dataState ->
                    dataState.data?.let { data ->
                        handleViewState(data)
                        removeStateEvent(dataState.stateEvent)
                    }
                    dataState.stateMessage?.let { stateMessage ->
                        addStateMessageStack(stateMessage)
                        removeStateEvent(dataState.stateEvent)
                    }
                }
                .launchIn(getChannelScope())
        }
    }
    fun setUpChannel() {
        cancelJobs()
        channelScope = CoroutineScope(Dispatchers.IO)
    }
    fun cancelJobs(){
        if (channelScope != null) {
            if (channelScope?.isActive == true) {
                channelScope?.cancel()
            }
            channelScope = null
        }
        clearActiveEvents()
    }
    override fun onCleared() {
        super.onCleared()
        if (areAnyJobActive()) {
            cancelJobs()
        }
    }
    fun getChannelScope(): CoroutineScope {
        return channelScope?: CoroutineScope(Dispatchers.IO)
    }
    open fun setViewState(viewState: ViewState) {
        _viewState.postValue(viewState)
    }
    fun getCurrentViewStateOrNew(): ViewState{
        return viewState.value?.let {
            it
        }?: initNewViewState()
    }
    fun retInvalidEvent(
        stateEvent: StateEvent
    ): DataState<ViewState> = DataState.error(
        response = Response(
            message = INVALID_STATE_EVENT,
            uiComponentType = UIComponentType.None,
            messageType = MessageType.Error
        ),
        stateEvent = stateEvent
    )
    private fun addStateEvent(stateEvent: StateEvent){
        hashActiveStateEvents.add(stateEvent.toString())
        liveActiveStateEvents.value = hashActiveStateEvents
    }
    private fun removeStateEvent(stateEvent: StateEvent?) {
        hashActiveStateEvents.remove(stateEvent.toString())
        liveActiveStateEvents.postValue(hashActiveStateEvents)
    }
    private fun clearActiveEvents(){
        hashActiveStateEvents.clear()
        liveActiveStateEvents.value = hashActiveStateEvents
    }
    private fun isStateEventActive(stateEvent: StateEvent) = hashActiveStateEvents.contains(stateEvent.toString())
    private fun addStateMessageStack(stateMessage: StateMessage) = messageStack.add(stateMessage)
    fun removeStateMessage(index: Int = 0) = messageStack.removeAt(index)
    fun areAnyJobActive() = totalActiveEvents.value?.let { it > 0 } ?: false
    fun isJobAlreadyActive(stateEvent: StateEvent): Boolean = hashActiveStateEvents.contains(stateEvent.toString())

    abstract fun handleViewState(data: ViewState)
    abstract fun setStateEvent(stateEvent: StateEvent)
    abstract fun initNewViewState(): ViewState
}