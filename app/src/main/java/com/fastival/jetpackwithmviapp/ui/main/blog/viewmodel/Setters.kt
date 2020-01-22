package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.net.Uri
import com.fastival.jetpackwithmviapp.bvm
import com.fastival.jetpackwithmviapp.models.BlogPost

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

fun bvm.setBlogPost(blogPost: BlogPost) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.blogPost = blogPost
    setViewState(update)
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

fun bvm.setQueryInProgress(isInProgress: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryInProgress = isInProgress
    setViewState(update)
}

// Filter can be "date_updated" or "username"
fun bvm.setBlogFilter(filter: String?) {
    filter?.let {
        val update = getCurrentViewStateOrNew()
        update.blogFields.filter = filter
        setViewState(update)
    }
}

// Order can be "-" or ""
// Note: "-" = DESC, "" = ASC
fun bvm.setBlogOrder(order: String?) {
    order?.let {
        val update = getCurrentViewStateOrNew()
        update.blogFields.order = order
        setViewState(update)
    }
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

fun bvm.setUpdatedBlogFields(title: String?, body: String?, uri: Uri?) {
    val update = getCurrentViewStateOrNew()
    val updatedBlogFields = update.updatedBlogFields
    title?.let{ updatedBlogFields.updatedBlogTitle = it }
    body?.let{ updatedBlogFields.updatedBlogBody = it }
    uri?.let{ updatedBlogFields.updatedImageUri = it }
    update.updatedBlogFields = updatedBlogFields
    setViewState(update)
}

fun bvm.updateListItem(newBlogPost: BlogPost) {
    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList.toMutableList()
    for (i in list.indices){
        if (list[i].pk == newBlogPost.pk) {
            list[i] = newBlogPost
            break
        }
    }
    update.blogFields.blogList = list
    setViewState(update)
}

fun bvm.setSyncBlogsFromServer(updateBlogPost: BlogPost){
    setUpdatedBlogFields(
        uri = null,
        title = updateBlogPost.title,
        body = updateBlogPost.body
    ) // update UpdateBlogFragment (not really necessary since navigating back)
    setBlogPost(updateBlogPost)
    updateListItem(updateBlogPost)
}