package com.we.beyond.presenter.dashboard

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.ConnectPojo
import com.we.beyond.model.GatheringListPojo
import com.we.beyond.model.NearByIssuesPojo

interface DashBoardSummaryDetailsPresenter
{
    /** this interface and functions are showing result on view */
    interface IDashBoardSummaryDetailsView
    {
        fun setUnresolvedIssueData( unresolvedIssues : NearByIssuesPojo)
        fun setUnresolvedIssueDataOnLoadMore( unresolvedIssues : NearByIssuesPojo)
        fun setResolvedIssueData( resolvedIssues : NearByIssuesPojo)
        fun setResolvedIssueDataOnLoadMore( resolvedIssues : NearByIssuesPojo)
        fun setUpcomingGatheringData( gatheringList : GatheringListPojo)
        fun setUpcomingGatheringDataOnLoadMore( gatheringList : GatheringListPojo)
        fun setNewPublishedData( newPublishedList : ConnectPojo)
        fun setNewPublishedDataOnLoadMore( newPublishedList : ConnectPojo)


    }

    /** this interface is working for calling summary api respectively */
    interface  IDashBoardSummaryDetailsPresenter
    {
        fun getUnresolvedIssues(context : Context, latitude : String, longitude : String, pageNo : Int ,pageSize : Int)
        fun getUnresolvedIssuesOnLoadMore(context : Context, latitude : String, longitude : String, pageNo : Int ,pageSize : Int)
        fun getResolvedIssues(context : Context, latitude : String, longitude : String, pageNo : Int ,pageSize : Int)
        fun getResolvedIssuesOnLoadMore(context : Context, latitude : String, longitude : String, pageNo : Int ,pageSize : Int)
        fun getUpcomingGathering(context : Context, latitude : String, longitude : String, pageNo : Int ,pageSize : Int)
        fun getUpcomingGatheringOnLoadMore(context : Context, latitude : String, longitude : String, pageNo : Int ,pageSize : Int)
        fun getNewPublished(context : Context, pageNo : Int ,pageSize : Int)
        fun getNewPublishedOnLoadMore(context : Context, pageNo : Int ,pageSize : Int)

    }
}