package com.we.beyond.presenter.comments.createComment

import android.content.Context
import com.google.gson.JsonObject

interface CreateCommentPresenter
{
    /** this interface and functions are showing result on view */
    interface ICreateCommentView
    {
        fun setCommentAdapter( )

    }

    /** this interface is working for calling respected api */
    interface  ICreateCommentPresenter
    {
        fun onCommentCreated(context: Context, jsonObject: JsonObject)

    }
}