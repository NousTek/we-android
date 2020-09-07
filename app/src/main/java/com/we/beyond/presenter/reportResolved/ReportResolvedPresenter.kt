package com.we.beyond.presenter.reportResolved

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.UserList

interface ReportResolvedPresenter
{
    /** this interface and functions are showing result on view */
    interface IReportResolvedView
    {
        fun goToNextScreen()
        fun setUserListAdapter (userList : ArrayList<UserList>)

    }

    /** this interface is working for calling respected apis */
    interface  IReportResolvedPresenter
    {
        fun onRequestReportResolvedData( context : Context, jsonObject: JsonObject)
        fun onRequestReportResolvedDataUpdate( context : Context, jsonObject: JsonObject)
        fun OnRequestUserList (context: Context)
        fun OnRequestUserListOnSearch (context: Context,searchText : String)

    }
}