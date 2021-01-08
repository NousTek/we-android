package com.we.beyond.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.we.beyond.R
import com.we.beyond.ui.gathering.gathering.GatheringActivity
import com.we.beyond.util.ConstantFonts
import kotlinx.android.synthetic.main.list_connect_categories.view.*

/** It binds all gathering criteria set to views */
class GatheringCriteriaAdapter
    (
    val context: Context,
    gatheringCriteria: ArrayList<String>
) : RecyclerView.Adapter<GatheringCriteriaAdapter.ViewHolder>() {

    /** init array list */
    var gatheringCriteria: ArrayList<String>? = null
    var viewArray: ArrayList<View> = ArrayList()

    /** init boolean and int variables */
    var isSelected: Boolean = false
    var selectedPosition: Int = -1

    init {
        this.gatheringCriteria = gatheringCriteria


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
        return gatheringCriteria!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** It set data to views */
        holder.categoryTitle.text = gatheringCriteria!![position]
        holder.categoryTitle.typeface = ConstantFonts.raleway_semibold
        if(position==0 && selectedPosition==-1)
        {
            holder.categoryTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.categoryTitle.setBackgroundResource(R.drawable.button_fill_border)
        }
        /** It select and un select criteria and changes its text color and background */
        holder.itemView.setOnClickListener {

            if (selectedPosition != position) {
                isSelected = true
                selectedPosition = position

                setSelected(position)

                (context as GatheringActivity).gatheringId = gatheringCriteria!![position].trim()
                (context as GatheringActivity).resetValue()
                (context as GatheringActivity).getDateAndTime()

                holder.categoryTitle.setTextColor(ContextCompat.getColor(context, R.color.white))
                holder.categoryTitle.setBackgroundResource(R.drawable.button_fill_border)


            } else if (selectedPosition == position) {
                if (isSelected) {
                    isSelected = false
                    selectedPosition = -1

                    if((context as GatheringActivity).gatheringArray!!.size == 0)
                    {
                        (context as GatheringActivity).resetValue()
                        (context as GatheringActivity).getCurrentDateGathering()

                        holder.categoryTitle.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                        holder.categoryTitle.setBackgroundResource(R.drawable.connect_border)
                    }
                    else {

                        (context as GatheringActivity).resetValue()
                        (context as GatheringActivity).gatheringAdapter!!.notifyDataSetChanged()
                        (context as GatheringActivity).getCurrentDateGathering()

                        holder.categoryTitle.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorPrimary
                            )
                        )
                        holder.categoryTitle.setBackgroundResource(R.drawable.connect_border)
                    }

                }

            }


        }


    }


    /** It select and un select the views*/
     fun setSelected(position: Int) {

        for (i in 0 until viewArray.size) {
            viewArray[i].txt_connect_category.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorPrimary
                )
            )
            viewArray[i].txt_connect_category.setBackgroundResource(R.drawable.connect_border)


        }

    }


    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryTitle = itemView.txt_connect_category!!


    }

}