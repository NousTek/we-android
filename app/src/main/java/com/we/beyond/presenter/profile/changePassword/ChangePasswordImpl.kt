package com.we.beyond.presenter.profile.changePassword

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ChangePassword
import com.we.beyond.model.ErrorPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of ChangePassword Activity
 * which implement all api regarding get gathering and deletion */

class ChangePasswordImpl (changePassword : ChangePasswordPresenter.IChangePasswordView) : ChangePasswordPresenter.IMyActivitiesPresenter
{


    var changePasswordData = changePassword

    /** It calls getUpdatePasswordData method which takes below inputs  */
    override fun onRequestChangePasswordData(context: Context, jsonObject: JsonObject)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getUpdatePasswordData(context,jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It calls the changePassword api which takes json object as parameter
     * and show success dialog
     */
    private fun getUpdatePasswordData(context: Context, jsonObject: JsonObject)
    {
        try {
            val changePasswordApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<ChangePassword> = changePasswordApi.changePassword(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ChangePassword>() {
                    override fun onSuccess(changePassword: ChangePassword) {

                        ConstantMethods.cancleProgessDialog()

                        if (changePassword != null) {

                            showSuccess(context,"Successful",changePassword.message)

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

    /** It will show dialog */
    fun showSuccess(context: Context, title: String, message: String) {
        try {
            val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.titleText = title
            sweetAlertDialog.contentText = message
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()
                changePasswordData.setChangePasswordData()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}