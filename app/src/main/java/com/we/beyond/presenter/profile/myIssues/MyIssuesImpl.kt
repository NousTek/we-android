package com.we.beyond.presenter.profile.myIssues

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DeleteNearByIssueByIdDetailsPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.NearByIssuesPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of MyIssue Activity
 * which implement all api regarding get and delete issue  */
class MyIssuesImpl(myIssues: MyIssuesPresenter.IMyIssuesView) :
    MyIssuesPresenter.IMyIssuesPresenter {

    var myIssues = myIssues

    /** It calls getDataToPost method which takes below inputs  */
    override fun onMyIssues(context: Context, pageNo: Int, size: Int, type: String) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, pageNo, size, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls getDataToPostOnLoadMore method which takes below inputs  */
    override fun onMyIssuesOnLoadMore(context: Context, pageNo: Int, size: Int, type: String) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnLoadMore(context, pageNo, size, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getMyIssues api which takes below inputs
     * and set MyIssuesAdapter
     */
    private fun getDataToPost(context: Context, pageNo: Int, size: Int, type: String) {
        try {
            val myIssuesApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<NearByIssuesPojo> = myIssuesApi.getMyIssues(pageNo, size, type)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(myIssuePojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        myIssues.setMyIssuesAdapter(myIssuePojo)

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

    /** It calls the getMyIssues api which takes below inputs
     * and notify MyIssuesAdapter
     */
    private fun getDataToPostOnLoadMore(context: Context, pageNo: Int, size: Int, type: String) {
        try {
            val myIssueApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<NearByIssuesPojo> = myIssueApi.getMyIssues(pageNo, size, type)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByIssuesPojo>() {
                    override fun onSuccess(myIssuePojo: NearByIssuesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        myIssues.setMyIssuesOnLoadMore(myIssuePojo)


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

    /** It calls getDataToDelete method which takes below inputs  */
    override fun onDelete(
        context: Context,
        jsonObject: JsonObject
    ) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToDelete(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    /** It calls the deleteIssue api which takes below inputs
     * and notify MyIssuesAdapter
     */
    private fun getDataToDelete(
        context: Context,
        jsonObject: JsonObject
    ) {
        try {
            val issueApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<DeleteNearByIssueByIdDetailsPojo> =
                issueApi.deleteIssue(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DeleteNearByIssueByIdDetailsPojo>() {
                    override fun onSuccess(deleteNearByIssueByIdDetailsPojo: DeleteNearByIssueByIdDetailsPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (deleteNearByIssueByIdDetailsPojo != null) {


                            myIssues.setOnDelete(deleteNearByIssueByIdDetailsPojo.data._id)

                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if (e is IOException) {
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.no_internet_title),
                                    context.resources.getString(R.string.no_internet_sub_title)
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