package com.fastival.jetpackwithmviapp.session

import android.app.Application
import com.fastival.jetpackwithmviapp.persistence.AuthTokenDao

class SessionManager
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
)
{
}