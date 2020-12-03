package com.we.beyond.presenter.feedback

import android.content.Context
import com.google.gson.JsonObject

interface FeedbackPresenter
{
    /** this interface and functions are showing result on view */
    interface IFeedbackView
    {
        fun onSuccessfulFeedbackSubmission(message : String)
    }

    /** this interface is working for calling request user type and register api respectively */
    interface  IRegistrationPresenter
    {
        fun onSubmitFeedback(context: Context, jsonObject: JsonObject)
    }
}