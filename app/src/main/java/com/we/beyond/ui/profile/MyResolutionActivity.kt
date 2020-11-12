package com.we.beyond.ui.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.we.beyond.adapter.MyResolutionAdapter
import com.we.beyond.R
import com.we.beyond.model.NearByIssues
import com.we.beyond.model.NearByIssuesPojo
import com.we.beyond.presenter.profile.myResolution.MyResolutionImpl
import com.we.beyond.presenter.profile.myResolution.MyResolutionPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods

/** It will show all resolved issue which created by user */
class MyResolutionActivity : AppCompatActivity(), MyResolutionPresenter.IMyResolutionView{


    val context: Context = this

    /** initialize implementors */
    var myResolutionPresenter: MyResolutionImpl? = null

    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var noContent : TextView?=null

    /** init recycler view and adapter and array list  */
    var issuesRecycler: RecyclerView? = null
    var myResolutionAdapter: MyResolutionAdapter? = null
    var myResolutionArray: ArrayList<NearByIssues>? = null


    /** pull to refresh layout */
    var pullToRefresh : SwipeRefreshLayout?=null


    /**  for Lazy loading */
    var firstVisibleItem: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading = true
    var previousTotal = 0
    var visibleThreshold = 5
    var page = 1
    var pageNo: Int = 1
    var linearLayoutManager: LinearLayoutManager? = null

    companion object {
        val PAGE_SIZE = 5
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_resolution)

        /** It hides keyboard */
        ConstantMethods.hideKeyBoard(this,this)

        /** array initialization */
        myResolutionArray = ArrayList()
        myResolutionArray!!.clear()

        /** initialize implementation */
        myResolutionPresenter = MyResolutionImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get data to post */
        getDataToPost()


    }

    /** It calls postDataToServer function */
    private fun getDataToPost() {

        try {

            if (ConstantMethods.checkForInternetConnection(this)) {

                postDataToServer(pageNo, PAGE_SIZE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It takes the below input and send to onMyResolution function of myResolution presenter */
    private fun postDataToServer(pageNo : Int, pageSize : Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                resetValue()
                myResolutionPresenter!!.onMyResolution(this, pageNo,pageSize)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the below input and send to onMyResolutionOnLoadMore function of myResolution presenter */
    private fun postDataToServerOnLoadMore(pageNo : Int, pageSize : Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {

                myResolutionPresenter!!.onMyResolutionOnLoadMore(this, pageNo,pageSize)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            //finish()
        }

        /** Call load more api when scrolling the recycler view */
        issuesRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                visibleItemCount = issuesRecycler!!.getChildCount()
                totalItemCount = linearLayoutManager!!.getItemCount()
                firstVisibleItem = linearLayoutManager!!.findFirstVisibleItemPosition()
                if (loading) {
                    // if(dy>0) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                    //}
                }
                if ((!loading && ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)))) {
                    // End has been reached
                    this@MyResolutionActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                loadMore()
                            }
                        }
                    })
                    // Do something
                    loading = true
                }
                println("loading")
            }
        })

        /** It calls again getDataToPost function when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            resetValue()
            getDataToPost()
            pullToRefresh!!.isRefreshing = false
        }


    }

    /** It calls postDataToServerOnLoadMore() with below inputs  */
    private fun loadMore() {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                // ConstantMethods.showProgessDialog(this, "Please Wait...")
                postDataToServerOnLoadMore(++pageNo, PAGE_SIZE)
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image view */
        back = findViewById(R.id.img_back)

        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of recycler view */
        issuesRecycler = findViewById(R.id.myResolutionRecycler)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        issuesRecycler!!.setHasFixedSize(true)
        issuesRecycler!!.layoutManager = linearLayoutManager

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

    }

    /** It checks the nearByIssueDetails data,
     *  if it is not empty then set data to MyResolutionAdapter
     */
    override fun setMyResolutionAdapter(nearByIssueDetails: NearByIssuesPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (nearByIssueDetails.data.size == 0) {

/*
            ConstantMethods.showWarning(
                this,
                "",
                "There are no reported resolutions found."
            )*/

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no resolutions found."


            try {
                val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "There are no resolutions found."
//                sweetAlertDialog.show()
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.setConfirmClickListener {
                    sweetAlertDialog.dismissWithAnimation()

                    //onBackPressed()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            noContent!!.visibility = View.GONE

            myResolutionArray!!.addAll(nearByIssueDetails.data)

            myResolutionAdapter = MyResolutionAdapter(this, myResolutionArray!!)
            issuesRecycler!!.adapter = myResolutionAdapter

        }


    }

    /** It checks the nearByIssueDetails data,
     *  if it is not empty then notify to MyResolutionAdapter
     */
    override fun setMyResolutionOnLoadMore(nearByIssueDetails: NearByIssuesPojo) {

        if (nearByIssueDetails.data.size > 0) {

            myResolutionArray!!.addAll(nearByIssueDetails.data)

            myResolutionAdapter!!.notifyDataSetChanged()
        }


    }


    override fun goToNextScreen() {

    }

    override fun goToPreviousScreen() {

    }


    fun resetValue() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        loading = true
        previousTotal = 0
        visibleThreshold = 5
        page = 1
        pageNo = 1
        myResolutionArray!!.clear()
    }


    /** It is used to delete resolution data and notify to adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("result code $resultCode")
        if(requestCode == 200 && resultCode == 201)
        {
            val deleteIssueList = ArrayList<NearByIssues>()
            deleteIssueList.addAll(myResolutionArray!!)

            val issueId = data!!.getStringExtra("issueId")

            for( i in 0 until deleteIssueList.size)
            {
                if(deleteIssueList[i]._id == issueId)
                {
                    deleteIssueList.remove(deleteIssueList[i])
                    myResolutionArray!!.clear()
                    myResolutionArray!!.addAll(deleteIssueList)
                    myResolutionAdapter!!.notifyDataSetChanged()

                    break
                }
            }

            if(myResolutionArray!!.size == 0)
            {
                ConstantMethods.showWarning(
                    this,
                    "",
                    "There are no resolutions found."
                )

                noContent!!.visibility = View.VISIBLE
                noContent!!.text = "There are no resolutions found."
            }

        }
        else{

        }

    }

    /** It go back to the previous activity */
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        // finish()
    }
}
