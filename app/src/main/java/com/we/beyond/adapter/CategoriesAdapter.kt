package com.we.beyond.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.model.Categories
import com.we.beyond.ui.dashboard.CategoriesActivity
import com.we.beyond.util.ConstantFonts
import kotlinx.android.synthetic.main.categories_item.view.*
import kotlin.collections.ArrayList

/** It binds category data set to views */

class CategoriesAdapter(var context: Context, categoryName: ArrayList<String>, categoryImage: ArrayList<Categories>, allCategorySelected: Boolean) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>()
{
    /** init array list */
    var categoryName : ArrayList<String>?=null
    var categoryImages : ArrayList<Categories>?=null
    var isSelected : ArrayList<Boolean>?=null
    var viewArray : ArrayList<View> = ArrayList()

    /** init boolean */
    var allCategorySelected = false


    init {
        this.categoryName = categoryName
        this.categoryImages = categoryImage
        this.allCategorySelected = allCategorySelected

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.categories_item,parent,false)

        viewArray.add(view)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return categoryImages!!.size
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        /** It set data to views */
        holder.categoryTitle.text = categoryName!![position]
        holder.categoryTitle.typeface = ConstantFonts.raleway_medium

        isSelected = ArrayList()
        isSelected!!.clear()

        holder.progressBar.visibility = View.VISIBLE
        Picasso.with(context)
            .load(categoryImages!![position].deselectedImageUrl)
            //.placeholder(R.drawable.progress_animation)
            //.memoryPolicy(MemoryPolicy.NO_CACHE)
            //.networkPolicy(NetworkPolicy.NO_CACHE)
            //.resize(400, 400)  // optional
            .into(holder.categoryImage , object : Callback{
                override fun onSuccess() {
                    holder.progressBar.visibility = View.GONE
                }

                override fun onError() {

                }

            })

        if(!allCategorySelected) {
            for (i in 0 until categoryImages!!.size) {
                isSelected!!.add(false)


            }
        }

        else{
            println("category size ${categoryImages!!.size}")
            println("isSelected size ${isSelected!!.size}")
            if(categoryImages!!.size >0) {
                for (i in 0 until viewArray.size) {

                    isSelected!!.add(true)

                    viewArray[i].txt_category_title.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    viewArray[i].cardView.setCardBackgroundColor(context.resources.getColor(R.color.colorAccent))

                    holder.progressBar.visibility = View.VISIBLE
                    Picasso.with(context)
                        .load(categoryImages!![i].selectedImageUrl)
                        //.placeholder(R.drawable.progress_animation)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(viewArray[i].img_category, object : Callback {
                            override fun onError() {
                            }

                            override fun onSuccess() {
                                holder.progressBar.visibility = View.GONE
                            }

                        })
                }
            }
        }

        /** Select and un select the category item depends on condition */
        holder.cardview.setOnClickListener {

           // if(isSelected!!.size != categoryImages!!.size)
           // {
              //  (context as CategoriesActivity).unCheckCheckBox()
               // println(" all category selecetd $isSelected")
           // }

            if(isSelected!![position])
            {
                (context as CategoriesActivity).unCheckCheckBox()
                isSelected!![position]=false
                holder.categoryTitle.setTextColor(ContextCompat.getColor(context,R.color.colorAccent))
                holder.cardview.setCardBackgroundColor(context.resources.getColor(R.color.white))

                holder.progressBar.visibility = View.VISIBLE
                Picasso.with(context)
                    .load(categoryImages!![position].deselectedImageUrl)
                    //.placeholder(R.drawable.progress_animation)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.categoryImage, object : Callback{
                        override fun onSuccess() {
                            holder.progressBar.visibility = View.GONE
                        }

                        override fun onError() {

                        }

                    })



            }
            else{
                isSelected!![position] = true
                if(isSelected!!.contains(false))
                {

                }
                else {
                    (context as CategoriesActivity).checkCheckBox()
                }
                holder.categoryTitle.setTextColor(ContextCompat.getColor(context,R.color.white))
                holder.cardview.setCardBackgroundColor(context.resources.getColor(R.color.colorAccent))

                holder.progressBar.visibility = View.VISIBLE
                Picasso.with(context)
                    .load(categoryImages!![position].selectedImageUrl)
                    //.placeholder(R.drawable.progress_animation)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.categoryImage , object : Callback{
                        override fun onSuccess() {
                            holder.progressBar.visibility = View.GONE
                        }

                        override fun onError() {
                        }

                    })

            }

        }

    }

    /** View holder to initialize ui */
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)
    {
        var categoryTitle = itemView.txt_category_title!!
        var categoryImage = itemView.img_category!!
        var cardview = itemView.cardView!!
        var progressBar = itemView.progressBar!!

    }

}