package com.fastival.jetpackwithmviapp.ui.main.create_blog.state

import com.fastival.jetpackwithmviapp.util.StateEvent
import okhttp3.MultipartBody

sealed class CreateBlogStateEvent: StateEvent {

    data class CreateNewBlogEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part
    ): CreateBlogStateEvent(){
        override fun errorInfo(): String {
            return "Unable to create a new blog post."
        }
        override fun toString(): String {
            return this.javaClass.simpleName
        }
    }

    class None: CreateBlogStateEvent(){
        override fun errorInfo(): String {
            return "None."
        }
    }
}