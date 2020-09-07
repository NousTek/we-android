package com.we.beyond.presenter.profile.myResolution

import android.content.Context
import com.we.beyond.model.NearByIssuesPojo

interface MyResolutionPresenter

{
    /** this interface and functions are showing result on view */
    interface IMyResolutionView
    {
        fun setMyResolutionAdapter( nearByIssueDetails : NearByIssuesPojo)
        fun setMyResolutionOnLoadMore(nearByIssueDetails : NearByIssuesPojo)
        fun goToNextScreen()
        fun goToPreviousScreen()

    }

    /** this interface is working for calling respective apis respectively */
    interface  IMyResolutionPresenter
    {

        fun onMyResolution(context: Context, pageNo :Int, size : Int)
        fun onMyResolutionOnLoadMore (context: Context, pageNo :Int, size : Int)

    }
}