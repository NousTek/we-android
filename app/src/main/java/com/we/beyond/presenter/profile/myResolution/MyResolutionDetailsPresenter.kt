package com.we.beyond.presenter.profile.myResolution

import android.content.Context
import com.we.beyond.model.ReportedResolutionDetailsPojo

interface MyResolutionDetailsPresenter
{
    /** this interface and functions are showing result on view */
    interface IMyResolutionDetailsView
    {
        fun setMyResolutionDetailsAdapter( issueDetails : ReportedResolutionDetailsPojo)

    }

    /** this interface is working for calling respected apis */
    interface  IMyResolutionDetailsPresenter
    {

        fun onMyResolutionDetails(context: Context, pageNo : Int, size : Int, issueId : String)

    }

}