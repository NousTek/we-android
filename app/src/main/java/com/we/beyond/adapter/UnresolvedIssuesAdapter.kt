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
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.R
import com.we.beyond.api.DashboardApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DeleteNearByIssueByIdDetailsPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.model.NearByIssues
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.dashboard.SummaryDetailsActivity
import com.we.beyond.ui.gathering.createGathering.CreateGatheringActivity
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
import kotlinx.android.synthetic.main.list_near_by_issue.view.*
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

/** It binds un resolved issue data set to views */
class UnresolvedIssuesAdapter(
    val context: Context,
    unresolveIssuesDetails: ArrayList<NearByIssues>
) : RecyclerView.Adapter<UnresolvedIssuesAdapter.ViewHolder>() {

    /** init array list */
    var unresolveIssuesDetails: ArrayList<NearByIssues>? = null

    /** init listeners */
    var onLikeDislikeListener: OnLikeDislikeListener? = null
    var onReportAbuseListener: OnReportAbuseListener? = null

    init {
        this.unresolveIssuesDetails = unresolveIssuesDetails
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


        return unresolveIssuesDetails!!.size
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


            /** share issue details using intent */
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
                        "${Constants.BASE_URL}issues/${unresolveIssuesDetails!![position]._id}"
                    )
                    (context).startActivity(Intent.createChooser(share, "Share Issue!"))
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }


            /** It opens LocationActivity and stores location coordinates */
            holder.locationLayout.setOnClickListener {
                val intent = Intent(context, LocationActivity::class.java)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                EasySP.init(context).put("lat",unresolveIssuesDetails!![position].coordinates[0])
                EasySP.init(context).put("long",unresolveIssuesDetails!![position].coordinates[1])
                EasySP.init(context).putBoolean("resolved",unresolveIssuesDetails!![position].resolved)
            }

            /** It opens BadgesActivity and passes below data */
            holder.profilePic.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID, unresolveIssuesDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            /** It opens BadgesActivity and passes below data */
            holder.profileName.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID, unresolveIssuesDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }


            if (unresolveIssuesDetails!![position].likeByUser != null && unresolveIssuesDetails!![position].likeByUser) {
                holder.like.visibility = View.GONE
                holder.dislike.visibility = View.VISIBLE
                holder.likeTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )

            }

            else{
                holder.like.visibility = View.VISIBLE
                holder.dislike.visibility = View.GONE
                holder.likeTitle.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }


            holder.issueNumber.text = "# ${unresolveIssuesDetails!![position].issueNumber}"
            //holder.issueNumber.typeface = ConstantFonts.raleway_semibold

            holder.issueTitle.text = unresolveIssuesDetails!![position].title
            holder.issueTitle.typeface = ConstantFonts.raleway_semibold



            if (unresolveIssuesDetails!![position].imageUrls != null && unresolveIssuesDetails!![position].imageUrls.isNotEmpty()) {

                Picasso.with(context)
                    .load(unresolveIssuesDetails!![position].imageUrls[0])
//                    .placeholder(R.drawable.loading_image)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.issueImage)

                holder.play.visibility = View.GONE

            } else if (unresolveIssuesDetails!![position].videoUrls != null && unresolveIssuesDetails!![position].videoUrls.isNotEmpty()) {
                Glide
                    .with(context)
                    .load(unresolveIssuesDetails!![position].videoUrlThumbnails[0])
                    .placeholder(R.drawable.processing_video)
                    .into(holder.issueImage)

                holder.play.visibility = View.VISIBLE

            }
            else{
                holder.play.visibility = View.GONE
                holder.issueImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.placeholder))
            }


            if (unresolveIssuesDetails!![position].resolved) {
                holder.resolvedFlag.visibility = View.VISIBLE
                holder.resolvedFlag.text = "Resolved"
                holder.resolvedFlag.typeface = ConstantFonts.raleway_regular
                holder.resolveTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.issue_resolved
                    )
                )



                holder.resolved.visibility = View.GONE
                holder.alreadyResolved.visibility = View.VISIBLE
                holder.notResolved.visibility = View.GONE
            }

            else if (!unresolveIssuesDetails!![position].resolved && unresolveIssuesDetails!![position].resolutionsCount>0)
            {
                holder.notResolved.visibility = View.VISIBLE
                holder.resolved.visibility = View.GONE
                holder.alreadyResolved.visibility = View.GONE
                holder.resolvedFlag.visibility = View.GONE

                holder.resolveTitle.setTextColor(ContextCompat.getColor(context,R.color.button_background))
            }

            else {
                holder.resolvedFlag.visibility = View.GONE

                holder.resolved.visibility = View.VISIBLE
                holder.alreadyResolved.visibility = View.GONE
                holder.notResolved.visibility = View.GONE

            }



            if (unresolveIssuesDetails!![position].videoUrls == null || unresolveIssuesDetails!![position].videoUrls.isEmpty() || unresolveIssuesDetails!![position].videoUrls.size == 0) {
                holder.issueImage!!.setBackgroundResource(R.drawable.placeholder)
            } else {
                /* Picasso.with(context)
                 .load(nearByIssueList.imageUrls[0])
                 .placeholder(R.drawable.background)
                 .memoryPolicy(MemoryPolicy.NO_CACHE)
                 .networkPolicy(NetworkPolicy.NO_CACHE)
                 //.resize(400, 400)  // optional
                 .into(issueImage)*/
            }

            holder.issueLocation.text =
                unresolveIssuesDetails!![position].address + ", ${unresolveIssuesDetails!![position].city}"
            holder.issueLocation.typeface = ConstantFonts.raleway_semibold

            val km = String.format("%.2f", unresolveIssuesDetails!![position].distance)

            holder.issueLocationDistance.text = km + "Km Away"
            holder.issueLocationDistance.typeface = ConstantFonts.raleway_semibold

            if (unresolveIssuesDetails!![position].user.profileUrl != null && unresolveIssuesDetails!![position].user.profileUrl.length > 0) {
                Picasso.with(context)
                    .load(unresolveIssuesDetails!![position].user.profileUrl)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }

            if (unresolveIssuesDetails!![position].user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {
                holder.profileName.text =
                    unresolveIssuesDetails!![position].user.firstName + " ${unresolveIssuesDetails!![position].user.lastName}"
                holder.profileName.typeface = ConstantFonts.raleway_regular
            } else {
                holder.profileName.text = unresolveIssuesDetails!![position].user.organizationName
                holder.profileName.typeface = ConstantFonts.raleway_regular

            }

            if (unresolveIssuesDetails!![position].category.name != null && unresolveIssuesDetails!![position].category.name.isNotEmpty()) {
                holder.categoryTitle.text = unresolveIssuesDetails!![position].category.name
                holder.categoryTitle.typeface = ConstantFonts.raleway_regular
            }



            holder.issueTime.text =
                ConstantMethods.convertStringToDateStringFull(unresolveIssuesDetails!![position].createdAt)
            holder.issueTime.typeface = ConstantFonts.raleway_regular

            holder.issueDescription.text = unresolveIssuesDetails!![position].description
            holder.issueDescription.typeface = ConstantFonts.raleway_medium



            holder.itemView.img_already_resolved!!.setOnClickListener {
                ConstantMethods.showToast(context,  "Issue already resolved")
            }

            /** It opens ReportResolvedActivity with pass below data when click on it */
            holder.itemView.img_resolved!!.setOnClickListener {
                val intent = Intent(context, ReportResolvedActivity::class.java)
                intent.putExtra("issueId", unresolveIssuesDetails!![position]._id)
                (context as SummaryDetailsActivity).startActivityForResult(intent, 200)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }

            /** It opens ReportResolvedActivity with pass below data when click on it */
            holder.itemView.img_not_resolved!!.setOnClickListener {
                val intent = Intent(context, ReportResolvedActivity::class.java)
                intent.putExtra("issueId", unresolveIssuesDetails!![position]._id)
                (context as SummaryDetailsActivity).startActivityForResult(intent, 200)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }


            /** show and hide image views,set text color and call onLike function of onLikeDislikeListener */
            holder.itemView.img_support.setOnClickListener {
                holder.dislike.visibility = View.VISIBLE
                holder.like.visibility = View.GONE

                onLikeDislikeListener!!.summaryType("issue")

                holder.likeTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )

                onLikeDislikeListener!!.onLike(unresolveIssuesDetails!![position]._id)

            }

            /** show and hide image views,set text color and call onDislike function of onLikeDislikeListener */
            holder.itemView.img_dislike.setOnClickListener {

                holder.dislike.visibility = View.GONE
                holder.like.visibility = View.VISIBLE

                onLikeDislikeListener!!.summaryType("issue")

                holder.likeTitle.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))

                onLikeDislikeListener!!.onDislike(unresolveIssuesDetails!![position]._id)
            }


            /** It opens NearByIssueDetailsActivity with pass below data when click on it */
            holder.itemView.img_comment.setOnClickListener {

                val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                intent.putExtra("issueId", unresolveIssuesDetails!![position]._id)
                intent.putExtra("issueNumber", unresolveIssuesDetails!![position].issueNumber)
                intent.putExtra("comment", true)
                (context as SummaryDetailsActivity).startActivity(intent)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                (context as SummaryDetailsActivity).finish()

            }


            holder.itemView.img_gathering.setOnClickListener {

                if (unresolveIssuesDetails!![position].resolved) {
                    ConstantMethods.showToast(
                        context,
                        "You cannot create gathering on resolved issues."
                    )
                }
                else{
                println("image click")
                val intent = Intent(context, CreateGatheringActivity::class.java)
                (context as SummaryDetailsActivity).startActivity(intent)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                //(context as NearByIssueActivity).finish()

                EasySP.init(context)
                    .putString(ConstantEasySP.ISSUE_ID, unresolveIssuesDetails!![position]._id)
                EasySP.init(context)
                    .putInt(
                        ConstantEasySP.ISSUE_NUMBER,
                        unresolveIssuesDetails!![position].issueNumber
                    )
                EasySP.init(context)
                    .putString(ConstantEasySP.ISSUE_TITLE, unresolveIssuesDetails!![position].title)

            }
            }


            /** It opens NearByIssueDetailsActivity with pass going id when click on it */
            holder.itemView.setOnClickListener {

                val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                intent.putExtra("issueId", unresolveIssuesDetails!![position]._id)
                intent.putExtra("distance",km)
                intent.putExtra("issueNumber", unresolveIssuesDetails!![position].issueNumber)
                intent.putExtra("unresolvedIssue", true)
                (context).startActivityForResult(intent, 200)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                //(context as SummaryDetailsActivity).finish()


                println("near by issue details")

            }

            /*holder.more.setOnClickListener {
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_up))

            }*/

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

            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {

                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )

                (context as SummaryDetailsActivity).unresolvedIssuesAdapter!!.notifyDataSetChanged()


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == unresolveIssuesDetails!![position].user._id) {
                    if (unresolveIssuesDetails!![position].resolved) {
                        ConstantMethods.showToast(
                            context,
                            "You cannot delete resolved issues."
                        )
                    } else {
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
                                        unresolveIssuesDetails!![position]._id
                                    )

/*
                                try {
                                    onDeletePresenter!!.onDelete(jsonObject)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }*/


                                    try {
                                        val issueApi =
                                            ApplicationController.retrofit.create(DashboardApi::class.java)
                                        val response: Single<DeleteNearByIssueByIdDetailsPojo> =
                                            issueApi.deleteIssue(jsonObject)
                                        response.subscribeOn(Schedulers.newThread())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(object :
                                                DisposableSingleObserver<DeleteNearByIssueByIdDetailsPojo>() {
                                                override fun onSuccess(
                                                    deleteNearByIssueByIdDetailsPojo: DeleteNearByIssueByIdDetailsPojo
                                                ) {
                                                    if (deleteNearByIssueByIdDetailsPojo != null) {

                                                        println("response $deleteNearByIssueByIdDetailsPojo")
                                                        ConstantMethods.cancleProgessDialog()

                                                        // summary.setOnDeleteIssuePosted(deleteNearByIssueByIdDetailsPojo.data._id)

                                                        val deleteIssueList =
                                                            ArrayList<NearByIssues>()
                                                        deleteIssueList.addAll(
                                                            unresolveIssuesDetails!!
                                                        )

                                                        val issueId =
                                                            unresolveIssuesDetails!![position]._id
                                                        for (i in 0 until deleteIssueList.size) {

                                                            if (deleteIssueList[i]._id == issueId) {
                                                                deleteIssueList.remove(
                                                                    deleteIssueList[i]
                                                                )
                                                                unresolveIssuesDetails!!.clear()
                                                                unresolveIssuesDetails!!.addAll(
                                                                    deleteIssueList
                                                                )
                                                                (context as SummaryDetailsActivity).unresolvedIssuesAdapter!!.notifyDataSetChanged()

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
                    if (userId == unresolveIssuesDetails!![position].user._id) {

                        ConstantMethods.showToast(context,  "You cannot abuse your issue.")
                    } else if (unresolveIssuesDetails!![position].resolved) {
                        ConstantMethods.showToast(
                            context,

                            "You cannot abuse resolved issues."
                        )
                    } else {

                        onReportAbuseListener!!.ReportAbuse(
                            unresolveIssuesDetails!![position]._id,
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
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
        var moreLayout = itemView.moreLayout!!
        var reportAbuse = itemView.txt_report_abuse!!
        var shareTitle = itemView.txt_share_title!!
        var likeTitle = itemView.txt_support!!
        var optionLayout = itemView.optionLayout!!
        var commentTitle = itemView.txt_comment!!
        var resolveTitle = itemView.txt_resolved!!
        var upvoteTitle = itemView.txt_support!!
        var gatheringTitle = itemView.txt_gathering!!
        var play = itemView.img_play!!
        var locationLayout = itemView.issueDetailsLayout!!
    }

}