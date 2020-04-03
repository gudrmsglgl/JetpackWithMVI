package com.fastival.jetpackwithmviapp.ui.main.blog.state

import com.fastival.jetpackwithmviapp.util.StateEvent
import okhttp3.MultipartBody

sealed class BlogStateEvent: StateEvent {

    class BlogSearchEvent(
        val clearLayoutManagerState: Boolean = true
    ): BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error searching for blog posts."
        }

        override fun toString(): String {
            return this.javaClass.simpleName
        }
    }

    class CheckAuthorOfBlogPost: BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error checking if you are the author of this blog post."
        }

        override fun toString(): String {
            return this.javaClass.simpleName
        }
    }

    class DeleteBlogPostEvent: BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error deleting that blog post."
        }

        override fun toString(): String {
            return this.javaClass.simpleName
        }
    }

    data class UpdateBlogPostEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part?
    ): BlogStateEvent() {
        override fun errorInfo(): String {
            return "Error updating that blog post."
        }

        override fun toString(): String {
            return this.javaClass.simpleName
        }
    }

    class None: BlogStateEvent() {
        override fun errorInfo(): String {
            return "None."
        }
    }

}