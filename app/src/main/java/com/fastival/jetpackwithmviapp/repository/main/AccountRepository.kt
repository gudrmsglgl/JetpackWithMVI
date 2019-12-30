package com.fastival.jetpackwithmviapp.repository.main

import android.util.Log
import com.fastival.jetpackwithmviapp.api.main.OpenApiMainService
import com.fastival.jetpackwithmviapp.persistence.AccountPropertiesDao
import com.fastival.jetpackwithmviapp.session.SessionManager
import kotlinx.coroutines.Job
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
)
{
    private val TAG: String = "AppDebug"

    private var repositoryJob: Job? = null

    fun cancelActiveJobs(){
        Log.d(TAG, "AccountRepository: Cancelling on-going jobs...")
        repositoryJob?.cancel()
    }
}