package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import com.fastival.jetpackwithmviapp.bvm

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