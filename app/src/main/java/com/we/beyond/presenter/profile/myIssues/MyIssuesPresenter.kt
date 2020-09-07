package com.we.beyond.presenter.profile.myIssues

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.NearByIssuesPojo

interface MyIssuesPresenter
{
    /** this interface and functions are showing result on view */
    interface IMyIssuesView
    {
        fun setMyIssuesAdapter( nearByIssueDetails : NearByIssuesPojo)
        fun setMyIssuesOnLoadMore(nearByIssueDetails : NearByIssuesPojo)
        fun setOnDelete(_id: String)
    }

    /** this interface is working for calling respected apis */
    interface  IMyIssuesPresenter
    {

        fun onMyIssues(context: Context, pageNo :Int, size : Int,type : String)
        fun onMyIssuesOnLoadMore (context: Context, pageNo :Int, size : Int,type : String)
        fun onDelete (context: Context, jsonObject: JsonObject)
    }
}