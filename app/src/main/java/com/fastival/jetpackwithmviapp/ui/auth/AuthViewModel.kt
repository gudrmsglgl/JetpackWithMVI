package com.fastival.jetpackwithmviapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fastival.jetpackwithmviapp.api.auth.network_response.LoginResponse
import com.fastival.jetpackwithmviapp.api.auth.network_response.RegistrationResponse
import com.fastival.jetpackwithmviapp.repository.auth.AuthRepository
import com.fastival.jetpackwithmviapp.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject constructor(val authRepository: AuthRepository): ViewModel()
{

    fun testLogin(): LiveData<GenericApiResponse<LoginResponse>> {
        return authRepository.testLoginRequest(
            "mitchelltabian@gmail.com",
            "codingwithmitch1"
        )
    }

    fun testRegister(): LiveData<GenericApiResponse<RegistrationResponse>>{
        return authRepository.testRegistrationRequest(
            "mitchelltabian1234@gmail.com",
            "mitchelltabian1234",
            "codingwithmitch1",
            "codingwithmitch1"
        )
    }
}