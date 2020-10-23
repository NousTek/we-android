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
import com.we.beyond.util.ConstantFonts
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.categories_item.view.*
import kotlinx.android.synthetic.main.categories_item.view.cardView

/** It binds issue category data set to views */
class IssueCategoriesAdapter(
    var context: Context,
    categoryName: ArrayList<String>,
    categoryImage: ArrayList<Categories>,
    categoryId: ArrayList<String>
) : RecyclerView.Adapter<IssueCategoriesAdapter.ViewHolder>() {

    /** init array list */
    var categoryName: ArrayList<String>? = null
    var categoryImages: ArrayList<Categories>? = null
    var categoryId : ArrayList<String>?=null
    var isSelected: ArrayList<Boolean>? = null
    var viewArray : ArrayList<View> = ArrayList()


    init {
        this.categoryName = categoryName
        this.categoryImages = categoryImage
        this.categoryId = categoryId


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.categories_item, parent, false)


        viewArray.add(view)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categoryImages!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** It set data to views */
        holder.categoryTitle.text = categoryName!![position]
        holder.categoryTitle.typeface = ConstantFonts.raleway_medium

        isSelected = ArrayList()
        isSelected!!.clear()

       /* val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.backgroundColor = R.color.colorPrimary
        circularProgressDrawable.start()*/

        if(categoryImages!![position].alreadySelected == categoryImages!![position]._id)
        {
            setSelected(position)

            val selectedCategory = categoryId!![position]

            if(selectedCategory.isNotEmpty()) {

                EasySP.init(context).put("categoryId", selectedCategory)

            }
            return
        }
      holder.progressBar.visibility = View.VISIBLE
        Picasso.with(context)
            .load(categoryImages!![position].deselectedImageUrl)
            //.placeholder(R.drawable.progress_animation)
            //.memoryPolicy(MemoryPolicy.NO_CACHE)
            //.networkPolicy(NetworkPolicy.NO_CACHE)
            //.resize(400, 400)  // optional

            .into(holder.categoryImage ,object  : Callback{
                override fun onSuccess() {
                    holder.progressBar.visibility = View.GONE
                }

                override fun onError() {

                }

            })


        for (i in 0 until categoryImages!!.size) {
                isSelected!!.add(false)

        }

        /** Select and un select the category item depends on condition */
        holder.cardview.setOnClickListener {

            setSelected(position)

            val selectedCategory = categoryId!![position]

            if(selectedCategory.isNotEmpty()) {

                EasySP.init(context).put("categoryId", selectedCategory)

            }


        }

    }

    /** select and un select card view */
    fun setSelected(position : Int){
        for(i in 0 until viewArray.size)
        {
           // isSelected!![i] = true
            viewArray[position].progressBar.visibility = View.VISIBLE
            viewArray[i].cardView.setCardBackgroundColor(context.resources.getColor(R.color.white))
            viewArray[i].txt_category_title.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorAccent
                ))

            Picasso.with(context)
                .load(categoryImages!![i].deselectedImageUrl)
                //.placeholder(R.drawable.progress_animation)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.resize(400, 400)  // optional
                //.into(viewArray[i].img_category)
                .into(viewArray[i].img_category ,object  : Callback{
                    override fun onSuccess() {
                        viewArray[position].progressBar.visibility = View.GONE
                        viewArray[i].img_category.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY)
                    }

                    override fun onError() {

                    }

                })
        }

        viewArray[position].cardView.setCardBackgroundColor(context.resources.getColor(R.color.colorAccent))
        viewArray[position].txt_category_title.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.white
            ))

        viewArray[position].progressBar.visibility = View.VISIBLE
        Picasso.with(context)
            .load(categoryImages!![position].selectedImageUrl)
            //.placeholder(R.drawable.progress_animation)
            //.memoryPolicy(MemoryPolicy.NO_CACHE)
            //.networkPolicy(NetworkPolicy.NO_CACHE)
            //.resize(400, 400)  // optional
            //.into(viewArray[position].img_category)
            .into(viewArray[position].img_category,object  : Callback{
            override fun onSuccess() {
                viewArray[position].progressBar.visibility = View.GONE
                viewArray[position].img_category.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY)
            }

            override fun onError() {

            }

        })

    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryTitle = itemView.txt_category_title!!
        var categoryImage = itemView.img_category!!
        var cardview = itemView.cardView!!
        var progressBar = itemView.progressBar!!

    }
}