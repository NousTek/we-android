package com.we.beyond.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.we.beyond.adapter.CategoriesAdapter
import com.we.beyond.adapter.FilterCategoriesAdapter
import com.we.beyond.Interface.OnChangeDataFilterListener
import com.we.beyond.R
import com.we.beyond.model.*
import com.we.beyond.presenter.filter.FilterCategoryImpl
import com.we.beyond.presenter.filter.FilterCategoryPresenter
import com.we.beyond.presenter.profile.updateCategory.UpdateUserDefaultCategoryImpl
import com.we.beyond.presenter.profile.updateCategory.UpdateUserDefaultCategoryPresenter
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import java.util.HashSet
/** It will show default categories */
class MyDefaultCategoriesActivity : AppCompatActivity(), FilterCategoryPresenter.IFilterCategoryView,
    OnChangeDataFilterListener,UpdateUserDefaultCategoryPresenter.IUpdateUserDefaultCategoryView
{
    /** init recycler view */
    var categoriesRecycler: RecyclerView? = null

    /** initialize respected implementors and model */
    var categoriesPresenter: FilterCategoryImpl? = null
    var categoryData : NearByCategoriesPojo?=null

    var postCategoryPresenter: UpdateUserDefaultCategoryImpl? = null
    var postCategoriesData : UpdateCategoriesPojo?=null

    /** init button */
    var update : Button?=null

    /** init image view */
    var back : ImageView?=null

    /** Array list */
    var categoryName: ArrayList<String>? = null
    var categoryImage: ArrayList<AllCategories>? = null
    var updateCategoryImage: ArrayList<Categories>? = null
    var categorySelected: ArrayList<String>? = null
    var categoryId = HashSet<String>()

    /** init shimmer layout */
    var shimmer_view_container : ShimmerFrameLayout?=null

    /** init adapter */
    var categoriesAdapter: FilterCategoriesAdapter? = null
    var updateCategoriesAdapter: CategoriesAdapter? = null

    var allCategorySelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_default_categories)

        /** array initialization */
        categoryName = ArrayList()
        categoryImage = ArrayList()
        categorySelected = ArrayList()

        /** initialize implementation */
        categoriesPresenter = FilterCategoryImpl(this)
        postCategoryPresenter = UpdateUserDefaultCategoryImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()

        /** set categories data to spinner */
        setCategories()

        /** initialize onclick listener */
        initWithListener()
    }

    override fun onDataChangeSortBy(selectedValue: String) {


    }



    override fun onDataChangeCategorySelected(categorySelectedCount : Int)
    {

    }



    override fun onDataChangeKmRadious(kmRadiusSelected: String)
    {

    }

    /** ui listeners */
    private fun initWithListener()
    {
        /** It goes back to previous activity when click on it */
        back!!.setOnClickListener {

            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }


        /** It will getDataToPost() */
        update!!.setOnClickListener {
            try {

                if (ConstantMethods.checkForInternetConnection(this)) {
                    getDataToPost()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /** It will take all required fields and call postDataToServer function */
    private fun getDataToPost() {

        try {
            val categoriesValue = EasySP.init(this).getStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)


            val selectedIdsArray = ArrayList<String>()
            selectedIdsArray.addAll(categoriesValue)

                val jsonObject = JsonObject()
                val jsonArray = JsonArray()

            if(selectedIdsArray.isNotEmpty()) {


                for (i in 0 until categoriesValue.size) {

                    if (selectedIdsArray.isNotEmpty()) {

                        jsonArray.add(selectedIdsArray[i])

                    }

                }


                EasySP.init(this)
                    .putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID, categoryId)

                jsonObject.add("categories", jsonArray)

                if (ConstantMethods.checkForInternetConnection(this)) {
                    postDataToServer(jsonObject)
                }
            }
             else {

                ConstantMethods.showWarning(this,"Category", "Please select at least one category")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to getCategories function of post category presenter */
    private fun postDataToServer(jsonObject: JsonObject) {


        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                postCategoryPresenter!!.getCategories(this, jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }


    /** ui initialization */
    private fun initElementsWithIds()
    {
        /** ids of recycler view */
        categoriesRecycler = findViewById(R.id.recycler_category)
        categoriesRecycler!!.layoutManager = GridLayoutManager(this, 3)

        shimmer_view_container = findViewById(R.id.shimmer_view_container)

        /** ids of button */
        update = findViewById(R.id.btn_update)
        update!!.typeface = ConstantFonts.raleway_semibold

        /** ids of image view */
        back = findViewById(R.id.img_back)
    }


    /** It will start the shimmer animation and call getCategories() of category presenter  */
    private fun setCategories() {
        shimmer_view_container!!.startShimmerAnimation()
        shimmer_view_container!!.visibility = View.VISIBLE

        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                categoriesPresenter!!.getCategories(this)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()

        }
    }

    /** It will stop the shimmer animation and set FilterCategoriesAdapter */
    override fun setCategoriesAdapter(categories: NearByCategoriesPojo) {
        try {
            categoryName!!.clear()
            categoryImage!!.clear()

            if (categories.categories.isNotEmpty()) {

                /*var userCategoryString = Gson().toJson(categories.categories)
                if(userCategoryString !=null && userCategoryString.isNotEmpty())
                {
                    EasySP.init(context).putString("userCategoryDetails",userCategoryString)
                }*/

                for (i in 0 until categories.categories.size) {
                    categoryData = categories
                    categoryName!!.add(categories.categories[i].name)


                }
            }

            if (categoryName!!.isNotEmpty()) {

                shimmer_view_container!!.stopShimmerAnimation()
                shimmer_view_container!!.visibility = View.GONE

                categoriesAdapter =
                    FilterCategoriesAdapter(this, categoryName!!, categories.categories,categories,false)
                categoriesRecycler!!.adapter = categoriesAdapter
            }
        } catch (e: Exception)
        {
            e.printStackTrace()

        }

    }

    /** It will go to UserProfileActivity */
    override fun goToNextScreen() {
        try {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It will go back to UserProfileActivity */
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }




}
