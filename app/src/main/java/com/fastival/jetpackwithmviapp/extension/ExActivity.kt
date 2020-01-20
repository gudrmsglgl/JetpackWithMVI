package com.fastival.jetpackwithmviapp.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.fastival.jetpackwithmviapp.R

inline fun <reified T: Activity> Context.navActivity(isFinish: Boolean, noinline extra: Intent.()-> Unit) {
    val intent = Intent(this, T::class.java)
    intent.extra()
    startActivity(intent)
    if (isFinish) {
        (this as AppCompatActivity).finish()
    }
}

fun Activity.displayToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.displaySuccessDialog(message: String?) {
    MaterialDialog(this)
        .show {
            title(R.string.text_success)
            message(text = message)
            positiveButton(R.string.text_ok)
        }
}

fun Activity.displayErrorDialog(errorMessage: String?) {
    MaterialDialog(this)
        .show {
            title(R.string.text_error)
            message(text = errorMessage)
            positiveButton(R.string.text_ok)
        }
}

fun Activity.displayInfoDialog(message: String?) {
    MaterialDialog(this)
        .show {
            title(R.string.text_info)
            message(text = message)
            positiveButton(R.string.text_ok)
        }
}

fun Activity.areYouSureDialog(message: String, callback: AreYouSureCallBack){
    MaterialDialog(this)
        .show {
            title(R.string.are_you_sure)
            message(text = message)
            negativeButton(R.string.text_cancel){
                callback.cancel()
            }
            positiveButton(R.string.text_yes) {
                callback.proceed()
            }
        }
}

interface AreYouSureCallBack {

    fun proceed()

    fun cancel()
}