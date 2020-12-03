package com.we.beyond.ui.feedback

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.presenter.feedback.FeedbackImpl
import com.we.beyond.presenter.feedback.FeedbackPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP

class FeedbackActivity : AppCompatActivity(), FeedbackPresenter.IFeedbackView {

    var context: Context? = null
    var feedbackPresenter: FeedbackImpl? = null
    var issueTypeLayout: TextInputLayout? = null
    var issueType: AutoCompleteTextView? = null
    var issueDescriptionET: EditText?=null
    var submitBtn:TextView?=null
    var titleTV:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        feedbackPresenter = FeedbackImpl(this)
        initUI()
    }
    override fun onSuccessfulFeedbackSubmission(message : String) {
        ConstantMethods.showToast(this@FeedbackActivity, message)
        finish()
    }

    private fun initUI()
    {
        issueType=findViewById(R.id.dropdown_feedbackType)
        issueType!!.typeface=ConstantFonts.raleway_regular
        issueTypeLayout=findViewById(R.id.feedbackTypeLayout)
        issueTypeLayout!!.typeface=ConstantFonts.raleway_regular
        issueDescriptionET=findViewById(R.id.et_issue_details)
        issueDescriptionET!!.typeface=ConstantFonts.raleway_regular
        titleTV=findViewById(R.id.txt_title)
        titleTV!!.typeface= ConstantFonts.raleway_semibold
        val issueCategories: Array<String> = resources.getStringArray(R.array.issueCategories)
        setIssueTypeAdapter(issueCategories!!.asList())
        submitBtn=findViewById(R.id.btn_submit)
        submitBtn!!.typeface= ConstantFonts.raleway_semibold
        submitBtn!!.setOnClickListener(View.OnClickListener {
            try {

                if (ConstantMethods.checkForInternetConnection(this@FeedbackActivity)) {
                    getDataToPost()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun setIssueTypeAdapter(issueTypes: List<String>)
    {
        if (issueTypes!!.isNotEmpty()) {
            val adapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
                this,
                android.R.layout.simple_spinner_dropdown_item, issueTypes!!
            ) {
                override fun getView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getView(position, convertView, parent)
                    val text =
                        view.findViewById<View>(android.R.id.text1) as TextView
                    text.setTypeface(ConstantFonts.raleway_regular)
                    return view
                }
            }
            issueType!!.setAdapter(adapter)
        }
    }

    private fun getDataToPost() {
        try {
            val feedbackDescription = issueDescriptionET!!.text!!.toString()
            val issueType = issueType!!.text!!.toString()

            if (issueType.isEmpty()) {
                issueTypeLayout!!.error = "Please select issue type"
                return
            } else {
                issueTypeLayout!!.isErrorEnabled = false
            }
            if (feedbackDescription.isEmpty()) {
                ConstantMethods.showToast(this, "Please enter description of issue")
                return
            }

            if (issueType!!.isNotEmpty()) {
                if (feedbackDescription!!.isNotEmpty()) {
                    var refreshedToken = EasySP.init(this).getString("token")

                    if (refreshedToken!!.length == 1) {
                        refreshedToken = FirebaseInstanceId.getInstance().token
                    }
                    if (ConstantMethods.checkForInternetConnection(this@FeedbackActivity)) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("type", issueType)
                        jsonObject.addProperty("feedback", feedbackDescription)
                        postDataToServer(jsonObject)
                    }
                }
            }

        } catch (e: Exception) {

        }
    }

    private fun postDataToServer(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                feedbackPresenter!!.onSubmitFeedback(this@FeedbackActivity, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}