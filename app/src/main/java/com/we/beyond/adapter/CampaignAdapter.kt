package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.Campaigns
import com.we.beyond.ui.campaign.campaignDetails.CampaignDetailsActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import kotlinx.android.synthetic.main.list_campaign.view.*
import kotlin.collections.ArrayList

/** It binds campaign data set to views */

class CampaignAdapter(
    val context: Context,
    campaignDetails: ArrayList<Campaigns>
) : RecyclerView.Adapter<CampaignAdapter.ViewHolder>() {

    /** init array list */
    var campaignDetails: ArrayList<Campaigns>? = null

    init {
        this.campaignDetails = campaignDetails
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.list_campaign,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {

        return campaignDetails!!.size
    }


    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** It set data to views */
        holder.campaignTextTitle.typeface = ConstantFonts.raleway_semibold

        if (campaignDetails!![position].imageUrls == null || campaignDetails!![position].imageUrls.isEmpty() || campaignDetails!![position].imageUrls.size == 0) {
            holder.campaignImage.setBackgroundResource(R.drawable.background)
        } else {
            Picasso.with(context)
                .load(campaignDetails!![position].imageUrls[0])
//                .placeholder(R.drawable.loading_image)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.resize(400, 400)  // optional
                .into(holder.campaignImage)
        }

        if (campaignDetails!![position].videoUrls != null && campaignDetails!![position].videoUrls.isNotEmpty()) {
            Glide
                .with(context)
                .load(campaignDetails!![position].videoUrlThumbnails[0])
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.processing_video)
                .into(holder.campaignImage)

            holder.play.visibility = View.VISIBLE

        }

        holder.campaignTitle.text = campaignDetails!![position].title
        holder.campaignTitle.typeface = ConstantFonts.raleway_regular

        holder.campaignTime.text =
            ConstantMethods.convertStringToDateStringFull(campaignDetails!![position].campaignDate)
        holder.campaignTime.typeface = ConstantFonts.raleway_regular

        holder.campaignLocation.text = campaignDetails!![position].address
        holder.campaignLocation.typeface = ConstantFonts.raleway_regular

        /** It opens CampaignDetailsActivity with pass ids when click on it */
        holder.itemView.setOnClickListener {

            val intent = Intent(context, CampaignDetailsActivity::class.java)
            intent.putExtra("campaignId", campaignDetails!![position]._id)
            intent.putExtra("gatheringId", GatheringDetailsActivity().gatheringId)
            (context as GatheringDetailsActivity).startActivityForResult(intent, 200)
            (context as GatheringDetailsActivity).overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }


    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var campaignTextTitle = itemView.txt_campaign_text_title!!
        var campaignImage = itemView.img_campaign!!
        var campaignTitle = itemView.txt_campaign_title!!
        var campaignLocation = itemView.txt_campaign_location!!
        var campaignTime = itemView.txt_campaign_time!!
        var play = itemView.img_play!!


    }


}