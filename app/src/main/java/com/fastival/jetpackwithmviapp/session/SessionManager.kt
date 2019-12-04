package com.fastival.jetpackwithmviapp.session

import android.app.Application
import com.fastival.jetpackwithmviapp.persistence.AuthTokenDao
import javax.inject.Inject

class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
)
{
}