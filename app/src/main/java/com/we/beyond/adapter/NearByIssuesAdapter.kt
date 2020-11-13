package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.R
import com.we.beyond.model.NearByIssues
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.gathering.createGathering.CreateGatheringActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.ui.reportResolved.ReportResolvedActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.list_near_by_issue.view.*
import kotlinx.android.synthetic.main.list_near_by_issue.view.img_gathering
import kotlinx.android.synthetic.main.list_near_by_issue.view.img_issue
import kotlinx.android.synthetic.main.list_near_by_issue.view.txt_name
import kotlin.Exception
import kotlin.collections.ArrayList

/** It binds near by issue data set to views */
class NearByIssuesAdapter(
    val context: Context,
    nearByIssueDetails: ArrayList<NearByIssues>
) : RecyclerView.Adapter<NearByIssuesAdapter.ViewHolder>() {

    /** init array list */
    var nearByIssueDetails: ArrayList<NearByIssues>? = null

    /** init listeners */
    var onLikeDislikeListener: OnLikeDislikeListener? = null
    var onDeletePresenter: OnDeleteListener? = null
    var onReportAbuseListener: OnReportAbuseListener? = null


    init {
        this.nearByIssueDetails = nearByIssueDetails
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_near_by_issue,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {

        return nearByIssueDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize listeners and set data to views */
        val activity = context as Activity
        try {
            onLikeDislikeListener = activity as OnLikeDislikeListener
        } catch (e: Exception) {
            e.printStackTrace()

        }


        try {
            onDeletePresenter = activity as OnDeleteListener
        } catch (e: Exception) {
            e.printStackTrace()

        }

        try {
            onReportAbuseListener = activity as OnReportAbuseListener
        } catch (e: Exception) {
            e.printStackTrace()

        }



        try {

            holder.commentTitle.typeface = ConstantFonts.raleway_regular
            holder.resolveTitle.typeface = ConstantFonts.raleway_regular
            holder.gatheringTitle.typeface = ConstantFonts.raleway_regular
            holder.shareTitle.typeface = ConstantFonts.raleway_regular
            holder.upvoteTitle.typeface = ConstantFonts.raleway_regular


            holder.issueNumber.text = "# ${nearByIssueDetails!![position].issueNumber}"
            //holder.issueNumber.typeface = ConstantFonts.raleway_semibold

            holder.issueTitle.text = nearByIssueDetails!![position].title
            holder.issueTitle.typeface = ConstantFonts.raleway_semibold

            /** share issues details using intent */
            holder.share.setOnClickListener {
                try {

                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )

                    val share = Intent(Intent.ACTION_SEND)
                    share.setType("text/plain")
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                    share.putExtra(Intent.EXTRA_SUBJECT, "WE")
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        "${Constants.BASE_URL}issues/${nearByIssueDetails!![position]._id}"
                    )
                    (context).startActivity(Intent.createChooser(share, "Share Issue!"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            /** It opens LocationActivity and stores location coordinates */
            holder.locationLayout.setOnClickListener {
                val intent = Intent(context, LocationActivity::class.java)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                EasySP.init(context).put("lat", nearByIssueDetails!![position].coordinates[0])
                EasySP.init(context).put("long", nearByIssueDetails!![position].coordinates[1])
                EasySP.init(context).putBoolean("resolved", nearByIssueDetails!![position].resolved)
            }

            if (nearByIssueDetails!![position].likeByUser != null && nearByIssueDetails!![position].likeByUser) {
                holder.like.visibility = View.GONE
                holder.dislike.visibility = View.VISIBLE
                holder.likeTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )
            } else {
                holder.like.visibility = View.VISIBLE
                holder.dislike.visibility = View.GONE
                holder.likeTitle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }

            if (nearByIssueDetails!![position].resolved != null && nearByIssueDetails!![position].resolved) {

                holder.resolved.visibility = View.GONE
                holder.notResolved.visibility = View.GONE

                holder.resolvedFlag.visibility = View.VISIBLE
                holder.resolvedFlag.text = "Resolved"
                holder.resolvedFlag.typeface = ConstantFonts.raleway_regular

                holder.alreadyResolved.visibility = View.VISIBLE

                holder.resolveTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.issue_resolved
                    )
                )
            } else if (!nearByIssueDetails!![position].resolved && nearByIssueDetails!![position].resolutionsCount > 0) {
                holder.notResolved.visibility = View.VISIBLE
                holder.resolved.visibility = View.GONE
                holder.alreadyResolved.visibility = View.GONE
                holder.resolvedFlag.visibility = View.GONE

                holder.resolveTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )
            } else {
                holder.resolvedFlag.visibility = View.GONE
                holder.resolved.visibility = View.VISIBLE
                holder.alreadyResolved.visibility = View.GONE
                holder.notResolved.visibility = View.GONE
            }



            if (nearByIssueDetails!![position].imageUrls != null && nearByIssueDetails!![position].imageUrls.isNotEmpty()) {

                Glide.with(context)
                    .load(nearByIssueDetails!![position].imageUrls[0])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.loading_image)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.issueImage)

                holder.play.visibility = View.GONE

            } else if (nearByIssueDetails!![position].videoUrls != null && nearByIssueDetails!![position].videoUrls.isNotEmpty()) {
                Glide
                    .with(context)
                    .load(nearByIssueDetails!![position].videoUrlThumbnails[0])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.processing_video)
                    .into(holder.issueImage)

                holder.play.visibility = View.VISIBLE

            } else {
                holder.play.visibility = View.GONE
                holder.issueImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.placeholder
                    )
                )
            }


            holder.issueLocation.text =
                nearByIssueDetails!![position].address + ", ${nearByIssueDetails!![position].city}"
            holder.issueLocation.typeface = ConstantFonts.raleway_semibold

            val km = String.format("%.2f", nearByIssueDetails!![position].distance)

            holder.issueLocationDistance.text = km + "Km Away"
            holder.issueLocationDistance.typeface = ConstantFonts.raleway_semibold

            if (nearByIssueDetails!![position].user.profileUrl != null && nearByIssueDetails!![position].user.profileUrl.length > 0) {
                Glide.with(context)
                    .load(nearByIssueDetails!![position].user.profileUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }

            if (nearByIssueDetails!![position].user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {
                holder.profileName.text =
                    nearByIssueDetails!![position].user.firstName + " ${nearByIssueDetails!![position].user.lastName}"
                holder.profileName.typeface = ConstantFonts.raleway_regular
            } else {
                holder.profileName.text = nearByIssueDetails!![position].user.organizationName
                holder.profileName.typeface = ConstantFonts.raleway_regular

            }

            if (nearByIssueDetails!![position].category.name != null && nearByIssueDetails!![position].category.name.isNotEmpty()) {
                holder.categoryTitle.text = nearByIssueDetails!![position].category.name
                holder.categoryTitle.typeface = ConstantFonts.raleway_regular
            }

            /** It opens BadgesActivity and passes below data */
            holder.profilePic.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID, nearByIssueDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            /** It opens BadgesActivity and passes below data */
            holder.profileName.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID, nearByIssueDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }




            holder.issueTime.text =
                ConstantMethods.convertStringToDateStringFull(nearByIssueDetails!![position].createdAt)
            holder.issueTime.typeface = ConstantFonts.raleway_regular

            holder.issueDescription.text = nearByIssueDetails!![position].description
            holder.issueDescription.typeface = ConstantFonts.raleway_medium

            /* holder.comment.setImageResource(R.drawable.ic_comment)
             holder.resolved.setImageResource(R.drawable.ic_report_resolved)
             holder.like.setImageResource(R.drawable.ic_support_button)
             holder.gathering.setImageResource(R.drawable.gathering)
             holder.more.setImageResource(R.drawable.ic_more)
             holder.locate.setImageResource(R.drawable.ic_locate)*/


            val activitySelection = (context as NearByIssueActivity).isSelected
            if (activitySelection) {
                holder.view.visibility = View.GONE
                holder.comment.visibility = View.GONE
                holder.gathering.visibility = View.GONE
                holder.share.visibility = View.GONE
                holder.resolved.visibility = View.GONE
                holder.alreadyResolved.visibility = View.GONE
                holder.notResolved.visibility = View.GONE
                holder.like.visibility = View.GONE
                holder.dislike.visibility = View.GONE
                holder.commentTitle.visibility = View.GONE
                holder.upvoteTitle.visibility = View.GONE
                holder.gatheringTitle.visibility = View.GONE
                holder.shareTitle.visibility = View.GONE
                holder.resolveTitle.visibility = View.GONE

            } else {
                holder.view.visibility = View.VISIBLE
                holder.comment.visibility = View.VISIBLE
                holder.gathering.visibility = View.VISIBLE
                holder.share.visibility = View.VISIBLE
                // holder.resolved.visibility = View.VISIBLE


            }

            holder.itemView.img_already_resolved!!.setOnClickListener {
                ConstantMethods.showWarning(context, "Issue Resolved", "Issue already resolved")
            }

            /** It opens ReportResolvedActivity with pass below data when click on it */
            holder.itemView.img_resolved!!.setOnClickListener {
                val intent = Intent(context, ReportResolvedActivity::class.java)
                intent.putExtra("issueId", nearByIssueDetails!![position]._id)
                (context as NearByIssueActivity).startActivityForResult(intent, 200)
                (context as NearByIssueActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )

            }

            /** It opens ReportResolvedActivity with pass below data when click on it */
            holder.itemView.img_not_resolved!!.setOnClickListener {
                val intent = Intent(context, ReportResolvedActivity::class.java)
                intent.putExtra("issueId", nearByIssueDetails!![position]._id)
                (context as NearByIssueActivity).startActivityForResult(intent, 200)
                (context as NearByIssueActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )

            }

            /** show and hide image views, set text color and call onLike function of onLikeDislikeListener */
            holder.itemView.img_support.setOnClickListener {
                holder.dislike.visibility = View.VISIBLE
                holder.like.visibility = View.GONE

                holder.likeTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )
                onLikeDislikeListener!!.onLike(nearByIssueDetails!![position]._id)

            }

            /** show and hide image views and call onLike function of onLikeDislikeListener */
            holder.itemView.img_dislike.setOnClickListener {

                holder.dislike.visibility = View.GONE
                holder.like.visibility = View.VISIBLE

                holder.likeTitle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))

                onLikeDislikeListener!!.onDislike(nearByIssueDetails!![position]._id)
            }


            /** It opens NearByIssueDetailsActivity with pass below data when click on it */
            holder.itemView.img_comment.setOnClickListener {
                val activitySelection = (context as NearByIssueActivity).isSelected
                if (!activitySelection) {
                    println("image click")
                    val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                    intent.putExtra("issueId", nearByIssueDetails!![position]._id)
                    intent.putExtra("issueNumber", nearByIssueDetails!![position].issueNumber)
                    intent.putExtra("comment", true)
                    (context as NearByIssueActivity).startActivity(intent)
                    (context as NearByIssueActivity).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    //(context as NearByIssueActivity).finish()
                }
            }

            /** It opens CreateGatheringActivity with pass below data when click on it */
            holder.itemView.img_gathering.setOnClickListener {

                if (nearByIssueDetails!![position].resolved) {
                    ConstantMethods.showWarning(
                        context,
                        "",
                        "You cannot create gathering on resolved issues."
                    )
                } else {
                    EasySP.init(context).putString(ConstantEasySP.GATHERING_DATE, "")
                    EasySP.init(context).putString(ConstantEasySP.SELECTED_GATHERING_ADDRESS, "")
                    EasySP.init(context).putString("gatheringTitle", "")
                    EasySP.init(context).putString("gatheringDetails", "")

                    println("image click")

                    val intent = Intent(context, CreateGatheringActivity::class.java)
                    intent.putExtra("issueList", true)
                    intent.putExtra("issueId", nearByIssueDetails!![position]._id)
                    (context as NearByIssueActivity).startActivityForResult(intent, 1)
                    (context).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    //(context as NearByIssueActivity).finish()

                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_ID, nearByIssueDetails!![position]._id)
                    EasySP.init(context)
                        .putInt(
                            ConstantEasySP.ISSUE_NUMBER,
                            nearByIssueDetails!![position].issueNumber
                        )
                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_TITLE, nearByIssueDetails!![position].title)
                }

            }


            /** It opens activities depends on condition with pass below data when click on it */
            holder.itemView.setOnClickListener {


                val activitySelection = (context as NearByIssueActivity).isSelected
                if (activitySelection) {

                    val intent = Intent(context, CreateGatheringActivity::class.java)
                    intent.putExtra("gathering", true)
                    intent.putExtra("issueId", nearByIssueDetails!![position]._id)
                    (context as NearByIssueActivity).startActivity(intent)
                    (context).overridePendingTransition(
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    (context as NearByIssueActivity).finish()


                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_ID, nearByIssueDetails!![position]._id)
                    val imageUrlJson = Gson().toJson(
                        nearByIssueDetails!![position].imageUrls!!
                    )
                    val videoUrlJson = Gson().toJson(
                        nearByIssueDetails!![position].videoUrlThumbnails!!
                    )
                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_IMAGE_URL, imageUrlJson)
                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_VIDEO_URL, videoUrlJson)
                    EasySP.init(context)
                        .putInt(
                            ConstantEasySP.ISSUE_NUMBER,
                            nearByIssueDetails!![position].issueNumber
                        )
                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_TITLE, nearByIssueDetails!![position].title)


                } else {

                    val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                    intent.putExtra("issueId", nearByIssueDetails!![position]._id)
                    intent.putExtra("distance", km)
                    intent.putExtra("issueNumber", nearByIssueDetails!![position].issueNumber)
                    (context).startActivityForResult(intent, 200)
                    (context as NearByIssueActivity).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )

                }

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


            holder.optionLayout.setOnClickListener {

            }

            /** It shows the normal dialog and call onDelete function of onDelete presenter */
            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {

                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )

                (context as NearByIssueActivity).nearByIssuesAdapter!!.notifyDataSetChanged()


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == nearByIssueDetails!![position].user._id) {
                    if (nearByIssueDetails!![position].resolved) {
                        ConstantMethods.showWarning(
                            context,
                            "",
                            "You can not delete resolved issues."
                        )
                    } else {
                        println("user id ${userId} issue user ${nearByIssueDetails!![position].user._id}")
                        try {
                            val sweetAlertDialog =
                                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                            sweetAlertDialog.titleText = ""
                            sweetAlertDialog.contentText = "Do you want to delete issue?"
                            sweetAlertDialog.confirmText = "Yes"
                            sweetAlertDialog.cancelText = "No"
                            sweetAlertDialog.show()
                            sweetAlertDialog.setCancelable(false)
                            sweetAlertDialog.setConfirmClickListener {
                                sweetAlertDialog.dismissWithAnimation()


                                try {

                                    val jsonObject = JsonObject()
                                    jsonObject.addProperty("type", "disabled")
                                    jsonObject.addProperty(
                                        "issueId",
                                        nearByIssueDetails!![position]._id
                                    )


                                    try {
                                        onDeletePresenter!!.onDelete(jsonObject)

                                    } catch (e: Exception) {
                                        e.printStackTrace()
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
                } else {

                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "You are not owner of this issue."
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

            /** It closes the more layout with animation */
            holder.moreLayout.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )
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
                    if (userId == nearByIssueDetails!![position].user._id) {

                        ConstantMethods.showWarning(context, "", "You can not abuse your issue.")
                    } else if (nearByIssueDetails!![position].resolved) {
                        ConstantMethods.showWarning(
                            context,
                            "",
                            "You can not abuse resolved issues."
                        )
                    } else {

                        onReportAbuseListener!!.ReportAbuse(
                            nearByIssueDetails!![position]._id,
                            "issue"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var issueNumber = itemView.txt_issue_number!!
        var issueTitle = itemView.txt_issue_title!!
        var issueImage = itemView.img_issue!!
        var issueLocation = itemView.txt_issue_location!!
        var issueLocationDistance = itemView.txt_issue_distance!!
        var profilePic = itemView.img_profile_pic!!
        var profileName = itemView.txt_name!!
        var issueTime = itemView.txt_time!!
        var categoryTitle = itemView.txt_category_title!!
        var issueDescription = itemView.txt_issue_description!!
        var comment = itemView.img_comment!!
        var resolved = itemView.img_resolved!!
        var alreadyResolved = itemView.img_already_resolved!!
        var notResolved = itemView.img_not_resolved!!
        var gathering = itemView.img_gathering!!
        var locate = itemView.img_locate!!
        var view = itemView.view!!
        var resolvedFlag = itemView.txt_flag!!
        var like = itemView.img_support!!
        var dislike = itemView.img_dislike!!
        var share = itemView.img_share!!
        var commentTitle = itemView.txt_comment!!
        var resolveTitle = itemView.txt_resolved!!
        var upvoteTitle = itemView.txt_support!!
        var gatheringTitle = itemView.txt_gathering!!
        var shareTitle = itemView.txt_share_title!!

        //var more = itemView.img_more!!
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
        var reportAbuse = itemView.txt_report_abuse!!
        var moreLayout = itemView.moreLayout!!

        //var moreTitle = itemView.txt_more_title!!
        var likeTitle = itemView.txt_support!!
        var optionLayout = itemView.optionLayout!!
        var play = itemView.img_play!!
        var locationLayout = itemView.issueDetailsLayout!!

    }


}