package com.we.beyond.presenter.profile.myResolution

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
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

/** It is implementation class of MyResolution Activity
 * which implement all api regarding get resolution data  */
class MyResolutionImpl(myResolution: MyResolutionPresenter.IMyResolutionView) :
    MyResolutionPresenter.IMyResolutionPresenter {

    var myResolution = myResolution

    /** It calls getDataToPost method which takes below inputs  */
    override fun onMyResolution(context: Context, pageNo: Int, size: Int) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, pageNo, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls getDataToPostOnLoadMore method which takes below inputs  */
    override fun onMyResolutionOnLoadMore(context: Context, pageNo: Int, size: Int) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnLoadMore(context, pageNo, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getMyResolution api which takes below inputs
     * and set MyResolutionAdapter
     */
    private fun getDataToPost(context: Context, pageNo: Int, size: Int) {
        try {
            val reportedResolutionApi =
                ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<NearByIssuesPojo> =
                reportedResolutionApi.getMyResolution(pageNo, size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuePojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        myResolution.setMyResolutionAdapter(nearByIssuePojo)

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

    /** It calls the getMyResolution api which takes below inputs
     * and notify MyResolutionAdapter
     */
    private fun getDataToPostOnLoadMore(context: Context, pageNo: Int, size: Int) {
        try {
            val reportedResolutionApi =
                ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<NearByIssuesPojo> =
                reportedResolutionApi.getMyResolution(pageNo, size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(nearByIssuePojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        myResolution.setMyResolutionOnLoadMore(nearByIssuePojo)


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