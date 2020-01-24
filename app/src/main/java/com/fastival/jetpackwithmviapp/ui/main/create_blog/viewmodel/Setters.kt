package com.fastival.jetpackwithmviapp.ui.main.create_blog.viewmodel

import android.net.Uri
import com.fastival.jetpackwithmviapp.cvm
import com.fastival.jetpackwithmviapp.ui.main.create_blog.state.CreateBlogViewState

fun cvm.clearNewBlogFields(){
    val update = getCurrentViewStateOrNew()
    update.blogFields = CreateBlogViewState.NewBlogFields()
    setViewState(update)
}

fun cvm.setNewBlogFields(title: String?, body: String?, uri: Uri?){
    val update = getCurrentViewStateOrNew()
    val newBlogFields = update.blogFields
    title?.let { newBlogFields.newBlogTitle = it }
    body?.let { newBlogFields.newBlogBody = it }
    uri?.let { newBlogFields.newImageUri = it }
    update.blogFields = newBlogFields
    setViewState(update)
}