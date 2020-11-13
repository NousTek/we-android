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
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.R
import com.we.beyond.model.NearByIssues
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.gathering.createGathering.CreateGatheringActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.ui.profile.MyIssuesActivity
import com.we.beyond.ui.reportResolved.ReportResolvedActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.list_near_by_issue.view.*
import java.lang.Exception

/** It binds user issue data set to views */
class MyIssuesAdapter ( val context: Context,
issueDetails: ArrayList<NearByIssues>
) : RecyclerView.Adapter<MyIssuesAdapter.ViewHolder>() {

    /** init array list */
    var issueDetails: ArrayList<NearByIssues>? = null

    /** init listeners */
    var onLikeDislikeListener: OnLikeDislikeListener? = null
    var onDeletePresenter: OnDeleteListener? = null

    init {
        this.issueDetails = issueDetails
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


        return issueDetails!!.size
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

            holder.commentTitle.typeface = ConstantFonts.raleway_regular
            holder.resolveTitle.typeface = ConstantFonts.raleway_regular
            holder.gatheringTitle.typeface = ConstantFonts.raleway_regular
            holder.shareTitle.typeface = ConstantFonts.raleway_regular
            holder.upvoteTitle.typeface = ConstantFonts.raleway_regular


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
                        "${Constants.BASE_URL}issues/${issueDetails!![position]._id}"
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

                EasySP.init(context).put("lat",issueDetails!![position].coordinates[0])
                EasySP.init(context).put("long",issueDetails!![position].coordinates[1])
                EasySP.init(context).putBoolean("resolved",issueDetails!![position].resolved)
            }

            /** It opens BadgesActivity and passes below data */
            holder.profilePic.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,issueDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            /** It opens BadgesActivity and passes below data */
            holder.profileName.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,issueDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }


            holder.issueNumber.text = "# ${issueDetails!![position].issueNumber}"
            //holder.issueNumber.typeface = ConstantFonts.raleway_semibold

            holder.issueTitle.text = issueDetails!![position].title
            holder.issueTitle.typeface = ConstantFonts.raleway_semibold

            if (issueDetails!![position].likeByUser != null && issueDetails!![position].likeByUser) {
                holder.like.visibility = View.GONE
                holder.dislike.visibility = View.VISIBLE
                holder.upvoteTitle.setTextColor(ContextCompat.getColor(context,R.color.button_background))
            }
            else{
                holder.like.visibility = View.VISIBLE
                holder.dislike.visibility = View.GONE
                holder.upvoteTitle.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }


            if (issueDetails!![position].resolved != null && issueDetails!![position].resolved) {

                holder.resolved.visibility = View.GONE

                holder.resolvedFlag.visibility = View.VISIBLE
                holder.resolvedFlag.text = "Resolved"
                holder.resolvedFlag.typeface = ConstantFonts.raleway_regular

                holder.resolveTitle.setTextColor(ContextCompat.getColor(context,R.color.issue_resolved))

                holder.alreadyResolved.visibility = View.VISIBLE
                holder.notResolved.visibility = View.GONE
            }
            else if (!issueDetails!![position].resolved && issueDetails!![position].resolutionsCount>0)
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

                holder.resolveTitle.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }




            if (issueDetails!![position].imageUrls != null && issueDetails!![position].imageUrls.isNotEmpty()) {

                Picasso.with(context)
                    .load(issueDetails!![position].imageUrls[0])
//                    .placeholder(R.drawable.loading_image)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.issueImage)

                holder.play.visibility = View.GONE

            } else if (issueDetails!![position].videoUrls != null && issueDetails!![position].videoUrls.isNotEmpty()) {
                Glide
                    .with(context)
                    .load(issueDetails!![position].videoUrlThumbnails[0])
                    .placeholder(R.drawable.processing_video)
                    .into(holder.issueImage)

                holder.play.visibility = View.VISIBLE

            }
            else{
                holder.play.visibility = View.GONE
                holder.issueImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.placeholder))
            }


            holder.issueLocation.text =
                issueDetails!![position].address + ", ${issueDetails!![position].city}"
            holder.issueLocation.typeface = ConstantFonts.raleway_semibold

            val km = String.format("%.2f", issueDetails!![position].distance)

            holder.issueLocationDistance.text = km + "Km Away"
            holder.issueLocationDistance.typeface = ConstantFonts.raleway_semibold

            if (issueDetails!![position].user.profileUrl != null && issueDetails!![position].user.profileUrl.length > 0) {
                Picasso.with(context)
                    .load(issueDetails!![position].user.profileUrl)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }

            if (issueDetails!![position].user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {
                holder.profileName.text =
                    issueDetails!![position].user.firstName + " ${issueDetails!![position].user.lastName}"
                holder.profileName.typeface = ConstantFonts.raleway_regular
            } else {
                holder.profileName.text = issueDetails!![position].user.organizationName
                holder.profileName.typeface = ConstantFonts.raleway_regular

            }

            if (issueDetails!![position].category.name != null && issueDetails!![position].category.name.isNotEmpty()) {
                holder.categoryTitle.text = issueDetails!![position].category.name
                holder.categoryTitle.typeface = ConstantFonts.raleway_regular
            }




            holder.issueTime.text =
                ConstantMethods.convertStringToDateStringFull(issueDetails!![position].createdAt)
            holder.issueTime.typeface = ConstantFonts.raleway_regular

            holder.issueDescription.text = issueDetails!![position].description
            holder.issueDescription.typeface = ConstantFonts.raleway_medium

            holder.itemView.img_already_resolved!!.setOnClickListener {
                ConstantMethods.showWarning(context, "Issue Resolved", "Issue already resolved")
            }

            /** It opens ReportResolvedActivity and passes below data */
            holder.itemView.img_resolved!!.setOnClickListener {
                val intent = Intent(context, ReportResolvedActivity::class.java)
                intent.putExtra("issueId", issueDetails!![position]._id)
                (context as MyIssuesActivity).startActivityForResult(intent, 200)
                (context as MyIssuesActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )


            }

            /** It opens ReportResolvedActivity and passes below data */
            holder.itemView.img_not_resolved!!.setOnClickListener {
                val intent = Intent(context, ReportResolvedActivity::class.java)
                intent.putExtra("issueId", issueDetails!![position]._id)
                (context as MyIssuesActivity).startActivityForResult(intent, 200)
                (context as MyIssuesActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )


            }

            /** show and hide image views, set text color and call onLike function of onLikeDislikeListener */
            holder.itemView.img_support.setOnClickListener {
                holder.dislike.visibility = View.VISIBLE
                holder.like.visibility = View.GONE

                onLikeDislikeListener!!.onLike(issueDetails!![position]._id)

                holder.upvoteTitle.setTextColor(ContextCompat.getColor(context,R.color.button_background))

            }

            /** show and hide image views, set text color and call onLike function of onLikeDislikeListener */
            holder.itemView.img_dislike.setOnClickListener {

                holder.dislike.visibility = View.GONE
                holder.like.visibility = View.VISIBLE

                onLikeDislikeListener!!.onDislike(issueDetails!![position]._id)

                holder.upvoteTitle.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }

            /** It opens NearByIssueDetailsActivity and passes below data */
            holder.itemView.img_comment.setOnClickListener {

                val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                intent.putExtra("issueId", issueDetails!![position]._id)
                intent.putExtra("issueNumber", issueDetails!![position].issueNumber)
                intent.putExtra("myIssue",true)
                intent.putExtra("comment", true)
                (context as MyIssuesActivity).startActivity(intent)
                (context as MyIssuesActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }


            /** It opens CreateGatheringActivity and passes below data */
            holder.itemView.img_gathering.setOnClickListener {
                if(issueDetails!![position].resolved)
                {
                    ConstantMethods.showWarning(context,"","You cannot create gathering on resolved issues.")
                }
                else {
                    println("image click")

                    val intent = Intent(context, CreateGatheringActivity::class.java)
                    (context as MyIssuesActivity).startActivity(intent)
                    (context as MyIssuesActivity).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    //(context as NearByIssueActivity).finish()

                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_ID, issueDetails!![position]._id)
                    EasySP.init(context)
                        .putInt(ConstantEasySP.ISSUE_NUMBER, issueDetails!![position].issueNumber)
                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_TITLE, issueDetails!![position].title)

                }
            }



            /** It opens NearByIssueDetailsActivity with pass going id when click on it */
            holder.itemView.setOnClickListener {


                val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                intent.putExtra("issueId", issueDetails!![position]._id)
                intent.putExtra("distance",km)
                intent.putExtra("issueNumber", issueDetails!![position].issueNumber)
                intent.putExtra("myIssues", true)
                context.startActivityForResult(intent, 200)
                (context as MyIssuesActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                //(context as MyIssuesActivity).finish()


                println("near by issue details")


            }



            /*holder.more.setOnClickListener {
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_up))

            }*/

            /** It closes more layout with animation */
            holder.close.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
            }

            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {
                if (issueDetails!![position].resolved != null && issueDetails!![position].resolved) {
                    ConstantMethods.showWarning(context,"","You cant not delete approved resolution")
                } else {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                    (context as MyIssuesActivity).issuesAdapter!!.notifyDataSetChanged()


                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == issueDetails!![position].user._id) {
                        println("user id ${userId} issue user ${issueDetails!![position].user._id}")
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
                                    jsonObject.addProperty("issueId", issueDetails!![position]._id)


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


            }

            /**It closes more layout with animation ,show warning dialog */
            holder.reportAbuse.typeface = ConstantFonts.raleway_semibold
            holder.reportAbuse.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))

                try {
                    val sweetAlertDialog =
                        SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "You can not abuse your issue."
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
            catch(e : Exception)
            {
                e.printStackTrace()
            }


    catch (e : Exception)
        {
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
        var shareTitle = itemView.txt_share_title!!
        var reportAbuse = itemView.txt_report_abuse!!
        var upvoteTitle = itemView.txt_support!!
        var commentTitle  = itemView.txt_comment!!
        var resolveTitle = itemView.txt_resolved!!
        var gatheringTitle = itemView.txt_gathering!!
        var play = itemView.img_play!!
        var locationLayout = itemView.issueDetailsLayout!!


    }


}