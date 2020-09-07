package com.we.beyond.presenter.dashboard

import android.content.Context
import com.we.beyond.model.NotificationPojo

interface NotificationPresenter
{
    /** this interface and functions are showing result on view */
    interface INotificationView
    {
        fun setNotificationDataAdapter( notificationData : NotificationPojo)
        fun setNotificationDataAdapterOnLoadMore( notificationData : NotificationPojo)

    }

    /** this interface is working for calling respected apis */
    interface  INotificationPresenter
    {
        fun onRequestNotificationData(context : Context, pageNo : Int, size : Int)
        fun onRequestNotificationDataOnLoadMore( context : Context,pageNo : Int, size : Int)
        fun onRequestNotificationFalse(context: Context)

    }
}