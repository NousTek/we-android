package com.we.beyond.presenter.profile.myCampaign

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.CampaignApi
import com.we.beyond.api.GatheringApi
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DeleteCampaignPojo
import com.we.beyond.model.DeleteGatheringPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.MyCampaignPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of MyCampaign Activity
 * which implement all api regarding get and delete campaign  */
class MyCampaignImpl (myCampaign : MyCampaignPresenter.IMyCampaignView) : MyCampaignPresenter.IMyCampaignPresenter
{

    var myCampaign = myCampaign

    /** It calls getDataToPost method which takes below inputs  */
    override fun onMyCampaign(context: Context, pageNo: Int, size: Int, type: String) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, pageNo,size,type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls getDataToPostOnLoadMore method which takes below inputs  */
    override fun onMyCampaignOnLoadMore(context: Context, pageNo: Int, size: Int, type: String) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnLoadMore(context, pageNo,size,type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** It calls the getMyCampaign api which takes below inputs
     * and set MyCampaignAdapter
     */
    private fun getDataToPost(context: Context, pageNo :Int, size : Int, type : String)
    {
        try {
            val myCampaignApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<MyCampaignPojo> = myCampaignApi.getMyCampaign(pageNo, size,type)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<MyCampaignPojo>() {
                    override fun onSuccess(myCampaignPojo: MyCampaignPojo) {


                        ConstantMethods.cancleProgessDialog()

                        myCampaign.setMyCampaignAdapter(myCampaignPojo)

                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if(e is IOException){
                                ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(
                                    R.string.no_internet_sub_title))
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

    /** It calls the getMyCampaign api which takes below inputs
     * and notify MyCampaignAdapter
     */
    private fun getDataToPostOnLoadMore(context: Context, pageNo :Int, size : Int, type : String)
    {
        try {
            val myCampaignApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<MyCampaignPojo> = myCampaignApi.getMyCampaign(pageNo  ,size ,type)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<MyCampaignPojo>() {
                    override fun onSuccess(myCampaignPojo: MyCampaignPojo) {

                        ConstantMethods.cancleProgessDialog()

                        myCampaign.setMyCampaignOnLoadMore(myCampaignPojo)


                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if(e is IOException){
                                ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(
                                    R.string.no_internet_sub_title))
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

    /** It calls getDataToDelete method which takes below inputs  */
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

    /** It calls the deleteCampaign api which takes below inputs
     * and notify MyCampaignAdapter
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

                            myCampaign.setOnDelete(deleteCampaignPojo.data._id)

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