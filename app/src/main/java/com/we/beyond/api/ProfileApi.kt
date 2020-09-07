package com.we.beyond.api


import com.google.gson.JsonObject
import com.we.beyond.model.*
import io.reactivex.Single
import retrofit2.http.*


interface ProfileApi
{
    /** Get user profile details  */
    @GET("api/profile/getMyProfile")
    fun getProfile() : Single<ProfilePojo>

    /** Get user's reported resolve issues */
    @GET("api/resolution/receivedResolutionIssues")
    fun getMyReportedResolution(@Query("pageNo") pageNo : Int, @Query("size")size : Int) : Single<NearByIssuesPojo>

    /** Get user's issue list  */
    @GET("api/issue/getMyIssueList")
    fun getMyIssues(@Query("pageNo") pageNo : Int, @Query("size")size : Int, @Query("type")type : String) : Single<NearByIssuesPojo>

    /** Get user's reported resolutions  */
    @GET("api/resolution/getMyReportedResolutions")
    fun getMyResolution(@Query("pageNo") pageNo : Int, @Query("size")size : Int) : Single<NearByIssuesPojo>

    /** Get user's reported resolution by using issue id */
    @GET("api/resolution/getMyReportedResolutionByIssueId")
    fun getMyResolutionById(@Query("pageNo") pageNo : Int, @Query("size")size : Int, @Query("issueId")id : String, @Query("projection") projection : String) : Single<ReportedResolutionDetailsPojo>


    /** Get user's gathering list  */
    @GET("api/gathering/getMyGatherings")
    fun getMyGathering(@Query("pageNo") pageNo : Int, @Query("size")size : Int, @Query("type")type : String) : Single<GatheringListPojo>

    /** Get user's campaign list */
    @GET("api/campaign/getMyCampaigns")
    fun getMyCampaign(@Query("pageNo") pageNo : Int, @Query("size")size : Int, @Query("type")type : String) : Single<MyCampaignPojo>

    /** Get user's article list  */
    @GET("api/connect/getMyConnects")
    fun getMyConnect(@Query("pageNo") pageNo : Int, @Query("size")size : Int) : Single<ConnectPojo>

    /** Get user's article by using category id */
    @GET("api/connect/getMyConnects")
    fun getMyConnectWithCategory(@Query("pageNo") pageNo : Int, @Query("size")size : Int, @Query("connectCategoryId")connectCategoryId : String) : Single<ConnectPojo>


    /** Get user's activities  */
    @GET("api/activity/getMyActivity")
    fun getMyActivity(@Query("pageNo") pageNo : Int, @Query("size")size : Int) : Single<MyActivityPojo>

    /** Update issue */
    @PATCH("api/issue/enabledDisabledIssue")
    fun deleteIssue(@Body jsonObject: JsonObject) : Single<DeleteNearByIssueByIdDetailsPojo>


    /** Update user's profile data */
    @PATCH("api/users/updateUserProfile")
    fun updateProfile(@Body jsonObject: JsonObject) : Single<UpdateProfilePojo>

    /** Update user's profile pic  */
    @PATCH("api/profile/updateUserProfileUrl")
    fun updateProfilePic(@Body jsonObject: JsonObject) : Single<UpdateProfilePic>

    /** Update password  */
    @PATCH("api/profile/changePassword")
    fun changePassword(@Body jsonObject: JsonObject) : Single<ChangePassword>

    /** Logout api  */
    @POST("api/users/logout")
    fun logout(): Single<Logout>
}