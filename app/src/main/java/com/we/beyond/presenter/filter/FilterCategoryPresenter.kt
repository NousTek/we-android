package com.we.beyond.presenter.filter

import android.content.Context
import com.we.beyond.model.NearByCategoriesPojo


interface FilterCategoryPresenter
{
    /** this interface and functions are showing result on view */
    interface IFilterCategoryView
    {
        fun setCategoriesAdapter( categories : NearByCategoriesPojo )


    }

    /** this interface is working for calling respective apis respectively */
    interface  IFilterCategoryPresenter
    {
        fun onRequestCategories( context : Context )


    }
}