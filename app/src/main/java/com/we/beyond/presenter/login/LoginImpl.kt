package com.we.beyond.presenter.login

import android.content.Context
import android.provider.Settings
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.LoginApi
import com.we.beyond.interceptor.ApplicationController.Companion.retrofit
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.ForgotPasswordPojo
import com.we.beyond.model.LoginPojo
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Login Activity
 * which implement api regarding login and forgot password */
class LoginImpl(login: LoginPresenter.ILoginView) : LoginPresenter.ILoginPresenter {
    var login = login

    override fun onLogin(context: Context, jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** calling login api */
    private fun getDataToPost(context: Context, jsonObject: JsonObject) {
        try {
            val loginApi = retrofit.create(LoginApi::class.java)
            val response: Single<LoginPojo> = loginApi.login(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<LoginPojo>() {
                    override fun onSuccess(loginPojo: LoginPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (loginPojo != null) {
                            if (loginPojo.accessToken != null && loginPojo.accessToken.isNotEmpty()) {


                                EasySP.init(context)
                                    .putBoolean(ConstantEasySP.SP_IS_LOGIN, true)

                                EasySP.init(context).putString(
                                    ConstantEasySP.SP_ACCESS_TOKEN,
                                    loginPojo.accessToken
                                )


                                if (loginPojo.userCategories != null) {
                                    if (loginPojo.userCategories) {
                                        EasySP.init(context).putBoolean(
                                            ConstantEasySP.SP_IS_CATEGORY_SELECTED,
                                            true
                                        )
                                        login.goToNextScreen()
                                    } else {
                                        login.goToCategoriesScreen()
                                    }
                                }

                            }
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
                context.resources.getString(R.string.error_message)
            )

        }

    }


    override fun onForgotPassword(context: Context, jsonObject: JsonObject) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostForgotPassword(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** calling forgot password api  */
    private fun getDataToPostForgotPassword(context: Context, jsonObject: JsonObject) {
        try {
            val loginApi = retrofit.create(LoginApi::class.java)
            val response: Single<ForgotPasswordPojo> = loginApi.forgotPassword(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ForgotPasswordPojo>() {
                    override fun onSuccess(forgotPasswordPojo: ForgotPasswordPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (forgotPasswordPojo != null) {

                            ConstantMethods.showWarning(context, "Successful", forgotPasswordPojo.message)


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
                context.resources.getString(R.string.error_message)
            )

        }

    }





}