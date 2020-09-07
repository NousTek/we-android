package com.we.beyond.ui.filter

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.we.beyond.Interface.OnChangeDataFilterListener
import com.we.beyond.R
import com.we.beyond.ui.filter.fragments.FilterCategoryFragment
import com.we.beyond.ui.filter.fragments.FilterKmRadiusFragment
import com.we.beyond.ui.filter.fragments.FilterSortByFragment
import com.we.beyond.ui.issues.nearByIssue.NearByIssueActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.white.easysp.EasySP

/** It will show the issue according to the filter such as sort by new to old,
 * category, filter by kilo meter radius with animation  */
class FilterActivity : AppCompatActivity() ,OnChangeDataFilterListener{


    /** init layout */
    var sortByLayout: RelativeLayout? = null
    var categoryLayout: RelativeLayout? = null
    var kmRadiusLayout: RelativeLayout? = null

    /** init fragment */
    var sortByFragment: FilterSortByFragment? = null
    var categoryFragment: FilterCategoryFragment? = null
    var kmRadiusFragment: FilterKmRadiusFragment? = null

    /** init text view */
    var sortByTitle: TextView? = null
    var sortBy: TextView? = null
    var categoryTitle: TextView? = null
    var category: TextView? = null
    var radiusTitle: TextView? = null
    var radius: TextView? = null

    /** init image view */
    var back: ImageView? = null

    /** init boolean */
    var isSelected : Boolean=false


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        /** initialize fragment */
        sortByFragment = FilterSortByFragment()
        categoryFragment = FilterCategoryFragment()
        kmRadiusFragment = FilterKmRadiusFragment()

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** set selected fragment */
        sortByLayout!!.setBackgroundResource(R.drawable.filter_selection_background)

        /** set fragment */
        setFragment(sortByFragment!!)

