package com.we.beyond.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.DataList
import com.we.beyond.util.ConstantFonts

/** It binds leader board profile data set to views pager*/
class LeaderBoardProfileAdapter(
    internal var profileList: ArrayList<DataList>,
    var type : String,
    internal var mContext: Context
) :
    PagerAdapter() {
    internal var mLayoutInflater: LayoutInflater
    var profileDataList : ArrayList<DataList>?=null


    override fun getCount(): Int {
        return  profileList.size

    }


    init {

        mLayoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        profileDataList = profileList
    }

    override  fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override  fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.list_leader_board_profile_item, container, false)

        /** initialize ui */
        try {
            val imageView = itemView.findViewById(R.id.img_profile_pic) as ImageView
            val titleView = itemView.findViewById(R.id.txt_profile_name) as TextView
            val category = itemView.findViewById(R.id.txt_leaderBoard_categories) as TextView
            val progressBar = itemView.findViewById(R.id.progressBar) as ProgressBar

            /** set data to view */

            if (profileDataList!![position] != null && profileDataList!!.size>0) {
                if (profileDataList!![position].user.profileUrl != null && profileDataList!![position].user.profileUrl.isNotEmpty()) {

                    progressBar.visibility = View.VISIBLE
                    Picasso.with(mContext)
                        .load("" + profileDataList!![position].user.profileUrl)
                        //.resize(500,400)  // optional
                        .into(imageView, object : Callback {
                            override fun onSuccess() {
                                progressBar.visibility = View.GONE
                            }

                            override fun onError() {

                            }

                        })
                }
                else {

                }


            if (profileDataList!![position].user.userLoginType.userType.equals(
                            "individual",
                            ignoreCase = true
                        )
                    ) {
                        titleView.text =
                            " ${profileDataList!![position].user.firstName} ${profileDataList!![position].user.lastName}"
                        titleView.typeface = ConstantFonts.raleway_regular
                    } else {
                        titleView.text =
                            " ${profileDataList!![position].user.organizationName}"
                        titleView.typeface = ConstantFonts.raleway_regular

                    }


                    if (type != null && type.isNotEmpty()) {
                        category.text = type
                        category.typeface = ConstantFonts.raleway_regular
                    }

                }
                else {
                    profileDataList!!.clear()
                }



            container.addView(itemView)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return itemView
    }

    override  fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as CardView)
    }

}