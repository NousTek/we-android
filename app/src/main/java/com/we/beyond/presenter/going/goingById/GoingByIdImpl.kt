package com.we.beyond.presenter.going.goingById

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.GoingApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.GoingIdPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

class GoingByIdImpl(going: GoingByIdPresenter.IGoingByIdView) :
    GoingByIdPresenter.IGoingByIdPresenter {
    var going = going

    /** It calls getDataToPostById method with below inputs  */
    override fun onGoingById(context: Context, pageNo: Int, size: Int, goingId: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostById(context, pageNo, size, goingId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getGoingListById api with below parameters
     * and set GoingIdAdapter
     */
    private fun getDataToPostById(context: Context, pageNo: Int, size: Int, goingId: String) {
        try {
            val goingApi = ApplicationController.retrofit.create(GoingApi::class.java)
            val response: Single<GoingIdPojo> = goingApi.getGoingListById(pageNo, size, goingId)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GoingIdPojo>() {
                    override fun onSuccess(goingIdPojo: GoingIdPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (goingIdPojo != null) {

                            going.onGoingIdAdapter(goingIdPojo)


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

    /** It calls getDataToPostByIdOnLoadMore method with below inputs  */
    override fun onGoingByIdOnLoadMore(context: Context, pageNo: Int, size: Int, goingId: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostByIdOnLoadMore(context, pageNo, size, goingId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getGoingListById api with below parameters
     * and notify GoingIdAdapter
     */
    private fun getDataToPostByIdOnLoadMore(
        context: Context,
        pageNo: Int,
        size: Int,
        goingId: String
    ) {
        try {
            val goingApi = ApplicationController.retrofit.create(GoingApi::class.java)
            val response: Single<GoingIdPojo> = goingApi.getGoingListById(pageNo, size, goingId)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GoingIdPojo>() {
                    override fun onSuccess(goingIdPojo: GoingIdPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (goingIdPojo != null) {


                            going.onGoingIdAdapterOnLoadMore(goingIdPojo)


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