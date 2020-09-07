package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.GoingIdData
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.Constants
import kotlinx.android.synthetic.main.list_going_members.view.*
import java.lang.Exception

/** It binds going data set to views */
class GoingAdapter(
    val context: Context,
    goingDetails: ArrayList<GoingIdData>
) : RecyclerView.Adapter<GoingAdapter.ViewHolder>() {

    var goingDetails: ArrayList<GoingIdData>? = null


    init {
        this.goingDetails = goingDetails
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.list_going_members,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {


        return goingDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** It set data to views */
        try {

            holder.title.text = goingDetails!![position].user.name
            holder.title.typeface = ConstantFonts.raleway_semibold



            if (goingDetails!![position].user.profileUrl != null && goingDetails!![position].user.profileUrl.length > 0) {
                Picasso.with(context)
                    .load(goingDetails!![position].user.profileUrl)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        /** It opens BadgesActivity with pass id when click on it */
        holder.itemView.setOnClickListener {
            val intent = Intent(context, BadgesActivity::class.java)
            intent.putExtra(Constants.USER_ID,goingDetails!![position].user._id)
            (context).startActivity(intent)

        }


    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var profilePic = itemView.img_profile_pic!!
        var title = itemView.txt_title!!

    }


}