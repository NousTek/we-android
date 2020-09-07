package com.we.beyond.presenter.reportAbuse

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ReportAbuseApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.ReportAbusePojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It implements report abuse api */
class ReportAbuseImpl (reportAbuse : ReportAbusePresenter.IReportAbuseView) : ReportAbusePresenter.IReportAbusePresenter
{

    var reportAbuse = reportAbuse

    /** It calls getDataToPost method which takes below input  */
    override fun onReportAbuse(context: Context, jsonObject: JsonObject) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the ReportAbuse api which takes below parameter
     */
    private fun getDataToPost(context: Context, jsonObject: JsonObject)
    {
        try {
            val reportAbuseApi = ApplicationController.retrofit.create(ReportAbuseApi::class.java)
            val response: Single<ReportAbusePojo> = reportAbuseApi.ReportAbuse(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ReportAbusePojo>() {
                    override fun onSuccess(reportAbusePojo: ReportAbusePojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (reportAbusePojo != null) {

                           ConstantMethods.showWarning(context,"Successful",reportAbusePojo.message)


                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if(e is IOException){
                                ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(
                                    R.string.no_internet_sub_title))
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

        } catch (e: Exception) {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(
                context,
                context.resources.getString(R.string.error_title),
                context.resources.getString(
                    R.string.error_message
                )
            )
        }
    }



}