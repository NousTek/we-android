package com.we.beyond.ui.dashboard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.we.beyond.adapter.CategoriesAdapter
import com.we.beyond.R
import com.we.beyond.model.Categories
import com.we.beyond.model.CategoriesPojo
import com.we.beyond.presenter.dashboard.CategoriesImpl
import com.we.beyond.presenter.dashboard.CategoryPresenter
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import java.util.HashSet

/**
 * this activity is select and un select the categories
 */
class CategoriesActivity : AppCompatActivity(), CategoryPresenter.ICategoriesView {

    var context: Context? = null
    var categoriesPresenter: CategoriesImpl? = null
    var categoryData : CategoriesPojo?=null


    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var welcomeTitle: TextView? = null
    var weTitle: TextView? = null
    var welcomeDescription: TextView? = null
    var selectCategoryTitle: TextView? = null

    /** init button */
    var finish: Button? = null

    /** init recycler view */
    var categoriesRecycler: RecyclerView? = null

    /** Array list */
    var categoryName: ArrayList<String>? = null
    var categoryImage: ArrayList<Categories>? = null
    var categorySelected: ArrayList<String>? = null
    var categoryId = HashSet<String>()


    /** init checkbox */
    var checkBox: CheckBox? = null

    /** init boolean */
    var allCategorySelected = false

    /** init adapter */
    var categoriesAdapter: CategoriesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        /** array initialization */
        categoryName = ArrayList()
        categoryImage = ArrayList()
        categorySelected = ArrayList()


        categoriesPresenter = CategoriesImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** set categories data to spinner */
        setCategories()

        /** initialize onclick listener */
        initWithListener()

        allCategorySelected = true
        println("check box checked")
        categoriesAdapter =
            CategoriesAdapter(this, categoryName!!, categoryImage!!, allCategorySelected)
        categoriesRecycler!!.adapter = categoriesAdapter
        categoriesAdapter!!.notifyDataSetChanged()


    }

    /** uncheck the check box  */
    fun unCheckCheckBox()
    {
        checkBox!!.isChecked = false

    }

    /** check the check box */
    fun checkCheckBox()
    {
        checkBox!!.isChecked = true

    }

    /** ui listener initialization  */
    private fun initWithListener() {

        /** this click listener used to select the categories
         * and set it to the category adapter
         */
        checkBox!!.setOnClickListener {
            if (checkBox!!.isChecked) {

                allCategorySelected = true
                println("check box checked")
                categoriesAdapter =
                    CategoriesAdapter(this, categoryName!!, categoryImage!!, allCategorySelected)
                categoriesRecycler!!.adapter = categoriesAdapter
                categoriesAdapter!!.notifyDataSetChanged()

            } else {
                allCategorySelected = false
                categoriesAdapter =
                    CategoriesAdapter(this, categoryName!!, categoryImage!!, allCategorySelected)
                categoriesRecycler!!.adapter = categoriesAdapter
                categoriesAdapter!!.notifyDataSetChanged()
            }
        }

        /** it is used to post category data to server  */
        finish!!.setOnClickListener {
            try {

                if (ConstantMethods.checkForInternetConnection(this@CategoriesActivity)) {
                    getDataToPost()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /** this is used to for going to previos activity */
        back!!.setOnClickListener {
            categoriesPresenter!!.onBackClick()
        }

    }

    /**
     * this function post data with selected categories and converts to json array
     * as a parameter
     */
    private fun getDataToPost() {

        try {

            categoryId.clear()
            if (categoriesAdapter!!.isSelected!!.contains(true)) {

                val jsonObject = JsonObject()
                val jsonArray =JsonArray()

                for (i in 0 until categoryData!!.data.size) {

                    if (categoriesAdapter!!.isSelected!![i]) {

                        jsonArray.add(categoryData!!.data[i]._id)
                        categoryId.add(categoryData!!.data[i]._id)

                    }

                }


                EasySP.init(context).putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID,categoryId)

                jsonObject.add("categoryIds", jsonArray)
                println("json object $jsonObject")
                if (ConstantMethods.checkForInternetConnection(this@CategoriesActivity)) {
                    postDataToServer(jsonObject)
                }
            } else {

                ConstantMethods.showWarning(this,"Category", "Please select at least one category")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * this function calls the getDataToPost method of category presenter
     * and passes json object as parameter
     */
    private fun postDataToServer(jsonObject: JsonObject) {
        println("categories id ==post data to server")

        try {
            if (ConstantMethods.checkForInternetConnection(this)) {

                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                categoriesPresenter!!.getDataToPost(this, jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** calling api to get the category data */
    private fun setCategories() {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                categoriesPresenter!!.getCategories(this)
            }
        }catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun initElementsWithIds() {
        /** ids of image */
        back = findViewById(R.id.img_back)

        /** ids of text view */
        title = findViewById(R.id.txt_categories_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        welcomeTitle = findViewById(R.id.txt_welcome)
        welcomeTitle!!.typeface = ConstantFonts.raleway_regular

        weTitle = findViewById(R.id.txt_we)
        weTitle!!.typeface = ConstantFonts.abys_regular

        welcomeDescription = findViewById(R.id.txt_select_category_description)
        welcomeDescription!!.typeface = ConstantFonts.raleway_regular

        selectCategoryTitle = findViewById(R.id.txt_select_category_title)
        selectCategoryTitle!!.typeface = ConstantFonts.raleway_medium

        /** ids of button */
        finish = findViewById(R.id.btn_category_select)

        /** ids of checkbox */
        checkBox = findViewById(R.id.checkBox)

        /** ids of recycler view */
        categoriesRecycler = findViewById(R.id.recycler_category)
        categoriesRecycler!!.layoutManager = GridLayoutManager(this, 3)


    }


    /** get all category data and set it to category adapter  */
    override fun setCategoriesAdapter(categories: CategoriesPojo) {
        try {
            categoryName!!.clear()
            categoryImage!!.clear()

            if (categories.data.isNotEmpty()) {
                for (i in 0 until categories.data.size) {
                    categoryData = categories
                    categoryName!!.add(categories.data[i].name)
                    categoryImage!!.add(categories.data[i])

                }
            }

            if (categoryName!!.isNotEmpty() && categoryImage!!.isNotEmpty()) {
                categoriesAdapter =
                    CategoriesAdapter(this, categoryName!!, categories.data, allCategorySelected)
                categoriesRecycler!!.adapter = categoriesAdapter
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }

    }

    /** after category selection success, going to next activity */
    override fun goToNextScreen() {
        try {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()

        } catch (e: Exception) {

        }

    }

    /** going to previous activity  */
    override fun goToPreviousScreen() {
        /* try {
             val intent = Intent(this, DashboardActivity::class.java)
             startActivity(intent)
             overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
             finish()

         } catch (e: Exception) {

         }*/

        finish()

    }


    /** go back to previous activity on when click on system back button */
    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }

}
