package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.we.beyond.Interface.OnBadgesDetailsListener
import com.we.beyond.R
import com.we.beyond.model.Badges
import com.we.beyond.model.BadgesData
import kotlinx.android.synthetic.main.list_badges_item.view.*

/** It binds badge data set to views */
class BadgesAdapter(
    var context: Context,
    badges: BadgesData,
    badgesArray: ArrayList<Badges>
)

    : RecyclerView.Adapter<BadgesAdapter.ViewHolder>() {

    /** init model */
    var badges: BadgesData? = null

    /** init array list */
    var viewArray: ArrayList<View> = ArrayList()
    var isSelected: ArrayList<Boolean>? = null
    var selectedPositionArray = ArrayList<Int>()
    var badgesArray: ArrayList<Badges> = ArrayList()

    /** init listener */
    var onBadgesDetailsListener: OnBadgesDetailsListener? = null

    init {

        this.badges = badges
        this.badgesArray = badgesArray

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.list_badges_item, parent, false)

        viewArray.add(view)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return badgesArray.size

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize the badgedetailsListener and set badges to image view using picasso library*/
        try {

            val activity = context as Activity
            try {
                onBadgesDetailsListener = activity as OnBadgesDetailsListener
            } catch (e: Exception) {
                e.printStackTrace()

            }

            isSelected = ArrayList()
            isSelected!!.clear()


            holder.progressBar.visibility = View.VISIBLE
            Picasso.with(context)
                .load(badgesArray[position].deselectedImageUrl)
                //.placeholder(R.drawable.progress_animation)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.resize(400, 400)  // optional
                .into(holder.image, object : Callback {
                    override fun onSuccess() {
                        holder.progressBar.visibility = View.GONE
                    }

                    override fun onError() {

                    }

                })

            for (i in 0 until badgesArray.size) {
                isSelected!!.add(false)

            }



            selectedPositionArray.clear()
            if (badgesArray[position].isSelected) {
                selectedPositionArray.add(position)


                viewArray[position].progressBar.visibility = View.VISIBLE
                Picasso.with(context)
                    .load(badgesArray[position].selectedImageUrl)
                    //.placeholder(R.drawable.progress_animation)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(viewArray[position].img_badges, object : Callback {
                        override fun onSuccess() {
                            viewArray[position].progressBar.visibility = View.GONE
                        }

                        override fun onError() {

                        }

                    })

            }

            /** It call onBadgeDetails of onBadgesDetailsListener when click on it */
            holder.itemView.setOnClickListener {

                onBadgesDetailsListener!!.onBadgesDetails(
                    position
                )


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image = itemView.img_badges!!
        var progressBar = itemView.progressBar!!
        var info = itemView.img_notification!!


    }

}