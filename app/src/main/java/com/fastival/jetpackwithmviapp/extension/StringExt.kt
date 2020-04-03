package com.fastival.jetpackwithmviapp.extension

import android.widget.EditText
import android.widget.TextView
import okhttp3.MediaType
import okhttp3.RequestBody

fun String.parseRequestBody(): RequestBody =
    RequestBody.create(
        MediaType.parse("text/plain"),
        this)

fun EditText.editToString() = this.text.toString()

fun TextView.tvToString(): String = this.text.toString()