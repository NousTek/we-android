package com.we.beyond.presenter.profile.myResolution

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.ReportedResolutionDetailsPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of MyResolutionDetails Activity
 * which implement all api regarding get resolution  */
class MyResolutionDetailsImpl(issueById: MyResolutionDetailsPresenter.IMyResolutionDetailsView) :
    MyResolutionDetailsPresenter.IMyResolutionDetailsPresenter {
    var issueById = issueById

    /** It calls getDataToPost method which takes below inputs  */
    override fun onMyResolutionDetails(context: Context, pageNo: Int, size: Int, issueId: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, pageNo, size, issueId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getMyResolutionById api which takes below inputs
     * and set MyResolutionDetailsAdapter
     */
    private fun getDataToPost(context: Context, sizeNo: Int, size: Int, issueId: String) {

        try {
            val issueApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<ReportedResolutionDetailsPojo> =
                issueApi.getMyResolutionById(sizeNo, size, issueId, "resolutionUsersDetails")
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ReportedResolutionDetailsPojo>() {
                    override fun onSuccess(issuePojo: ReportedResolutionDetailsPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (issuePojo != null) {


                            issueById.setMyResolutionDetailsAdapter(issuePojo)

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
                context.resources.getString(R.string.error_message)
            )

        }
    }

}