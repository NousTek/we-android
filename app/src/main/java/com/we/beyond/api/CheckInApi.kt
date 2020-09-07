package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.CheckInPojo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CheckInApi

{
    /** Post check in api */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/checkin")
    fun CheckIn(@Body jsonObject: JsonObject): Single<CheckInPojo>
}