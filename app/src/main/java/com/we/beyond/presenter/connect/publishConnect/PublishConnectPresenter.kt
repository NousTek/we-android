package com.we.beyond.presenter.connect.publishConnect

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.ConnectCategories


interface PublishConnectPresenter
{
    /** this interface and functions are showing result on view */
    interface IPublishConnectView
    {
        fun setConnectCategoryAdapter( connectCategories : ArrayList<ConnectCategories> )
        fun goToPreviousScreen()
    }

    /** this interface is working for calling respected apis */
    interface  IPublishConnectPresenter
    {
        fun onRequestConnectCategory( context : Context)
        fun onConnectCreated(context: Context, jsonObject: JsonObject)
        fun onConnectUpdated(context: Context, jsonObject: JsonObject)
        fun onBackClick()
    }
}