package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import com.fastival.jetpackwithmviapp.bvm
import com.fastival.jetpackwithmviapp.models.BlogPost

fun bvm.getSearchQuery() =
    getCurrentViewStateOrNew().blogFields.searchQuery

fun bvm.getPage() =
    getCurrentViewStateOrNew().blogFields.page

fun bvm.getIsQueryExhausted() =
    getCurrentViewStateOrNew().blogFields.isQueryExhausted

fun bvm.getIsQueryInProgress() =
    getCurrentViewStateOrNew().blogFields.isQueryInProgress

fun bvm.getOrder(): String =
    getCurrentViewStateOrNew().blogFields.order

fun bvm.getFilter(): String =
    getCurrentViewStateOrNew().blogFields.filter

fun bvm.getSlug(): String =
    getCurrentViewStateOrNew().viewBlogFields.blogPost?.slug ?: ""

fun bvm.isAuthorOfBlogPost(): Boolean =
    getCurrentViewStateOrNew().viewBlogFields.isAuthorOfBlogPost

fun bvm.getBlogPost(): BlogPost =
    getCurrentViewStateOrNew().viewBlogFields.blogPost ?:
    BlogPost(-1,"","","","",1,"")