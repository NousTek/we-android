package com.we.beyond.presenter.nearByMe

import android.content.Context
import android.content.Intent
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.DashboardApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.NearByMeUsersPojo
import com.we.beyond.model.NotifyServer
import com.we.beyond.presenter.dashboard.NotificationPresenter
import com.we.beyond.ui.profile.EditProfileActivity
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of NearByMe Activity
 * which implement all api regarding get near by me location */

class NearByMeImpl (nearByMe : NearByMePresenter.INearByMeView) : NearByMePresenter.INearByMePresenter
{

    var nearByMe = nearByMe


    /** It calls getNearByMeData method which takes below inputs  */
    override fun onRequestNearByMeData(context: Context, pageNo: Int, size: Int) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getNearByMeData(context,pageNo,size)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getNearByMeUsersList api which takes below parameter
     * and set NearByMeDataAdapter
     */
    private fun getNearByMeData(context: Context, pageNo: Int, size: Int)
    {
        try {
            val nearByUsersApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NearByMeUsersPojo> = nearByUsersApi.getNearByMeUsersList(pageNo,size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByMeUsersPojo>() {
                    override fun onSuccess(nearByUsersPojo: NearByMeUsersPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (nearByUsersPojo != null) {

                            nearByMe.setNearByMeDataAdapter(nearByUsersPojo)

                        }
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
                                            /*ConstantMethods.showError(
                                                context,
                                                errorPojo.error,
                                                errorPojo.message
                                            )*/


                                            try {
                                                val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                                                sweetAlertDialog.titleText = errorPojo.error
                                                sweetAlertDialog.contentText = errorPojo.message
                                                sweetAlertDialog.show()
                                                sweetAlertDialog.setCancelable(false)
                                                sweetAlertDialog.setConfirmClickListener {
                                                    sweetAlertDialog.dismissWithAnimation()

                                                    nearByMe.goToNextScreen()



                                                }

                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
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


    /** It calls getNearByMenDataOnLoadMore method which takes below inputs  */
    override fun onRequestNearByMeDataOnLoadMore(context: Context, pageNo: Int, size: Int)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getNearByMenDataOnLoadMore(context,pageNo,size)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getNearByMeUsersList api which takes below parameter
     * and notify NearByMeDataAdapter
     */
    private fun getNearByMenDataOnLoadMore(context: Context, pageNo: Int, size: Int) {
        try {
            val nearByUsersApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NearByMeUsersPojo> = nearByUsersApi.getNearByMeUsersList(pageNo,size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NearByMeUsersPojo>() {
                    override fun onSuccess(nearByUsersPojo: NearByMeUsersPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (nearByUsersPojo != null) {

                            nearByMe.setNearByMeDataAdapterOnLoadMore(nearByUsersPojo)

                        }
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

}