        /** Get all stored data using intent and assign it respectively */
        isSelected = intent.getBooleanExtra("gathering",false)

    }


    /** It will show and hide sort by text depends on selected value */
    override fun onDataChangeSortBy(selectedValue: String) {

        if (selectedValue.isNotEmpty()) {
            sortBy!!.visibility = View.VISIBLE
            sortBy!!.text = selectedValue
        } else {
            sortBy!!.visibility = View.GONE
        }


    }


    /** It will show and hide category text depends on category selected count */
    override fun onDataChangeCategorySelected(categorySelectedCount : Int)
    {
        if (categorySelectedCount!=null && categorySelectedCount !=0) {
            category!!.visibility = View.VISIBLE
            category!!.text = "$categorySelectedCount  selected"
        } else {
            category!!.visibility = View.GONE
        }
    }

    /** It will show and hide category text depends on category selected count */
    fun getCategorySelected(categorySelectedCount : Int)
    {
        if (categorySelectedCount!=null) {
            category!!.visibility = View.VISIBLE
            category!!.text = "$categorySelectedCount  selected"
        } else {
            category!!.visibility = View.GONE
        }
    }

    /** It will show and hide radius text depends on kmRadius selected value */
    override fun onDataChangeKmRadious(kmRadiusSelected: String)
    {
        if (kmRadiusSelected.isNotEmpty()) {
            radius!!.visibility = View.VISIBLE
            radius!!.text = (kmRadiusSelected) + " Km"
        } else {
            radius!!.visibility = View.GONE
        }
    }

    /** It will show and hide radius text depends on kmRadius selected value */
    fun getKmRadius(kmRadiusSelected: String)
    {
        if (kmRadiusSelected.isNotEmpty()) {
            radius!!.visibility = View.VISIBLE
            radius!!.text = kmRadiusSelected + " Km"
        } else {
            radius!!.visibility = View.GONE
        }
    }



   /* override fun onDataChange(sortBySelected : String,categorySelected :  ArrayList<String>, kmRadiusSelected : String ) {


        val categoriesValue = EasySP.init(this).getStringSet("selectedFilterCategory")
        val sortByValue = EasySP.init(this).getString("sortBy")
        val radiusValue = EasySP.init(this).getString("radius")
        val lat = EasySP.init(this).getString("lat")
        val long = EasySP.init(this).getString("long")


        if (sortBySelected.isNotEmpty()) {
            sortBy!!.visibility = View.VISIBLE
            sortBy!!.text = sortBySelected
        } else {
            sortBy!!.visibility = View.GONE
        }

        if (kmRadiusSelected!!.isNotEmpty()) {
            radius!!.visibility = View.VISIBLE
            radius!!.text = kmRadiusSelected + " Km"
        } else {
            radius!!.visibility = View.GONE
        }
        if (categorySelected.isNotEmpty()) {
            category!!.visibility = View.VISIBLE
            category!!.text = "${categorySelected.size}  selected"
        } else {
            category!!.visibility = View.GONE
        }
    }
*/

    /** It take fragment as input and load it */
    fun setFragment(fragment: Fragment) {
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.filterFrameLayout, fragment)
        fm.commit()


    }

    /** ui listeners */
    private fun initWithListener() {

        /** It changes the background of layouts and set FilterSortByFragment */
        sortByLayout!!.setOnClickListener {
            sortByLayout!!.setBackgroundResource(R.drawable.filter_selection_background)
            categoryLayout!!.background = null
            kmRadiusLayout!!.background = null
            setFragment(sortByFragment!!)
        }

        /** It changes the background of layouts and set FilterCategoryFragment */
        categoryLayout!!.setOnClickListener {
            categoryLayout!!.setBackgroundResource(R.drawable.filter_selection_background)
            sortByLayout!!.background = null
            kmRadiusLayout!!.background = null
            setFragment(categoryFragment!!)
        }

        /** It changes the background of layouts and set FilterKmRadiusFragment */
        kmRadiusLayout!!.setOnClickListener {
            kmRadiusLayout!!.setBackgroundResource(R.drawable.filter_selection_background)
            sortByLayout!!.background = null
            categoryLayout!!.background = null
            setFragment(kmRadiusFragment!!)
        }

        /** It gets all stored filter data and check empty or not
         * If empty, it will show warning dialog
         * else open NearByIssueActivity when click on it*/
        back!!.setOnClickListener {

            try {
                val categoriesValue = EasySP.init(this).getStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)
                val sortByValue = EasySP.init(this).getString("sortBy")
                val radiusValue = EasySP.init(this).getString("radius")


                if(categoriesValue.isEmpty())
                {
                    showWarning("Category", "Please select at least one category")
                }
                else if(sortByValue.isEmpty())
                {
                    showWarning("Sort By", "Please select at least one value")
                }
                else if(radiusValue.isEmpty())
                {
                    EasySP.init(this).put("radius",5.0)
                    //showWarning("Km Radius", "Please select at least radius")
                }
                else{
                    if(isSelected)
                    {
                        val intent = Intent(this, NearByIssueActivity::class.java)
                        intent.putExtra("gathering",true)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        finish()
                    }
                    else{
                        val intent = Intent(this, NearByIssueActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                        finish()
                    }

                }



            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    /** ui initialization */
    private fun initElementsWithIds() {


        /** id of image view */
        back = findViewById(R.id.img_back)

        /** ids of layout */
        sortByLayout = findViewById(R.id.sortByLayout)
        categoryLayout = findViewById(R.id.categoryLayout)
        kmRadiusLayout = findViewById(R.id.radiusLayout)

        /** ids of text view */
        sortByTitle = findViewById(R.id.txt_sort_by_title)
        sortByTitle!!.typeface = ConstantFonts.raleway_semibold

        sortBy = findViewById(R.id.txt_sort_by)
        sortBy!!.typeface = ConstantFonts.raleway_regular

        categoryTitle = findViewById(R.id.txt_category_title)
        categoryTitle!!.typeface = ConstantFonts.raleway_semibold

        category = findViewById(R.id.txt_category)
        category!!.typeface = ConstantFonts.raleway_regular

        radiusTitle = findViewById(R.id.txt_km_radius_title)
        radiusTitle!!.typeface = ConstantFonts.raleway_semibold

        radius = findViewById(R.id.txt_km_radius)
        radius!!.typeface = ConstantFonts.raleway_regular


    }

    /** show warning dialog */
    fun showWarning(title: String, message: String) {
        try {
            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = title
            sweetAlertDialog.contentText = message
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }
    }

    /** It gets all stored filter data and check empty or not
     * If empty, it will show warning dialog
     * else open NearByIssueActivity when click on system back */
    override fun onBackPressed() {

        try {
            val categoriesValue = EasySP.init(this).getStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)
            val sortByValue = EasySP.init(this).getString("sortBy")
            val radiusValue = EasySP.init(this).getString("radius")

            println("categories $categoriesValue")

            if(categoriesValue.isEmpty())
            {
                showWarning("Category", "Please select at least one category")
            }
            else if(sortByValue.isEmpty())
            {
                showWarning("Sort By", "Please select at least one value")
            }
            else if(radiusValue.isEmpty())
            {
                EasySP.init(this).put("radius",5.0)
                //showWarning("Km Radius", "Please select at least radius")
            }
            else{
                if(isSelected)
                {
                    val intent = Intent(this, NearByIssueActivity::class.java)
                    intent.putExtra("gathering",true)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                else{
                    val intent = Intent(this, NearByIssueActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
            }



        } catch (e: Exception) {
            e.printStackTrace()
        }

        super.onBackPressed()
    }


}
