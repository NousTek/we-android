package com.we.beyond.ui.filter.fragments



import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.we.beyond.adapter.FilterCategoriesAdapter
import com.we.beyond.R
import com.we.beyond.model.AllCategories
import com.we.beyond.model.NearByCategoriesPojo
import com.we.beyond.presenter.filter.FilterCategoryImpl
import com.we.beyond.presenter.filter.FilterCategoryPresenter
import com.we.beyond.util.ConstantMethods
import java.util.HashSet

/** It show filter selection sort by categories */
class FilterCategoryFragment : Fragment() , FilterCategoryPresenter.IFilterCategoryView
{

    /** init recycler view */
    var categoriesRecycler: RecyclerView? = null

    /** initialize implementors */
    var categoriesPresenter: FilterCategoryImpl? = null

    /** initialize model */
    var categoryData : NearByCategoriesPojo?=null


    /** Array list */
    var categoryName: ArrayList<String>? = null
    var categoryImage: ArrayList<AllCategories>? = null
    var categorySelected: ArrayList<String>? = null
    var categoryIds : HashSet<String>?=null

    /** initialize shimmer layout */
    var shimmer_view_container : ShimmerFrameLayout?=null

    /** initialize adpater */
    var categoriesAdapter: FilterCategoriesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_filter_category, container, false)

        /** array initialization */
        categoryName = ArrayList()
        categoryImage = ArrayList()
        categorySelected = ArrayList()

        /** initialize implementation */
        categoriesPresenter = FilterCategoryImpl(this)


        /** initialize ids of elements */
        initElementsWithIds(v)

        /** set categories data to spinner */
        setCategories()

        /** initialize onclick listener */
        initWithListener()


        return  v
    }

    private fun initWithListener()
    {


    }

    /** ui  initialization */
    private fun initElementsWithIds(v : View)
    {
        /** ids of recycler view */
        categoriesRecycler = v.findViewById(R.id.recycler_category)
        categoriesRecycler!!.layoutManager = GridLayoutManager(context!!, 2)

        shimmer_view_container = v.findViewById(R.id.shimmer_view_container)
    }


    /** It will start the shimmer animation and call getCategories() of category presenter  */
    private fun setCategories() {
        shimmer_view_container!!.startShimmerAnimation()
        shimmer_view_container!!.visibility = View.VISIBLE

        try {
            if (ConstantMethods.checkForInternetConnection(context!!)) {
                    ConstantMethods.showProgessDialog(context!!, "Please Wait...")
                categoriesPresenter!!.getCategories(context!!)
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
            println("categories $categories")

            if (categoryName!!.isNotEmpty()) {

                shimmer_view_container!!.stopShimmerAnimation()
                shimmer_view_container!!.visibility = View.GONE

                categoriesAdapter =
                    FilterCategoriesAdapter(context!!, categoryName!!, categories.categories,categories,true)
                categoriesRecycler!!.adapter = categoriesAdapter
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }

    }




}
