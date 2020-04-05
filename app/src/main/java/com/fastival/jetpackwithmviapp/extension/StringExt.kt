package com.fastival.jetpackwithmviapp.extension

import android.widget.EditText
import android.widget.TextView
import okhttp3.MediaType
import okhttp3.RequestBody
import java.text.SimpleDateFormat
import java.util.*

fun String.parseRequestBody(): RequestBody =
    RequestBody.create(
        MediaType.parse("text/plain"),
        this)

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

fun EditText.editToString() = this.text.toString()

fun TextView.tvToString(): String = this.text.toString()

