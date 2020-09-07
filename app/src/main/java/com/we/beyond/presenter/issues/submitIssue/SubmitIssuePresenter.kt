package com.we.beyond.presenter.issues.submitIssue

import android.content.Context
import com.google.gson.JsonObject

interface SubmitIssuePresenter
{
    /** this interface and functions are showing result on view */
    interface ISubmitIssueView
    {
        fun goToNextScreen()

    }

    /** this interface is working for calling onIssueCreated and onIssueUpdated api respectively */
    interface  ISubmitIssuePresenter
    {
        fun onIssueCreated(context: Context, jsonObject: JsonObject)
        fun onIssueUpdated(context: Context, jsonObject: JsonObject)

    }
}