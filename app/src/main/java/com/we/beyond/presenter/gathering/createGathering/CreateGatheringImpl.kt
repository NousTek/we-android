package com.we.beyond.presenter.gathering.createGathering

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.GatheringApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Create Gathering Activity
 * which implement all api regarding gathering creation and updation */
class CreateGatheringImpl (createGathering : CreateGatheringPresenter.ICreateGatheringView) : CreateGatheringPresenter.ICreateGatheringPresenter
{
    var createGathering = createGathering
    val imagesSet = HashSet<String>()



    /** It calls getDataToPost method which takes context and json object as input  */
    override fun onGatheringCreated(context: Context, jsonObject: JsonObject)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }
    }

    /** It calls the createGathering api which takes json object as parameter
     * and show the success dialog
     */
    private fun getDataToPost(context: Context, jsonObject: JsonObject) {
        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<GatheringPojo> = gatheringApi.createGathering(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GatheringPojo>() {
                    override fun onSuccess(createGatheringPojo: GatheringPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (createGatheringPojo != null) {


                            showSuccess(context, "Successful", createGatheringPojo.message)

                            EasySP.init(context).remove(ConstantEasySP.ISSUE_ID)
                            EasySP.init(context).remove(ConstantEasySP.ISSUE_NUMBER)
                            EasySP.init(context).remove(ConstantEasySP.GATHERING_DATE)
                            EasySP.init(context).getString(ConstantEasySP.ISSUE_TITLE)
                            EasySP.init(context).getString(ConstantEasySP.SELECTED_GATHERING_ADDRESS)
                            EasySP.init(context).getString("city")
                            EasySP.init(context).getString("lat")
                            EasySP.init(context).getString("long")
                            EasySP.init(context).remove("uploadedImage")
                            EasySP.init(context).remove("gatheringTitle")
                            EasySP.init(context).remove("gatheringDetails")

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
    override fun onGatheringUpdated(context: Context, jsonObject: JsonObject)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostUpdate(context, jsonObject)
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }
    }


    /** It calls the updateGathering api which takes json object as parameter
     * and show the success dialog
     */
    private fun getDataToPostUpdate(context: Context, jsonObject: JsonObject) {
        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<UpdateGatheringPojo> = gatheringApi.updateGathering(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateGatheringPojo>() {
                    override fun onSuccess(updateGatheringPojo: UpdateGatheringPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (updateGatheringPojo != null) {


                            showSuccess(context, "Successful", updateGatheringPojo.message)

                            EasySP.init(context).remove(ConstantEasySP.ISSUE_ID)
                            EasySP.init(context).remove(ConstantEasySP.ISSUE_NUMBER)
                            EasySP.init(context).remove(ConstantEasySP.GATHERING_DATE)
                            EasySP.init(context).getString(ConstantEasySP.ISSUE_TITLE)
                            EasySP.init(context).getString(ConstantEasySP.SELECTED_GATHERING_ADDRESS)
                            EasySP.init(context).getString("city")
                            EasySP.init(context).getString("lat")
                            EasySP.init(context).getString("long")
                            EasySP.init(context).remove("uploadedImage")
                            EasySP.init(context).remove("gatheringTitle")
                            EasySP.init(context).remove("gatheringDetails")

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
                createGathering.goToNextScreen()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}