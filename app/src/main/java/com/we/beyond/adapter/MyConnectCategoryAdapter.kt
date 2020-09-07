package com.we.beyond.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.we.beyond.R
import com.we.beyond.model.ConnectCategories
import com.we.beyond.ui.profile.MyConnectActivity
import com.we.beyond.util.ConstantFonts
import kotlinx.android.synthetic.main.list_connect_categories.view.*

/** It binds article category data set to views */

class MyConnectCategoryAdapter(
    val context: Context,
    connectCategories: ArrayList<ConnectCategories>
) : RecyclerView.Adapter<MyConnectCategoryAdapter.ViewHolder>() {
    /** init array list */
    var category: ArrayList<ConnectCategories>? = null
    var viewArray: ArrayList<View> = ArrayList()

    init {
        this.category = connectCategories


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
        return category!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /** set data to views */
        holder.categoryTitle.text = category!![position].name
        holder.categoryTitle.typeface = ConstantFonts.raleway_semibold

        holder.itemView.setOnClickListener {

            setSelected(position)

            (context as MyConnectActivity).connectCategoryId = category!![position]._id.trim()
            (context as MyConnectActivity).resetValue()
            (context as MyConnectActivity).getDataToPost()
            /*val selectedCategory = category!![position]._id.trim()

            if(selectedCategory.isNotEmpty()) {

                EasySP.init(context).put("connectCategoryId", selectedCategory)

            }*/
        }


    }

    /** It select and un select views */
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

        viewArray[position].txt_connect_category.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.white
            )
        )
        viewArray[position].txt_connect_category.setBackgroundResource(R.drawable.button_fill_border)

    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryTitle = itemView.txt_connect_category!!


    }

}