package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.NotificationData
import com.we.beyond.ui.campaign.campaignDetails.CampaignDetailsActivity
import com.we.beyond.ui.connect.connectIssue.ConnectDetailsActivity
import com.we.beyond.ui.dashboard.NotificationActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import kotlinx.android.synthetic.main.list_my_activities.view.*
import java.lang.Exception

/** It binds notification data set to views */
class NotificationAdapter(
    val context: Context,
    notificationDetails: ArrayList<NotificationData>
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    /** init array list */
    var notificationDetails: ArrayList<NotificationData>? = null

    init {
        this.notificationDetails = notificationDetails
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_my_activities,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {


        return notificationDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            /** It set data to views */
            holder.title.text = notificationDetails!![position].displayText
            holder.title.typeface = ConstantFonts.raleway_semibold


            holder.description.text = notificationDetails!![position].data
            holder.description.typeface = ConstantFonts.raleway_semibold

            if (notificationDetails!![position].createdBy.profileUrl != null && notificationDetails!![position].createdBy.profileUrl.length > 0) {
                Picasso.with(context)
                    .load(notificationDetails!![position].createdBy.profileUrl)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }

            holder.date.text =
                ConstantMethods.convertStringToDateStringFull(notificationDetails!![position].createdAt)
            holder.date.typeface = ConstantFonts.raleway_regular


            /** It opens respective activity depends on notification type when click on it */
            holder.itemView.setOnClickListener {
                when (notificationDetails!![position].type) {
                    "issue" -> {
                        val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                        intent.putExtra("issueId", notificationDetails!![position].typeId)
                        intent.putExtra("notification", true)
                        (context).startActivity(intent)
                        (context as NotificationActivity).overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }

                    "resolution" -> {
                        val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                        intent.putExtra("issueId", notificationDetails!![position].issue)
                        intent.putExtra("notification", true)
                        (context).startActivity(intent)
                        (context as NotificationActivity).overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }

                    "gathering" -> {
                        val intent = Intent(context, GatheringDetailsActivity::class.java)
                        intent.putExtra("gatheringId", notificationDetails!![position].typeId)
                        intent.putExtra("notification", true)
                        (context).startActivity(intent)
                        (context as NotificationActivity).overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }


                    "issue-campaign" -> {
                        val intent = Intent(context, CampaignDetailsActivity::class.java)
                        intent.putExtra("campaignId", notificationDetails!![position].typeId)
                        intent.putExtra("notification", true)
                        (context).startActivity(intent)
                        (context as NotificationActivity).overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }


                    "gathering-campaign" -> {
                        val intent = Intent(context, CampaignDetailsActivity::class.java)
                        intent.putExtra("campaignId", notificationDetails!![position].typeId)
                        intent.putExtra("notification", true)
                        (context).startActivity(intent)
                        (context as NotificationActivity).overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }

                    "campaign" -> {
                        val intent = Intent(context, CampaignDetailsActivity::class.java)
                        intent.putExtra("campaignId", notificationDetails!![position].typeId)
                        intent.putExtra("notification", true)
                        (context).startActivity(intent)
                        (context as NotificationActivity).overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }


                    "connect" -> {
                        val intent = Intent(context, ConnectDetailsActivity::class.java)
                        intent.putExtra("connectCategoryId", notificationDetails!![position].typeId)
                        intent.putExtra("notification", true)
                        (context).startActivity(intent)
                        (context as NotificationActivity).overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }

                    "comment" -> {
                        if (notificationDetails!![position].issue != null) {
                            val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                            intent.putExtra(
                                Constants.COMMENT_ID,
                                notificationDetails!![position].typeId
                            )
                            intent.putExtra("issueId", notificationDetails!![position].issue)
                            intent.putExtra("notification", true)
                            (context).startActivity(intent)
                            (context as NotificationActivity).overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                        } else if (notificationDetails!![position].connect != null) {
                            val intent = Intent(context, ConnectDetailsActivity::class.java)
                            intent.putExtra(
                                Constants.COMMENT_ID,
                                notificationDetails!![position].typeId
                            )
                            intent.putExtra(
                                "connectCategoryId",
                                notificationDetails!![position].connect
                            )
                            intent.putExtra("notification", true)
                            (context).startActivity(intent)
                            (context as NotificationActivity).overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                        } else if (notificationDetails!![position].gathering != null) {
                            val intent = Intent(context, GatheringDetailsActivity::class.java)
                            intent.putExtra(
                                Constants.COMMENT_ID,
                                notificationDetails!![position].typeId
                            )
                            intent.putExtra(
                                "gatheringId",
                                notificationDetails!![position].gathering
                            )
                            intent.putExtra("notification", true)
                            (context).startActivity(intent)
                            (context as NotificationActivity).overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
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


        var profilePic = itemView.img_profile_pic!!
        var title = itemView.txt_title!!
        var description = itemView.txt_description!!
        var date = itemView.txt_time!!


    }

}