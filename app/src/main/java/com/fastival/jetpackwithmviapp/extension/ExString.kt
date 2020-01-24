package com.fastival.jetpackwithmviapp.extension

import okhttp3.MediaType
import okhttp3.RequestBody

fun String.parseRequestBody(): RequestBody =
    RequestBody.create(
        MediaType.parse("text/plain"),
        this)
