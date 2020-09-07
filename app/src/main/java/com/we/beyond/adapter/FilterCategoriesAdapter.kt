package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.we.beyond.Interface.OnChangeDataFilterListener
import com.we.beyond.R
import com.we.beyond.model.AllCategories
import com.we.beyond.model.NearByCategoriesPojo
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.categories_item.view.*
import java.lang.Exception
import java.util.HashSet

/** It binds filter category data set to views */

class FilterCategoriesAdapter(
    var context: Context,
    categoryName: ArrayList<String>,
    allCategories: ArrayList<AllCategories>,
    categories: NearByCategoriesPojo,
    filter: Boolean
) : RecyclerView.Adapter<FilterCategoriesAdapter.ViewHolder>() {

    /** init array list */
    var categoryName: ArrayList<String>? = null
    var isSelected: ArrayList<Boolean>? = null
    var allCategories: ArrayList<AllCategories>? = null
    var viewArray: ArrayList<View> = ArrayList()
    var selectedPositionArray = ArrayList<Int>()
    var selectedFilterCategory = HashSet<String>()
    var selectedCategotyId = HashSet<String>()
    var defaultSelectedCategoryId = HashSet<String>()

    /** init model*/
    var categories: NearByCategoriesPojo? = null

    /** init listener */
    var onDataChangeListener: OnChangeDataFilterListener? = null

    /** init boolean */
    var filter: Boolean = false

    val activity = context as Activity

    init {
        this.categoryName = categoryName
        this.allCategories = allCategories
        this.categories = categories
        this.onDataChangeListener = activity as OnChangeDataFilterListener
        this.filter = filter

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
        return categoryName!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** It set data to views */

        holder.categoryTitle.text = categoryName!![position]
        holder.categoryTitle.typeface = ConstantFonts.raleway_medium

        isSelected = ArrayList()
        isSelected!!.clear()
        //selectedPositionArray = ArrayList()
        selectedPositionArray.clear()
        selectedFilterCategory.clear()
        selectedCategotyId.clear()


        holder.progressBar.visibility = View.VISIBLE
        Picasso.with(context)
            .load(allCategories!![position].deselectedImageUrl)
            //.placeholder(R.drawable.ic_loading)
            //.memoryPolicy(MemoryPolicy.NO_CACHE)
            //.networkPolicy(NetworkPolicy.NO_CACHE)
            //.resize(400, 400)  // optional
            .into(holder.categoryImage, object : Callback {
                override fun onSuccess() {
                    holder.progressBar.visibility = View.GONE
                }

                override fun onError() {

                }

            })

        for (i in 0 until categoryName!!.size) {
            isSelected!!.add(false)

        }

        if (filter) {
            val array = EasySP.init(context).getStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)
            if (array != null && array.isNotEmpty()) {

                selectedPositionArray.clear()
                for (j in 0 until allCategories!!.size) {
                    for (i in 0 until array.size) {
                        if (i.toString() == allCategories!![j]._id)
                            selectedCategotyId.add(i.toString())
                        selectedPositionArray.add(allCategories!![i].order)

                    }
                }
                EasySP.init(context)
                    .putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID, selectedCategotyId)
                onDataChangeListener!!.onDataChangeCategorySelected(selectedCategotyId.size)
                EasySP.init(context)
                    .putInt(ConstantEasySP.SELECTED_FILTER_CATEGORY_SIZE, selectedCategotyId.size)


                /* if (selectedPositionArray.isNotEmpty()) {
                     for (k in 0 until selectedPositionArray.size) {
                         setSelected(selectedPositionArray[allCategories!![k].order])
                         isSelected!![selectedPositionArray[allCategories!![k].order]] = true
                     }
                 }*/

                println("selected position arraylist ${array}")

                if (array != null) {
                    for (i in 0 until array.size) {
                        if (array.toString() == allCategories!![i]._id) {
                            setSelected(allCategories!![i].order)
                            isSelected!![allCategories!![i].order] = true
                        }
                    }
                }

                /* if (array.isNotEmpty()) {
                     for (j in 0 until allCategories!!.size) {
                         for (i in 0 until array.size) {
                             if (i.toString() == allCategories!![j]._id)
                                 setSelected(selectedPositionArray[allCategories!![j].order])
                                 isSelected!![selectedPositionArray[allCategories!![j].order]] = true


                         }
                     }

                 }
 */
            } else {
                selectedPositionArray.clear()
                for (i in 0 until allCategories!!.size) {
                    for (j in 0 until categories!!.userCategories.size) {

                        if (categories!!.userCategories[j]._id == allCategories!![i]._id) {

                            selectedPositionArray.add(allCategories!![i].order)

                            setSelected(allCategories!![i].order)

                            isSelected!![allCategories!![i].order] = true

                            /*  defaultSelectedCategoryId.add(allCategories!![i]._id)

                              EasySP.init(context).putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID, defaultSelectedCategoryId)
  */

                        }

                    }


                }


            }
        } else {
            selectedPositionArray.clear()
            for (i in 0 until allCategories!!.size) {
                for (j in 0 until categories!!.userCategories.size) {

                    if (categories!!.userCategories[j]._id == allCategories!![i]._id) {

                        selectedPositionArray.add(allCategories!![i].order)

                        setSelected(allCategories!![i].order)

                        isSelected!![allCategories!![i].order] = true

                        /* defaultSelectedCategoryId.add(allCategories!![i]._id)

                         EasySP.init(context).putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID, defaultSelectedCategoryId)

 */
                    }

                }


            }

        }

        selectedFilterCategory.clear()
        for (i in 0 until selectedPositionArray.size) {
            selectedFilterCategory.add(selectedPositionArray[i].toString())
            onDataChangeListener!!.onDataChangeCategorySelected(selectedFilterCategory.size)
            EasySP.init(context)
                .putInt(ConstantEasySP.SELECTED_FILTER_CATEGORY_SIZE, selectedFilterCategory.size)

        }

        /**It changes text and background color of card view and set image to image view */
        holder.cardview.setOnClickListener {

            try {
                if (isSelected!![position]) {
                    isSelected!![position] = false
                    holder.categoryTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent
                        )
                    )
                    holder.cardview.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    selectedPositionArray.remove(position)
                    selectedFilterCategory.remove(position.toString())

                    holder.progressBar.visibility = View.VISIBLE
                    Picasso.with(context)
                        .load(allCategories!![position].deselectedImageUrl)
                        //.placeholder(R.drawable.progress_animation)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(holder.categoryImage, object : Callback {
                            override fun onError() {

                            }

                            override fun onSuccess() {
                                holder.progressBar.visibility = View.GONE
                            }

                        })


                } else {
                    isSelected!![position] = true
                    holder.categoryTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                    holder.cardview.setCardBackgroundColor(context.resources.getColor(R.color.colorAccent))
                    selectedPositionArray.add(position)
                    selectedFilterCategory.add(position.toString())


                    holder.progressBar.visibility = View.VISIBLE
                    Picasso.with(context)
                        .load(allCategories!![position].selectedImageUrl)
                        // .placeholder(R.drawable.progress_animation)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(holder.categoryImage, object : Callback {
                            override fun onSuccess() {
                                holder.progressBar.visibility = View.GONE
                            }

                            override fun onError() {

                            }

                        })


                }


                EasySP.init(context)
                    .putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID, selectedFilterCategory)


                selectedCategotyId.clear()
                for (i in 0 until selectedPositionArray.size) {
                    selectedCategotyId.add(allCategories!![selectedPositionArray[i]]._id)
                }


                EasySP.init(context)
                    .putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID, selectedCategotyId)
                onDataChangeListener!!.onDataChangeCategorySelected(selectedCategotyId.size)
                EasySP.init(context)
                    .putInt(ConstantEasySP.SELECTED_FILTER_CATEGORY_SIZE, selectedCategotyId.size)


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    /** change background color of card view */
    fun setSelected(position: Int) {
        if (allCategories!!.size == viewArray.size) {

            viewArray[position].cardView.setCardBackgroundColor(context.resources.getColor(R.color.colorAccent))
            viewArray[position].txt_category_title.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )

            viewArray[position].progressBar.visibility = View.VISIBLE
            Picasso.with(context)
                .load(allCategories!![position].selectedImageUrl)
                //.placeholder(R.drawable.progress_animation)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.networkPolicy(NetworkPolicy.NO_CACHE)
                //.resize(400, 400)  // optional
                .into(viewArray[position].img_category, object : Callback {
                    override fun onSuccess() {
                        viewArray[position].progressBar.visibility = View.GONE
                    }

                    override fun onError() {

                    }

                })

            selectedCategotyId.clear()
            for (i in 0 until viewArray.size) {
                selectedCategotyId.add(allCategories!![i]._id)
            }
            EasySP.init(context)
                .putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID, selectedCategotyId)

        }

    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryTitle = itemView.txt_category_title!!
        var categoryImage = itemView.img_category!!
        var cardview = itemView.cardView!!
        var progressBar = itemView.progressBar!!

    }


}