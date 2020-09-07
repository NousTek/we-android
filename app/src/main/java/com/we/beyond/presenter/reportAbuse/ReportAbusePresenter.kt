package com.we.beyond.presenter.reportAbuse

import android.content.Context
import com.google.gson.JsonObject

interface ReportAbusePresenter
{
    interface IReportAbuseView
    {

    }

    /** this interface is working for calling onReportAbuse api */
    interface  IReportAbusePresenter
    {
        fun onReportAbuse(context: Context, jsonObject: JsonObject)

    }
}