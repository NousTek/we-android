package com.we.beyond.presenter.issues.nearByIssues

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ConnectApi
import com.we.beyond.api.NearByIssueApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DeleteConnectDetailsPojo
import com.we.beyond.model.DeleteNearByIssueByIdDetailsPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.NearByIssueByIdDetailsPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of NearByIssueDetails and HeatMap Activity
 * which implement all api regarding get issue and deletion etc  */
class NearByIssueByIdImpl( nearByIssueById : NearByIssueByIdPresenter.INearByIssueByIdView) :
    NearByIssueByIdPresenter.INearByIssueByIdPresenter
{
    var nearByIssueById = nearByIssueById

    /** It calls getDataToPost method which takes below inputs  */
    override fun onNearByIssueById(context: Context, nearByIssueId : String) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, nearByIssueId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getNearByIssueById api which takes json object as parameter
     * and set NearByIssueByIdAdapter
     */
        private fun getDataToPost(context: Context, nearByIssueId: String)
        {

            try {
                val issueApi = ApplicationController.retrofit.create(NearByIssueApi::class.java)
                val response: Single<NearByIssueByIdDetailsPojo> = issueApi.getNearByIssueById(nearByIssueId)
                response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : DisposableSingleObserver<NearByIssueByIdDetailsPojo>() {
                        override fun onSuccess(nearByIssuePojo: NearByIssueByIdDetailsPojo) {

                            println("issue details $nearByIssuePojo")

                            ConstantMethods.cancleProgessDialog()

                            if (nearByIssuePojo != null) {

                                nearByIssueById.setNearByIssueByIdAdapter(nearByIssuePojo)

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
                ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString( R.string.error_message))

            }
        }


    /** It calls getDataToDelete method which takes context and json object as input  */
    override fun onDelete(
        context: Context,
        jsonObject: JsonObject
    ) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToDelete(context,jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the deleteIssue api which takes json object as parameter
     */
    private fun getDataToDelete(
        context: Context,
        jsonObject: JsonObject
    )

    {
        try {
            val issueApi = ApplicationController.retrofit.create(NearByIssueApi::class.java)
            val response: Single<DeleteNearByIssueByIdDetailsPojo> = issueApi.deleteIssue(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DeleteNearByIssueByIdDetailsPojo>() {
                    override fun onSuccess(deleteNearByIssueByIdDetailsPojo: DeleteNearByIssueByIdDetailsPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (deleteNearByIssueByIdDetailsPojo != null) {


                            nearByIssueById.setOnDelete()

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

