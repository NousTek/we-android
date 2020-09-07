package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.SubscribePojo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SubscribeApi{

    /** Subscribe the issue  */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/subscribe")
    fun Subscribe(@Body jsonObject: JsonObject): Single<SubscribePojo>
}