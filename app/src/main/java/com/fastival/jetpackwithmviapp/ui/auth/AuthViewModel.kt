package com.fastival.jetpackwithmviapp.ui.auth

import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.repository.auth.AuthRepository
import javax.inject.Inject

class AuthViewModel
@Inject constructor(val authRepository: AuthRepository): ViewModel(){
}