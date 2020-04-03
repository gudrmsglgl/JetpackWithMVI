package com.fastival.jetpackwithmviapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@FlowPreview
@ExperimentalCoroutinesApi
abstract class DataChannelManager<ViewState> {

    private val TAG: String = "AppDebug"

    private val _activeStateEvents: HashSet<String> = HashSet()
    private val _numActiveJobs: MutableLiveData<Int> = MutableLiveData()

    private val dataChannel: ConflatedBroadcastChannel<DataState<ViewState>>
            = ConflatedBroadcastChannel()

    private var channelScope: CoroutineScope? = null

    val messageStack = MessageStack()

    val numActiveJob: LiveData<Int>
        get() = _numActiveJobs


    init {
        dataChannel
            .asFlow()
            .onEach { dataState ->
                dataState.data?.let { data ->
                    handleNewData(data)
                    removeStateEvent(dataState.stateEvent)
                }
                dataState.stateMessage?.let { stateMessage ->
                    addStateMessageStack(stateMessage)
                    removeStateEvent(dataState.stateEvent)
                }
            }
            .launchIn(CoroutineScope(Main))
    }


    fun setupChannel() {
        cancelJobs()
        setupNewChannelScope(CoroutineScope(IO))
    }


    private fun offerToDataChannel(dataState: DataState<ViewState>) {
        dataChannel.let {
            if (!it.isClosedForSend) it.offer(dataState)
        }
    }


    private fun setupNewChannelScope(coroutineScope: CoroutineScope): CoroutineScope {
        channelScope = coroutineScope
        return channelScope as CoroutineScope
    }


    fun getChannelScope(): CoroutineScope {
        return channelScope?: setupNewChannelScope(CoroutineScope(IO))
    }


    fun launchJob(
        stateEvent: StateEvent,
        jobFunction: Flow<DataState<ViewState>>
    ){
        if (!isStateEventActive(stateEvent) && messageStack.size == 0)
        {

            addStateEvent(stateEvent)

            jobFunction
                .onEach { dataState ->
                    offerToDataChannel(dataState)
                }
                .launchIn(getChannelScope())

        }
    }


    fun cancelJobs(){
        if (channelScope != null) {
            if (channelScope?.isActive == true) {
                channelScope?.cancel()
            }
            channelScope = null
        }
        clearActiveStateEventCounter()
    }


    fun isJobAlreadyActive(stateEvent: StateEvent) =
        isStateEventActive(stateEvent)


    private fun isStateEventActive(stateEvent: StateEvent) =
        _activeStateEvents.contains(stateEvent.toString())


    private fun addStateEvent(stateEvent: StateEvent) {
        _activeStateEvents.add(stateEvent.toString())
        syncNumActiveStateEvents()
    }


    private fun removeStateEvent(stateEvent: StateEvent?) {
        _activeStateEvents.remove(stateEvent.toString())
        syncNumActiveStateEvents()
    }


    private fun clearActiveStateEventCounter() {
        _activeStateEvents.clear()
        syncNumActiveStateEvents()
    }


    private fun syncNumActiveStateEvents(){
        _numActiveJobs.value = _activeStateEvents.size
    }


    private fun addStateMessageStack(stateMessage: StateMessage): Boolean =
        messageStack.add(stateMessage)


    fun removeStateMessageFromStack(index: Int = 0): StateMessage =
        messageStack.removeAt(index)


    abstract fun handleNewData(data: ViewState)

}