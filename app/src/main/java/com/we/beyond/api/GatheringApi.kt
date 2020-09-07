package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.*
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface GatheringApi {

    /** Create gathering  */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/gathering")
    fun createGathering(@Body jsonObject: JsonObject): Single<GatheringPojo>

    /** Get gathering list which is sorted by date range */
    @GET("api/gathering/getAllGatheringsDateRange")
    fun getGatheringList(@Query ("pageNo")pageNo:Int, @Query("size")size : Int,
                       @Query("startDate")startDate:String) : Single<GatheringListPojo>

    /** Get gathering list by criteria  */
    @GET("api/gathering/getAllGatheringsDateRange")
    fun getGatheringListByCriteria(@Query ("pageNo")pageNo:Int, @Query("size")size : Int,
                                   @Query("startDate")startDate:String,@Query("endDate")endDate : String ): Single<GatheringListPojo>

    /** Get gathering details by using gathering id  */
    @GET("api/gathering/findGatheringByGatheringId")
    fun getGatheringById(@Query ("gatheringId")gatheringId:String) : Single<GatheringDetails>

    /** Update gathering */
    @PATCH("api/gathering/enabledDisabledGathering")
    fun deleteGathering(@Body jsonObject: JsonObject) : Single<DeleteGatheringPojo>

    /** Update gathering */
    @PATCH("api/gathering/updateGathering")
    fun updateGathering(@Body jsonObject: JsonObject) : Single<UpdateGatheringPojo>
}
