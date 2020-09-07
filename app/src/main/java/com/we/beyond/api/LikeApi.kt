package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.LikePojo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LikeApi
{
    /** Post like and dislike the issue */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/like")
    fun likeDislike(@Body jsonObject: JsonObject): Single<LikePojo>
}