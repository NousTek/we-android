package com.we.beyond.presenter.dashboard

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.Categories
import com.we.beyond.model.CategoriesPojo
import com.we.beyond.model.RegistrationPojo
import com.we.beyond.model.UserTypes
import org.json.JSONObject

interface CategoryPresenter
{
    /** this interface is working to show result on views */
    interface ICategoriesView
    {
        fun setCategoriesAdapter( categories : CategoriesPojo)
        fun goToNextScreen()
        fun goToPreviousScreen()
    }

    /** this interface is working for calling get all categories and post selected category to server */
    interface  ICategoriesPresenter
    {
        fun onRequestCategories( context : Context)
        fun onPostCategories(context: Context, jsonObject: JsonObject)
        fun onBackClick()
    }
}