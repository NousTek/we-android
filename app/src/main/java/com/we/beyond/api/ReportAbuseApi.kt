package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.ReportAbusePojo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ReportAbuseApi
{
    /** Post report abuse  */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/abuse")
    fun ReportAbuse(@Body jsonObject: JsonObject): Single<ReportAbusePojo>
}