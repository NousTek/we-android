package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.FeedbackData
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FeedbackAPI
{
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/feedback")
    fun submitFeedback(@Body jsonObject: JsonObject): Single<FeedbackData>
}