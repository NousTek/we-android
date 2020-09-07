package com.we.beyond.presenter.profile.myConnect

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.ConnectCategories
import com.we.beyond.model.ConnectPojo

interface MyConnectPresenter
{
    /** this interface and functions are showing result on view */
    interface IMyConnectIssueView
    {
        fun setConnectCategoryAdapter( connectCategories : ArrayList<ConnectCategories> )
        fun setConnectAdapter( connectPojo : ConnectPojo)
        fun setConnectLoadMoreAdapter( connectPojo: ConnectPojo)
        fun goToPreviousScreen()
        fun setOnDelete(_id : String)
    }

    /** this interface is working for calling respected apis */
    interface  IMyConnectIssuePresenter
    {
        fun onRequestConnectCategory( context : Context)
        fun getConnectList(context: Context, pageNo : Int, size : Int)
        fun getConnectWithCategoryList(context: Context, pageNo : Int, size : Int, connectCategoryId : String)
        fun getConnectListOnLoadMore(context: Context, pageNo : Int, size : Int)
        fun getConnectWithCategoryListOnLoadMore(context: Context, pageNo : Int, size : Int, connectCategoryId : String)
        fun onDelete (context: Context, jsonObject: JsonObject)
    }
}