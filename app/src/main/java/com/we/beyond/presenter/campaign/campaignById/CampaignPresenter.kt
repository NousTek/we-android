package com.we.beyond.presenter.campaign.campaignById

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.CampaignPojo

interface CampaignPresenter
{
    /** this interface and functions are showing result on view */
    interface ICampaignByIdView {
        fun setCampaignByIdAdapter(campaignDetails: CampaignPojo)
        fun setOnDelete()
    }

    /** this interface is working for calling respected apis */
    interface ICampaignByIdPresenter {

        fun onCampaignById(context: Context, CampaignId: String)
        fun onDelete (context: Context, jsonObject: JsonObject)
    }
}