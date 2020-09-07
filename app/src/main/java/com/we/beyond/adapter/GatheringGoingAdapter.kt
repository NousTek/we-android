package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.we.beyond.R
import com.we.beyond.model.GatheringDetailsData
import com.we.beyond.model.GatheringGoingList
import com.we.beyond.ui.GoingActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import kotlinx.android.synthetic.main.list_going.view.*

/** It binds gathering going data set to views */
class GatheringGoingAdapter(
    var context: Context,
    mediaList: ArrayList<GatheringGoingList>,
    gatheringId: GatheringDetailsData
) :
    RecyclerView.Adapter<GatheringGoingAdapter.ViewHolder>() {

    /** init array list and model */
    var mediaList: ArrayList<GatheringGoingList>? = null
    var gatheringDetails : GatheringDetailsData?=null

    init {

        this.mediaList = mediaList
        this.gatheringDetails = gatheringId
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.list_going, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mediaList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        println("uploaded media $mediaList")


        /** set data to view */
        if(mediaList!![position].user.profileUrl != null && mediaList!![position].user.profileUrl.isNotEmpty()) {
            holder.progressBar.visibility = View.VISIBLE
            Glide
                .with(context)
                .load(mediaList!![position].user.profileUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.progressBar.visibility = View.GONE

                        return false
                    }


                })
                .into(holder.image)
        }
        else{
            holder.image.setBackgroundResource(R.drawable.profile)
        }

        /** It opens GoingActivity with passes id when click on it */
        holder.itemView.setOnClickListener {
            val intent = Intent(context, GoingActivity::class.java)
            intent.putExtra("goingId",gatheringDetails!!._id)
            (context as GatheringDetailsActivity).startActivity(intent)
        }

    }
    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image = itemView.img_profile_pic!!
        var progressBar = itemView.progressBar!!


    }
}