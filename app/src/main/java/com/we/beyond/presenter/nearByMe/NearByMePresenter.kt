package com.we.beyond.presenter.nearByMe

import android.content.Context
import com.we.beyond.model.NearByMeUsersPojo

interface NearByMePresenter
{
    /** this interface and functions are showing result on view */
    interface INearByMeView
    {
        fun setNearByMeDataAdapter( nearByMeUsersData : NearByMeUsersPojo)
        fun setNearByMeDataAdapterOnLoadMore( nearByMeUsersData : NearByMeUsersPojo)
        fun goToNextScreen()

    }

    /** this interface is working for calling apis respectively */
    interface  INearByMePresenter
    {
        fun onRequestNearByMeData(context : Context, pageNo : Int, size : Int)
        fun onRequestNearByMeDataOnLoadMore(context : Context, pageNo : Int, size : Int)

    }
}