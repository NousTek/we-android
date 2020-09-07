package com.we.beyond.api

import com.we.beyond.model.HeatMapPojo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface HeatMapApi
{
    /** Get heat map by using lat-long */
    @GET("api/heatmap/getHeatMap")
    fun getHeatMap(@Query("lat") latitude : String , @Query("long")longitude : String) : Single<HeatMapPojo>
}