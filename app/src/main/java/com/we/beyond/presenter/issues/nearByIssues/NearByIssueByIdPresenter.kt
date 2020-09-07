package com.we.beyond.presenter.issues.nearByIssues

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.NearByIssueByIdDetailsPojo


interface NearByIssueByIdPresenter
{
    /** this interface and functions are showing result on view */
    interface INearByIssueByIdView
    {
        fun setNearByIssueByIdAdapter( nearByIssueDetails : NearByIssueByIdDetailsPojo)
        fun setOnDelete()

    }

    /** this interface is working for calling respected apis */
    interface  INearByIssueByIdPresenter
    {

        fun onNearByIssueById(context: Context, nearByIssueId : String)
        fun onDelete (context: Context, jsonObject: JsonObject)
    }
}