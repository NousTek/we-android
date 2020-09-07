package com.we.beyond.presenter.dashboard

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.DashboardApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.NotificationPojo
import com.we.beyond.model.NotifyServer
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Notification Activity
 * which implement api regarding get all notification list, remove notification etc  */
class NotificationImpl(notification : NotificationPresenter.INotificationView) : NotificationPresenter.INotificationPresenter
{

    var notification = notification


    /** It calls getNotificationData method which takes below inputs  */
    override fun onRequestNotificationData(context: Context, pageNo : Int, size : Int)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getNotificationData(context,pageNo,size)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getNotificationList api which below inputs
     * and set notification adapter
     */
    private fun getNotificationData(context: Context, pageNo: Int, size: Int)
    {
        try {
            val notificationApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NotificationPojo> = notificationApi.getNotificationList(pageNo,size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NotificationPojo>() {
                    override fun onSuccess(notificationPojo: NotificationPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (notificationPojo != null) {

                            notification.setNotificationDataAdapter(notificationPojo)

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


    /** It calls getNotificationDataOnLoadMore method which takes below inputs  */
    override fun onRequestNotificationDataOnLoadMore(context: Context, pageNo : Int, size : Int)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getNotificationDataOnLoadMore(context,pageNo,size)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getNotificationList api which takes below parameter
     * and set NotificationDataAdapterOnLoadMore
     */
    private fun getNotificationDataOnLoadMore(context: Context, pageNo: Int, size: Int) {
        try {
            val notificationApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NotificationPojo> = notificationApi.getNotificationList(pageNo, size)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NotificationPojo>() {
                    override fun onSuccess(notificationPojo: NotificationPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (notificationPojo != null) {

                            notification.setNotificationDataAdapterOnLoadMore(notificationPojo)

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

    /** It calls getNotificationState method   */
    override fun onRequestNotificationFalse(context: Context) {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getNotificationState(context)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the removeNotificationFlag api */
    private fun getNotificationState(context: Context)
    {
        try {
            val notificationApi = ApplicationController.retrofit.create(DashboardApi::class.java)
            val response: Single<NotifyServer> = notificationApi.removeNotificationFlag()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<NotifyServer>() {
                    override fun onSuccess(notificationPojo: NotifyServer) {

                        ConstantMethods.cancleProgessDialog()

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

}