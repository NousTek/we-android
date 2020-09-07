package com.we.beyond.presenter.profile.reportedResolution

import android.content.Context
import com.we.beyond.model.NearByIssuesPojo

interface ReportedResolutionPresenter
{
    /** this interface and functions are showing result on view */
    interface IReportedResolutionView
    {
        fun setReportedResolutionAdapter( nearByIssueDetails : NearByIssuesPojo)
        fun setReportedResolutionOnLoadMore(nearByIssueDetails : NearByIssuesPojo)
        fun goToNextScreen()
        fun goToPreviousScreen()
    }

    /** this interface is working for calling respected apis */
    interface  IReportedResolutionPresenter
    {

        fun onReportedResolution(context: Context, pageNo :Int ,size : Int)
        fun onReportedResolutionOnLoadMore (context: Context, pageNo :Int ,size : Int)

    }
}