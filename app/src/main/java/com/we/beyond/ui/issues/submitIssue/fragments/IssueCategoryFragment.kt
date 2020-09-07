package com.we.beyond.ui.issues.submitIssue.fragments



import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.we.beyond.adapter.IssueCategoriesAdapter

import com.we.beyond.R
import com.we.beyond.model.CategoriesPojo
import com.we.beyond.model.NearByIssueByIdDetailsPojo
import com.we.beyond.presenter.dashboard.CategoriesImpl
import com.we.beyond.presenter.dashboard.CategoryPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods

/** It show the categories to select it for create or submitting an issue */
class IssueCategoryFragment : Fragment() , CategoryPresenter.ICategoriesView {

    /** initialize implementors and model */
    var categoriesPresenter: CategoriesImpl? = null
    var categoryData: CategoriesPojo? = null
    var issueData : NearByIssueByIdDetailsPojo?=null

    /** init recycler view */
    var categoriesRecycler: RecyclerView? = null

    /** Array list */
    var categoryName: ArrayList<String>? = null
    var categoryImage: ArrayList<String>? = null
    var categoryId : ArrayList<String>?=null
    var categorySelected: ArrayList<String>? = null

    /** init text view */
    var issueCategoryTitle : TextView?=null

    /** init shimmer layout */
    var shimmer_view_container : ShimmerFrameLayout?=null

    /** init adapter */
    var categoriesAdapter: IssueCategoriesAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_category, container, false)

        /** array initialization */
        categoryName = ArrayList()
        categoryImage = ArrayList()
        categoryId = ArrayList()
        categorySelected = ArrayList()

        categoriesPresenter = CategoriesImpl(this)

        /** initialize ids of elements */
        initElementsWithIds(v)

        /** set categories data to spinner */
        setCategories()

        /** initialize onclick listener */
        initWithListener()

        /** Get all stored data using intent and assign it */
        val getIntentData = arguments!!.getString("issueData")
        issueData = Gson().fromJson(getIntentData, NearByIssueByIdDetailsPojo::class.java)

        return v
    }



    private fun initWithListener()
    {


    }


    override fun goToNextScreen() {

    }

    override fun goToPreviousScreen() {

    }

    /** It will start the shimmer animation and call getCategories() of category presenter  */
    private fun setCategories()
    {
        shimmer_view_container!!.startShimmerAnimation()
        shimmer_view_container!!.visibility = View.VISIBLE

        try {
            if (ConstantMethods.checkForInternetConnection(context!!)) {

                categoriesPresenter!!.getCategories(context!!)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }



    }

    /** ui  initialization */
    private fun initElementsWithIds(v : View) {

        /** ids of recycler view */
        categoriesRecycler = v.findViewById(R.id.recycler_category)
        categoriesRecycler!!.layoutManager = GridLayoutManager(context!!, 3)

        /** ids of text view */
        issueCategoryTitle = v.findViewById(R.id.txt_category_selection_title)
        issueCategoryTitle!!.typeface = ConstantFonts.raleway_semibold

        shimmer_view_container = v.findViewById(R.id.shimmer_view_container)


    }

    /** It will stop the shimmer animation and set IssueCategoriesAdapter */
    override fun setCategoriesAdapter(categories: CategoriesPojo) {
        try {
            categoryName!!.clear()
            categoryImage!!.clear()

            if (categories.data.isNotEmpty()) {
                for (i in 0 until categories.data.size) {
                    categoryData = categories
                    categoryName!!.add(categories.data[i].name)
                    //categoryImage!!.add(categories.data[i].deselectedImageUrl)
                    categoryId!!.add(categories.data[i]._id)

                    if(issueData!=null)
                    {
                        categoryData!!.data[i].alreadySelected = issueData!!.data.category._id
                    }


                }
            }

            if (categoryName!!.isNotEmpty()) {
                shimmer_view_container!!.stopShimmerAnimation()
                shimmer_view_container!!.visibility = View.GONE

                categoriesAdapter = IssueCategoriesAdapter(context!!, categoryName!!, categories.data,categoryId!!)
                categoriesRecycler!!.adapter = categoriesAdapter

            }
        } catch (e: Exception) {

        }

    }


}
