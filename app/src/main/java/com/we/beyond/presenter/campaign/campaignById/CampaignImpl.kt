package com.we.beyond.presenter.campaign.campaignById

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.CampaignApi
import com.we.beyond.api.ConnectApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.CampaignPojo
import com.we.beyond.model.DeleteCampaignPojo
import com.we.beyond.model.DeleteConnectDetailsPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of campaignDetails Activity
 * which implement all api regarding get campaign and deletion */

class CampaignImpl (campaign : CampaignPresenter.ICampaignByIdView) : CampaignPresenter.ICampaignByIdPresenter
{
    var campaign = campaign

    /** It calls getDataToPost method which takes below inputs  */
    override fun onCampaignById(context: Context, CampaignId: String)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, CampaignId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getCampaignById api which takes json object as parameter
     * and set CampaignByIdAdapter
     */
    private fun getDataToPost(context: Context, campaignId: String)
    {
        try {
            val campaignApi = ApplicationController.retrofit.create(CampaignApi::class.java)
            val response: Single<CampaignPojo> = campaignApi.getCampaignById(campaignId)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<CampaignPojo>() {
                    override fun onSuccess(campaignPojo: CampaignPojo) {
                        ConstantMethods.cancleProgessDialog()
                        if (campaignPojo != null) {


                            campaign.setCampaignByIdAdapter(campaignPojo)

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

        }
    }

    /** It calls the deleteCampaign api which takes json object as parameter
     */
    private fun getDataToDelete(
        context: Context,
        jsonObject: JsonObject
    )

    {
        try {
            val campaignApi = ApplicationController.retrofit.create(CampaignApi::class.java)
            val response: Single<DeleteCampaignPojo> = campaignApi.deleteCampaign(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DeleteCampaignPojo>() {
                    override fun onSuccess(deleteCampaignPojo: DeleteCampaignPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (deleteCampaignPojo != null) {

                            campaign.setOnDelete()

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
