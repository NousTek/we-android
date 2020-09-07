package com.we.beyond.presenter.registration

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.RegistrationApi
import com.we.beyond.interceptor.ApplicationController.Companion.retrofit
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.RegistrationPojo
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/** It is implementation class of Registration Activity
 * which implement apis regarding user types and registration  */
class RegistrationImpl (register : RegistrationPresenter.IRegistrationView) : RegistrationPresenter.IRegistrationPresenter
{
    var register = register

    override fun onRequestUserTypes(context: Context)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                 getUserTypes(context)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }


    /** get all user type for registration */
    fun getUserTypes(context : Context)
    {
        try {
            val registerApi = retrofit.create(RegistrationApi::class.java)
            val response: Single<RegistrationPojo> = registerApi.getAllUserTypes()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<RegistrationPojo>() {
                    override fun onSuccess(userTypesPojo: RegistrationPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (userTypesPojo != null) {

                            if (userTypesPojo.data != null && userTypesPojo.data.size > 0) {
                                register.setUserTypesAdapter(userTypesPojo.data)
                            } else {

                            }
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
           ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(R.string.error_message))
        }
    }

   /* override fun onRequestCities(context: Context)
    {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getCities(context)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }


    }

    fun getCities(context: Context)
    {

        try {
            val registerApi = retrofit.create(RegistrationApi::class.java)
            val response: Single<ArrayList<RegistrationPojo>> = registerApi.getAllCities()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ArrayList<RegistrationPojo>>() {
                    override fun onSuccess(citiesPojo: ArrayList<RegistrationPojo>) {

                        ConstantMethods.cancleProgessDialog()

                        if (citiesPojo != null) {


                            if (citiesPojo != null && citiesPojo.isNotEmpty()) {
                                register.setCitiesAdapter(citiesPojo)
                            } else {

                            }

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
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(R.string.error_message))
        }

    }*/

    override fun onRegister(context: Context, jsonObject: JsonObject)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getDataToPost(context,jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }


    }

    /** user register api with json object as parameter */
    fun getDataToPost(context: Context,jsonObject: JsonObject)
    {

        try {
            val builder = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            val retrofit = builder.build()

            val registrationApi = retrofit.create(RegistrationApi::class.java)
            val response: Single<RegistrationPojo> = registrationApi.registerUser(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<RegistrationPojo>() {
                    override fun onSuccess(registrationPojo : RegistrationPojo) {
                        ConstantMethods.cancleProgessDialog()

                        if (registrationPojo != null) {

                            val sweetAlertDialog = SweetAlertDialog(
                                context,
                                SweetAlertDialog.SUCCESS_TYPE
                            ).setTitleText("Successful")
                                .setContentText("Successfully registered on WE")
                                .setConfirmText(" OK ")
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                register.goToNextScreen()
                                sweetAlertDialog.dismissWithAnimation()
                            }

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
            ConstantMethods.showError(context,context.resources.getString(R.string.error_title),context.resources.getString(R.string.error_message))
        }


    }


    override fun onBackClick()
    {
        register.goToPreviousScreen()

    }

}