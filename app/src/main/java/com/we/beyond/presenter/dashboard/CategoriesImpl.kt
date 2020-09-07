package com.we.beyond.presenter.dashboard

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.CategoriesApi
import com.we.beyond.api.RegistrationApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.interceptor.ApplicationController.Companion.retrofit
import com.we.beyond.model.Categories
import com.we.beyond.model.CategoriesPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.RegistrationPojo
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

/**
 * this class is implementing category apis.
 */
class CategoriesImpl (category : CategoryPresenter.ICategoriesView) : CategoryPresenter.ICategoriesPresenter

{
    var categories = category

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

    /** call api to get all categories from server */
    fun getCategories(context: Context)
    {
        try {
            val categoriesApi = retrofit.create(CategoriesApi::class.java)
            val response: Single<CategoriesPojo> = categoriesApi.getAllCategories()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<CategoriesPojo>() {
                    override fun onSuccess(categoriesPojo: CategoriesPojo) {
                        ConstantMethods.cancleProgessDialog()

                        if (categoriesPojo != null) {

                            if (categoriesPojo != null && categoriesPojo.data.size > 0) {

                                /** set categories to category adapter  */
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

    override fun onPostCategories(context: Context, jsonObject: JsonObject)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPost(context,jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * call api to post selected category which user interested
     */
    fun getDataToPost(context: Context, jsonObject: JsonObject)
    {
        try {
            val categoriesApi = retrofit.create(CategoriesApi::class.java)
            val response: Single<CategoriesPojo> = categoriesApi.postInterestedCategories(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<CategoriesPojo>() {
                    override fun onSuccess(categoriesPojo: CategoriesPojo) {
                        //if (categoriesPojo != null) {

                            ConstantMethods.cancleProgessDialog()

                            EasySP.init(context).putBoolean(ConstantEasySP.SP_IS_CATEGORY_SELECTED, true)

                            categories.goToNextScreen()



                       // }
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

    override fun onBackClick()
    {
        categories.goToPreviousScreen()
    }




}