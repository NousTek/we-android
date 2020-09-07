package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.NearByIssues
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.profile.MyResolutionActivity
import com.we.beyond.ui.profile.MyResolutionDetailsActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.list_reported_resolution.view.*
import java.lang.Exception

/** It binds user resolution data set to views */
class MyResolutionAdapter (
    val context: Context,
    issueDetails: ArrayList<NearByIssues>
) : RecyclerView.Adapter<MyResolutionAdapter.ViewHolder>() {

    /** init array list */
    var issueDetails: ArrayList<NearByIssues>? = null

    init {
        this.issueDetails = issueDetails
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_reported_resolution,
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

        try {

            /** It opens LocationActivity and stores location coordinates */
            holder.locationLayout.setOnClickListener {
                val intent = Intent(context, LocationActivity::class.java)
                (context).startActivity(intent)
                (context as MyResolutionActivity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

                EasySP.init(context).put("lat",issueDetails!![position].coordinates[0])
                EasySP.init(context).put("long",issueDetails!![position].coordinates[1])
                EasySP.init(context).putBoolean("resolved",issueDetails!![position].resolved)
            }

            /** It opens BadgesActivity and passes below data */
            holder.profilePic.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,issueDetails!![position].user._id)
                (context).startActivity(intent)
                (context as MyResolutionActivity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            /** It opens BadgesActivity and passes below data */
            holder.profileName!!.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,issueDetails!![position].user._id)
                (context).startActivity(intent)
                (context as MyResolutionActivity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }


            holder.issueNumber.text = "# ${issueDetails!![position].issueNumber}"
            //holder.issueNumber.typeface = ConstantFonts.raleway_semibold

            holder.issueTitle.text = issueDetails!![position].title
            holder.issueTitle.typeface = ConstantFonts.raleway_semibold



            if (issueDetails!![position].resolved != null && issueDetails!![position].resolved) {

                holder.resolvedFlag.visibility = View.VISIBLE
                holder.resolvedFlag.text = "Resolved"
                holder.resolvedFlag.typeface = ConstantFonts.raleway_regular

            } else {
                holder.resolvedFlag.visibility = View.GONE

            }

            if (issueDetails!![position].imageUrls != null && issueDetails!![position].imageUrls.isNotEmpty()) {

                Picasso.with(context)
                    .load(issueDetails!![position].imageUrls[0])
                    .placeholder(R.drawable.loading_image)
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

            holder.issueResolvedCount.text =
                "Resolution Count : ${issueDetails!![position].myResolutionsCount}"
            holder.issueResolvedCount.typeface = ConstantFonts.raleway_semibold


            /** It opens MyResolutionDetailsActivity with pass going id when click on it */
            holder.itemView.setOnClickListener {

                val intent = Intent(context, MyResolutionDetailsActivity::class.java)
                intent.putExtra("issueId", issueDetails!![position]._id)
                intent.putExtra("distance",km)
                intent.putExtra("issueNumber", issueDetails!![position].issueNumber)
                (context as MyResolutionActivity).startActivityForResult(intent, 200)
                (context as MyResolutionActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )


                println("near by issue details")

            }

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
        var issueResolvedCount = itemView.txt_reported_resolution_Count!!
        var view = itemView.view!!
        var resolvedFlag = itemView.txt_flag!!
        var play = itemView.img_play!!
        var locationLayout = itemView.issueDetailsLayout!!

    }
}