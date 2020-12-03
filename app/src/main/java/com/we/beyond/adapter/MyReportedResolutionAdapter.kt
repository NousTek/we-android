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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.Interface.*
import com.we.beyond.R
import com.we.beyond.api.ResolutionApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DeleteReportedResolutionPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.MediaUploadingPojo
import com.we.beyond.model.Resolution
import com.we.beyond.ui.profile.ReportedResolutionDetailsActivity
import com.we.beyond.ui.reportResolved.ReportResolvedActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.list_resolution_item.view.*
import retrofit2.HttpException
import java.io.IOException

/** It binds user reported resolution data set to views */

class MyReportedResolutionAdapter (
    val context: Context,
    resolutionDetails: ArrayList<Resolution>,
    userId: String,
    issueResolved: Boolean
) : RecyclerView.Adapter<MyReportedResolutionAdapter.ViewHolder>() {

    /** init array list */
    var resolutionDetails: ArrayList<Resolution>? = null

    /** init listeners */
    var onLikeDislikeListener : OnLikeDislikeListener?=null
    var onApprovedListener : OnIssuesResolvedListener?=null
    var onReportAbuseListener : OnReportAbuseListener?=null
    var onDeletePresenter : OnDeleteListener?=null
    var onEditListener : OnEditListener?=null

    /** init adapter and layout manager  */
    var mediaAdapter : CommentMediaAdapter?=null
    var gridLayoutManager: GridLayoutManager? = null

    /** init string and boolean */
    var userIssueId : String =""
    var isResolved = false

    init {
        this.resolutionDetails = resolutionDetails
        this.userIssueId =userId
        this.isResolved = issueResolved


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.list_resolution_item,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {

        return resolutionDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize listeners and set data to views */

        try {


            val activity = context as Activity

            try {
                onDeletePresenter = activity as OnDeleteListener
            } catch (e: java.lang.Exception) {
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


            //media recycler

            var mediaStatusArray = ArrayList<MediaUploadingPojo>()

            val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
            println("user id $userId comment id ${userIssueId}")
            if(userId != null && userId.isNotEmpty())
            {
                if(!isResolved) {
                    if (userId.equals(userIssueId, ignoreCase = true)) {
                        if (resolutionDetails!![position].commentType.equals(
                                "resolution",
                                ignoreCase = true
                            ) && !resolutionDetails!![position].markAsFinal
                        ) {
                            holder.approved.visibility = View.VISIBLE
                            holder.approved.typeface = ConstantFonts.raleway_semibold
                        }
                    }
                }
                else{
                    holder.approved.visibility = View.GONE
                    holder.approved.typeface = ConstantFonts.raleway_semibold
                }
            }


            try {
                onApprovedListener = activity as OnIssuesResolvedListener
            } catch (e: Exception) {
                e.printStackTrace()

            }
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
                                onApprovedListener!!.onApproved(resolutionDetails!![position]._id)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    }


                    sweetAlertDialog.setCancelClickListener {
                        sweetAlertDialog.dismissWithAnimation()
                    }


                } catch (e: Exception) {

                    e.printStackTrace()
                }

            }


            if (resolutionDetails!![position].imageUrls != null && resolutionDetails!![position].videoUrls != null) {
                /* mediaArrayList.addAll(commentDetails!![position].imageUrls)
             mediaArrayList.addAll(commentDetails!![position].videoUrls)*/
                // mediaStatusArray.clear()
                for (i in 0 until resolutionDetails!![position].imageUrls.size) {

                    mediaStatusArray.add(
                        MediaUploadingPojo(
                            "",
                            resolutionDetails!![position].imageUrls[i],
                            "image",
                            false
                        )
                    )
                }
                for (i in 0 until resolutionDetails!![position].videoUrls.size) {

                    mediaStatusArray.add(
                        MediaUploadingPojo(
                            "",
                            resolutionDetails!![position].videoUrls[i],
                            "video",
                            false
                        )
                    )
                }
            }
            else if (resolutionDetails!![position].imageUrls != null) {
                // mediaArrayList.addAll(commentDetails!![position].imageUrls)
                mediaStatusArray.add(
                    MediaUploadingPojo(
                        "",
                        resolutionDetails!![position].imageUrls[position],
                        "image",
                        false
                    )
                )
            }

            else if (resolutionDetails!![position].videoUrls != null) {
                //mediaArrayList.addAll(commentDetails!![position].videoUrls)
                mediaStatusArray.add(
                    MediaUploadingPojo(
                        "",
                        resolutionDetails!![position].videoUrls[position],
                        "video",
                        false
                    )
                )
            }
            else{
                mediaStatusArray.clear()
                mediaStatusArray[position].serverUrl = ""
                mediaStatusArray[position].localUrl = ""
                mediaStatusArray[position].mimeType = ""
                mediaStatusArray[position].isUpload = false
            }


            try {
                onLikeDislikeListener = activity as OnLikeDislikeListener
            } catch (e: Exception) {
                e.printStackTrace()

            }

            /** show and hide image views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_comment_like.setOnClickListener {
                holder.commentAlreadyLike.visibility = View.VISIBLE
                holder.commentLike.visibility = View.GONE

                onLikeDislikeListener!!.onLike(resolutionDetails!![position]._id)

            }

            /** show and hide image views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_comment_already_liked.setOnClickListener {

                holder.commentAlreadyLike.visibility = View.GONE
                holder.commentLike.visibility = View.VISIBLE


                onLikeDislikeListener!!.onDislike(resolutionDetails!![position]._id)
            }


            /* if(resolutionDetails!![position].likeByUser != null && resolutionDetails!![position].likeByUser)
             {
                 holder.commentLike.visibility = View.GONE
                 holder.commentAlreadyLike.visibility = View.VISIBLE
             }
 */


            //set profile pic
            if (resolutionDetails!![position].user.profileUrl != null && resolutionDetails!![position].user.profileUrl.length > 0) {
                Glide.with(context)
                    .load(resolutionDetails!![position].user.profileUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.commentPofilePic)
            } else {
                holder.commentPofilePic.setBackgroundResource(R.drawable.profile)
            }

            //set user name
            if (resolutionDetails!![position].user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {
                holder.name.text =
                    resolutionDetails!![position].user.firstName + " ${resolutionDetails!![position].user.lastName}"
                holder.name.typeface = ConstantFonts.raleway_semibold
            } else {
                holder.name.text =
                    resolutionDetails!![position].user.organizationName
                holder.name.typeface = ConstantFonts.raleway_semibold
            }

            //set comment description
            holder.commentDescription.text = resolutionDetails!![position].text
            holder.commentDescription.typeface = ConstantFonts.raleway_regular

            //set comment time
            holder.commentTime.text =
                ConstantMethods.convertStringToDateStringFull(resolutionDetails!![position].createdAt)
            holder.commentTime.typeface = ConstantFonts.raleway_regular

            //set comment reply
            //holder.commentReply.typeface = ConstantFonts.raleway_semibold

            //set comment like
            holder.commentLike.typeface = ConstantFonts.raleway_semibold
            holder.commentAlreadyLike.typeface = ConstantFonts.raleway_semibold

            /*holder.commentReply.setOnClickListener {
                (context as ReportedResolutionDetailsActivity).commentId = resolutionDetails!![position]._id
                (context as ReportedResolutionDetailsActivity).commentPosition = position
                // (context as ReportedResolutionDetailsActivity).resetValue()
                (context as ReportedResolutionDetailsActivity).getData()
            }*/

            /*   holder.commentLayout.setOnClickListener {
                   val intent = Intent(context, CommentActivity::class.java)
                   intent.putExtra(Constants.COMMENT_ID,resolutionDetails!![position]._id)
                   (context as NearByIssueDetailsActivity).startActivity(intent)
                   (context as NearByIssueDetailsActivity).overridePendingTransition(
                       R.anim.slide_in_right,
                       R.anim.slide_out_left
                   )


               }*/

            //set resolved flag
            if (resolutionDetails!![position].commentType.equals(
                    "resolution",
                    ignoreCase = true
                ) && resolutionDetails!![position].markAsFinal
            ) {
                holder.commentResolved.visibility = View.VISIBLE
                holder.commentResolved.text = "Approved Resolution"
                holder.commentResolved.typeface = ConstantFonts.raleway_regular
                if(resolutionDetails!![position].imageUrls.isNotEmpty() || resolutionDetails!![position].videoUrls.isNotEmpty()) {
                    if (mediaStatusArray != null && mediaStatusArray.isNotEmpty()) {
                        gridLayoutManager = GridLayoutManager(context, 3)
                        holder.mediaRecycler.layoutManager = gridLayoutManager

                        holder.mediaRecycler.visibility = View.VISIBLE
                        val mediaAdapter = CommentMediaAdapter(context, mediaStatusArray)
                        holder.mediaRecycler.adapter = mediaAdapter


                    }
                }
                else {
                    holder.mediaRecycler.visibility = View.GONE

                }

            } else if (resolutionDetails!![position].commentType.equals(
                    "resolution",
                    ignoreCase = true
                ) && !resolutionDetails!![position].markAsFinal
            ) {

                holder.commentResolved.visibility = View.VISIBLE
                holder.commentResolved.text = "Reported  Resolution"

                holder.commentResolved.backgroundTintList=
                    ContextCompat.getColorStateList(context, R.color.button_background)
                holder.commentResolved.typeface = ConstantFonts.raleway_regular


                if(resolutionDetails!![position].imageUrls.isNotEmpty() || resolutionDetails!![position].videoUrls.isNotEmpty()) {

                    if (mediaStatusArray != null && mediaStatusArray.isNotEmpty()) {
                        holder.mediaRecycler.visibility = View.VISIBLE

                        gridLayoutManager = GridLayoutManager(context, 3)
                        holder.mediaRecycler.layoutManager = gridLayoutManager

                        mediaAdapter = CommentMediaAdapter(context, mediaStatusArray)
                        holder.mediaRecycler.adapter = mediaAdapter

                        println("media status array $mediaStatusArray")
                    }
                }
                else {
                    holder.mediaRecycler.visibility = View.GONE
                }
            } else {
                holder.commentResolved.visibility = View.GONE
            }

            /** It opens more layout with animation */
            holder.more.setOnClickListener {
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_up))

            }

            /** It closes more layout with animation */
            holder.close.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_down))
            }

            /** It opens normal dialog and call deleteResolution api with below json object depends on conditions */
            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {

                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_down))
                (context as ReportedResolutionDetailsActivity).resolutionAdapter!!.notifyDataSetChanged()


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == resolutionDetails!![position].user._id) {
                    println("user id ${userId} resolution user ${resolutionDetails!![position].user._id}")
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


                            try {

                                val jsonObject = JsonObject()
                                jsonObject.addProperty("type", "disabled")
                                jsonObject.addProperty("resolutionId", resolutionDetails!![position]._id)


                                try {
                                    val resolutionApi = ApplicationController.retrofit.create(
                                        ResolutionApi::class.java)
                                    val response: Single<DeleteReportedResolutionPojo> = resolutionApi.deleteResolution(jsonObject)
                                    response.subscribeOn(Schedulers.newThread()).observeOn(
                                        AndroidSchedulers.mainThread())
                                        .subscribe(object : DisposableSingleObserver<DeleteReportedResolutionPojo>() {
                                            override fun onSuccess(deleteReportedResolutionPojo: DeleteReportedResolutionPojo) {
                                                if (deleteReportedResolutionPojo != null) {

                                                    println("response $deleteReportedResolutionPojo")
                                                    ConstantMethods.cancleProgessDialog()

                                                    //nearByIssue.setOnDelete(deleteNearByIssueByIdDetailsPojo.data._id)

                                                    val deleteConnectList = ArrayList<Resolution>()
                                                    deleteConnectList.addAll(resolutionDetails!!)

                                                    val issueId = resolutionDetails!![position]._id
                                                    for( i in 0 until deleteConnectList.size)
                                                    {

                                                        if(deleteConnectList[i]._id == issueId)
                                                        {
                                                            deleteConnectList.remove(deleteConnectList[i])
                                                            resolutionDetails!!.clear()
                                                            resolutionDetails!!.addAll(deleteConnectList)
                                                            (context as ReportedResolutionDetailsActivity).resolutionAdapter!!.notifyDataSetChanged()

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




                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }


                        }


                        sweetAlertDialog.setCancelClickListener {
                            sweetAlertDialog.dismissWithAnimation()
                        }


                    } catch (e: java.lang.Exception) {


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

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }


                }
            }

            /** It closes the more layout with animation and call ReportAbuse function  */
            holder.reportAbuse.typeface = ConstantFonts.raleway_semibold
            holder.reportAbuse.setOnClickListener {
                try {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )
                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == resolutionDetails!![position].user._id) {

                        ConstantMethods.showToast(context,"You cannot abuse your resolution.")
                    }
                    else if (resolutionDetails!![position].commentType.equals(
                            "resolution",
                            ignoreCase = true
                        ) && resolutionDetails!![position].markAsFinal
                    ) {
                        ConstantMethods.showToast(context,"You cannot abuse approved resolution.")
                    }
                    else if (resolutionDetails!![position].commentType.equals(
                            "resolution",
                            ignoreCase = true
                        ) && !resolutionDetails!![position].markAsFinal
                    ) {
                        onReportAbuseListener!!.ReportAbuse(
                            resolutionDetails!![position]._id,
                            "resolution"
                        )
                    }

                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }

            //edit
            holder.edit.typeface = ConstantFonts.raleway_semibold
            holder.edit.setOnClickListener {
                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)

                if (userId == resolutionDetails!![position].user._id && resolutionDetails!![position].commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && resolutionDetails!![position].markAsFinal
                ) {
                    ConstantMethods.showToast(
                        context,
                        "You cannot edit approved resolution"
                    )
                } else if (userId == resolutionDetails!![position].user._id && resolutionDetails!![position].commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && !resolutionDetails!![position].markAsFinal
                ) {

                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )
                    (context as ReportedResolutionDetailsActivity).resolutionAdapter!!.notifyDataSetChanged()

                    println("user id $userId resolution id ${resolutionDetails!![position]._id} ")

                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == resolutionDetails!![position].user._id)
                    {
                        try {
                            /*onEditListener!!.OnEdit(
                                commentDetails!![position]._id,
                                commentDetails!![position].text
                            )*/
                            val jsonString = Gson().toJson(resolutionDetails!![position])
                            val intent = Intent(context, ReportResolvedActivity::class.java)
                            intent.putExtra("issueId", resolutionDetails!![position].issue)
                            intent.putExtra("resolutionId", resolutionDetails!![position]._id)
                            intent.putExtra("userResolutionData",jsonString)
                            intent.putExtra("edit", true)
                            (context ).startActivityForResult(intent,200)
                            (context as ReportedResolutionDetailsActivity).overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )

                        } catch (e: Exception) {


                        }
                    }
                    else {

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
                }


                else {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_down))
                    (context as ReportedResolutionDetailsActivity).resolutionAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == resolutionDetails!![position].user._id) {
                        println("user id ${userId} comment user ${resolutionDetails!![position].user._id}")
                        try {
                            onEditListener!!.OnEdit(
                                resolutionDetails!![position]._id,
                                resolutionDetails!![position].text
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


        catch(e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var commentPofilePic = itemView.img_comment_profile_pic!!
        var name = itemView.txt_name!!
        var commentDescription = itemView.txt_comment_description!!
        var commentTime = itemView.txt_time!!
        //var commentReply = itemView.txt_comment_reply!!
        var commentLike = itemView.txt_comment_like!!
        var commentAlreadyLike = itemView.txt_comment_already_liked!!
        var commentResolved = itemView.txt_flag!!
        var mediaRecycler = itemView.recycler_media!!
        var approved = itemView.txt_resolution_approved!!
        var more = itemView.img_more!!
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
        var moreLayout = itemView.moreLayout!!
        var reportAbuse = itemView.txt_report_abuse!!
        var edit = itemView.txt_edit!!

    }


}