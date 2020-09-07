package com.we.beyond.api

import com.we.beyond.model.LeaderBoardPojo
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface LeaderBoardApi
{
    /** Get leader board data  */
    @GET("api/leaderboard/getLeaderboardData")
    fun getLeaderBoardData(@Query("startDate")startDate:String, @Query("endDate")endDate : String , @Query("type")type : String ): Single<LeaderBoardPojo>


    /** Get leader board data by using type id  */
    @GET("api/leaderboard/getLeaderboardData")
    fun getLeaderBoardDataWithCriteria( @Query("type")type : String ): Single<LeaderBoardPojo>

}