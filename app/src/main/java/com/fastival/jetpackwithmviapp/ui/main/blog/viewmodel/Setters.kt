package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.net.Uri
import android.os.Parcelable
import com.fastival.jetpackwithmviapp.bvm
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState

fun bvm.setQuery(query: String) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.searchQuery = query
    setViewState(update)
}

fun bvm.setBlogListData(blogList: List<BlogPost>) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.blogList = blogList
    setViewState(update)
}

fun bvm.setBlogPost(
    blogPost: BlogPost,
    isViewStateUpdate: Boolean
): BlogViewState.ViewBlogFields? {

    val update = getCurrentViewStateOrNew().apply {
        viewBlogFields.blogPost = blogPost
    }

    return if (isViewStateUpdate) {
        setViewState(update)
        null
    } else {
        update.viewBlogFields
    }
}

fun bvm.setIsAuthorOfBlogPost(isAuthorOfBlogPost: Boolean){
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}

fun bvm.setQueryExhausted(isExhauted: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryExhausted = isExhauted
    setViewState(update)
}

/**
 *    Filter
 *    - Filter can be "date_updated" or "username"
 *
 *    Order
 *    - Order can be "-" or ""
 *    - "-" = DESC, "" = ASC
 */
fun bvm.setBlogFilterOrder(
    DialogFilter: String?,
    DialogOrder: String?
){
    val update = getCurrentViewStateOrNew().apply {
        blogFields.apply {
            DialogFilter?.let { this.filter = it }
            DialogOrder?.let { this.order = it }
        }
    }
    setViewState(update)
}


fun bvm.removeDeletedBlogPost() {
    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList.toMutableList()
    for (i in 0 until list.size ) {
        if (list[i] == getBlogPost()){
            list.remove(getBlogPost())
            break
        }
    }
    setBlogListData(list)
}

fun bvm.setUpdatedBlogFields(
    title: String?,
    body: String?,
    uri: Uri?,
    isViewStateUpdate: Boolean
): BlogViewState.UpdatedBlogFields? {

    val update = getCurrentViewStateOrNew().apply {
        updatedBlogFields.apply {
            title?.let { updatedBlogTitle = it }
            body?.let { updatedBlogBody = it }
            uri?.let { updatedImageUri = it }
        }
    }

    return if (isViewStateUpdate) {
        setViewState(update)
        null
    } else {
        update.updatedBlogFields
    }
}

fun bvm.updateListItem(
    newBlogPost: BlogPost,
    isViewStateUpdate: Boolean
): BlogViewState.BlogFields? {

    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList.toMutableList()
    for (i in list.indices){
        if (list[i].pk == newBlogPost.pk) {
            list[i] = newBlogPost
            break
        }
    }
    update.blogFields.blogList = list

    return if (isViewStateUpdate){
        setViewState(update)
        return null
    } else {
        update.blogFields
    }
}

fun bvm.setSyncBlogFromServer(updateBlogPost: BlogPost){
    val update = getCurrentViewStateOrNew().apply {

        updatedBlogFields = setUpdatedBlogFields(
            uri = null,
            title = updateBlogPost.title,
            body = updateBlogPost.body,
            isViewStateUpdate = false
        )!!

        viewBlogFields = setBlogPost(
            updateBlogPost,
            false)!!

        blogFields = updateListItem(
            updateBlogPost,
            false)!!
    }
    setViewState(update)
}

fun bvm.setLayoutManagerState(state: Parcelable) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.layoutManagerState = state
    setViewState(update)
}

fun bvm.clearLayoutManagerState(){
    val update = getCurrentViewStateOrNew()
    update.blogFields.layoutManagerState = null
    setViewState(update)
}