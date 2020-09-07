package com.we.beyond.presenter.comments.commentDetails

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.CommentsApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.CommentDetailsPojo
import com.we.beyond.model.CommentsPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.UpdateCommentPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of comment and connect comment Activity
 * which implement all api regarding get comment history and update comment  */
class CommentDetailsImpl(comments: CommentDetailsPresenter.ICommentsByIdView) :
    CommentDetailsPresenter.ICommentsByIdPresenter {


    var comments = comments

    /** It calls getDataToPost method which takes below inputs  */
    override fun onCommentsById(
        context: Context,
        CommentsId: String
    ) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, CommentsId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getCommentsHistory api which takes below inputs
     * and set CommentsByIdAdapter
     */
    private fun getDataToPost(context: Context, commentsId: String) {
        try {
            val commentsApi = ApplicationController.retrofit.create(CommentsApi::class.java)
            val response: Single<CommentDetailsPojo> =
                commentsApi.getCommentsHistory(commentsId, "resolutionUsersDetails")
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<CommentDetailsPojo>() {
                    override fun onSuccess(commentsPojo: CommentDetailsPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (commentsPojo != null) {


                            comments.setCommentsByIdAdapter(commentsPojo)

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
                context.resources.getString(
                    R.string.error_message
                )
            )
        }
    }

    /** It calls getDataToUpdateComment method which takes below inputs  */
    override fun onUpdateComment(context: Context, jsonObject: JsonObject) {


        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToUpdateComment(context, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** It calls the updateComment api which takes below inputs
     * and show success dialog
     */
    private fun getDataToUpdateComment(
        context: Context,
        jsonObject: JsonObject
    ) {
        try {
            val issueApi = ApplicationController.retrofit.create(CommentsApi::class.java)
            val response: Single<UpdateCommentPojo> = issueApi.updateComment(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateCommentPojo>() {
                    override fun onSuccess(updateCommentPojo: UpdateCommentPojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (updateCommentPojo != null) {


                            onSuccess(context)


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
                context.resources.getString(
                    R.string.error_message
                )
            )
        }

    }

    /** show success dialog */
    fun onSuccess(context: Context) {
        try {
            val sweetAlertDialog =
                SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            sweetAlertDialog.titleText = "Success!"
            sweetAlertDialog.contentText = "Successfully updated comment."
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()

                comments.onUpdateSuccessfully()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


}