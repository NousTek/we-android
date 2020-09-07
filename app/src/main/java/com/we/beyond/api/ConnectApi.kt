package com.we.beyond.api


import com.google.gson.JsonObject
import com.we.beyond.model.*
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface ConnectApi
{
    /** Get all categories  */
    @GET("api/connectCategory/getAllSortByOrder")
    fun getCategories(): Single<ConnectCategoriesPojo>


    /** Create article  */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/connect")
    fun createConnect(@Body jsonObject: JsonObject): Single<ConnectCreatePojo>


    /** Get all sorted articles  */
    @GET("api/connect/getAllByFilters")
    fun getConnectList(@Query ("pageNo")pageNo:Int, @Query("size")size : Int,
                       @Query("sortBy")sortBy:String) : Single<ConnectPojo>

    /** Get all sorted articles by using category id */
    @GET("api/connect/getAllByFilters")
    fun getConnectListByCategoryId(@Query ("pageNo")pageNo:Int, @Query("size")size : Int,
                       @Query("sortBy")sortBy:String,@Query("connectCategoryId")connectCategoryId : String ): Single<ConnectPojo>

    /** Get article details by using connect id  */
    @GET("api/connect/getConnectInformationById")
    fun getConnectById(@Query ("connectId")connectCategoryId:String) : Single<ConnectDetailsPojo>

    /** Update article  */
    @PATCH("api/connect/enabledDisabledConnect")
    fun deleteConnect(@Body jsonObject: JsonObject) : Single<DeleteConnectDetailsPojo>

    /** Update article  */
    @PATCH("api/connect/updateConnect")
    fun updateConnect(@Body jsonObject: JsonObject) : Single<UpdateConnectPojo>

    /** Get all article by using search text   */
    @GET("api/connect/searchConnectByTitleOrDescription")
    fun getConnectOnSearch(@Query ("searchText") searchText : String, @Query("pageNo")pageNo : Int, @Query("size")size : Int) : Single<ConnectPojo>

}