package com.fastival.jetpackwithmviapp.ui

import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.ui.base.BaseViewModel
import com.fastival.jetpackwithmviapp.util.AbsentLiveData
import javax.inject.Inject

class EmptyViewModel @Inject constructor(): BaseViewModel<EmptyEvent, EmptyViewState>() {

    override fun handleStateEvent(stateEvent: EmptyEvent): LiveData<DataState<EmptyViewState>> {
        return AbsentLiveData.create()
    }

    override fun initNewViewState(): EmptyViewState {
        return EmptyViewState()
    }

    override fun cancelActiveJobs() {

    }
}