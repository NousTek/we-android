package com.we.beyond.presenter.reportResolved

import android.content.Context
import com.we.beyond.model.ReportedResolutionDetailsPojo

interface MarAsResolvedDetailsPresenter
{
    /** this interface and functions are showing result on view */
    interface IMarAsResolvedDetailsView
    {
        fun setMarAsResolvedDetailsAdapter( issueDetails : ReportedResolutionDetailsPojo)

    }

    /** this interface is working for calling respected apis */
    interface  IMarAsResolvedDetailsPresenter
    {

        fun onMarAsResolvedDetails(context: Context,pageNo : Int,size : Int, issueId : String)

    }
}