package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.NearByMeUsersData
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.nearByMe.NearByMeActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.Constants
import kotlinx.android.synthetic.main.list_near_by_me_users.view.*
import java.lang.Exception

/** It binds user data set to views */

class NearByMeUsersAdapter(
    val context: Context,
    nearByUsersDetails: ArrayList<NearByMeUsersData>
) : RecyclerView.Adapter<NearByMeUsersAdapter.ViewHolder>() {


    /** init array list */
    var nearByUsersDetails: ArrayList<NearByMeUsersData>? = null


    init {
        this.nearByUsersDetails = nearByUsersDetails
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_near_by_me_users,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {


        return nearByUsersDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** set data to views */

        try {


            holder.title.text = nearByUsersDetails!![position].name
            holder.title.typeface = ConstantFonts.raleway_semibold


            holder.description.text = nearByUsersDetails!![position].userLoginType.name
            holder.description.typeface = ConstantFonts.raleway_semibold

            if (nearByUsersDetails!![position].profileUrl != null && nearByUsersDetails!![position].profileUrl.length > 0) {
                Picasso.with(context)
                    .load(nearByUsersDetails!![position].profileUrl)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }

            /** It opens BadgesActivity with pass going id when click on it */
            holder.itemView.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID, nearByUsersDetails!![position]._id)
                (context).startActivity(intent)
                (context as NearByMeActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
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


    }

}