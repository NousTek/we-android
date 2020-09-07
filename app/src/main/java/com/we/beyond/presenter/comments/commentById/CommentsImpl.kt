package com.we.beyond.presenter.comments.commentById

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.CommentsApi
import com.we.beyond.interceptor.ApplicationController
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
/** It is implementation class of NearbyIssueDetails, ConnectDetails, GatheringDetails Activity
 * which implement api regarding  get and update comments */
class CommentsImpl (comments : CommentsPresenter.ICommentsByIdView) :
    CommentsPresenter.ICommentsByIdPresenter
{


    var comments = comments

    /** It calls getDataToPost method which takes below input  */
    override fun onCommentsById(
        context: Context,
        CommentsId: String,
        CommentsType: String,
        pageNo: Int,
        pageSize: Int
    ) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context, CommentsId,CommentsType,pageNo,pageSize)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getCommentsById api which takes below parameter
     * and sets CommentsByIdAdapter
     */
    private fun getDataToPost(context: Context, commentsId: String, commentsType: String, pageNo: Int, pageSize: Int)
    {
        try {
            val commentsApi = ApplicationController.retrofit.create(CommentsApi::class.java)
            val response: Single<CommentsPojo> = commentsApi.getCommentsById(commentsId,commentsType,pageNo,pageSize,"resolutionUsersDetails")
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<CommentsPojo>() {
                    override fun onSuccess(commentsPojo: CommentsPojo) {
                        ConstantMethods.cancleProgessDialog()


                        if (commentsPojo != null) {


                            comments.setCommentsByIdAdapter(commentsPojo)

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

    /** It calls getDataToPostOnLoadMore method which takes below input  */
    override fun onCommentsByIdOnLoadMore(
        context: Context,
        CommentsId: String,
        CommentsType: String,
        pageNo: Int,
        pageSize: Int
    ) {

        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostOnLoadMore(context, CommentsId,CommentsType,pageNo,pageSize)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls the getCommentsById api which takes below parameter
     * and sets CommentsByIdAdapter
     */
    private fun getDataToPostOnLoadMore(context: Context, commentsId: String, commentsType: String, pageNo: Int, pageSize: Int)
    {
        try {
            val commentsApi = ApplicationController.retrofit.create(CommentsApi::class.java)
            val response: Single<CommentsPojo> = commentsApi.getCommentsById(commentsId,commentsType,pageNo,pageSize,"resolutionUsersDetails")
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<CommentsPojo>() {
                    override fun onSuccess(commentsPojo: CommentsPojo) {

                        ConstantMethods.cancleProgessDialog()

                        println("load more response $commentsPojo")
                            comments.setCommentsByIdAdapterOnLoadMore(commentsPojo)


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


    /** It calls getDataToUpdateComment method which takes context and json object as input  */
    override fun onUpdateComment(context: Context, jsonObject: JsonObject) {


    try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToUpdateComment(context,jsonObject)
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }
    }

    /** It calls the updateComment api which takes json object as parameter
     * and show success dialog
     */
    private fun getDataToUpdateComment(
        context: Context,
        jsonObject: JsonObject
    )

    {
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
    fun onSuccess(context: Context)
    {
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
