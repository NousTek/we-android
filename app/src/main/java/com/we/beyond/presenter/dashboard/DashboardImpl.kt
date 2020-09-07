package com.we.beyond.presenter.dashboard

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.DashboardApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DashboardPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Dashboard Activity
 * which implement all api regarding summary for dashboard */
class DashboardImpl (dashboard : DashboardPresenter.IDashboardView) : DashboardPresenter.IDashboardPresenter
{


    var dashboard = dashboard

    /** It calls getDataToPost method which takes context, latitude and longitude */
    override fun getSummary(context: Context, latitude: String, longitude: String)

    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPost(context,latitude,longitude)
            }
        }
        catch (e : Exception)
        {
                e.printStackTrace()
        }
    }

    /** It calls the summary api which takes latitude and longitude as parameter
     * and set data to dashboard activity
     */
    private fun getDataToPost(context: Context, latitude: String, longitude: String)
    {
        try {
            val dashboardApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<DashboardPojo> = dashboardApi.getSummary(latitude,longitude)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DashboardPojo>() {
                    override fun onSuccess(dashboardPojo: DashboardPojo) {

                        ConstantMethods.cancleProgessDialog()
                        if (dashboardPojo != null) {

                            dashboard.setSummaryData(dashboardPojo)

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

    /** It calls getSummaryOnResume method which takes context, latitude and longitude */
    override fun getSummaryOnResume(context: Context, latitude: String, longitude: String)

    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPostOnResume(context,latitude,longitude)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the summary api which takes latitude and longitude as parameter
     * and set data to dashboard activity after resume the activity
     */
    private fun getDataToPostOnResume(context: Context, latitude: String, longitude: String)
    {
        try {
            val dashboardApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<DashboardPojo> = dashboardApi.getSummary(latitude,longitude)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DashboardPojo>() {
                    override fun onSuccess(dashboardPojo: DashboardPojo) {
                        ConstantMethods.cancleProgessDialog()
                        if (dashboardPojo != null) {

                            println("dashboard $dashboardPojo")
                            dashboard.setSummeryDataOnResume(dashboardPojo)

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