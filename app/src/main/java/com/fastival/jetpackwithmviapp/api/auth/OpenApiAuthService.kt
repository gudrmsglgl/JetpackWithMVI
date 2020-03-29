package com.fastival.jetpackwithmviapp.api.auth

import androidx.lifecycle.LiveData
import com.fastival.jetpackwithmviapp.api.auth.network_response.LoginResponse
import com.fastival.jetpackwithmviapp.api.auth.network_response.RegistrationResponse
import com.fastival.jetpackwithmviapp.di.auth.AuthScope
import com.fastival.jetpackwithmviapp.util.GenericApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

@AuthScope
interface OpenApiAuthService {

    @POST("account/login")
    @FormUrlEncoded
    fun login(
        @Field("username") email: String,
        @Field("password") password: String
    ): LiveData<GenericApiResponse<LoginResponse>>

    @POST("account/register")
    @FormUrlEncoded
    fun register(
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("password2") password2: String
    ): LiveData<GenericApiResponse<RegistrationResponse>>

}