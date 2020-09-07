package com.we.beyond.presenter.gathering.gatheringByIssueId

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.GatheringApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DeleteGatheringPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.GatheringDetails
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of gatheringDetails Activity
 * which implement all api regarding get gathering and deletion */
class GatheringByIdImpl(gatheringById : GatheringByIdPresenter.IGatheringByIdView) : GatheringByIdPresenter.IGatheringByIdPresenter
{


    val gatheringById = gatheringById

    /** It calls getDataToPost method which takes below inputs  */
    override fun onGatheringById(context: Context, gatheringId: String)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, gatheringId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It calls the getGatheringById api which takes json object as parameter
     * and set GatheringByIdAdapter
     */
    private fun getDataToPost(context: Context, gatheringId: String) {

        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<GatheringDetails> = gatheringApi.getGatheringById(gatheringId)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<GatheringDetails>() {
                    override fun onSuccess(gatheringDetailsPojoPojo: GatheringDetails) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringDetailsPojoPojo != null) {

                            gatheringById.setGatheringByIdAdapter(gatheringDetailsPojoPojo)

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

    /** It calls the deleteGathering api which takes json object as parameter
     */
    private fun getDataToDelete(
        context: Context,
        jsonObject: JsonObject )

    {
        try {
            val gatheringApi = ApplicationController.retrofit.create(GatheringApi::class.java)
            val response: Single<DeleteGatheringPojo> = gatheringApi.deleteGathering(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<DeleteGatheringPojo>() {
                    override fun onSuccess(gatheringDetailsPojoPojo: DeleteGatheringPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (gatheringDetailsPojoPojo != null) {

                            gatheringById.setOnDelete()

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


