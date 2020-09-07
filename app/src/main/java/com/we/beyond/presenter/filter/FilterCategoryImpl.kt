package com.we.beyond.presenter.filter

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.FilterCategoiesApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.NearByCategoriesPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException
/** It is implementation class of FilterCategory and MyDefaultCategory Activity
 * which implement all api regarding get category  */

class FilterCategoryImpl(category : FilterCategoryPresenter.IFilterCategoryView) : FilterCategoryPresenter.IFilterCategoryPresenter

{


    var categories = category

    /** It calls getCategories method  */
    override fun onRequestCategories(context: Context)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getCategories(context)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It calls the getCategories api
     * and set CategoriesAdapter
     */
    fun getCategories(context: Context)
    {
        try {
            val categoriesApi = ApplicationController.retrofit.create(FilterCategoiesApi::class.java)
            val response: Single<NearByCategoriesPojo> = categoriesApi.getCategories()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByCategoriesPojo>() {
                    override fun onSuccess(categoriesPojo: NearByCategoriesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (categoriesPojo != null) {

                            if (categoriesPojo != null && categoriesPojo.categories.size > 0) {
                                categories.setCategoriesAdapter(categoriesPojo)

                            } else {

                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if(e is IOException){
                                ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(R.string.no_internet_sub_title))
                            }
                            else {
                                val exception: HttpException = e as HttpException
                                val er: String = exception.response()!!.errorBody()!!.string()
                                val errorPojo: ErrorPojo =
                                    Gson().fromJson(er, ErrorPojo::class.java)

                                if (errorPojo != null) {
                                    if (errorPojo.error.isNotEmpty()) {
                                        if (errorPojo.message.isNotEmpty()) {
                                            ConstantMethods.showError(
                                                context,
                                                errorPojo.error,
                                                errorPojo.message
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        catch (e : Exception)
                        {
                            ConstantMethods.cancleProgessDialog()
                            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                                R.string.error_message))

                        }
                    }

                })

        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }

    }


}