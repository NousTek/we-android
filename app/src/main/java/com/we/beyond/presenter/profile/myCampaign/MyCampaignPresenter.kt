package com.we.beyond.presenter.profile.myCampaign

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.MyCampaignPojo

interface MyCampaignPresenter
{
    /** this interface and functions are showing result on view */
    interface IMyCampaignView
    {
        fun setMyCampaignAdapter( campaignListDetails : MyCampaignPojo)
        fun setMyCampaignOnLoadMore(campaignListDetails : MyCampaignPojo)
        fun setOnDelete(_id : String)
    }

    /** this interface is working for calling respected apis */
    interface  IMyCampaignPresenter
    {

        fun onMyCampaign(context: Context, pageNo :Int, size : Int, type : String)
        fun onMyCampaignOnLoadMore (context: Context, pageNo :Int, size : Int, type : String)
        fun onDelete (context: Context, jsonObject: JsonObject)
    }
}