package com.we.beyond.presenter.connect.connectIssue

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.ConnectCategories
import com.we.beyond.model.ConnectPojo

interface ConnectIssuePresenter
{
    /** this interface and functions are showing result on view */
    interface IConnectIssueView
    {
        fun setConnectCategoryAdapter( connectCategories : ArrayList<ConnectCategories> )
        fun setConnectAdapter( connectPojo : ConnectPojo )
        fun setConnectLoadMoreAdapter( connectPojo: ConnectPojo )
        fun setConnectOnSearchAdapter( connectPojo: ConnectPojo)
        fun setConnectOnSearchAdapterOnLoadMore(connectPojo: ConnectPojo)
        fun goToPreviousScreen()
        fun setOnDelete(_id: String)
    }

    /** this interface is working for calling respected apis */
    interface  IConnectIssuePresenter
    {
        fun onRequestConnectCategory( context : Context)
        fun getConnectList(context: Context, pageNo : Int, size : Int, sortBy : String)
        fun getConnectWithCategoryList(context: Context, pageNo : Int, size : Int, sortBy : String, connectCategoryId : String)
        fun getConnectListOnLoadMore(context: Context, pageNo : Int, size : Int, sortBy : String)
        fun getConnectWithCategoryListOnLoadMore(context: Context, pageNo : Int, size : Int, sortBy : String, connectCategoryId : String)
        fun onDelete (context: Context, jsonObject: JsonObject)
        fun onSearch(context: Context, searchText : String,pageNo : Int ,size : Int)
        fun onSearchLoadMore(context: Context, searchText : String,pageNo : Int ,size : Int)
    }
}