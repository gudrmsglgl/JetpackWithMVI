package com.fastival.jetpackwithmviapp.extension.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.fastival.jetpackwithmviapp.R
import com.fastival.jetpackwithmviapp.util.StateMessageCallback

inline fun <reified T: Activity> Context.navActivity(isFinish: Boolean, noinline extra: Intent.()-> Unit) {
    val intent = Intent(this, T::class.java)
    intent.extra()
    startActivity(intent)
    if (isFinish) {
        (this as AppCompatActivity).finish()
    }
}


fun Activity.displayToast(
    @StringRes message: Int,
    stateMessageCallback: StateMessageCallback
){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    stateMessageCallback.removeMessageFromStack()
}


fun Activity.displayToast(
    message: String,
    stateMessageCallback: StateMessageCallback
) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    stateMessageCallback.removeMessageFromStack()
}


interface AreYouSureCallBack {

    fun proceed()

    fun cancel()
}