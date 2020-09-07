package com.we.beyond.presenter.comments.commentById

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.CommentsPojo
import org.json.JSONObject

interface CommentsPresenter
{
    /** this interface and functions are showing result on view */
    interface ICommentsByIdView {

        fun setCommentsByIdAdapter(commentsDetails: CommentsPojo)
        fun setCommentsByIdAdapterOnLoadMore(commentsDetails: CommentsPojo)
        fun onUpdateSuccessfully()
    }

    /** this interface is working for calling respected apis */
    interface ICommentsByIdPresenter {

        fun onCommentsById(context: Context, CommentsId: String, CommentsType : String , pageNo : Int , pageSize : Int)
        fun onCommentsByIdOnLoadMore(context: Context, CommentsId: String, CommentsType : String , pageNo : Int , pageSize : Int)
        fun onUpdateComment(context: Context,jsonObject: JsonObject)
    }
}