package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.DeleteReportedResolutionPojo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.PATCH

interface ResolutionApi
{
    /** Update resolution  */
    @PATCH("api/resolution/enableDisableResolution")
    fun deleteResolution(@Body jsonObject: JsonObject) : Single<DeleteReportedResolutionPojo>
}