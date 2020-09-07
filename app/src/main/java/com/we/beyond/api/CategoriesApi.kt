package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.CategoriesPojo
import com.we.beyond.model.DeleteNearByIssueByIdDetailsPojo
import com.we.beyond.model.UpdateCategoriesPojo
import io.reactivex.Single
import retrofit2.http.*

interface CategoriesApi
{
    /** Get all categories */
    @GET("api/category/getAllSortByOrder")
    fun getAllCategories(): Single<CategoriesPojo>

    /** Post categories selected by user */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/category/createUserCategories")
    fun postInterestedCategories(@Body jsonObject: JsonObject): Single<CategoriesPojo>

    /** Update categories */
    @PATCH("api/profile/updateUserCategories")
    fun updateCategories(@Body jsonObject: JsonObject) : Single<UpdateCategoriesPojo>

}