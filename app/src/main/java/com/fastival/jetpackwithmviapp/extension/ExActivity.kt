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

fun Context.displayToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.displayToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.displaySuccessDialog(message: String?) {
    MaterialDialog(this)
        .show {
            title(R.string.text_success)
            message(text = message)
            positiveButton(R.string.text_ok)
        }
}

fun Context.displayErrorDialog(errorMessage: String?) {
    MaterialDialog(this)
        .show {
            title(R.string.text_error)
            message(text = errorMessage)
            positiveButton(R.string.text_ok)
        }
}