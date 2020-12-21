package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.Interface.OnEditListener
import com.we.beyond.Interface.OnIssuesResolvedListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.R
import com.we.beyond.api.CommentsApi
import com.we.beyond.api.ResolutionApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.ui.CommentActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.ui.reportResolved.ReportResolvedActivity
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
import kotlinx.android.synthetic.main.list_comments.view.img_close
import kotlinx.android.synthetic.main.list_comments.view.img_more
import kotlinx.android.synthetic.main.list_comments.view.moreLayout
import kotlinx.android.synthetic.main.list_comments.view.txt_delete
import kotlinx.android.synthetic.main.list_comments.view.txt_flag
import kotlinx.android.synthetic.main.list_comments.view.txt_name
import kotlinx.android.synthetic.main.list_comments.view.txt_report_abuse
import kotlinx.android.synthetic.main.list_comments.view.txt_time
import retrofit2.HttpException
import java.io.IOException
import kotlin.Exception

/** It binds comment data set to views */

class CommentAdapter(
    val context: Context,
    commentDetails: ArrayList<CommentsData>,
    userId: String,
    isIssueResolved: Boolean
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    /** init array list */
    var commentDetails: ArrayList<CommentsData>? = null

    /** init listeners */
    var onLikeDislikeListener: OnLikeDislikeListener? = null
    var onApprovedListener: OnIssuesResolvedListener? = null
    var onReportAbuseListener : OnReportAbuseListener?=null
    var onEditListener : OnEditListener?=null

    /** init adapter and layout manager */
    var mediaAdapter: CommentMediaAdapter? = null
    var gridLayoutManager: GridLayoutManager? = null

    /** init variables */
    var userIssueId: String = ""
    var isResolved = false
    var selectedPositionId : String =""

    init {
        this.commentDetails = commentDetails
        this.userIssueId = userId
        this.isResolved = isIssueResolved

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

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize listeners and set data to views */
        try {
            //media recycler
            val activity = context as Activity

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

            /** It show and hide text view depends on condition */
            var mediaStatusArray = ArrayList<MediaUploadingPojo>()

            val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
            println("user id $userId comment id ${userIssueId}")
            if (userId != null && userId.isNotEmpty()) {
                if (userId != commentDetails!![position].user._id)
                {
                    holder.delete.visibility=View.GONE
                    holder.divider1.visibility=View.GONE
                    holder.edit.visibility=View.GONE
                    holder.divider2.visibility=View.GONE
                }
                else
                {
                    holder.delete.visibility=View.VISIBLE
                    holder.divider1.visibility=View.VISIBLE
                    holder.edit.visibility=View.VISIBLE
                    holder.divider2.visibility=View.VISIBLE
                }

                if (!isResolved) {
                    if (userId.equals(userIssueId, ignoreCase = true)) {
                        if (commentDetails!![position].commentType.equals(
                                "resolution",
                                ignoreCase = true
                            ) && !commentDetails!![position].markAsFinal
                        ) {
                            holder.approved.visibility = View.VISIBLE
                            holder.approved.typeface = ConstantFonts.raleway_semibold
                        }
                    }
                } else {
                    holder.approved.visibility = View.GONE
                    holder.approved.typeface = ConstantFonts.raleway_semibold
                }
            }

            try {
                onApprovedListener = activity as OnIssuesResolvedListener
            } catch (e: Exception) {
                e.printStackTrace()

            }

            /**It opens normal dialog and call onApproved function of onApprovedListener */
            holder.approved.setOnClickListener {
                try {
                    val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = "Approve"
                    sweetAlertDialog.contentText = "Do you want to Approve this Issue?"
                    sweetAlertDialog.show()
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.setConfirmClickListener {
                        sweetAlertDialog.dismissWithAnimation()

                        try {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                onApprovedListener!!.onApproved(commentDetails!![position]._id)
                            }

                        } catch (e: Exception) {

                        }

                    }

                    sweetAlertDialog.setCancelClickListener {
                        sweetAlertDialog.dismissWithAnimation()
                    }


                } catch (e: Exception) {


                }

            }

            /** It check image and video urls and add to array */
            if (commentDetails!![position].imageUrls != null && commentDetails!![position].videoUrls != null) {
                /* mediaArrayList.addAll(commentDetails!![position].imageUrls)
             mediaArrayList.addAll(commentDetails!![position].videoUrls)*/
                // mediaStatusArray.clear()
                for (i in 0 until commentDetails!![position].imageUrls.size) {

                    mediaStatusArray.add(
                        MediaUploadingPojo(
                            "",
                            commentDetails!![position].imageUrls[i],
                            "image",
                            false
                        )
                    )
                }
                for (i in 0 until commentDetails!![position].videoUrls.size) {

                    mediaStatusArray.add(
                        MediaUploadingPojo(
                            "",
                            commentDetails!![position].videoUrls[i],
                            "video",
                            false
                        )
                    )
                }
            } else if (commentDetails!![position].imageUrls != null) {
                // mediaArrayList.addAll(commentDetails!![position].imageUrls)
                mediaStatusArray.add(
                    MediaUploadingPojo(
                        "",
                        commentDetails!![position].imageUrls[position],
                        "image",
                        false
                    )
                )
            } else if (commentDetails!![position].videoUrls != null) {
                //mediaArrayList.addAll(commentDetails!![position].videoUrls)
                mediaStatusArray.add(
                    MediaUploadingPojo(
                        "",
                        commentDetails!![position].videoUrls[position],
                        "video",
                        false
                    )
                )
            } else {
                mediaStatusArray.clear()
                mediaStatusArray[position].serverUrl = ""
                mediaStatusArray[position].localUrl = ""
                mediaStatusArray[position].mimeType = ""
                mediaStatusArray[position].isUpload = false
            }

            /** initialize listener */
            try {
                onLikeDislikeListener = activity as OnLikeDislikeListener
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


            /** show and hide views */
            if (commentDetails!!.size > 1) {
                holder.divider.visibility = View.VISIBLE
            } else {
                holder.divider.visibility = View.GONE
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
            holder.commentAlreadyLike.typeface = ConstantFonts.raleway_semibold

            /** It set comment id and position of NearByIssueDetailsActivity and call getData() */
            holder.commentReply.setOnClickListener {
                (context as NearByIssueDetailsActivity).commentId = commentDetails!![position]._id
                (context as NearByIssueDetailsActivity).commentPosition = position
                // (context as NearByIssueDetailsActivity).resetValue()
                (context as NearByIssueDetailsActivity).getData()
            }

            /** It opens CommentActivity with pass below data when click on it */
            holder.commentLayout.setOnClickListener {
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra(Constants.COMMENT_ID, commentDetails!![position]._id)
                intent.putExtra("issueId",commentDetails!![position].issue)
                intent.putExtra("isResolved",isResolved)
                intent.putExtra("userId",userIssueId)
                (context as NearByIssueDetailsActivity).startActivityForResult(intent,1)
                (context as NearByIssueDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )


            }

            //set resolved flag
            if (commentDetails!![position].commentType.equals(
                    "resolution",
                    ignoreCase = true
                ) && commentDetails!![position].markAsFinal
            ) {
                holder.commentResolved.visibility = View.VISIBLE
                holder.commentResolved.text = "Approved Resolution"
                holder.commentResolved.typeface = ConstantFonts.raleway_regular
                if (commentDetails!![position].imageUrls.isNotEmpty() || commentDetails!![position].videoUrls.isNotEmpty()) {
                    if (mediaStatusArray != null && mediaStatusArray.isNotEmpty()) {
                        gridLayoutManager = GridLayoutManager(context, 3)
                        holder.mediaRecycler.layoutManager = gridLayoutManager

                        holder.mediaRecycler.visibility = View.VISIBLE
                        val mediaAdapter = CommentMediaAdapter(context, mediaStatusArray)
                        holder.mediaRecycler.adapter = mediaAdapter


                    }
                } else {
                    holder.mediaRecycler.visibility = View.GONE

                }

            } else if (commentDetails!![position].commentType.equals(
                    "resolution",
                    ignoreCase = true
                ) && !commentDetails!![position].markAsFinal
            ) {

                holder.commentResolved.visibility = View.VISIBLE
                holder.commentResolved.text = "Reported  Resolution"
                holder.commentResolved.backgroundTintList=ContextCompat.getColorStateList(context,R.color.button_background)
                holder.commentResolved.typeface = ConstantFonts.raleway_regular

                if (commentDetails!![position].imageUrls.isNotEmpty() || commentDetails!![position].videoUrls.isNotEmpty()) {

                    if (mediaStatusArray != null && mediaStatusArray.isNotEmpty()) {
                        holder.mediaRecycler.visibility = View.VISIBLE

                        gridLayoutManager = GridLayoutManager(context, 3)
                        holder.mediaRecycler.layoutManager = gridLayoutManager

                        mediaAdapter = CommentMediaAdapter(context, mediaStatusArray)
                        holder.mediaRecycler.adapter = mediaAdapter

                        println("media status array $mediaStatusArray")
                    }
                } else {
                    holder.mediaRecycler.visibility = View.GONE
                }
            } else {
                holder.commentResolved.visibility = View.GONE
            }


            //set profile pic
            if (commentDetails!![position].reply != null) {

                if (commentDetails!![position].reply.likeByUser != null && commentDetails!![position].reply.likeByUser) {
                    holder.replyLike.visibility = View.GONE
                    holder.replyAlreadyLike.visibility = View.VISIBLE
                }



                holder.replyLayout.visibility = View.VISIBLE
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
                holder.replyAlreadyLike.typeface = ConstantFonts.raleway_semibold



                println("comment type ${commentDetails!![position].commentType}")


                /** It set comment id and position of NearByIssueDetailsActivity and call getData() */
                holder.reply.setOnClickListener {
                    (context as NearByIssueDetailsActivity).commentId =
                        commentDetails!![position]._id
                    (context as NearByIssueDetailsActivity).commentPosition = position
                    //(context as NearByIssueDetailsActivity).resetValue()
                    (context as NearByIssueDetailsActivity).getData()
                }


                /** It opens CommentActivity with pass below data when click on it */
                holder.replyLayout.setOnClickListener {
                    val intent = Intent(context, CommentActivity::class.java)
                    intent.putExtra(Constants.COMMENT_ID, commentDetails!![position].reply._id)
                    intent.putExtra("issueId",commentDetails!![position].issue)
                    intent.putExtra("isResolved",isResolved)
                    intent.putExtra("userId",userIssueId)
                    (context as NearByIssueDetailsActivity).startActivityForResult(intent,1)
                    (context as NearByIssueDetailsActivity).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )

                }

            } else {
                holder.replyLayout.visibility = View.GONE
            }

            /** It opens more layout with animation */
            holder.more.setOnClickListener {
                selectedPositionId = commentDetails!![position]._id
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_up))

            }

            /** It opens more layout with animation */
            holder.moreReply.setOnClickListener {
                selectedPositionId = commentDetails!![position].reply._id
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_up))
            }

            /** It closes more layout with animation */
            holder.close.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
            }

            /** It opens normal dialog and call deleteResolution and deleteComment api with below json object depends on conditions */
            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {
                if (selectedPositionId == commentDetails!![position]._id && commentDetails!![position].commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && commentDetails!![position].markAsFinal
                ) {
                    ConstantMethods.showToast(
                        context,
                        "You cannot delete approved resolution"
                    )
                } else if (selectedPositionId == commentDetails!![position]._id && commentDetails!![position].commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && !commentDetails!![position].markAsFinal
                ) {

                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                    (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == commentDetails!![position].user._id) {

                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "Do you want to delete resolution?"
                            sweetAlertDialog.confirmText = "Yes"
                            sweetAlertDialog.cancelText = "No"
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()

                                println("resolution id ${commentDetails!![position]._id}")

                                try {

                                    val jsonObject = JsonObject()
                                    jsonObject.addProperty(
                                        "resolutionId", commentDetails!![position]._id)
                                    jsonObject.addProperty("type","disabled")



                                    try {
                                        val commentsApi =
                                            ApplicationController.retrofit.create(ResolutionApi::class.java)
                                        val response: Single<DeleteReportedResolutionPojo> =
                                            commentsApi.deleteResolution(jsonObject)
                                        response.subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(object :
                                                DisposableSingleObserver<DeleteReportedResolutionPojo>() {
                                                override fun onSuccess(deleteCommentPojo: DeleteReportedResolutionPojo) {
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
                            sweetAlertDialog.contentText = "You are not owner of this resolution."
//                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }
                            ConstantMethods.showToast(context,"You are not the owner of this resolution.")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                } else if(commentDetails!![position].reply!=null) {

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
                                sweetAlertDialog.contentText = "You are not owner of this reply."
//                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }
                                ConstantMethods.showToast(context,"You are not the owner of this reply.")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }

                    }

                    else {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                        (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


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
//                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }
                                ConstantMethods.showToast(context,"You are not the owner of this comment.")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }
                    }
                }
                    else {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                    (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


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
//                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }
                            ConstantMethods.showToast(context,"You are not the owner of this comment.")
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
                    (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


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
//                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }
                            ConstantMethods.showToast(context,"You are not the owner of this resolution.")
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
                        (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


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
//                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }
                                ConstantMethods.showToast(context,"You are not the owner of this reply.")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }

                    }
                    else {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                        (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


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
//                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }
                                ConstantMethods.showToast(context,"You are not the owner of this comment.")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }
                    }


                }
                else {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                    (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


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
//                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }
                            ConstantMethods.showToast(context,"You are not the owner of this comment.")
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
                    (context as NearByIssueDetailsActivity).tagsList!!.clear()
                    (context as NearByIssueDetailsActivity).tagUserName!!.clear()


                    for(i in 0 until commentDetails!![position].tags.size)
                    {
                        (context as NearByIssueDetailsActivity).tagsList!!.add(commentDetails!![position].tags[i])
                        (context as NearByIssueDetailsActivity).tagUserName!!.add(commentDetails!![position].tagNames[i])

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
                    (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == commentDetails!![position].user._id)
                    {
                        try {

                            println("resolution id ${commentDetails!![position]._id}")

                            val jsonString = Gson().toJson(commentDetails!![position])
                            val intent = Intent(context, ReportResolvedActivity::class.java)
                            intent.putExtra("issueId", commentDetails!![position].issue)
                            intent.putExtra("resolutionId", commentDetails!![position]._id)
                            intent.putExtra("resolutionData",jsonString)
                            intent.putExtra("edit", true)
                            (context ).startActivityForResult(intent,200)
                            (context as NearByIssueDetailsActivity).overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )

                        } catch (e: Exception) {
                            e.printStackTrace()

                        }
                    }
                    else {

                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "You are not owner of this resolution."
//                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }
                            ConstantMethods.showToast(context,"You are not the owner of this resolution.")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }
               /* else if(){
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                    (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


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
*/
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
                        (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == commentDetails!![position].reply.user._id) {
                            println("user id ${userId} comment user ${commentDetails!![position].reply.user._id}")
                            try {
                                onEditListener!!.OnEdit(
                                    commentDetails!![position].reply._id,
                                    commentDetails!![position].reply.text
                                )
                            } catch (e: Exception) {

                                e.printStackTrace()
                            }

                        } else {

                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "You are not owner of this reply."
//                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }
                                ConstantMethods.showToast(context,"You are not the owner of this reply.")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }

                    }
                    else {
                        holder.moreLayout.visibility = View.GONE
                        holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                        (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                        val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                        if (userId == commentDetails!![position].user._id) {
                            println("user id ${userId} comment user ${commentDetails!![position].user._id}")
                            try {
                                onEditListener!!.OnEdit(
                                    commentDetails!![position]._id,
                                    commentDetails!![position].text
                                )

                            } catch (e: Exception) {
                            e.printStackTrace()

                            }
                        } else {

                            try {
                                val sweetAlertDialog =
                                    SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                                sweetAlertDialog.titleText = ""
                                sweetAlertDialog.contentText = "You are not owner of this comment."
//                                sweetAlertDialog.show()
                                sweetAlertDialog.setCancelable(false)
                                sweetAlertDialog.setConfirmClickListener {
                                    sweetAlertDialog.dismissWithAnimation()
                                }
                                ConstantMethods.showToast(context,"You are not the owner of this comment.")
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }
                    }


                }
                else {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                    (context as NearByIssueDetailsActivity).commentsAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == commentDetails!![position].user._id) {
                        println("user id ${userId} comment user ${commentDetails!![position].user._id}")
                        try {
                            onEditListener!!.OnEdit(
                                commentDetails!![position]._id,
                                commentDetails!![position].text
                            )

                        } catch (e: Exception) {
                        e.printStackTrace()

                        }
                    } else {

                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "You are not owner of this comment."
//                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()
                            }
                            ConstantMethods.showToast(context,"You are not the owner of this comment.")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var commentPofilePic = itemView.img_comment_profile_pic!!
        var replyProfilePic = itemView.img_reply_profile_pic!!
        var name = itemView.txt_name!!
        var replyName = itemView.txt_reply_name!!
        var commentDescription = itemView.txt_comment_description!!
        var replyDescription = itemView.txt_reply_description!!
        var commentTime = itemView.txt_time!!
        var replyTime = itemView.txt_reply_time!!
        var commentReply = itemView.txt_comment_reply!!
        var reply = itemView.txt_reply!!
        var commentLike = itemView.txt_comment_like!!
        var commentAlreadyLike = itemView.txt_comment_already_liked!!
        var replyLike = itemView.txt_reply_like!!
        var replyAlreadyLike = itemView.txt_reply_already_liked!!
        var replyLayout = itemView.replyLayout!!
        var divider = itemView.dividerView!!
        var commentResolved = itemView.txt_flag!!
        var mediaRecycler = itemView.recycler_media!!
        var commentLayout = itemView.commentCard!!
        var approved = itemView.txt_resolution_approved!!
        var more = itemView.img_more!!
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
        var moreLayout = itemView.moreLayout!!
        var moreReply = itemView.img_reply_more!!
        var reportAbuse = itemView.txt_report_abuse!!
        var edit = itemView.txt_edit!!
        var divider1= itemView.view1!!
        var divider2= itemView.view2!!
    }


}