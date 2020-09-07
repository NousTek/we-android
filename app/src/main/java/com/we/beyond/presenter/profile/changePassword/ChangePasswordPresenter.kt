package com.we.beyond.presenter.profile.changePassword

import android.content.Context
import com.google.gson.JsonObject

interface ChangePasswordPresenter
{
    /** this interface and functions are showing result on view */
    interface IChangePasswordView
    {
        fun setChangePasswordData()


    }

    /** this interface is working for calling respected apis */
    interface  IMyActivitiesPresenter
    {
        fun onRequestChangePasswordData(context : Context, jsonObject: JsonObject)


    }
}