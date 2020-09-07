package com.we.beyond.presenter.like

import android.content.Context
import com.google.gson.JsonObject

interface LikePresenter
{
    interface ILikeView
    {

    }

    /** this interface is working for calling respected api */
    interface  ILikePresenter
    {
        fun onLike(context: Context, jsonObject: JsonObject)

    }
}