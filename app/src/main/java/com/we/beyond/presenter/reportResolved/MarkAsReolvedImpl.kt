package com.we.beyond.presenter.reportResolved

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.IssueResolvedApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.MarkAsResolvedPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It implements mark as resolved the issue api */
class MarkAsResolvedImpl(resolved : MarkAsResolvedPresenter.IMarkAsResolvedView) : MarkAsResolvedPresenter.IMarkAsResolvedPresenter
{


    var resolved = resolved
    /** It calls getDataToPost method which takes below input  */
    override fun onMarkAsResolvedData(context: Context, jsonObject: JsonObject)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It calls the markAsResolved api which takes below parameter
     */
    private fun getDataToPost(context: Context, jsonObject: JsonObject) {
        try {
            val resolvedApi = ApplicationController.retrofit.create(IssueResolvedApi::class.java)
            val response: Single<MarkAsResolvedPojo> = resolvedApi.markAsResolved(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<MarkAsResolvedPojo>() {
                    override fun onSuccess(resolutionPojo: MarkAsResolvedPojo) {
                        ConstantMethods.cancleProgessDialog()

                        if (resolutionPojo != null) {

                            resolved.onResolved()

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