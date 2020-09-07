package com.we.beyond.presenter.profile.updateCategory

import android.content.Context
import com.google.gson.JsonObject

interface UpdateUserDefaultCategoryPresenter
{
    /** this interface and functions are showing result on view */
    interface IUpdateUserDefaultCategoryView
    {

        fun goToNextScreen()

    }

    /** this interface is working for calling respected apis */
    interface  IUpdateUserDefaultCategoryPresenter
    {

        fun onUpdateCategories(context: Context, jsonObject: JsonObject)

    }

}