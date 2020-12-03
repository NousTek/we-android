package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.Interface.OnEditListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.R
import com.we.beyond.api.CommentsApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.CommentsData
import com.we.beyond.model.ConnectDetailsPojo
import com.we.beyond.model.DeleteCommentPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.ui.ConnectCommentActivity
import com.we.beyond.ui.connect.connectIssue.ConnectDetailsActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_comments.view.*
import retrofit2.HttpException
import java.io.IOException
import kotlin.Exception

/** It binds article comment data set to views */
class ConnectCommentsAdapter(
    val context: Context,
    commentDetails: ArrayList<CommentsData>,
    connectId: ConnectDetailsPojo?
) : RecyclerView.Adapter<ConnectCommentsAdapter.ViewHolder>() {

    /** init array list */
    var commentDetails: ArrayList<CommentsData>? = null

    /** init listeners */
    var onLikeDislikeListener : OnLikeDislikeListener?=null
    var onReportAbuseListener : OnReportAbuseListener?=null
    var onEditListener : OnEditListener?=null

    /** init string */
    var selectedPositionId : String =""

    /** init model */
    var connectDetails : ConnectDetailsPojo?=null

    init {
        this.commentDetails = commentDetails
        this.connectDetails = connectId

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.list_comments,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {

        return commentDetails!!.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize listeners and set data to views */

        try {
            if (commentDetails!!.size > 1) {
                holder.divider.visibility = View.VISIBLE
            } else {
                holder.divider.visibility = View.GONE
            }

            val activity = context as Activity
            try {
                onLikeDislikeListener = activity as OnLikeDislikeListener
            } catch (e: Exception) {
                e.printStackTrace()

            }


            try {
                onReportAbuseListener = activity as OnReportAbuseListener
            } catch (e: Exception) {
                e.printStackTrace()

            }

            try {
                onEditListener = activity as OnEditListener
            } catch (e: Exception) {
                e.printStackTrace()

            }

            /** show and hide text views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_comment_like.setOnClickListener {
                holder.commentAlreadyLike.visibility = View.VISIBLE
                holder.commentLike.visibility = View.GONE

                onLikeDislikeListener!!.onLike(commentDetails!![position]._id)

            }

            /** show and hide text views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_comment_already_liked.setOnClickListener {

                holder.commentAlreadyLike.visibility = View.GONE
                holder.commentLike.visibility = View.VISIBLE


                onLikeDislikeListener!!.onDislike(commentDetails!![position]._id)
            }

            /** show and hide text views */
            if (commentDetails!![position].likeByUser != null && commentDetails!![position].likeByUser) {
                holder.commentLike.visibility = View.GONE
                holder.commentAlreadyLike.visibility = View.VISIBLE
            }

            /** show and hide text views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_reply_like.setOnClickListener {
                holder.replyAlreadyLike.visibility = View.VISIBLE
                holder.replyLike.visibility = View.GONE

                onLikeDislikeListener!!.onLike(commentDetails!![position].reply._id)

            }

            /** show and hide text views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_reply_already_liked.setOnClickListener {

                holder.replyAlreadyLike.visibility = View.GONE
                holder.replyLike.visibility = View.VISIBLE


                onLikeDislikeListener!!.onDislike(commentDetails!![position].reply._id)
            }


            //set profile pic
            if (commentDetails!![position].user.profileUrl != null && commentDetails!![position].user.profileUrl.length > 0) {
                Picasso.with(context)
                    .load(commentDetails!![position].user.profileUrl)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.commentPofilePic)
            } else {
                holder.commentPofilePic.setBackgroundResource(R.drawable.profile)
            }

            //set user name
            if (commentDetails!![position].user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {
                holder.name.text =
                    commentDetails!![position].user.firstName + " ${commentDetails!![position].user.lastName}"
                holder.name.typeface = ConstantFonts.raleway_semibold
            } else {
                holder.name.text =
                    commentDetails!![position].user.organizationName
                holder.name.typeface = ConstantFonts.raleway_semibold
            }

            //set comment description
            holder.commentDescription.text = commentDetails!![position].text
            holder.commentDescription.typeface = ConstantFonts.raleway_regular

            //set comment time
            holder.commentTime.text =
                ConstantMethods.convertStringToDateStringFull(commentDetails!![position].createdAt)
            holder.commentTime.typeface = ConstantFonts.raleway_regular

            //set comment reply
            holder.commentReply.typeface = ConstantFonts.raleway_semibold

            //set comment like
            holder.commentLike.typeface = ConstantFonts.raleway_semibold

            /** It set comment id and position of ConnectDetailsActivity and call getData() */
            holder.commentReply.setOnClickListener {
                (context as ConnectDetailsActivity).commentId = commentDetails!![position]._id
                (context as ConnectDetailsActivity).commentPosition = position
                // (context as ConnectDetailsActivity).resetValue()
                (context as ConnectDetailsActivity).getData()
            }

            /** It opens CommentActivity with pass below data when click on it */
            holder.commentLayout.setOnClickListener {
                val intent = Intent(context, ConnectCommentActivity::class.java)
                intent.putExtra("connectId", connectDetails!!.data._id)
                intent.putExtra(Constants.COMMENT_ID, commentDetails!![position]._id)
                (context as ConnectDetailsActivity).startActivityForResult(intent,1)
                (context as ConnectDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )


            }

            //set profile pic
            if (commentDetails!![position].reply != null) {

                if (commentDetails!![position].reply.likeByUser != null && commentDetails!![position].reply.likeByUser) {
                    holder.replyLike.visibility = View.GONE
                    holder.replyAlreadyLike.visibility = View.VISIBLE
                }


                holder.replyLayout!!.visibility = View.VISIBLE
                if (commentDetails!![position].reply.user.profileUrl != null && commentDetails!![position].reply.user.profileUrl.length > 0) {
                    Picasso.with(context)
                        .load(commentDetails!![position].reply.user.profileUrl)
                        .placeholder(R.drawable.profile)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(holder.replyProfilePic)
                } else {
                    holder.replyProfilePic.setBackgroundResource(R.drawable.profile)
                }

                //set use name
                if (commentDetails!![position].reply.user.userLoginType.userType.equals(
                        "individual",
                        ignoreCase = true
                    )
                ) {
                    holder.replyName.text =
                        commentDetails!![position].reply.user.firstName + " ${commentDetails!![position].reply.user.lastName}"
                    holder.replyName.typeface = ConstantFonts.raleway_semibold
                } else {
                    holder.replyName.text =
                        commentDetails!![position].reply.user.organizationName
                    holder.replyName.typeface = ConstantFonts.raleway_semibold
                }

                //set reply description
                holder.replyDescription.text = commentDetails!![position].reply.text
                holder.replyDescription.typeface = ConstantFonts.raleway_regular

                //set reply time
                holder.replyTime.text =
                    ConstantMethods.convertStringToDateStringFull(commentDetails!![position].reply.createdAt)
                holder.replyTime.typeface = ConstantFonts.raleway_regular

                //set reply reply
                holder.reply.typeface = ConstantFonts.raleway_semibold

                //set reply like
                holder.replyLike.typeface = ConstantFonts.raleway_semibold

                /** It set comment id and position of ConnectDetailsActivity and call getData() */
                holder.reply.setOnClickListener {
                    (context as ConnectDetailsActivity).commentId = commentDetails!![position]._id
                    (context as ConnectDetailsActivity).commentPosition = position
                    //(context as ConnectDetailsActivity).resetValue()
                    (context as ConnectDetailsActivity).getData()
                }

                /** It opens ConnectCommentActivity with pass below data when click on it */
                holder.replyLayout.setOnClickListener {
                    val intent = Intent(context, ConnectCommentActivity::class.java)
                    intent.putExtra("connectId", connectDetails!!.data._id)
                    intent.putExtra(Constants.COMMENT_ID, commentDetails!![position].reply._id)
                    (context as ConnectDetailsActivity).startActivityForResult(intent,1)
                    (context as ConnectDetailsActivity).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )

                }

            } else {
                holder.replyLayout!!.visibility = View.GONE
            }

            /** It opens more layout with animation */
            holder.more.setOnClickListener {
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_in_up
                    )
                )

            }

            /** It opens more layout with animation */
            holder.moreReply.setOnClickListener {

                selectedPositionId = commentDetails!![position].reply._id
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_in_up
                    )
                )
            }

            /** It closes more layout with animation */
            holder.close.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )
            }

            /** It opens normal dialog and call deleteComment api with below json object depends on conditions */
            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {

                if (commentDetails!![position].reply != null) {

                    if (selectedPositionId == commentDetails!![position].reply._id && commentDetails!![position].reply.commentType.equals(
                            "comment",
                            ignoreCase = true
                        )
                    ) {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.slide_out_down
                            )
                        )
                        (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == commentDetails!![position].reply.user._id) {
                            println("user id ${userId} comment user ${commentDetails!![position].reply.user._id}")
                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "Do you want to delete reply?"
                                sweetAlertDialog.confirmText = "Yes"
                                sweetAlertDialog.cancelText = "No"
                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()


                                    try {

                                        val jsonObject = JsonObject()
                                        jsonObject.addProperty(
                                            "commentId",
                                            commentDetails!![position].reply._id
                                        )



                                        try {
                                            val commentsApi =
                                                ApplicationController.retrofit.create(CommentsApi::class.java)
                                            val response: Single<DeleteCommentPojo> =
                                                commentsApi.deleteComment(jsonObject)
                                            response.subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(object :
                                                    DisposableSingleObserver<DeleteCommentPojo>() {
                                                    override fun onSuccess(deleteCommentPojo: DeleteCommentPojo) {
                                                        if (deleteCommentPojo != null) {

                                                            println("response $deleteCommentPojo")
                                                            ConstantMethods.cancleProgessDialog()

                                                            val deleteCommentsList =
                                                                ArrayList<CommentsData>()
                                                            deleteCommentsList.addAll(commentDetails!!)

                                                            val issueId =
                                                                commentDetails!![position]._id
                                                            for (i in 0 until deleteCommentsList.size) {

                                                                if (deleteCommentsList[i]._id == issueId) {
                                                                    deleteCommentsList.remove(
                                                                        deleteCommentsList[i]
                                                                    )
                                                                    commentDetails!!.clear()
                                                                    commentDetails!!.addAll(
                                                                        deleteCommentsList
                                                                    )
                                                                    (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()

                                                                    break
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
                                                                val exception: HttpException =
                                                                    e as HttpException
                                                                val er: String =
                                                                    exception.response()!!.errorBody()!!.string()
                                                                val errorPojo: ErrorPojo =
                                                                    Gson().fromJson(
                                                                        er,
                                                                        ErrorPojo::class.java
                                                                    )

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

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }


                                }


                                sweetAlertDialog.setCancelClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }


                            } catch (e: Exception) {


                            }

                        } else {

                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "You are not owner of this reply."
                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }
                    } else {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.slide_out_down
                            )
                        )
                        (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == commentDetails!![position].user._id) {
                            println("user id ${userId} comment user ${commentDetails!![position].user._id}")
                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "Do you want to delete comment?"
                                sweetAlertDialog.confirmText = "Yes"
                                sweetAlertDialog.cancelText = "No"
                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()


                                    try {

                                        val jsonObject = JsonObject()
                                        jsonObject.addProperty(
                                            "commentId",
                                            commentDetails!![position]._id
                                        )



                                        try {
                                            val commentsApi =
                                                ApplicationController.retrofit.create(CommentsApi::class.java)
                                            val response: Single<DeleteCommentPojo> =
                                                commentsApi.deleteComment(jsonObject)
                                            response.subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(object :
                                                    DisposableSingleObserver<DeleteCommentPojo>() {
                                                    override fun onSuccess(deleteCommentPojo: DeleteCommentPojo) {
                                                        if (deleteCommentPojo != null) {

                                                            println("response $deleteCommentPojo")
                                                            ConstantMethods.cancleProgessDialog()

                                                            val deleteCommentsList =
                                                                ArrayList<CommentsData>()
                                                            deleteCommentsList.addAll(commentDetails!!)

                                                            val issueId =
                                                                commentDetails!![position]._id
                                                            for (i in 0 until deleteCommentsList.size) {

                                                                if (deleteCommentsList[i]._id == issueId) {
                                                                    deleteCommentsList.remove(
                                                                        deleteCommentsList[i]
                                                                    )
                                                                    commentDetails!!.clear()
                                                                    commentDetails!!.addAll(
                                                                        deleteCommentsList
                                                                    )
                                                                    (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()

                                                                    break
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
                                                                val exception: HttpException =
                                                                    e as HttpException
                                                                val er: String =
                                                                    exception.response()!!.errorBody()!!.string()
                                                                val errorPojo: ErrorPojo =
                                                                    Gson().fromJson(
                                                                        er,
                                                                        ErrorPojo::class.java
                                                                    )

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

                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }


                                }


                                sweetAlertDialog.setCancelClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }


                            } catch (e: Exception) {


                            }
                        } else {

                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "You are not owner of this comment."
                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }
                    }
                } else {

                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )
                    (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == commentDetails!![position].user._id) {
                        println("user id ${userId} comment user ${commentDetails!![position].user._id}")
                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "Do you want to delete comment?"
                            sweetAlertDialog.confirmText = "Yes"
                            sweetAlertDialog.cancelText = "No"
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()


                                try {

                                    val jsonObject = JsonObject()
                                    jsonObject.addProperty(
                                        "commentId",
                                        commentDetails!![position]._id
                                    )



                                    try {
                                        val commentsApi = ApplicationController.retrofit.create(
                                            CommentsApi::class.java
                                        )
                                        val response: Single<DeleteCommentPojo> =
                                            commentsApi.deleteComment(jsonObject)
                                        response.subscribeOn(Schedulers.newThread()).observeOn(
                                            AndroidSchedulers.mainThread()
                                        )
                                            .subscribe(object :
                                                DisposableSingleObserver<DeleteCommentPojo>() {
                                                override fun onSuccess(deleteCommentPojo: DeleteCommentPojo) {
                                                    if (deleteCommentPojo != null) {

                                                        println("response $deleteCommentPojo")
                                                        ConstantMethods.cancleProgessDialog()

                                                        val deleteCommentsList =
                                                            ArrayList<CommentsData>()
                                                        deleteCommentsList.addAll(commentDetails!!)

                                                        val issueId = commentDetails!![position]._id
                                                        for (i in 0 until deleteCommentsList.size) {

                                                            if (deleteCommentsList[i]._id == issueId) {
                                                                deleteCommentsList.remove(
                                                                    deleteCommentsList[i]
                                                                )
                                                                commentDetails!!.clear()
                                                                commentDetails!!.addAll(
                                                                    deleteCommentsList
                                                                )
                                                                (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()

                                                                break
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
                                                            val exception: HttpException =
                                                                e as HttpException
                                                            val er: String =
                                                                exception.response()!!.errorBody()!!.string()
                                                            val errorPojo: ErrorPojo =
                                                                Gson().fromJson(
                                                                    er,
                                                                    ErrorPojo::class.java
                                                                )

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

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }


                            }


                            sweetAlertDialog.setCancelClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }


                        } catch (e: Exception) {


                        }
                    } else {

                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "You are not owner of this comment."
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }


                }
            }

            /** It closes the more layout with animation and notify adapter */
            //abuse report
            holder.reportAbuse.typeface = ConstantFonts.raleway_semibold
            holder.reportAbuse.setOnClickListener {
                if (selectedPositionId == commentDetails!![position]._id && commentDetails!![position].commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && commentDetails!![position].markAsFinal
                ) {
                    ConstantMethods.showToast(
                        context,
                        "You cannot abuse approved resolution"
                    )
                } else if (selectedPositionId == commentDetails!![position]._id && commentDetails!![position].commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && !commentDetails!![position].markAsFinal
                ) {

                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )
                    (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == commentDetails!![position].user._id) {
                        println("user id ${userId} resolution user ${commentDetails!![position].user._id}")
                        try {
                            onReportAbuseListener!!.ReportAbuse(
                                commentDetails!![position]._id,
                                "resolution"
                            )
                        } catch (e: Exception) {


                        }

                    } else {

                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "You are not owner of this resolution."
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                } else if (commentDetails!![position].reply != null) {

                    if (selectedPositionId == commentDetails!![position].reply._id && commentDetails!![position].reply.commentType.equals(
                            "comment",
                            ignoreCase = true
                        )
                    ) {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.slide_out_down
                            )
                        )
                        (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == commentDetails!![position].reply.user._id) {
                            println("user id ${userId} comment user ${commentDetails!![position].reply.user._id}")
                            try {
                                onReportAbuseListener!!.ReportAbuse(
                                    commentDetails!![position].reply._id,
                                    "comment"
                                )
                            } catch (e: Exception) {


                            }

                        } else {

                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "You are not owner of this reply."
                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }

                    } else {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.slide_out_down
                            )
                        )
                        (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == commentDetails!![position].user._id) {
                            println("user id ${userId} comment user ${commentDetails!![position].user._id}")
                            try {
                                onReportAbuseListener!!.ReportAbuse(
                                    commentDetails!![position]._id,
                                    "comment"
                                )

                            } catch (e: Exception) {


                            }
                        } else {

                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "You are not owner of this comment."
                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }
                    }


                } else {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )
                    (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == commentDetails!![position].user._id) {
                        println("user id ${userId} comment user ${commentDetails!![position].user._id}")
                        try {
                            onReportAbuseListener!!.ReportAbuse(
                                commentDetails!![position]._id,
                                "comment"
                            )

                        } catch (e: Exception) {


                        }
                    } else {

                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "You are not owner of this comment."
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }

            }


            //edit
            holder.edit.typeface = ConstantFonts.raleway_semibold
            holder.edit.setOnClickListener {

                if(commentDetails!![position!!].tags !=null && commentDetails!![position].tags.isNotEmpty())
                {
                    (context as ConnectDetailsActivity).tagsList!!.clear()
                    (context as ConnectDetailsActivity).tagUserName!!.clear()


                    for(i in 0 until commentDetails!![position].tags.size)
                    {
                        (context as ConnectDetailsActivity).tagsList!!.add(commentDetails!![position].tags[i])
                        (context as ConnectDetailsActivity).tagUserName!!.add(commentDetails!![position].tagNames[i])

                    }

                }

                if (selectedPositionId == commentDetails!![position]._id && commentDetails!![position].commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && commentDetails!![position].markAsFinal
                ) {
                    ConstantMethods.showToast(
                        context,
                        "You cannot edit approved resolution"
                    )
                } else if (selectedPositionId == commentDetails!![position]._id && commentDetails!![position].commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && !commentDetails!![position].markAsFinal
                ) {

                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )
                    (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == commentDetails!![position].user._id)
                    {
                        try {
                            onEditListener!!.OnEdit(
                                commentDetails!![position]._id,
                                commentDetails!![position].text
                            )

                        } catch (e: Exception) {


                        }
                    }
                    else {

                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "You are not owner of this comment."
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }

                else if(commentDetails!![position].reply!=null) {

                    if (selectedPositionId == commentDetails!![position].reply._id && commentDetails!![position].reply.commentType.equals(
                            "comment",
                            ignoreCase = true
                        )
                    ) {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.slide_out_down
                            )
                        )
                        (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == commentDetails!![position].reply.user._id) {
                            println("user id ${userId} comment user ${commentDetails!![position].reply.user._id}")
                            try {
                                onEditListener!!.OnEdit(
                                    commentDetails!![position].reply._id,
                                    commentDetails!![position].reply.text
                                )
                            } catch (e: Exception) {


                            }

                        } else {

                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "You are not owner of this reply."
                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }

                    }
                    else {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                        (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == commentDetails!![position].user._id) {
                            println("user id ${userId} comment user ${commentDetails!![position].user._id}")
                            try {
                                onEditListener!!.OnEdit(
                                    commentDetails!![position]._id,
                                    commentDetails!![position].text
                                )

                            } catch (e: Exception) {


                            }
                        } else {

                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "You are not owner of this comment."
                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }
                    }


                }
                else {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                    (context as ConnectDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == commentDetails!![position].user._id) {
                        println("user id ${userId} comment user ${commentDetails!![position].user._id}")
                        try {
                            onEditListener!!.OnEdit(
                                commentDetails!![position]._id,
                                commentDetails!![position].text
                            )

                        } catch (e: Exception) {


                        }
                    } else {

                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "You are not owner of this comment."
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }

            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }


    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var commentPofilePic = itemView.img_comment_profile_pic
        var replyProfilePic = itemView.img_reply_profile_pic
        var name = itemView.txt_name
        var replyName = itemView.txt_reply_name
        var commentDescription = itemView.txt_comment_description
        var replyDescription = itemView.txt_reply_description
        var commentTime = itemView.txt_time
        var replyTime = itemView.txt_reply_time
        var commentReply = itemView.txt_comment_reply
        var reply = itemView.txt_reply
        var commentLike = itemView.txt_comment_like
        var commentAlreadyLike = itemView.txt_comment_already_liked!!
        var replyLike = itemView.txt_reply_like
        var replyAlreadyLike = itemView.txt_reply_already_liked!!
        var replyLayout = itemView.replyLayout
        var divider = itemView.dividerView
        var commentLayout = itemView.commentCard!!
        var more = itemView.img_more!!
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
        var moreLayout = itemView.moreLayout!!
        var moreReply = itemView.img_reply_more!!
        var reportAbuse = itemView.txt_report_abuse!!
        var edit = itemView.txt_edit!!
    }


}