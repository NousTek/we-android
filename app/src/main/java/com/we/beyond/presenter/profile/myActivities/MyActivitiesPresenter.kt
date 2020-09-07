package com.we.beyond.presenter.profile.myActivities

import android.content.Context
import com.we.beyond.model.MyActivityPojo

interface MyActivitiesPresenter
{
    /** this interface and functions are showing result on view */
    interface IMyActivitiesView
    {
        fun setMyActivitiesDataAdapter( activityData : MyActivityPojo)
        fun setMyActivitiesDataAdapterOnLoadMore( activityData : MyActivityPojo)

    }

    /** this interface is working for calling respected apis */
    interface  IMyActivitiesPresenter
    {
        fun onRequestMyActivitiesData( context : Context,pageNo : Int, size : Int)
        fun onRequestMyActivitiesDataOnLoadMore( context : Context,pageNo : Int, size : Int)

    }
}