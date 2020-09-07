package com.we.beyond.presenter.reportResolved

import android.content.Context
import com.google.gson.JsonObject

interface MarkAsResolvedPresenter
{
    /** this interface and functions are showing result on view */
    interface IMarkAsResolvedView
    {
      fun onResolved()
    }

    /** this interface is working for calling respected apis */
    interface  IMarkAsResolvedPresenter
    {
        fun onMarkAsResolvedData(context : Context, jsonObject: JsonObject)

    }
}