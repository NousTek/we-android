package com.we.beyond.api

import com.google.gson.JsonObject
import com.we.beyond.model.*
import io.reactivex.Single
import retrofit2.http.*

interface CampaignApi
{
    /** Get campaign by using campaign id  */
    @GET("api/campaign/findCampaignByCampaignId")
    fun getCampaignById(@Query("campaignId")campaignId:String) : Single<CampaignPojo>

    /** Create campaign */
    @Headers(
        "Content-Type: application/json"
    )
    @POST("api/campaign")
    fun createCampaign(@Body jsonObject: JsonObject): Single<CampaignPostPojo>

    /** Delete campaign */
    @PATCH("api/campaign/enabledDisabledCampaign")
    fun deleteCampaign(@Body jsonObject: JsonObject) : Single<DeleteCampaignPojo>

    /** Update campaign  */
    @PATCH("api/campaign/updateCampaign")
    fun updateCampaign(@Body jsonObject: JsonObject) : Single<UpdateCampaignPojo>
}