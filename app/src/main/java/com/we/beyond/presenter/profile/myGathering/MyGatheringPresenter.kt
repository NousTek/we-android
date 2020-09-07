package com.we.beyond.presenter.profile.myGathering

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.GatheringListPojo

interface MyGatheringPresenter
{
    /** this interface and functions are showing result on view */
    interface IMyGatheringView
    {
        fun setMyGatheringAdapter( gatheringListDetails : GatheringListPojo)
        fun setMyGatheringOnLoadMore(gatheringListDetails : GatheringListPojo)
        fun setOnDelete(_id : String)
    }

    /** this interface is working for calling respected apis */
    interface  IMyGatheringPresenter
    {

        fun onMyGathering(context: Context, pageNo :Int, size : Int, type : String)
        fun onMyGatheringOnLoadMore (context: Context, pageNo :Int, size : Int, type : String)
        fun onDelete (context: Context, jsonObject: JsonObject)
    }
}