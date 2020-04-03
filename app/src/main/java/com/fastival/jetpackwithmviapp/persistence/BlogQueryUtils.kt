package com.fastival.jetpackwithmviapp.persistence

import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.models.BlogPost
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.ORDER_BY_DESC_DATE_UPDATED
import com.fastival.jetpackwithmviapp.persistence.BlogQueryUtils.Companion.ORDER_BY_DESC_USERNAME

/*
*   SERVER {
*   http req
*   order by query
*   ""+filter or "-"+filter
*   }
*
*   TO
*
*   Client {
*   local db
*   order by query ASC or DESC
*   }
* */
class BlogQueryUtils {

    companion object{
        private val TAG: String = "AppDebug"

        const val BLOG_ORDER_ASC: String = ""
        const val BLOG_ORDER_DESC: String = "-"
        const val BLOG_FILTER_USERNAME = "username"
        const val BLOG_FILTER_DATE_UPDATED = "date_updated"

        val ORDER_BY_ASC_DATE_UPDATED = BLOG_ORDER_ASC + BLOG_FILTER_DATE_UPDATED
        val ORDER_BY_DESC_DATE_UPDATED = BLOG_ORDER_DESC + BLOG_FILTER_DATE_UPDATED
        val ORDER_BY_ASC_USERNAME = BLOG_ORDER_ASC + BLOG_FILTER_USERNAME
        val ORDER_BY_DESC_USERNAME = BLOG_ORDER_DESC + BLOG_FILTER_USERNAME
    }
}

suspend fun BlogPostDao.returnOrderedBlogQuery(
    query: String,
    filterAndOrder: String,
    page: Int
): List<BlogPost> {

    when {

        filterAndOrder.contains(ORDER_BY_DESC_DATE_UPDATED) -> {
            return searchBlogPostsOrderByDateDESC(query, page)
        }

        filterAndOrder.contains(ORDER_BY_ASC_DATE_UPDATED) -> {
            return searchBlogPostsOrderByDateASC(query, page)
        }

        filterAndOrder.contains(ORDER_BY_DESC_USERNAME) -> {
            return searchBlogPostsOrderByAuthorDESC(query, page)
        }

        else -> {
            return searchBlogPostsOrderByAuthorASC(query, page)
        }
    }
}