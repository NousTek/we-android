package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.R
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
import kotlinx.android.synthetic.main.list_near_by_issue.view.*
import java.lang.Exception

/** It binds resloved issue data set to views */
class ResolvedIssuesAdapter (
    val context: Context,
    resolveIssuesDetails: ArrayList<NearByIssues>
) : RecyclerView.Adapter<ResolvedIssuesAdapter.ViewHolder>() {

    /** init array list */
    var resolveIssuesDetails: ArrayList<NearByIssues>? = null

    /** init listeners */
    var onLikeDislikeListener: OnLikeDislikeListener? = null
    var onDeletePresenter : OnDeleteListener?=null

    init {
        this.resolveIssuesDetails = resolveIssuesDetails
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


        return resolveIssuesDetails!!.size
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


            /** share issue details using intent */
            holder.share.setOnClickListener {
                try {

                    val share = Intent(Intent.ACTION_SEND)
                    share.setType("text/plain")
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                    share.putExtra(Intent.EXTRA_SUBJECT, "WE")
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        "${Constants.BASE_URL}issues/${resolveIssuesDetails!![position]._id}"
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

                EasySP.init(context).put("lat",resolveIssuesDetails!![position].coordinates[0])
                EasySP.init(context).put("long",resolveIssuesDetails!![position].coordinates[1])
                EasySP.init(context).putBoolean("resolved",resolveIssuesDetails!![position].resolved)
            }

            /** It opens BadgesActivity and passes below data */
            holder.profilePic.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,resolveIssuesDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }


            /** It opens BadgesActivity and passes below data */
            holder.profileName.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,resolveIssuesDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }


            holder.issueNumber.text = "# ${resolveIssuesDetails!![position].issueNumber}"
            //holder.issueNumber.typeface = ConstantFonts.raleway_semibold

            holder.issueTitle.text = resolveIssuesDetails!![position].title
            holder.issueTitle.typeface = ConstantFonts.raleway_semibold

            if(resolveIssuesDetails!![position].likeByUser != null && resolveIssuesDetails!![position].likeByUser)
            {
                holder.like.visibility = View.GONE
                holder.dislike.visibility = View.VISIBLE
                holder.likeTitle.setTextColor(ContextCompat.getColor(context,R.color.button_background))

            }
            else{
                holder.like.visibility = View.VISIBLE
                holder.dislike.visibility = View.GONE
                holder.likeTitle.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))
            }




            if (resolveIssuesDetails!![position].imageUrls != null && resolveIssuesDetails!![position].imageUrls.isNotEmpty()) {

                Glide.with(context)
                    .load(resolveIssuesDetails!![position].imageUrls[0])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.loading_image)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.issueImage)

                holder.play.visibility = View.GONE

            } else if (resolveIssuesDetails!![position].videoUrls != null && resolveIssuesDetails!![position].videoUrls.isNotEmpty()) {
                Glide
                    .with(context)
                    .load(resolveIssuesDetails!![position].videoUrlThumbnails[0])
                    .placeholder(R.drawable.processing_video)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.issueImage)

                holder.play.visibility = View.VISIBLE

            }
            else{
                holder.play.visibility = View.GONE
                holder.issueImage!!.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.placeholder))
            }


            if (resolveIssuesDetails!![position].resolved) {
                holder.resolvedFlag.visibility = View.VISIBLE
                holder.resolvedFlag.text = "Resolved"
                holder.resolvedFlag.typeface = ConstantFonts.raleway_regular

                holder.resolveTitle.setTextColor(ContextCompat.getColor(context,R.color.issue_resolved))


                holder.resolved.visibility = View.GONE
                holder.alreadyResolved.visibility = View.VISIBLE
                holder.notResolved.visibility = View.GONE
            }

            else if (!resolveIssuesDetails!![position].resolved && resolveIssuesDetails!![position].resolutionsCount>0)
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



            if (resolveIssuesDetails!![position].videoUrls == null || resolveIssuesDetails!![position].videoUrls.isEmpty() || resolveIssuesDetails!![position].videoUrls.size == 0) {
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
                resolveIssuesDetails!![position].address + ", ${resolveIssuesDetails!![position].city}"
            holder.issueLocation.typeface = ConstantFonts.raleway_semibold

            val km = String.format("%.2f", resolveIssuesDetails!![position].distance)

            holder.issueLocationDistance.text = km + "Km Away"
            holder.issueLocationDistance.typeface = ConstantFonts.raleway_semibold

            if (resolveIssuesDetails!![position].user.profileUrl != null && resolveIssuesDetails!![position].user.profileUrl.length > 0) {
                Glide.with(context)
                    .load(resolveIssuesDetails!![position].user.profileUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }

            if (resolveIssuesDetails!![position].user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {
                holder.profileName.text =
                    resolveIssuesDetails!![position].user.firstName + " ${resolveIssuesDetails!![position].user.lastName}"
                holder.profileName.typeface = ConstantFonts.raleway_regular
            } else {
                holder.profileName.text = resolveIssuesDetails!![position].user.organizationName
                holder.profileName.typeface = ConstantFonts.raleway_regular

            }

            if (resolveIssuesDetails!![position].category.name != null && resolveIssuesDetails!![position].category.name.isNotEmpty()) {
                holder.categoryTitle.text = resolveIssuesDetails!![position].category.name
                holder.categoryTitle.typeface = ConstantFonts.raleway_regular
            }



            holder.issueTime.text =
                ConstantMethods.convertStringToDateStringFull(resolveIssuesDetails!![position].createdAt)
            holder.issueTime.typeface = ConstantFonts.raleway_regular

            holder.issueDescription.text = resolveIssuesDetails!![position].description
            holder.issueDescription.typeface = ConstantFonts.raleway_medium

            holder.comment.setImageResource(R.drawable.ic_comment)
            holder.resolved.setImageResource(R.drawable.ic_report_resolved)
            holder.like.setImageResource(R.drawable.ic_support_button)
            holder.gathering.setImageResource(R.drawable.gathering)
            holder.share.setImageResource(R.drawable.ic_share)
            holder.locate.setImageResource(R.drawable.ic_locate)



            holder.itemView.img_already_resolved!!.setOnClickListener {
                ConstantMethods.showToast(context, "Issue already resolved")
            }

            /** It opens ReportResolvedActivity with pass below data when click on it */
            holder.itemView.img_resolved!!.setOnClickListener {
                val intent = Intent(context, ReportResolvedActivity::class.java)
                intent.putExtra("issueId", resolveIssuesDetails!![position]._id)
                (context as SummaryDetailsActivity).startActivityForResult(intent, 200)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }

            /** It opens ReportResolvedActivity with pass below data when click on it */
            holder.itemView.img_not_resolved!!.setOnClickListener {
                val intent = Intent(context, ReportResolvedActivity::class.java)
                intent.putExtra("issueId", resolveIssuesDetails!![position]._id)
                (context as SummaryDetailsActivity).startActivityForResult(intent, 200)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }


            /** show and hide image views and call onLike function of onLikeDislikeListener */
            holder.itemView.img_support.setOnClickListener {
                holder.dislike.visibility = View.VISIBLE
                holder.like.visibility = View.GONE
                holder.likeTitle.setTextColor(ContextCompat.getColor(context,R.color.button_background))

                onLikeDislikeListener!!.summaryType("issue")

                onLikeDislikeListener!!.onLike(resolveIssuesDetails!![position]._id)

            }

            /** show and hide image views and call onLike function of onLikeDislikeListener */
            holder.itemView.img_dislike.setOnClickListener {

                holder.dislike.visibility = View.GONE
                holder.like.visibility = View.VISIBLE

                holder.likeTitle.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary))


                onLikeDislikeListener!!.summaryType("issue")

                onLikeDislikeListener!!.onDislike(resolveIssuesDetails!![position]._id)
            }


            /** It opens NearByIssueDetailsActivity with pass below data when click on it */
            holder.itemView.img_comment.setOnClickListener {

                val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                intent.putExtra("issueId", resolveIssuesDetails!![position]._id)
                intent.putExtra("issueNumber", resolveIssuesDetails!![position].issueNumber)
                intent.putExtra("comment", true)
                (context as SummaryDetailsActivity).startActivity(intent)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                (context as SummaryDetailsActivity).finish()

            }

            /** It opens CreateGatheringActivity with pass below data when click on it */
            holder.itemView.img_gathering.setOnClickListener {

                if(resolveIssuesDetails!![position].resolved)
                {
                    ConstantMethods.showToast(context,"You cannot create gathering on resolved issues.")
                }
                else {
                    println("image click")
                    val intent = Intent(context, CreateGatheringActivity::class.java)
                    (context as SummaryDetailsActivity).startActivity(intent)
                    (context as SummaryDetailsActivity).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    //(context as NearByIssueActivity).finish()

                    EasySP.init(context)
                        .putString(ConstantEasySP.ISSUE_ID, resolveIssuesDetails!![position]._id)
                    EasySP.init(context)
                        .putInt(
                            ConstantEasySP.ISSUE_NUMBER,
                            resolveIssuesDetails!![position].issueNumber
                        )
                    EasySP.init(context)
                        .putString(
                            ConstantEasySP.ISSUE_TITLE,
                            resolveIssuesDetails!![position].title
                        )

                }
            }


            /** It opens NearByIssueDetailsActivity with pass going id when click on it */
            holder.itemView.setOnClickListener {

                val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                intent.putExtra("issueId", resolveIssuesDetails!![position]._id)
                intent.putExtra("distance",km)
                intent.putExtra("issueNumber", resolveIssuesDetails!![position].issueNumber)
                intent.putExtra("resolvedIssue", true)
                (context ).startActivityForResult(intent,200)
                (context as SummaryDetailsActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                //(context as SummaryDetailsActivity).finish()

                println("near by issue details")

            }
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
        var shareTitle = itemView.txt_share_title!!
        var likeTitle = itemView.txt_support!!
        var optionLayout = itemView.optionLayout!!
        var commentTitle  = itemView.txt_comment!!
        var resolveTitle = itemView.txt_resolved!!
        var upvoteTitle = itemView.txt_support!!
        var gatheringTitle = itemView.txt_gathering!!
        var play = itemView.img_play!!
        var locationLayout = itemView.issueDetailsLayout!!

    }
}