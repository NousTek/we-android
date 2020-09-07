package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.GoingIdPojo
import com.we.beyond.model.GoingPojo
import io.reactivex.Single
import retrofit2.http.*

interface GoingApi
{
    /** Post going */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/going")
    fun Going(@Body jsonObject: JsonObject): Single<GoingPojo>

    /** Get going list by using type id */
    @GET("api/going/getAllGoingListByTypeId")
    fun getGoingListById(@Query("pageNo")pageNo:Int, @Query("size")size:Int,@Query("typeId")typeId : String) : Single<GoingIdPojo>
}