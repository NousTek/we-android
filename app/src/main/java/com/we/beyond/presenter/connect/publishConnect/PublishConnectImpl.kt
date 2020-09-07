package com.we.beyond.presenter.connect.publishConnect

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ConnectApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Publish Connect Activity
 * which implement all api regarding connect creation, updation and get categories */
class PublishConnectImpl (connect : PublishConnectPresenter.IPublishConnectView) : PublishConnectPresenter.IPublishConnectPresenter
{


    var connect = connect
    val imagesSet = HashSet<String>()

    /** It calls getConnectCategories method  */
    override fun onRequestConnectCategory(context: Context)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getConnectCategories(context)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls the getCategories api
     * and set connect category adapter
     */
    private fun getConnectCategories(context: Context)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectCategoriesPojo> = connectApi.getCategories()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectCategoriesPojo>() {
                    override fun onSuccess(connectCategoryPojo: ConnectCategoriesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (connectCategoryPojo != null) {


                            connect.setConnectCategoryAdapter(connectCategoryPojo.data)

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

    /** It calls getDataToPost method which takes below inputs  */
    override fun onConnectCreated(context: Context, jsonObject: JsonObject)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the createConnect api which takes json object as parameter
     * and show success dialog
     */
    private fun getDataToPost(context: Context, jsonObject: JsonObject)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<ConnectCreatePojo> = connectApi.createConnect(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ConnectCreatePojo>() {
                    override fun onSuccess(createConnectPojo: ConnectCreatePojo) {
                        ConstantMethods.cancleProgessDialog()
                        if (createConnectPojo != null) {


                            showSuccess(context, "Successful", createConnectPojo.message)


                            EasySP.init(context).remove("uploadedImage")

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

    /** It calls getDataToPostUpdate method which takes below inputs  */
    override fun onConnectUpdated(context: Context, jsonObject: JsonObject)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostUpdate(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the updateConnect api which takes json object as parameter
     * and show success dialog
     */
    private fun getDataToPostUpdate(context: Context, jsonObject: JsonObject)
    {
        try {
            val connectApi = ApplicationController.retrofit.create(ConnectApi::class.java)
            val response: Single<UpdateConnectPojo> = connectApi.updateConnect(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateConnectPojo>() {
                    override fun onSuccess(updateConnectPojo: UpdateConnectPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (updateConnectPojo != null) {

                            showSuccess(context, "Successful", updateConnectPojo.message)

                            //  EasySP.init(context).putStringSet("uploadedImage",null)
                            EasySP.init(context).remove("uploadedImage")

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

    override fun onBackClick()
    {

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
                connect.goToPreviousScreen()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}