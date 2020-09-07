package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.*
import io.reactivex.Single
import retrofit2.http.*

interface IssueResolvedApi
{
    /** Post report resolved  */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/resolution")
    fun reportResolved(@Body jsonObject: JsonObject): Single<ReportedResolutionPojo>

    /** Get user list */
    @GET("api/users/getAllForSearch")
    fun getUsersList() : Single<ReportedUserData>

    /** Get user list which search by user  */
    @GET("api/users/searchByName")
    fun getUsersListOnSearch(@Query ("searchText") searchText : String) : Single<ReportedUserData>

    /** Post mark as resolve the issue */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/resolution/markAsResolved")
    fun markAsResolved(@Body jsonObject: JsonObject): Single<MarkAsResolvedPojo>

    /** Get resolution details by issue id */
    @GET("api/resolution/getResolutionsByIssueIdForApproval")
    fun getResolutionDetails(@Query ("pageNo") pageNo : Int,@Query("size")size : Int,@Query("issueId")id : String) : Single<ReportedResolutionDetailsPojo>

    /** Update resolution  */
    @PATCH("api/resolution/updateResolution")
    fun updateResolution(@Body jsonObject: JsonObject) : Single<UpdateReportResolutionPojo>
}