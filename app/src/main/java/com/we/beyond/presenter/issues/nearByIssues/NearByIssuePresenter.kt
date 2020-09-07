package com.we.beyond.presenter.issues.nearByIssues

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.NearByIssuesPojo


interface NearByIssuePresenter
{
    /** this interface and functions are showing result on view */
    interface INearByIssueView
    {
        fun setNearByIssueAdapter( nearByIssueDetails : NearByIssuesPojo)
        fun setNearByIssueAdapterOnLoadMore(nearByIssueDetails : NearByIssuesPojo)
        fun setNearByIssueOnSearchAdapter( nearByIssueDetails : NearByIssuesPojo)
        fun setNearByIssueOnSearchAdapterOnLoadMore(nearByIssueDetails : NearByIssuesPojo)
        fun goToNextScreen()
        fun goToPreviousScreen()
        fun setOnDelete(_id: String)
    }

    /** this interface is working for calling respective apis respectively */
    interface  INearByIssuePresenter
    {

        fun onNearByIssue(context: Context, jsonObject: JsonObject)
        fun onNearByIssueOnLoadMore (context: Context, jsonObject: JsonObject)
        fun onDelete (context: Context, jsonObject: JsonObject)
        fun onSearch(context: Context, searchText : String,pageNo : Int ,size : Int)
        fun onSearchLoadMore(context: Context, searchText : String,pageNo : Int ,size : Int)
    }
}