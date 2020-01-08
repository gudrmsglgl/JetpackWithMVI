package com.fastival.jetpackwithmviapp.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.fastival.jetpackwithmviapp.R
import java.text.SimpleDateFormat
import java.util.*

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

fun String.convertServerStringDateToLong(): Long {
    var stringDate = this.removeRange(this.indexOf("T") until this.length)
    var sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    try {
        val time = sdf.parse(stringDate).time
        return time
    } catch (e: Exception){
        throw Exception(e)
    }
}

fun Long.convertLongToStringDate(): String{
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    try{
        val date = sdf.format(Date(this))
        return date
    } catch (e: Exception){
        throw Exception(e)
    }
}