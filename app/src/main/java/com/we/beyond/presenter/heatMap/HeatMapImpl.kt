package com.we.beyond.presenter.heatMap

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.HeatMapApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.HeatMapPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of heat map Activity
 * which implement all api regarding get heatmap  */
class HeatMapImpl(heatMap: HeatMapPresenter.IHeatMapView) : HeatMapPresenter.IHeatMapPresenter {


    var heatMap = heatMap


    /** It calls getLocationList method which takes below inputs  */
    override fun OnRequestHeatMapList(context: Context, latitude: String, longitude: String) {
        try {
            getLocationList(context, latitude, longitude)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getHeatMap api which takes below inputs
     * and set LocationListAdapter
     */
    private fun getLocationList(
        context: Context,
        latitude: String,
        longitude: String
    ) {
        try {
            val mapApi = ApplicationController.retrofit.create(HeatMapApi::class.java)
            val response: Single<HeatMapPojo> = mapApi.getHeatMap(latitude, longitude)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<HeatMapPojo>() {
                    override fun onSuccess(mapPojo: HeatMapPojo) {
                        ConstantMethods.cancleProgessDialog()

                        if (mapPojo != null) {

                            heatMap.setLocationListAdapter(mapPojo.data)

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