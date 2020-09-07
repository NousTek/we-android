package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
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
import com.we.beyond.model.CommentDetailsPojo
import com.we.beyond.model.CommentsReplyDetails
import com.we.beyond.model.DeleteCommentPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.ui.ConnectCommentActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_comment_details_item.view.*
import retrofit2.HttpException
import java.io.IOException

/** It binds article comment details data set to views */

class ConnectCommentDetailsAdapter(
    val context: Context,
    commentDetails: ArrayList<CommentsReplyDetails>,
    commentId: CommentDetailsPojo
) : RecyclerView.Adapter<ConnectCommentDetailsAdapter.ViewHolder>() {

    /** init array list */
    var replyDetails: ArrayList<CommentsReplyDetails>? = null

    /** init listeners */
    var onLikeDislikeListener: OnLikeDislikeListener? = null
    var onReportAbuseListener: OnReportAbuseListener? = null
    var onEditListener: OnEditListener? = null

    /** init model */
    var commentId: CommentDetailsPojo? = null

    /** init string */
    var selectedPositionId: String = ""


    init {
        this.replyDetails = commentDetails
        this.commentId = commentId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.list_comment_details_item,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {

        return replyDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize listeners and set data to views */
        try {
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

            /** It opens more layout with animation */
            holder.moreReply.setOnClickListener {
                selectedPositionId = replyDetails!![position]._id
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


            /** show and hide text views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_reply_like.setOnClickListener {
                holder.replyAlreadyLike.visibility = View.VISIBLE
                holder.replyLike.visibility = View.GONE

                onLikeDislikeListener!!.onLike(replyDetails!![position]._id)

            }

            /** show and hide text views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_reply_already_liked.setOnClickListener {

                holder.replyAlreadyLike.visibility = View.GONE
                holder.replyLike.visibility = View.VISIBLE


                onLikeDislikeListener!!.onDislike(replyDetails!![position]._id)
            }


            //edit
            holder.edit.typeface = ConstantFonts.raleway_semibold
            holder.edit.setOnClickListener {
                if (replyDetails != null) {


                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )
                    (context as ConnectCommentActivity).commentsAdapter!!.notifyDataSetChanged()


                    if (replyDetails!![position].tags != null && replyDetails!![position].tags.isNotEmpty()) {
                        (context as ConnectCommentActivity).tagsList!!.clear()
                        (context as ConnectCommentActivity).tagUserName!!.clear()

                        for (i in 0 until replyDetails!![position].tags.size) {
                            (context as ConnectCommentActivity).tagsList!!.add(replyDetails!![position].tags[i])
                            (context as ConnectCommentActivity).tagUserName!!.add(replyDetails!![position].tagNames[i])

                            println("tags from server ${(context as ConnectCommentActivity).tagsList}")
                            println("tags names from server ${(context as ConnectCommentActivity).tagUserName}")
                        }

                    }
                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == replyDetails!![position].user._id) {
                        println("user id ${userId} comment user ${replyDetails!![position].user._id}")
                        try {
                            onEditListener!!.OnEdit(
                                replyDetails!![position]._id,
                                replyDetails!![position].text
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

            }

            //delete reply
            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {

                if (replyDetails != null) {

                    if (selectedPositionId == replyDetails!![position]._id) {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.slide_out_down
                            )
                        )
                        (context as ConnectCommentActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == replyDetails!![position].user._id) {
                            println("user id ${userId} comment user ${replyDetails!![position].user._id}")
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
                                            replyDetails!![position]._id
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
                                                                ArrayList<CommentsReplyDetails>()
                                                            deleteCommentsList.addAll(replyDetails!!)

                                                            val issueId =
                                                                replyDetails!![position]._id
                                                            for (i in 0 until deleteCommentsList.size) {

                                                                if (deleteCommentsList[i]._id == issueId) {
                                                                    deleteCommentsList.remove(
                                                                        deleteCommentsList[i]
                                                                    )
                                                                    replyDetails!!.clear()
                                                                    replyDetails!!.addAll(
                                                                        deleteCommentsList
                                                                    )
                                                                    (context as ConnectCommentActivity).commentsAdapter!!.notifyDataSetChanged()

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
                                                                    exception.response()!!
                                                                        .errorBody()!!.string()
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

                    }
                }


            }

            /** It closes the more layout with animation and call ReportAbuse function */
            //abuse report
            holder.reportAbuse.typeface = ConstantFonts.raleway_semibold
            holder.reportAbuse.setOnClickListener {
                if (replyDetails != null) {

                    if (selectedPositionId == replyDetails!![position]._id
                    ) {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(
                            AnimationUtils.loadAnimation(
                                context,
                                R.anim.slide_out_down
                            )
                        )
                        (context as ConnectCommentActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == replyDetails!![position].user._id) {
                            println("user id ${userId} comment user ${replyDetails!![position].user._id}")
                            try {
                                onReportAbuseListener!!.ReportAbuse(
                                    replyDetails!![position]._id,
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


                    }
                }

            }


            //set profile pic
            if (replyDetails!![position] != null) {

                if (replyDetails!![position].likeByUser != null && replyDetails!![position].likeByUser) {
                    holder.replyLike.visibility = View.GONE
                    holder.replyAlreadyLike.visibility = View.VISIBLE
                }



                holder.replyLayout.visibility = View.VISIBLE
                if (replyDetails!![position].user.profileUrl != null && replyDetails!![position].user.profileUrl.length > 0) {
                    Picasso.with(context)
                        .load(replyDetails!![position].user.profileUrl)
                        .placeholder(R.drawable.profile)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(holder.replyProfilePic)
                } else {
                    holder.replyProfilePic.setBackgroundResource(R.drawable.profile)
                }

                //set use name
                if (replyDetails!![position].user.userLoginType.userType.equals(
                        "individual",
                        ignoreCase = true
                    )
                ) {
                    holder.replyName.text =
                        replyDetails!![position].user.firstName + " ${replyDetails!![position].user.lastName}"
                    holder.replyName.typeface = ConstantFonts.raleway_semibold
                } else {
                    holder.replyName.text =
                        replyDetails!![position].user.organizationName
                    holder.replyName.typeface = ConstantFonts.raleway_semibold
                }

                //set reply description
                holder.replyDescription.text = replyDetails!![position].text
                holder.replyDescription.typeface = ConstantFonts.raleway_regular

                //set reply time
                holder.replyTime.text =
                    ConstantMethods.convertStringToDateStringFull(replyDetails!![position].createdAt)
                holder.replyTime.typeface = ConstantFonts.raleway_regular

                //set reply reply
                holder.reply.typeface = ConstantFonts.raleway_semibold

                //set reply like
                holder.replyLike.typeface = ConstantFonts.raleway_semibold
                holder.replyAlreadyLike.typeface = ConstantFonts.raleway_semibold


                holder.reply.setOnClickListener {
                    (context as ConnectCommentActivity).commentId = commentId!!.data._id
                    (context as ConnectCommentActivity).commentPosition = position
                    (context as ConnectCommentActivity).getData()
                }

            } else {
                holder.replyLayout.visibility = View.GONE
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var replyProfilePic = itemView.img_reply_profile_pic!!
        var replyName = itemView.txt_reply_name!!
        var replyDescription = itemView.txt_reply_description!!
        var replyTime = itemView.txt_reply_time!!
        var reply = itemView.txt_reply!!
        var replyLike = itemView.txt_reply_like!!
        var replyAlreadyLike = itemView.txt_reply_already_liked!!
        var replyLayout = itemView.replyLayout!!
        var reportAbuse = itemView.txt_report_abuse!!
        var edit = itemView.txt_edit!!
        var moreLayout = itemView.moreLayout!!
        var moreReply = itemView.img_reply_more!!
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
    }
}