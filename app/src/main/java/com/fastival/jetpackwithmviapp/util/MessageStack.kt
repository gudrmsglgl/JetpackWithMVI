package com.fastival.jetpackwithmviapp.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.parcel.IgnoredOnParcel
import java.lang.IndexOutOfBoundsException

class MessageStack: ArrayList<StateMessage>() {

    private val TAG: String = "AppDebug"

    @IgnoredOnParcel
    private val _stateMessage: MutableLiveData<StateMessage?> = MutableLiveData()

    @IgnoredOnParcel
    val stateMessage: LiveData<StateMessage?>
        get() = _stateMessage

    override fun addAll(elements: Collection<StateMessage>): Boolean {
        for (element in elements) add(element)

        return true
    }

    override fun add(element: StateMessage): Boolean {

        if (this.contains(element)) return false // prevent duplicate errors added to Stack

        val transaction = super.add(element)
        if (this.size == 1) setStateMessage(stateMessage = element)

        return transaction
    }

    private fun setStateMessage(stateMessage: StateMessage?) {
        _stateMessage.postValue(stateMessage)
    }

    override fun removeAt(index: Int): StateMessage {
        try {
            val transaction = super.removeAt(index)

            if (this.size > 0) setStateMessage(stateMessage = this[0])
            else setStateMessage(null)

            return transaction
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        return StateMessage(
            Response(
                message = "does nothing",
                uiComponentType = UIComponentType.None,
                messageType = MessageType.None
            )
        ) // this does nothing
    }
}