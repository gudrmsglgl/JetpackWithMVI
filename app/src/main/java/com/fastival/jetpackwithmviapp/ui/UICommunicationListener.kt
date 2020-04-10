package com.fastival.jetpackwithmviapp.ui
import com.fastival.jetpackwithmviapp.util.Response
import com.fastival.jetpackwithmviapp.util.StateMessageCallback

interface UICommunicationListener {

    fun onResponseReceived(
        response: Response,
        stateMessageCallback: StateMessageCallback
    )

    fun displayProgressBar(isAnyActiveJob: Boolean)

    fun expandAppBar()

    fun hideSoftKeyboard()

    fun isStoragePermissionGranted(): Boolean

}