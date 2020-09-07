package com.we.beyond.ui.filter.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import com.we.beyond.Interface.OnChangeDataFilterListener

import com.we.beyond.R
import com.we.beyond.ui.filter.FilterActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.white.easysp.EasySP
import java.lang.Exception

/** It show filter selection sort by values */
class FilterSortByFragment : Fragment() {

    /** init radio group */
    var radioGroup: RadioGroup? = null
    var newest: RadioButton? = null
    var oldest: RadioButton? = null
    var resolveNewest: RadioButton? = null
    var resolveOldest: RadioButton? = null
    var unresolveNewest: RadioButton? = null
    var unresolvedOldest: RadioButton? = null

    var onDataChangeListener: OnChangeDataFilterListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_filter_sort_by, container, false)


        /** initialize ids of elements */
        initElementsWithIds(v)

        /** get stored data */
        getStoredData()

        /** initialize onclick listener */
        initWithListener()

        /** sort Newest every time when open FilterActivity  */
        onDataChangeListener!!.onDataChangeSortBy("Newest")

        /** Get stored data using Easysp and assign it,
         * check and set respected radio button */
        val sortByStoredValue = EasySP.init(context!!).getString("sortBy")

        if (sortByStoredValue.isNotEmpty()) {

            if (sortByStoredValue.equals("Newest", ignoreCase = true)) {
                newest!!.isChecked = true
            } else if (sortByStoredValue.equals("Oldest", ignoreCase = true)) {
                oldest!!.isChecked = true
            } else if (sortByStoredValue.equals("Resolved Newest", ignoreCase = true)) {
                resolveNewest!!.isChecked = true
            } else if (sortByStoredValue.equals("Resolved Oldest", ignoreCase = true)) {
                resolveOldest!!.isChecked = true
            } else if (sortByStoredValue.equals("Unresolved Newest", ignoreCase = true)) {
                unresolveNewest!!.isChecked = true
            } else if (sortByStoredValue.equals("Unresolved Oldest", ignoreCase = true)) {
                unresolvedOldest!!.isChecked = true
            }
        } else {
            EasySP.init(context!!).put("sortBy", newest!!.text)
            onDataChangeListener!!.onDataChangeSortBy(newest!!.text.toString())

        }



        return v
    }

    /** Get all stored data using Easysp,
     * call getCategorySelected() and getKmRadius() of FilterActivity */
    private fun getStoredData() {
        val category = EasySP.init(context).getInt(ConstantEasySP.SELECTED_FILTER_CATEGORY_SIZE)
        val radius = EasySP.init(context).getString(ConstantEasySP.SELECTED_FILTER_RADIUD)

        if (category != null) {
            (context as FilterActivity).getCategorySelected(category)
        }

        if (radius != null && radius.isNotEmpty()) {
            (context as FilterActivity).getKmRadius(radius)
        }


    }

    /** ui listeners */
    private fun initWithListener() {

        /** It will set respected value to onDataChangeSortBy()
         * of onDataChangeListener on selection of radio button */
        radioGroup!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_newest -> {
                    println("newest ${newest!!.text}")
                    EasySP.init(context!!).put("sortBy", newest!!.text)
                    onDataChangeListener!!.onDataChangeSortBy(newest!!.text.toString())
                }

                R.id.rb_oldest -> {
                    println("oldest ${oldest!!.text}")
                    EasySP.init(context!!).put("sortBy", oldest!!.text)
                    onDataChangeListener!!.onDataChangeSortBy(oldest!!.text.toString())
                }

                R.id.rb_resolve_newest -> {
                    println("resolve newest ${resolveNewest!!.text}")
                    EasySP.init(context!!).put("sortBy", resolveNewest!!.text)
                    onDataChangeListener!!.onDataChangeSortBy(resolveNewest!!.text.toString())
                }

                R.id.rb_resolved_oldest -> {
                    println("resolve oldest ${resolveOldest!!.text}")
                    EasySP.init(context!!).put("sortBy", resolveOldest!!.text)
                    onDataChangeListener!!.onDataChangeSortBy(resolveOldest!!.text.toString())
                }

                R.id.rb_unresolved_newest -> {
                    println("unresolved newest ${unresolveNewest!!.text}")
                    EasySP.init(context!!).put("sortBy", unresolveNewest!!.text)
                    onDataChangeListener!!.onDataChangeSortBy(unresolveNewest!!.text.toString())
                }
                R.id.rb_unresolved_oldest -> {
                    println("unresolved oldest ${unresolvedOldest!!.text}")
                    EasySP.init(context!!).put("sortBy", unresolvedOldest!!.text)
                    onDataChangeListener!!.onDataChangeSortBy(unresolvedOldest!!.text.toString())
                }

            }
        }


    }

    /** ui initialization */
    private fun initElementsWithIds(v: View) {
        /** ids of radio group */
        radioGroup = v.findViewById(R.id.radioGroup)

        newest = v.findViewById(R.id.rb_newest)
        newest!!.typeface = ConstantFonts.raleway_regular

        oldest = v.findViewById(R.id.rb_oldest)
        oldest!!.typeface = ConstantFonts.raleway_regular

        resolveNewest = v.findViewById(R.id.rb_resolve_newest)
        resolveNewest!!.typeface = ConstantFonts.raleway_regular

        resolveOldest = v.findViewById(R.id.rb_resolved_oldest)
        resolveOldest!!.typeface = ConstantFonts.raleway_regular

        unresolveNewest = v.findViewById(R.id.rb_unresolved_newest)
        unresolveNewest!!.typeface = ConstantFonts.raleway_regular

        unresolvedOldest = v.findViewById(R.id.rb_unresolved_oldest)
        unresolvedOldest!!.typeface = ConstantFonts.raleway_regular
    }


    /** initialize the listener */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val activity = context as Activity
        try {
            onDataChangeListener = activity as OnChangeDataFilterListener
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

}
