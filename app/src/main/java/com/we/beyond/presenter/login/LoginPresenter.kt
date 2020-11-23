package com.we.beyond.presenter.login

import android.content.Context
import com.google.gson.JsonObject


interface LoginPresenter
{
    /** this interface and functions are showing result on view */
    interface ILoginView
    {

        fun goToNextScreen()
        fun goToCategoriesScreen()
        fun registerExternalUser(email :String, firstName:String, lastName:String)
    }

    /** this interface is working for calling login and forgot password api respectively */
    interface  ILoginPresenter
    {

        fun onLogin(context: Context, jsonObject: JsonObject)
        fun onForgotPassword(context: Context, jsonObject: JsonObject)
        fun onFbLogin(context: Context, jsonObject: JsonObject)
        fun onGoogleLogin(context: Context, jsonObject: JsonObject)
    }
}