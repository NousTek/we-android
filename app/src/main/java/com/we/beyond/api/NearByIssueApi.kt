package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.*
import io.reactivex.Single
import retrofit2.http.*

interface NearByIssueApi
{

    /** Get issues near by lat-long location  */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/issue/findIssuesNearByLatLongNew")
    fun getNearByIssues(@Body jsonObject: JsonObject): Single<NearByIssuesPojo>

    /** Get issue details by using issue id  */
    @GET("api/issue/findIssueByIssueId")
    fun getNearByIssueById(@Query ("issueId")issueId:String) : Single<NearByIssueByIdDetailsPojo>

    /** Update issue  */
    @PATCH("api/issue/enabledDisabledIssue")
    fun deleteIssue(@Body jsonObject: JsonObject) : Single<DeleteNearByIssueByIdDetailsPojo>

    /** Update issue */
    @PATCH("api/issue/updateIssue")
    fun updateIssue(@Body jsonObject: JsonObject) : Single<UpdateIssuePojo>

    /** Get issue by using search text */
    @GET("api/issue/searchByIssueTitleOrIssueNumber")
    fun getIssueOnSearch(@Query ("searchText") searchText : String, @Query("pageNo")pageNo : Int, @Query("size")size : Int) : Single<NearByIssuesPojo>
}