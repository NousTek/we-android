package com.we.beyond.presenter.gathering.createGathering

import android.content.Context
import com.google.gson.JsonObject

interface CreateGatheringPresenter
{
    /** this interface and functions are showing result on view */
    interface ICreateGatheringView
    {
        fun goToNextScreen()
    }

    /** this interface is working for calling onGatheringCreated and onGatheringUpdated api respectively */
    interface  ICreateGatheringPresenter
    {
        fun onGatheringCreated(context: Context, jsonObject: JsonObject)
        fun onGatheringUpdated(context: Context, jsonObject: JsonObject)

    }
}