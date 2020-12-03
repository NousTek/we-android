package com.we.beyond.presenter.feedback

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.FeedbackAPI
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.FeedbackData
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Registration Activity
 * which implement apis regarding user types and registration  */
class FeedbackImpl (feedback : FeedbackPresenter.IFeedbackView) : FeedbackPresenter.IRegistrationPresenter
{
    var feedback = feedback

    /** user register api with json object as parameter */
    fun getDataToPost(context: Context,jsonObject: JsonObject)
    {

        try {
            val feedbackApi = ApplicationController.retrofit.create(FeedbackAPI::class.java)
            val response: Single<FeedbackData> = feedbackApi.submitFeedback(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<FeedbackData>() {
                    override fun onSuccess(feedbackData : FeedbackData) {
                        ConstantMethods.cancleProgessDialog()
                        if (feedbackData != null) {
                            feedback.onSuccessfulFeedbackSubmission(feedbackData.message)
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

    override fun onSubmitFeedback(context: Context, jsonObject: JsonObject) {
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

}