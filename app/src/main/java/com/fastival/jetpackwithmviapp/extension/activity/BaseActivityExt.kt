package com.fastival.jetpackwithmviapp.extension.activity

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.ui.base.BaseActivity
import com.fastival.jetpackwithmviapp.util.MessageType
import com.fastival.jetpackwithmviapp.util.Response
import com.fastival.jetpackwithmviapp.util.StateMessageCallback

fun BaseActivity.areYouSureDialog(
    message: String,
    callback: AreYouSureCallBack,
    stateMessageCallback: StateMessageCallback
): MaterialDialog =
    MaterialDialog(this).show {
        title(R.string.are_you_sure)
        message(text = message)
        negativeButton(R.string.text_cancel) {
            callback.cancel()
            stateMessageCallback.removeMessageFromStack()
            dismiss()
        }
        positiveButton(R.string.text_yes) {
            callback.proceed()
            stateMessageCallback.removeMessageFromStack()
            dismiss()
        }
        onDismiss {
            dialogInView = null
        }
        cancelable(false)
    }


fun BaseActivity.displayDialog(
    response: Response,
    stateMessageCallback: StateMessageCallback
){
    response.message?.let { message ->

        dialogInView = MaterialDialog(this)
            .show {

                when (response.messageType) {
                    is MessageType.Error -> title(R.string.text_error)
                    is MessageType.Success -> title(R.string.text_success)
                    is MessageType.Info -> title(R.string.text_info)
                }

                message(text = message)
                positiveButton(R.string.text_ok) {
                    stateMessageCallback.removeMessageFromStack()
                    dismiss()
                }
                onDismiss {
                    dialogInView = null
                }
                cancelable(false)
            }

    }?: stateMessageCallback.removeMessageFromStack()
}