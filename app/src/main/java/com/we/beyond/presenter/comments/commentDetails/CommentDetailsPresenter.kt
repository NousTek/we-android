package com.we.beyond.presenter.comments.commentDetails

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.CommentDetailsPojo

interface CommentDetailsPresenter
{
    /** this interface and functions are showing result on view */
    interface ICommentsByIdView {

        fun setCommentsByIdAdapter(commentsDetails: CommentDetailsPojo)
        fun onUpdateSuccessfully()

    }

    /** this interface is working for calling respected apis */
    interface ICommentsByIdPresenter {

        fun onCommentsById(context: Context, CommentsId: String)
        fun onUpdateComment(context: Context,jsonObject: JsonObject)
    }
}