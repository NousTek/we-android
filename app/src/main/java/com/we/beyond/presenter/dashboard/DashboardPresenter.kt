package com.we.beyond.presenter.dashboard

import android.content.Context
import com.we.beyond.model.DashboardPojo

interface DashboardPresenter
{
    /** this interface and functions are showing result on view */
    interface IDashboardView
    {
        fun setSummaryData( summary : DashboardPojo)
        fun setSummeryDataOnResume( summary : DashboardPojo )

    }

    /** this interface is working for calling summary api respectively */
    interface  IDashboardPresenter
    {
        fun getSummary( context : Context , latitude : String , longitude : String)
        fun getSummaryOnResume( context : Context , latitude : String , longitude : String)
    }
}