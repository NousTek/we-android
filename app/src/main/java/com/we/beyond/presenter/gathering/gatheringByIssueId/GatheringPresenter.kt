package com.we.beyond.presenter.gathering.gatheringByIssueId

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.GatheringListPojo

interface GatheringPresenter
{
    /** this interface and functions are showing result on view */
    interface IGatheringView
    {

        fun setGatheringAdapter( gatheringPojo : GatheringListPojo)
        fun setGatheringLoadMoreAdapter( gatheringPojo: GatheringListPojo)
        fun goToPreviousScreen()
        fun setOnDelete(_id : String)
    }

    /** this interface is working for calling respected apis */
    interface  IGatheringPresenter
    {

        fun getGatheringList(context: Context, pageNo : Int, size : Int, startDate : String)
        fun getGatheringWithCriteriaList(context: Context, pageNo : Int, size : Int, startDate : String, endDate : String)
        fun getGatheringListOnLoadMore(context: Context, pageNo : Int, size : Int, startDate : String)
        fun getGatheringWithCriteriaListOnLoadMore(context: Context, pageNo : Int, size : Int, startDate : String, endDate : String)
        fun onDelete (context: Context, jsonObject: JsonObject)
    }
}