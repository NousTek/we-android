package com.we.beyond.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.we.beyond.adapter.GoingAdapter
import com.we.beyond.R
import com.we.beyond.interceptor.ApplicationController.Companion.context
import com.we.beyond.model.GoingIdData
import com.we.beyond.model.GoingIdPojo
import com.we.beyond.presenter.going.goingById.GoingByIdImpl
import com.we.beyond.presenter.going.goingById.GoingByIdPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods

/** It is used to see particular user profile */
class GoingActivity : AppCompatActivity(),GoingByIdPresenter.IGoingByIdView
{
    /** initialize implementors */
    var goingPresenter : GoingByIdImpl?=null

    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null

    /** init recycler view */
    var goingRecycler: RecyclerView? = null

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

    /** init array list */
    var goingList : ArrayList<GoingIdData>?=null

    /** init adapter */
    var goingAdapter : GoingAdapter?=null

    companion object {
        val PAGE_SIZE = 5
    }
    /** init string */
    var goingId : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_going)

        /** array initialization */
        goingList = ArrayList()
        goingList!!.clear()

        /** initialize implementation */
        goingPresenter = GoingByIdImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()


        goingId = intent.getStringExtra("goingId")

        if(goingId!=null && goingId.isNotEmpty())
        {
            /** get data to post */
            getDataToPost()

        }

        /** initialize onclick listener */
        initWithListener()

    }

    /** It checks the goingIdDetails data,
     *  if it is not empty then set GoingAdapter
     *  else show warning dialog
     */
    override fun onGoingIdAdapter(goingIdDetails: GoingIdPojo)
    {
        if (goingIdDetails.data.size == 0) {


            ConstantMethods.showToast(
                this,
                "There are no going members."
            )
        } else {

            goingList!!.addAll(goingIdDetails.data)


            goingAdapter = GoingAdapter(this, goingList!!)
            goingRecycler!!.adapter = goingAdapter

        }

    }

    /** It checks the goingIdDetails data,
     *  if it is not empty then notify GoingAdapter
     */
    override fun onGoingIdAdapterOnLoadMore(goingIdDetails: GoingIdPojo) {

        if (goingIdDetails.data.size > 0) {

            goingList!!.addAll(goingIdDetails.data)

            goingAdapter!!.notifyDataSetChanged()
        }



    }


    /** It calls onGoingById() of going presenter */

    private fun getDataToPost()
    {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                resetValue()
                goingPresenter!!.onGoingById(this, pageNo, PAGE_SIZE,goingId)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** ui listeners */
    private fun initWithListener()
    {
        back!!.setOnClickListener {
            onBackPressed()
        }

        /** Call load more api when scrolling the recycler view */
        goingRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                visibleItemCount = goingRecycler!!.getChildCount()
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
                    this@GoingActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                loadMore()
                            }
                        }
                    })
                    // Do something
                    loading = true
                }

            }
        })

    }

    /** It calls onGoingByIdOnLoadMore function of going presenter for loading data */
    private fun loadMore()
    {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                goingPresenter!!.onGoingByIdOnLoadMore(this, ++page, PAGE_SIZE,goingId)
            }

        } catch (e: Exception) {
                e.printStackTrace()
        }

    }

    /** ui initialization */
    private fun initElementsWithIds()
    {
        /** ids of image view */
        back = findViewById(R.id.img_back)


        /** ids of text view */

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold


        /** ids of recycler view */
        goingRecycler = findViewById(R.id.goingRecycler)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        goingRecycler!!.setHasFixedSize(true)
        goingRecycler!!.layoutManager = linearLayoutManager


    }

    /** reset all values  */
    fun resetValue() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        loading = true
        previousTotal = 0
        visibleThreshold = 5
        page = 1
        pageNo = 1
        goingList!!.clear()
    }


    /** It goes back to previous activity */
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
