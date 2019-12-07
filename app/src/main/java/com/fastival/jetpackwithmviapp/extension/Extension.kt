package com.fastival.jetpackwithmviapp.extension

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity

inline fun <reified T: Activity> Context.navActivity(isFinish: Boolean, noinline extra: Intent.()-> Unit) {
    val intent = Intent(this, T::class.java)
    intent.extra()
    startActivity(intent)
    if (isFinish) {
        (this as AppCompatActivity).finish()
    }
}