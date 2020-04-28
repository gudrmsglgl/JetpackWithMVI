package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.BLOG_ORDER_DESC
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.getSearchQuery() =
    getCurrentViewStateOrNew().blogFields.searchQuery ?: ""

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.getPage() =
    getCurrentViewStateOrNew().blogFields.page ?: 1

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.getIsQueryExhausted() =
    getCurrentViewStateOrNew().blogFields.isQueryExhausted ?: true


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.getOrder(): String =
    getCurrentViewStateOrNew().blogFields.order ?: BLOG_ORDER_DESC

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.getFilter(): String =
    getCurrentViewStateOrNew().blogFields.filter ?: BLOG_FILTER_DATE_UPDATED

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.getSlug(): String =
    getCurrentViewStateOrNew().viewBlogFields.blogPost?.slug ?: ""

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.isAuthorOfBlogPost(): Boolean =
    getCurrentViewStateOrNew().viewBlogFields.isAuthorOfBlogPost ?: false

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.getBlogPost(): BlogPost =
    getCurrentViewStateOrNew().viewBlogFields.blogPost ?:
    BlogPost(-1,"","","","",1,"")