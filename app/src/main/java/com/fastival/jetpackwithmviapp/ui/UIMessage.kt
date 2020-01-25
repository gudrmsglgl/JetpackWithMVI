package com.fastival.jetpackwithmviapp.ui

import com.fastival.jetpackwithmviapp.extension.activity.AreYouSureCallBack

data class UIMessage (
    val message: String,
    val uiMessageType: UIMessageType
)

sealed class UIMessageType{

    class Toast: UIMessageType()

    class Dialog: UIMessageType()

    class AreYouSureDialog(
        val callback: AreYouSureCallBack
    ): UIMessageType()

    class None: UIMessageType()
}