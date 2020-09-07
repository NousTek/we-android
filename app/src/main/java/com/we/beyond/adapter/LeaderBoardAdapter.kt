package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.DataList
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.leaderBoard.LeaderBoardActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.Constants
import kotlinx.android.synthetic.main.list_leader_board_items.view.*

/** It binds leader board data set to views */
class LeaderBoardAdapter  ( var context: Context,
   leaderBoardDataList: ArrayList<DataList>,
   leaderBoardTopListDataList: ArrayList<DataList>,
    type : String
) : RecyclerView.Adapter<LeaderBoardAdapter.ViewHolder>() {

    /** init array list */
    var leaderBoardDataList: ArrayList<DataList>? = null
    var leaderBoardDataTopList: ArrayList<DataList>? = null
    var viewArray: ArrayList<View> = ArrayList()

    /** init string */
    var type : String=""



    init {
        this.leaderBoardDataList = leaderBoardDataList
        this.leaderBoardDataTopList = leaderBoardTopListDataList
        this.type = type

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.list_leader_board_items, parent, false)

        viewArray.add(view)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return leaderBoardDataList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** It set data to views */

        if(leaderBoardDataList!![position].user.profileUrl!=null && leaderBoardDataList!![position].user.profileUrl.isNotEmpty()) {
            Picasso.with(context)
                .load(leaderBoardDataList!![position].user.profileUrl)
                .placeholder(R.drawable.profile)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.resize(400, 400)  // optional
                .into(holder.imgProfile)
        }

        if (leaderBoardDataList!![position].user.userLoginType.userType.equals(
                "individual",
                ignoreCase = true
            )
        ) {
            holder.profileTitle.text =
                " ${leaderBoardDataList!![position].user.firstName} ${leaderBoardDataList!![position].user.lastName}"
            holder.profileTitle.typeface = ConstantFonts.raleway_regular
        } else {
            holder.profileTitle.text =
                " ${leaderBoardDataList!![position].user.organizationName}"
            holder.profileTitle.typeface = ConstantFonts.raleway_regular

        }

        holder.count.text = leaderBoardDataList!![position].count.toString()
        holder.count.typeface = ConstantFonts.raleway_regular

        holder.countTitle.text = type
        holder.countTitle.typeface = ConstantFonts.raleway_regular

        /** It opens BadgesActivity with passes below data*/
        holder.itemView.setOnClickListener {
            val intent = Intent(context, BadgesActivity::class.java)
            intent.putExtra(Constants.USER_ID,leaderBoardDataList!![position].user._id)
            (context as LeaderBoardActivity).startActivity(intent)
            (context as LeaderBoardActivity).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        for (i in 0 until leaderBoardDataList!!.size) {
            for (j in 0 until leaderBoardDataTopList!!.size) {

                if (leaderBoardDataTopList!![j].user._id == leaderBoardDataList!![i].user._id) {

                    setSelected(j)
                }
            }
        }


    }

    /** select and un select text */
    private fun setSelected( position: Int) {

        if (leaderBoardDataTopList!!.size == viewArray.size) {
            viewArray[position].txt_count_title.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            viewArray[position].txt_count.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
        }


    }
    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgProfile = itemView.img_profile_pic
        var profileTitle = itemView.txt_profile_name!!
        var count = itemView.txt_count!!
        var countTitle = itemView.txt_count_title!!
    }

}