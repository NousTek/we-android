package com.we.beyond.presenter.campaign.createCampaign

import android.content.Context
import com.google.gson.JsonObject

interface CreateCampaignPresenter
{
    /** this interface and functions are showing result on view */
    interface ICreateCampaignView
    {
        fun goToNextScreen()
    }

    /** this interface is working for calling onGatheringCreated and onGatheringUpdated api respectively */
    interface  ICreateCampaignPresenter
    {
        fun onCampaignCreated(context: Context, jsonObject: JsonObject)
        fun onUpdateCampaign(context: Context,jsonObject: JsonObject)
    }

}