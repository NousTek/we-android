package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.SubmitIssuePojo
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface SubmitIssueApi
{
    /** Sumbit the created issue */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/issue")
    fun submitIssue(@Body jsonObject: JsonObject): Single<SubmitIssuePojo>

}