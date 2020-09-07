package com.we.beyond.presenter.profile.updateCategory

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.CategoriesApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.CategoriesPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.UpdateCategoriesPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of MyDefaultCategories Activity
 * which implement api regarding update category */
class UpdateUserDefaultCategoryImpl(updateCategory: UpdateUserDefaultCategoryPresenter.IUpdateUserDefaultCategoryView) :
    UpdateUserDefaultCategoryPresenter.IUpdateUserDefaultCategoryPresenter {

    var updateCategory = updateCategory

    /** It calls getCategories method which takes below input  */
    override fun onUpdateCategories(context: Context, jsonObject: JsonObject) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getCategories(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the updateCategories api which below inputs
     * and show success dialog
     */
    fun getCategories(context: Context, jsonObject: JsonObject) {
        try {
            val categoriesApi = ApplicationController.retrofit.create(CategoriesApi::class.java)
            val response: Single<UpdateCategoriesPojo> = categoriesApi.updateCategories(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateCategoriesPojo>() {
                    override fun onSuccess(updateCategoriesPojo: UpdateCategoriesPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (updateCategoriesPojo != null) {


                            showSuccess(context, "Successful", updateCategoriesPojo.message)

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

    /** show success dialog */
    fun showSuccess(context: Context, title: String, message: String) {
        try {
            val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.titleText = title
            sweetAlertDialog.contentText = message
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()
                updateCategory.goToNextScreen()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}