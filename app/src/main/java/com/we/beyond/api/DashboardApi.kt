package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface DashboardApi
{
    /** Get summary data by using lat-long */
    @GET("api/dashboard/getSummary")
    fun getSummary(@Query("lat")latitude : String, @Query("long")longitude : String) : Single<DashboardPojo>

    /** Get unresolved list by using lat-long */
    @GET("api/dashboard/getUnresolvedIssueList")
    fun getUnresolvedList(@Query ("lat")latitude : String, @Query("long")longitude :String, @Query("pageNo")pageNo : Int, @Query("size")pageSize : Int): Single<NearByIssuesPojo>


    /** Get resolved list by using lat-long */
    @GET("api/dashboard/getResolvedIssueList")
    fun getResolvedList(@Query ("lat")latitude : String, @Query("long")longitude :String, @Query("pageNo")pageNo : Int, @Query("size")pageSize : Int): Single<NearByIssuesPojo>

    /** Get upcoming gathering list by using lat-long */
    @GET("api/dashboard/getUpcomingGatheringList")
    fun getUpcomingGatheringList(@Query ("lat")latitude : String, @Query("long")longitude :String, @Query("pageNo")pageNo : Int, @Query("size")pageSize : Int): Single<GatheringListPojo>

    /** Get published article list  */
    @GET("api/dashboard/getPublishedConnectList")
    fun getPublishedConnectList(@Query("pageNo")pageNo : Int, @Query("size")pageSize : Int) : Single<ConnectPojo>

    /** Update Issue */
    @PATCH("api/issue/enabledDisabledIssue")
    fun deleteIssue(@Body jsonObject: JsonObject) : Single<DeleteNearByIssueByIdDetailsPojo>

    /** Update gathering */
    @PATCH("api/gathering/enabledDisabledGathering")
    fun deleteGathering(@Body jsonObject: JsonObject) : Single<DeleteGatheringPojo>

    /** Update article */
    @PATCH("api/connect/enabledDisabledConnect")
    fun deleteConnect(@Body jsonObject: JsonObject) : Single<DeleteConnectDetailsPojo>

    /** Get user notification  */
    @GET("api/notification/getMyNotifications")
    fun getNotificationList(@Query ("pageNo")pageNo : Int, @Query("size")pageSize: Int): Single<NotificationPojo>

    /**  */
    @GET("api/notification/updateNotificationReceivedFlag")
    fun removeNotificationFlag(): Single<NotifyServer>

    /** Get all user near by my location  */
    @GET("api/users/getAllUsersNearByMe")
    fun getNearByMeUsersList(@Query ("pageNo")pageNo : Int, @Query("size")pageSize: Int): Single<NearByMeUsersPojo>



}