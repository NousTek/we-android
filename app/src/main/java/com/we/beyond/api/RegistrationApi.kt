package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.RegistrationPojo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface RegistrationApi
{
    /** Get all user types  */
    @GET("api/userLoginType/getAllSortByOrder")
    fun getAllUserTypes(): Single<RegistrationPojo>


    @GET("api/city/getAllCitiesOrderByName")
    fun getAllCities(): Single<ArrayList<RegistrationPojo>>

    /** Register the user */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/users")
    fun registerUser(@Body jsonObject: JsonObject): Single<RegistrationPojo>
}