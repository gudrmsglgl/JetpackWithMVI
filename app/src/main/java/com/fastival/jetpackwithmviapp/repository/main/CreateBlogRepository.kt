package com.fastival.jetpackwithmviapp.repository.main

import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.persistence.BlogPostDao
import com.fastival.jetpackwithmviapp.repository.JobManager
import com.fastival.jetpackwithmviapp.session.SessionManager
import javax.inject.Inject

class CreateBlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("CreateBlogRepository")
{
    private val TAG = "AppDebug"

}