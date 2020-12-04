package com.we.beyond.ui.issues.submitIssue.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.google.gson.Gson

import com.we.beyond.R
import com.we.beyond.model.NearByIssueByIdDetailsPojo
import com.we.beyond.ui.issues.submitIssue.SubmitAnIssueActivity
import com.we.beyond.util.ConstantFonts
import com.white.easysp.EasySP

/** It will add description of issue */
class IssueDescriptionFragment : Fragment() {

    /** init text view */
    var issueDescriptionTitle : TextView?=null

    /** init edit text */
    var issueTitle : TextView?=null
    var issueDetails : TextView?=null

    var issueData : NearByIssueByIdDetailsPojo?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v= inflater.inflate(R.layout.fragment_description, container, false)

        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val submitActivity= activity as SubmitAnIssueActivity
        submitActivity.shouldEnableNextBtn(false)

        /** initialize ids of elements */
        initElementsWithIds(v)

        /** load data from shared */
//        loadData()

        /** initialize onclick listener */
        initWithListener()


        /** Get stored data using intent and assign it to text view */
        val getIntentData = arguments!!.getString("issueData")
        issueData = Gson().fromJson(getIntentData, NearByIssueByIdDetailsPojo::class.java)

        if(issueData!=null)
        {
            issueTitle!!.text = issueData!!.data.title
            issueDetails!!.text = issueData!!.data.description
        }


        return  v
    }

    /** Get stored data using intent and assign it to text view */
    private fun loadData() {
        val issue = EasySP.init(context).getString("issueTitle")
        val description =EasySP.init(context).getString("issueDetails")
        if(issue.isEmpty())
        {

        }
        else{
            issueTitle!!.text = issue
        }

        if(description.isEmpty())
        {

        }
        else{
            issueDetails!!.text = description
        }
    }

    /** ui listeners */
    private fun initWithListener()
    {
        /** It will save the issue title */
        issueTitle!!.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(p0: Editable?)
            {
                EasySP.init(context).put("issueTitle",issueTitle!!.text)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

        /** It will save the issue details */
        issueDetails!!.addTextChangedListener(object : TextWatcher
        {
            override fun afterTextChanged(p0: Editable?) {
                EasySP.init(context).put("issueDetails",issueDetails!!.text)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })

    }

    /** ui  initialization */
    private fun initElementsWithIds(v: View)
    {
        /** ids of text view */
        issueDescriptionTitle = v.findViewById(R.id.txt_description_title)
        issueDescriptionTitle!!.typeface = ConstantFonts.raleway_regular
        val submitActivity = activity as SubmitAnIssueActivity
        /** ids of edit text */
        issueTitle = v.findViewById(R.id.et_issue_title)
        issueTitle!!.typeface = ConstantFonts.raleway_regular
        issueTitle!!.addTextChangedListener(
            issueTitle!!.doOnTextChanged { text, start, count, after ->
                run {
                    if (issueDetails!!.text != null && issueDetails!!.text.isNotEmpty() && issueTitle!!.text != null && issueTitle!!.text.isNotEmpty()) {
                        submitActivity!!.shouldEnableNextBtn(true)
                        submitActivity!!.shouldEnableLocationIcon(true)
                    }
                    else
                    {
                        submitActivity!!.shouldEnableNextBtn(false)
                        submitActivity!!.shouldEnableLocationIcon(false)
                    }
                }
            }
        )
        issueDetails = v.findViewById(R.id.et_issue_details)
        issueDetails!!.typeface = ConstantFonts.raleway_regular
        issueDetails!!.addTextChangedListener(
            issueDetails!!.doOnTextChanged { text, start, count, after ->
                run {
                    if (issueDetails!!.text != null && issueDetails!!.text.isNotEmpty() && issueTitle!!.text != null && issueTitle!!.text.isNotEmpty()) {
                        submitActivity!!.shouldEnableNextBtn(true)
                        submitActivity!!.shouldEnableLocationIcon(true)
                    }
                    else
                    {
                        submitActivity!!.shouldEnableNextBtn(false)
                        submitActivity!!.shouldEnableLocationIcon(false)
                    }
                }
            }
        )
    }


}
