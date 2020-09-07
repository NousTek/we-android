package com.we.beyond.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.MyActivityData
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import kotlinx.android.synthetic.main.list_my_activities.view.*
import java.lang.Exception

/** It binds user activity details data set to views */

class MyActivitiesAdapter(
    val context: Context,
    myActivityDetails: ArrayList<MyActivityData>
) : RecyclerView.Adapter<MyActivitiesAdapter.ViewHolder>() {

    /** init array list */
    var myActivityDetails: ArrayList<MyActivityData>? = null


    init {
        this.myActivityDetails = myActivityDetails
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


        return myActivityDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {

            /** set data to views */
            holder.title.text = myActivityDetails!![position].displayText
            holder.title.typeface = ConstantFonts.raleway_semibold


            holder.description.text =myActivityDetails!![position].data
            holder.description.typeface = ConstantFonts.raleway_semibold

            if (myActivityDetails!![position].user.profileUrl != null && myActivityDetails!![position].user.profileUrl.length > 0) {
                Picasso.with(context)
                    .load(myActivityDetails!![position].user.profileUrl)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }

            holder.date.text =
                ConstantMethods.convertStringToDateStringFull(myActivityDetails!![position].createdAt)
            holder.date.typeface = ConstantFonts.raleway_regular



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