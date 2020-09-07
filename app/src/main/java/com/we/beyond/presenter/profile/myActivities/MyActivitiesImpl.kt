package com.we.beyond.presenter.profile.myActivities

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.MyActivityPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of MyActivities Activity
 * which implement all api regarding get activities */

class MyActivitiesImpl(myActivity: MyActivitiesPresenter.IMyActivitiesView) :
    MyActivitiesPresenter.IMyActivitiesPresenter {

    var myActivity = myActivity

    /** It calls getMyActivityData method with below inputs */
    override fun onRequestMyActivitiesData(context: Context, pageNo: Int, size: Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getMyActivityData(context, pageNo, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getMyActivity api with below inputs
     * and set MyActivitiesDataAdapter
     */
    private fun getMyActivityData(context: Context, pageNo: Int, size: Int) {
        try {
            val myActivityApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<MyActivityPojo> = myActivityApi.getMyActivity(pageNo, size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<MyActivityPojo>() {
                    override fun onSuccess(activityPojo: MyActivityPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (activityPojo != null) {

                            myActivity.setMyActivitiesDataAdapter(activityPojo)

                        }
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


    /** It calls getMyActivityDataOnLoadMore method with below inputs */
    override fun onRequestMyActivitiesDataOnLoadMore(context: Context, pageNo: Int, size: Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getMyActivityDataOnLoadMore(context, pageNo, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getMyActivity api with below inputs
     * and notify MyActivitiesDataAdapter
     */
    private fun getMyActivityDataOnLoadMore(context: Context, pageNo: Int, size: Int) {
        try {
            val myActivityApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<MyActivityPojo> = myActivityApi.getMyActivity(pageNo, size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<MyActivityPojo>() {
                    override fun onSuccess(activityPojo: MyActivityPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (activityPojo != null) {

                            myActivity.setMyActivitiesDataAdapterOnLoadMore(activityPojo)

                        }
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