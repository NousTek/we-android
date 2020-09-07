package com.we.beyond.presenter.going.goingById

import android.content.Context
import com.we.beyond.model.GoingIdPojo

interface GoingByIdPresenter
{
    /** this interface and functions are showing result on view */
    interface IGoingByIdView
    {
        fun onGoingIdAdapter(goingIdDetails : GoingIdPojo)
        fun onGoingIdAdapterOnLoadMore(goingIdDetails : GoingIdPojo)
    }

    /** this interface is working for calling respected apis */
    interface  IGoingByIdPresenter
    {

         fun onGoingById(context: Context,pageNo : Int,size : Int,goingId : String)
         fun onGoingByIdOnLoadMore(context: Context,pageNo : Int,size : Int,goingId : String)

    }
}