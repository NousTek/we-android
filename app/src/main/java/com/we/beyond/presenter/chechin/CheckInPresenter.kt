package com.we.beyond.presenter.chechin

import android.content.Context
import com.google.gson.JsonObject

interface CheckInPresenter
{
    interface ICheckInView
    {

    }

    /** this interface is working for calling respected apis */
    interface  ICheckInPresenter
    {
        fun onCheckIn(context: Context, jsonObject: JsonObject)

    }
}