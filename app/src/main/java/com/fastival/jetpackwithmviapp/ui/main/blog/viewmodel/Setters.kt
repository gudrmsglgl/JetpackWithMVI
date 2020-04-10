package com.fastival.jetpackwithmviapp.ui.main.blog.viewmodel

import android.net.Uri
import android.os.Parcelable
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.ui.main.blog.state.BlogViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.blogFragmentViewState(data: BlogViewState) = with(data.blogFields) {

    blogList?.let {
        setBlogListData(it)
    }

    isQueryExhausted?.let {
        setQueryExhausted(it)
    }

}

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.viewBlogFragmentViewState(data: BlogViewState) = with(data.viewBlogFields){

    blogPost?.let { setBlogPost(it) }

    isAuthorOfBlogPost?.let { setIsAuthorOfBlogPost(it) }

}

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.updateBlogFragmentViewState(data: BlogViewState) =
    getCurrentViewStateOrNew().apply {

        data.updatedBlogFields.updatedBlogTitle?.let { this.updatedBlogFields.updatedBlogTitle = it }

        data.updatedBlogFields.updatedBlogBody?.let { this.updatedBlogFields.updatedBlogBody = it }

        data.updatedBlogFields.updatedImageUri?.let { this.updatedBlogFields.updatedImageUri = it }

    }.run { setViewState(this) }


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.setQuery(query: String) =
    getCurrentViewStateOrNew()
        .apply { blogFields.searchQuery = query }
        .run { setViewState(this) }


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.setBlogListData(
    blogList: List<BlogPost>
) =
    getCurrentViewStateOrNew()
        .apply{ blogFields.blogList = blogList }
        .run { setViewState(this)}


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.setBlogPost(
    blogPost: BlogPost
) =
    getCurrentViewStateOrNew()
        .apply { viewBlogFields.blogPost = blogPost }
        .run { setViewState(this) }

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.setIsAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) =
    getCurrentViewStateOrNew()
        .apply { viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost }
        .run { setViewState(this) }


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) =
    getCurrentViewStateOrNew()
        .apply {
            blogFields.isQueryExhausted = isExhausted
        }
        .run {
            setViewState(this)
        }


/**
 *    Filter
 *    - Filter can be "date_updated" or "username"
 *
 *    Order
 *    - Order can be "-" or ""
 *    - "-" = DESC, "" = ASC
 */
@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.setBlogFilterOrder(
    DialogFilter: String?,
    DialogOrder: String?
) =
    getCurrentViewStateOrNew()
        .apply {

            blogFields.apply {
                DialogFilter?.let { this.filter = it }
                DialogOrder?.let { this.order = it }
            }

        }
        .run { setViewState(this) }





@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.removeDeletedBlogPost() =
    getCurrentViewStateOrNew()
        .apply {

            val curBlogList = blogFields.blogList?.toMutableList()
            if (curBlogList != null) {

                val deletedPost = getBlogPost()

                for (i in curBlogList.indices) {
                    if (curBlogList[i] == deletedPost){
                        curBlogList.remove(deletedPost)
                        break
                    }
                }

                blogFields.blogList = curBlogList
            }

        }.run {
            setViewState(this)
        }
    

@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.setUpdatedBlogFields(
    title: String?,
    body: String?,
    uri: Uri?
) = getCurrentViewStateOrNew().apply {

    updatedBlogFields.apply {

        title?.let { updatedBlogTitle = it }
        body?.let { updatedBlogBody = it }
        uri?.let { updatedImageUri = it }

    }}
    .run { setViewState(this) }




@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.updateBlogListItem() =
    getCurrentViewStateOrNew()
        .apply {

            val curList = blogFields.blogList?.toMutableList()

            if (curList != null){

                val updatedBlog = getBlogPost()

                for (i in curList.indices){
                    if (curList[i].pk == updatedBlog.pk){
                        curList[i] = updatedBlog
                        break
                    }
                }
            }

            blogFields.blogList = curList

        }
        .run {
            setViewState(this)
        }


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.setLayoutManagerState(state: Parcelable) =
    getCurrentViewStateOrNew()
        .apply { blogFields.layoutManagerState = state }
        .run { setViewState(this) }


@FlowPreview
@ExperimentalCoroutinesApi
fun BlogViewModel.clearLayoutManagerState() =
    getCurrentViewStateOrNew()
        .apply { blogFields.layoutManagerState = null }
        .run { setViewState(this) }
