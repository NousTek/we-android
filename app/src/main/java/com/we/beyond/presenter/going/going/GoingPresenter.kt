package com.we.beyond.presenter.going.going

import android.content.Context
import com.google.gson.JsonObject

interface GoingPresenter
{
    /** this interface and functions are showing result on view */
    interface IGoingView
    {
        fun onSuccess()
    }

    /** this interface is working for calling onGoing api */
    interface  IGoingPresenter
    {
        fun onGoing(context: Context, jsonObject: JsonObject)


    }
}