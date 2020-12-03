package com.we.beyond.ui.nearByMe

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
import com.we.beyond.adapter.NearByMeUsersAdapter
import com.we.beyond.R
import com.we.beyond.model.NearByMeUsersData
import com.we.beyond.model.NearByMeUsersPojo
import com.we.beyond.presenter.nearByMe.NearByMeImpl
import com.we.beyond.presenter.nearByMe.NearByMePresenter
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.ui.profile.EditProfileActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods

/** It will show issues near by my location */
class NearByMeActivity : AppCompatActivity() , NearByMePresenter.INearByMeView {

    /** initialize implementors */
    var nearByMePresenter: NearByMeImpl? = null

    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var noContent : TextView?=null

    /** init recycler view */
    var nearByMeRecycler: RecyclerView? = null

    /** init adapter */
    var nearByMeUserAdapter: NearByMeUsersAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null

    var nearByMeArray: ArrayList<NearByMeUsersData>? = null

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
        setContentView(R.layout.activity_near_by_me)

        /** array initialization */
        nearByMeArray = ArrayList()
        nearByMeArray!!.clear()

        /** initialize implementation */
        nearByMePresenter = NearByMeImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get data to post */
        getDataToPost()


    }

    /** It will call postDataToServer function */
    private fun getDataToPost() {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                postDataToServer(pageNo, PAGE_SIZE)
            }
        }

        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It call onRequestNearByMeData function of near by me presenter */
    private fun postDataToServer(pageNo: Int, pageSize: Int)
    {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                nearByMePresenter!!.onRequestNearByMeData(
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

    /** ui listeners */
    private fun initWithListener()
    {
        /** It goes back to previous activity */
        back!!.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        }

        /** It calls loadMore() when scrolling  */
        nearByMeRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = nearByMeRecycler!!.getChildCount()
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
                    this@NearByMeActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(this@NearByMeActivity)) {
                                loadMore()
                            }
                        }
                    })
                    // Do something
                    loading = true
                }

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

                postDataToServerOnLoadMore(++page)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onRequestNearByMeDataOnLoadMore function of nearbyMe presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int)
    {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {

                nearByMePresenter!!.onRequestNearByMeDataOnLoadMore(this, pageNo, PAGE_SIZE
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
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of recycler view */
        nearByMeRecycler = findViewById(R.id.nearByMeRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        nearByMeRecycler!!.setHasFixedSize(true)
        nearByMeRecycler!!.layoutManager = linearLayoutManager


        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

    }


    /** It checks the nearByMeUsers data,
     *  if it is not empty then set NearByMeUsersAdapter
     */
    override fun setNearByMeDataAdapter(nearByMeUsersData: NearByMeUsersPojo) {


        if (nearByMeUsersData.data.size == 0) {


            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "No Nearby Users found."

            try {
                val sweetAlertDialog = SweetAlertDialog(this , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "No Nearby Users found."
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

            nearByMeArray!!.addAll(nearByMeUsersData.data)

            println("notification list ${nearByMeUsersData.data}")

            nearByMeUserAdapter = NearByMeUsersAdapter(this, nearByMeArray!!)
            nearByMeRecycler!!.adapter = nearByMeUserAdapter


        }

    }

    /** It checks the nearByMeUsers data,
     *  if it is not empty then notify to NearByMeUsersAdapter
     */
    override fun setNearByMeDataAdapterOnLoadMore(nearByMeUsersData: NearByMeUsersPojo) {

        if (nearByMeUsersData.data.size > 0) {

            nearByMeArray!!.addAll(nearByMeUsersData.data)

            nearByMeUserAdapter!!.notifyDataSetChanged()
        }



    }

    /** It opens  EditProfileActivity */
    override fun goToNextScreen()
    {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
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
        nearByMeArray!!.clear()
    }


    /** It opens DashboardActivity */
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
