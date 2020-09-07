package com.we.beyond.api

import com.we.beyond.model.BadgesPojo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface BadgesApi
{
    /** Get user badges by user id */
    @GET("api/profile/getUserBadges")
    fun getBadgesById(@Query("userId")id : String) : Single<BadgesPojo>
    
    /** get all badges of user */
    @GET("api/profile/getUserBadges")
    fun getBadges() : Single<BadgesPojo>
}