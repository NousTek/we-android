package com.we.beyond.presenter.reportResolved

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.IssueResolvedApi
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It implements the apis regarding get user list, report resolution and updation */
class ReportResolvedImpl (reportResolved : ReportResolvedPresenter.IReportResolvedView) : ReportResolvedPresenter.IReportResolvedPresenter
{

    var reportResolved = reportResolved


    /** It calls getDataToPost method which takes context and json object as input  */
    override fun onRequestReportResolvedData(context: Context, jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the reportResolved api which takes json object as parameter
     * and show success dialog
     */
    private fun getDataToPost(context: Context, jsonObject: JsonObject) {
        try {
            val reportedResolvedApi = ApplicationController.retrofit.create(IssueResolvedApi::class.java)
            val response: Single<ReportedResolutionPojo> = reportedResolvedApi.reportResolved(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ReportedResolutionPojo>() {
                    override fun onSuccess(reportResolutionPojo: ReportedResolutionPojo) {


                        ConstantMethods.cancleProgessDialog()

                        if (reportResolutionPojo != null) {

                            showSuccess(context, "Successful", reportResolutionPojo.message)

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


    /** It calls getDataToPostUpdate method which takes context and json object as input  */
    override fun onRequestReportResolvedDataUpdate(context: Context, jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostUpdate(context, jsonObject)
            }
        } catch (e: Exception) {
        e.printStackTrace()
        }

    }

    /** It calls the updateResolution api which takes json object as parameter
     * and show success dialog
     */
    private fun getDataToPostUpdate(context: Context, jsonObject: JsonObject) {
        try {
            val reportedResolvedApi = ApplicationController.retrofit.create(IssueResolvedApi::class.java)
            val response: Single<UpdateReportResolutionPojo> = reportedResolvedApi.updateResolution(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateReportResolutionPojo>() {
                    override fun onSuccess(updateReportResolutionPojo: UpdateReportResolutionPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (updateReportResolutionPojo != null) {

                            showSuccess(context, "Successful", updateReportResolutionPojo.message)

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

    /** It calls getUserList method which takes context as input  */
    override fun OnRequestUserList(context: Context)
    {
        try{

                if (ConstantMethods.checkForInternetConnection(context)) {
                    getUserList(context)
                }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }


    /** It calls the getUsersList api and sets UserListAdapter
     */
    private fun  getUserList(context: Context)
    {
        try {
            val userApi = ApplicationController.retrofit.create(IssueResolvedApi::class.java)
            val response: Single<ReportedUserData> = userApi.getUsersList()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ReportedUserData>() {
                    override fun onSuccess(userPojo: ReportedUserData) {

                        ConstantMethods.cancleProgessDialog()

                        if (userPojo != null) {

                            reportResolved.setUserListAdapter(userPojo.data)

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

    /** It calls getDataToPostOnSearch method which takes below input  */
    override fun OnRequestUserListOnSearch(context: Context,searchText: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnSearch(context,searchText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the getUsersListOnSearch api which takes below parameter
     * and sets UserListAdapter
     */
    private fun getDataToPostOnSearch(context: Context,searchText : String)
    {
        try {
            val userApi = ApplicationController.retrofit.create(IssueResolvedApi::class.java)
            val response: Single<ReportedUserData> = userApi.getUsersListOnSearch(searchText)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ReportedUserData>() {
                    override fun onSuccess(userPojo: ReportedUserData) {

                        ConstantMethods.cancleProgessDialog()

                        if (userPojo != null) {

                            reportResolved.setUserListAdapter(userPojo.data)

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



    /** show success dialog */
    fun showSuccess(context: Context, title: String, message: String) {
        try {
            val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.titleText = title
            sweetAlertDialog.contentText = message
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()
                reportResolved.goToNextScreen()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}