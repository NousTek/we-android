package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.Gatherings
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import kotlinx.android.synthetic.main.list_linked_gathering.view.*

/** It binds linked gathering data set to views */

class LinkedGatheringAdapter(
val context: Context,
gatheringDetails: ArrayList<Gatherings>
) : RecyclerView.Adapter<LinkedGatheringAdapter.ViewHolder>() {

    /** init array list */
    var gatheringDetails: ArrayList<Gatherings>? = null

    init {
        this.gatheringDetails = gatheringDetails
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.list_linked_gathering,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {

        return gatheringDetails!!.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        /** It set data to views */
        holder.gatheringTextTitle.typeface = ConstantFonts.raleway_semibold

        if (gatheringDetails!![position].imageUrls == null || gatheringDetails!![position].imageUrls.isEmpty() || gatheringDetails!![position].imageUrls.size == 0) {
            holder.gatheringImage.setBackgroundResource(R.drawable.background)
        } else {
            Picasso.with(context)
                .load(gatheringDetails!![position].imageUrls[0])
                .placeholder(R.drawable.background)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.resize(400, 400)  // optional
                .into(holder.gatheringImage)
        }

        holder.gatheringTitle.text = gatheringDetails!![position].title
        holder.gatheringTitle.typeface = ConstantFonts.raleway_regular

        holder.gatheringTime.text = ConstantMethods.convertStringToDateStringFull(gatheringDetails!![position].gatheringDate)
        holder.gatheringTime.typeface = ConstantFonts.raleway_regular

        holder.gatheringLocation.text = gatheringDetails!![position].address
        holder.gatheringLocation.typeface = ConstantFonts.raleway_regular


        /** It opens GatheringDetailsActivity and passes below data when click on it  */
        holder.itemView.setOnClickListener {

            val intent = Intent(context, GatheringDetailsActivity::class.java)
            intent.putExtra("gatheringId",gatheringDetails!![position]._id)
            intent.putExtra("linkedGathering", true)
            (context as NearByIssueDetailsActivity).startActivityForResult(intent,200)
            (context as NearByIssueDetailsActivity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }


    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var gatheringTextTitle = itemView.txt_gathering_text_title!!
        var gatheringImage = itemView.img_gathering!!
        var gatheringTitle = itemView.txt_gathering_title!!
        var gatheringLocation = itemView.txt_gathering_location!!
        var gatheringTime = itemView.txt_gathering_time!!




    }


}