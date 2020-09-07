package com.we.beyond.api


import com.we.beyond.model.NearByCategoriesPojo
import io.reactivex.Single
import retrofit2.http.GET

interface FilterCategoiesApi
{
    /** Get all user categories */
    @GET("api/category/getAllUserCategories")
    fun getCategories(): Single<NearByCategoriesPojo>
}