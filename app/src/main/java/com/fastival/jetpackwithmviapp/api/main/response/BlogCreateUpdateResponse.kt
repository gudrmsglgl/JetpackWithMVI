package com.fastival.jetpackwithmviapp.api.main.response

import android.util.Log
import com.fastival.jetpackwithmviapp.extension.convertServerStringDateToLong
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BlogCreateUpdateResponse(

    @SerializedName("response")
    @Expose
    var response: String,

    @SerializedName("pk")
    @Expose
    var pk: Int,

    @SerializedName("title")
    @Expose
    var title: String,

    @SerializedName("slug")
    @Expose
    var slug: String,

    @SerializedName("body")
    @Expose
    var body: String,

    @SerializedName("image")
    @Expose
    var image: String,

    @SerializedName("date_updated")
    @Expose
    var date_updated: String,

    @SerializedName("username")
    @Expose
    var username: String



) {
    fun toBlogPost() =
        BlogPost(
            pk = pk,
            title = title,
            slug = slug,
            body = body,
            image = image,
            date_updated = date_updated.convertServerStringDateToLong(),
            username = username
        )
}