package com.fastival.jetpackwithmviapp.ui.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.util.*
import com.fastival.jetpackwithmviapp.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@ExperimentalCoroutinesApi
abstract class BaseViewModel<ViewState>: ViewModel() {

    val TAG = "AppDebug"

    protected val _viewState: MutableLiveData<ViewState> = MutableLiveData()

    open val viewState: LiveData<ViewState>
        get() = _viewState

    val dataChannelManager: DataChannelManager<ViewState> = object: DataChannelManager<ViewState>(){
        override fun handleNewData(data: ViewState) {
           this@BaseViewModel.handleViewState(data)
        }
    }

    val numActiveJobs: LiveData<Int>
        get() = dataChannelManager.numActiveJob

    val stateMessage: LiveData<StateMessage?>
        get() = dataChannelManager.messageStack.stateMessage


    fun launchJob(
        stateEvent: StateEvent,
        jobFunc: Flow<DataState<ViewState>>
    ) = dataChannelManager.launchJob(stateEvent, jobFunc)


    fun areAnyJobActive(): Boolean =
        dataChannelManager.numActiveJob.value?.let {
            it > 0
        }?: false


    fun isJobAlreadyActive(stateEvent: StateEvent): Boolean =
        dataChannelManager.isJobAlreadyActive(stateEvent)


    // For Debugging
    fun getMessageStackSize(): Int =
        dataChannelManager.messageStack.size


    fun setUpChannel() = dataChannelManager.setupChannel()


    fun getCurrentViewStateOrNew(): ViewState{
        return viewState.value?.let {
            it
        }?: initNewViewState()
    }

    open fun setViewState(viewState: ViewState) {
        _viewState.value = viewState
    }

    fun removeStateMessage(index: Int = 0) =
        dataChannelManager.removeStateMessageFromStack(index)


    fun cancelActiveJobs(){
        if (areAnyJobActive()) {
            Log.d(TAG, "cancel active jobs: ${dataChannelManager.numActiveJob.value ?: 0}")
            dataChannelManager.cancelJobs()
        }
    }

    fun retInvalidEvent(stateEvent: StateEvent): DataState<ViewState> =
        DataState.error(
            response = Response(
                message = INVALID_STATE_EVENT,
                uiComponentType = UIComponentType.None,
                messageType = MessageType.Error
            ),
            stateEvent = stateEvent
        )

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    abstract fun handleViewState(data: ViewState)

    abstract fun setStateEvent(stateEvent: StateEvent)

    abstract fun initNewViewState(): ViewState
}