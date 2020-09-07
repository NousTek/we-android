package com.we.beyond.presenter.profile.reportedResolution

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.NearByIssueApi
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.NearByIssuesPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of ReportedResolution Activity
 * which implement all api regarding get reported resolution data  */
class ReportedResolutionImpl(reportedResolution: ReportedResolutionPresenter.IReportedResolutionView) :
    ReportedResolutionPresenter.IReportedResolutionPresenter {

    var reportedResolution = reportedResolution

    /** It calls getDataToPost method which takes below inputs  */
    override fun onReportedResolution(context: Context, pageNo: Int, size: Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, pageNo, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls getDataToPostOnLoadMore method which takes below inputs  */
    override fun onReportedResolutionOnLoadMore(context: Context, pageNo: Int, size: Int) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnLoadMore(context, pageNo, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getMyReportedResolution api which takes below inputs
     * and set ReportedResolutionAdapter
     */
    private fun getDataToPost(context: Context, pageNo: Int, size: Int) {
        try {
            val reportedResolutionApi =
                ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<NearByIssuesPojo> =
                reportedResolutionApi.getMyReportedResolution(pageNo, size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuePojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        reportedResolution.setReportedResolutionAdapter(nearByIssuePojo)

                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if (e is IOException) {
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.no_internet_title),
                                    context.resources.getString(
                                        R.string.no_internet_sub_title
                                    )
                                )
                            } else {
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

    /** It calls the getMyReportedResolution api which takes below inputs
     * and notify ReportedResolutionAdapter
     */
    private fun getDataToPostOnLoadMore(context: Context, pageNo: Int, size: Int) {
        try {
            val reportedResolutionApi =
                ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<NearByIssuesPojo> =
                reportedResolutionApi.getMyReportedResolution(pageNo, size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuePojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        reportedResolution.setReportedResolutionOnLoadMore(nearByIssuePojo)


                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if (e is IOException) {
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.no_internet_title),
                                    context.resources.getString(
                                        R.string.no_internet_sub_title
                                    )
                                )
                            } else {
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