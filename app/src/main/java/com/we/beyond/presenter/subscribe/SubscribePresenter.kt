package com.we.beyond.presenter.subscribe

import android.content.Context
import com.google.gson.JsonObject

interface SubscribePresenter
{
    interface ISubscribeView
    {

    }

    /** this interface is working for calling onSubscribe api */
    interface  ISubscribePresenter
    {
        fun onSubscribe(context: Context, jsonObject: JsonObject)

    }
}