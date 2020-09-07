package com.we.beyond.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.we.beyond.adapter.MyActivitiesAdapter
import com.we.beyond.R
import com.we.beyond.model.MyActivityData
import com.we.beyond.model.MyActivityPojo
import com.we.beyond.presenter.profile.myActivities.MyActivitiesImpl
import com.we.beyond.presenter.profile.myActivities.MyActivitiesPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods

/** It will show all activities done by user */
class MyActivitiesActivity : AppCompatActivity(), MyActivitiesPresenter.IMyActivitiesView {

    /** initialize implementors */
    var myActivityPresenter: MyActivitiesImpl? = null

    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var noContent : TextView?=null

    /** init recycler view  and its adapter */
    var myActivityRecycler: RecyclerView? = null
    var myActivitiesAdapter: MyActivitiesAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null

    var activitiesArray: ArrayList<MyActivityData>? = null

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


    companion object {
        var PAGE_SIZE = 50
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_activities)

        /** array initialization */
        activitiesArray = ArrayList()
        activitiesArray!!.clear()

        /** initialize implementation */
        myActivityPresenter = MyActivitiesImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get data to post */
        getDataToPost()
    }

    /** It will call postDataToServer() */
    private fun getDataToPost() {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyActivitiesActivity)) {
                postDataToServer(pageNo, PAGE_SIZE)
            }
        }

        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It will call onRequestMyActivitiesData function of myActivities presenter */
    private fun postDataToServer(pageNo: Int, pageSize: Int)
    {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                myActivityPresenter!!.onRequestMyActivitiesData(
                    this,
                    pageNo,
                    pageSize
                )
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** ui  listeners */
    private fun initWithListener()
    {
        /** It goes back to previous activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            /* val intent = Intent(this, DashboardActivity::class.java)
             startActivity(intent)*/
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            //finish()
        }


        /** It calls loadMore() when scrolling  */
        myActivityRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = myActivityRecycler!!.getChildCount()
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
                    this@MyActivitiesActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(this@MyActivitiesActivity)) {
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

                    postDataToServerOnLoadMore(++page)
                }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onRequestMyActivitiesDataOnLoadMore function of myActivity presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int)
    {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                    //ConstantMethods.showProgessDialog(this, "Please Wait...")
                myActivityPresenter!!.onRequestMyActivitiesDataOnLoadMore(this, pageNo, PAGE_SIZE
                )
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
        title = findViewById(R.id.txt_activity_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of recycler view */
        myActivityRecycler = findViewById(R.id.activityRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        myActivityRecycler!!.setHasFixedSize(true)
        myActivityRecycler!!.layoutManager = linearLayoutManager


        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

    }

    /** It checks the activity data,
     *  if it is not empty then set MyActivitiesAdapter
     */
    override fun setMyActivitiesDataAdapter(activityData: MyActivityPojo)
    {
        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (activityData.data.size == 0) {
           // ConstantMethods.showWarning(this,"", "No Activities found.")

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "No Activities found."

            try {
                val sweetAlertDialog = SweetAlertDialog(this , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "No Activities found."
                sweetAlertDialog.show()
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.setConfirmClickListener {
                    sweetAlertDialog.dismissWithAnimation()

                    onBackPressed()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {

            noContent!!.visibility = View.GONE

            activitiesArray!!.addAll(activityData.data)


            println("near by issue $activitiesArray")

            myActivitiesAdapter = MyActivitiesAdapter(this, activitiesArray!!)
            myActivityRecycler!!.adapter = myActivitiesAdapter


        }

    }

    /** It checks the activity data,
     *  if it is not empty then notify to MyActivitiesAdapter
     */
    override fun setMyActivitiesDataAdapterOnLoadMore(activityData: MyActivityPojo)
    {
        if (activityData.data.size > 0) {

            activitiesArray!!.addAll(activityData.data)

            myActivitiesAdapter!!.notifyDataSetChanged()
        }



    }

    /** reset all values  */
    fun resetValue() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        loading = true
        previousTotal = 0
        visibleThreshold = 5
        PAGE_SIZE = 50
        page = 1
        pageNo = 1
        activitiesArray!!.clear()
    }
}
