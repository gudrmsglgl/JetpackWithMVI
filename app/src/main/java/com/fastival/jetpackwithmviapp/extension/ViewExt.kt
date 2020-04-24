package com.fastival.jetpackwithmviapp.extension

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import java.util.concurrent.TimeUnit

fun View.singleClick()=
    clicks().throttleFirst(2000, TimeUnit.MILLISECONDS)
