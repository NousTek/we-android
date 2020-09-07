package com.we.beyond.presenter.connect.connectIssue

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.ConnectDetailsPojo


interface ConnectByIdPresenter {

    /** this interface and functions are showing result on view */
    interface IConnectByIdView {
        fun setConnectByIdAdapter(connectDetails: ConnectDetailsPojo)
        fun setOnDelete()
    }

    /** this interface is working for calling respected apis */
    interface IConnectByIdPresenter {

        fun onConnectById(context: Context, connectCategoryId: String)
        fun onDelete (context: Context, jsonObject: JsonObject)

    }
}