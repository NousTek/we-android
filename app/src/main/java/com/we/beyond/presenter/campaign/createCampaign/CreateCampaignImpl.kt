package com.we.beyond.presenter.campaign.createCampaign

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.CampaignApi
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

/** It is implementation class of Create Campaign Activity
 * which implement all api regarding campaign creation and updation  */

class CreateCampaignImpl (createCampaign : CreateCampaignPresenter.ICreateCampaignView) : CreateCampaignPresenter.ICreateCampaignPresenter
{
    var createCampaign = createCampaign

    /** It calls getDataToPost method which takes context and json object as input  */
    override fun onCampaignCreated(context: Context, jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the createCampaign api which takes json object as parameter
     * and show the success dialog
     */
    private fun getDataToPost(context: Context, jsonObject: JsonObject) {
        try {
            val campaignApi = ApplicationController.retrofit.create(CampaignApi::class.java)
            val response: Single<CampaignPostPojo> = campaignApi.createCampaign(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<CampaignPostPojo>() {
                    override fun onSuccess(createCampaignPojo: CampaignPostPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (createCampaignPojo != null) {

                            showSuccess(context, "Successful", createCampaignPojo.message)

                            EasySP.init(context).remove(ConstantEasySP.ISSUE_ID)
                            EasySP.init(context).remove(ConstantEasySP.ISSUE_NUMBER)
                            EasySP.init(context).remove(ConstantEasySP.CAMPAIGN_DATE)
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
    override fun onUpdateCampaign(context: Context, jsonObject: JsonObject) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostUpdate(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the updateCampaign api which takes json object as parameter
     * and show the success dialog
     */
    private fun getDataToPostUpdate(context: Context, jsonObject: JsonObject) {
        try {
            val campaignApi = ApplicationController.retrofit.create(CampaignApi::class.java)
            val response: Single<UpdateCampaignPojo> = campaignApi.updateCampaign(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateCampaignPojo>() {
                    override fun onSuccess(updateCampaignPojo: UpdateCampaignPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (updateCampaignPojo != null) {


                            showSuccess(context, "Successful", updateCampaignPojo.message)

                            EasySP.init(context).remove(ConstantEasySP.ISSUE_ID)
                            EasySP.init(context).remove(ConstantEasySP.ISSUE_NUMBER)
                            EasySP.init(context).remove(ConstantEasySP.CAMPAIGN_DATE)
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
                createCampaign.goToNextScreen()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}