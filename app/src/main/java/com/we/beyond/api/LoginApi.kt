package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.ForgotPasswordPojo
import com.we.beyond.model.LoginPojo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginApi
{
    /** Login api */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/auth")
    fun login(@Body jsonObject: JsonObject): Single<LoginPojo>

    /** Forgot password api  */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/auth/forgetPassword")
    fun forgotPassword(@Body jsonObject: JsonObject): Single<ForgotPasswordPojo>

    @Headers(
        "Content-Type: application/json"
    )
    @POST("/api/auth/login/facebook")
    fun loginWithFacebook(@Body jsonObject: JsonObject): Single<LoginPojo>

    @Headers(
        "Content-Type: application/json"
    )
    @POST("/api/auth/login/google")
    fun loginWithGoogle(@Body jsonObject: JsonObject): Single<LoginPojo>
}