package com.we.beyond.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.we.beyond.ui.leaderBoard.LeaderBoardActivity
import com.we.beyond.R
import com.we.beyond.util.ConstantFonts
import kotlinx.android.synthetic.main.list_connect_categories.view.*

/** It binds leader board criteria data set to views */
class LeaderBoardCriteriaAdapter
    (
    val context: Context,
    leaderBoardCriteria: ArrayList<String>
) : RecyclerView.Adapter<LeaderBoardCriteriaAdapter.ViewHolder>() {

    /** init array list */
    var leaderBoardCriteria: ArrayList<String>? = null
    var viewArray: ArrayList<View> = ArrayList()

    /** init boolean */
    var isSelected: Boolean = false


    init {
        this.leaderBoardCriteria = leaderBoardCriteria


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view =
            LayoutInflater.from(context).inflate(R.layout.list_connect_categories, parent, false)

        viewArray.add(view)


        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return leaderBoardCriteria!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** It set data to views */

        holder.categoryTitle.text = leaderBoardCriteria!![position]
        holder.categoryTitle.typeface = ConstantFonts.raleway_semibold


        setSelected(0)
        (context as LeaderBoardActivity).leaderBoardId = leaderBoardCriteria!![0].trim()
        (context as LeaderBoardActivity).getDateAndTime()


        holder.itemView.setOnClickListener {


            setSelected(position)

            (context as LeaderBoardActivity).leaderBoardId = leaderBoardCriteria!![position].trim()
            (context as LeaderBoardActivity).getDateAndTime()

            holder.categoryTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.categoryTitle.setBackgroundResource(R.drawable.button_fill_border)


        }


    }

    /** select and un select text */
    private fun setSelected(position: Int) {

        for (i in 0 until viewArray.size) {
            viewArray[i].txt_connect_category.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            viewArray[i].txt_connect_category.setBackgroundResource(R.drawable.connect_border)

        }

        viewArray[position].txt_connect_category.setTextColor(ContextCompat.getColor(context, R.color.white))
        viewArray[position].txt_connect_category.setBackgroundResource(R.drawable.button_fill_border)

    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryTitle = itemView.txt_connect_category!!


    }


}