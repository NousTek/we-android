package com.we.beyond.presenter.issues.submitIssue

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.NearByIssueApi
import com.we.beyond.api.SubmitIssueApi
import com.we.beyond.interceptor.ApplicationController.Companion.retrofit
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.SubmitIssuePojo
import com.we.beyond.model.UpdateIssuePojo
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Submit An Issue Activity
 * which implement all api regarding submit and update an issue */
class SubmitIssueImpl(submitIssue: SubmitIssuePresenter.ISubmitIssueView) :
    SubmitIssuePresenter.ISubmitIssuePresenter {


    var submitIssue = submitIssue
    val imagesSet = HashSet<String>()

    /** It calls getDataToPost method which takes context and json object as input  */
    override fun onIssueCreated(context: Context, jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the submit issue api which takes json object as parameter
     * and show the success dialog
     */
    private fun getDataToPost(context: Context, jsonObject: JsonObject) {
        try {
            val issueApi = retrofit.create(SubmitIssueApi::class.java)
            val response: Single<SubmitIssuePojo> = issueApi.submitIssue(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<SubmitIssuePojo>() {
                    override fun onSuccess(submitIssuePojo: SubmitIssuePojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (submitIssuePojo != null) {


                            showSuccess(context, "Successful", submitIssuePojo.message)


                            EasySP.init(context).remove("uploadedImage")

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
    override fun onIssueUpdated(context: Context, jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostUpdate(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the update issue api which takes json object as parameter
     * and show the success dialog
     */
    private fun getDataToPostUpdate(context: Context, jsonObject: JsonObject) {
        try {
            val issueApi = retrofit.create(NearByIssueApi::class.java)
            val response: Single<UpdateIssuePojo> = issueApi.updateIssue(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateIssuePojo>() {
                    override fun onSuccess(updateIssuePojo: UpdateIssuePojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (updateIssuePojo != null) {

                            showSuccess(context, "Successful", updateIssuePojo.message)

                            //  EasySP.init(context).putStringSet("uploadedImage",null)
                            EasySP.init(context).remove("uploadedImage")

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


    /** It shows the success dialog using sweet alert dialog */
    fun showSuccess(context: Context, title: String, message: String) {
        try {
            val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.titleText = title
            sweetAlertDialog.contentText = message
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()
                submitIssue.goToNextScreen()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}