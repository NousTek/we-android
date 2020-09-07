package com.we.beyond.presenter.gathering.gatheringByIssueId

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.GatheringDetails

interface GatheringByIdPresenter
{
    /** this interface and functions are showing result on view */
    interface IGatheringByIdView
    {
        fun setGatheringByIdAdapter( gatheringDetails : GatheringDetails)
        fun setOnDelete()

    }

    /** this interface is working for calling respected apis */
    interface  IGatheringByIdPresenter
    {

        fun onGatheringById(context: Context, gatheringId : String)
        fun onDelete (context: Context, jsonObject: JsonObject)

    }
}