package com.we.beyond.ui.dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.we.beyond.adapter.NotificationAdapter
import com.we.beyond.R
import com.we.beyond.model.NotificationData
import com.we.beyond.model.NotificationPojo
import com.we.beyond.presenter.dashboard.NotificationImpl
import com.we.beyond.presenter.dashboard.NotificationPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
/**
 * It shows all notifications list
 * */
class NotificationActivity : AppCompatActivity() , NotificationPresenter.INotificationView {

    /** initialize implementors */
    var notificationPresenter: NotificationImpl? = null

    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var noContent : TextView?=null

    /** init recycler view and its adapter */
    var notiticationRecycler: RecyclerView? = null

    var notificationAdapter: NotificationAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null

    /** init array list */
    var notificationArray: ArrayList<NotificationData>? = null

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
        setContentView(R.layout.activity_notification)

        /** array initialization */
        notificationArray = ArrayList()
        notificationArray!!.clear()

        /** initialize implementation */
        notificationPresenter = NotificationImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get data to post */
        getDataToPost()

        /** post server notification state */
        postNotificationState()
    }

    /** It calls onRequestNotificationFalse function of notification presenter */
    private fun postNotificationState()
    {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                notificationPresenter!!.onRequestNotificationFalse(
                    this
                )
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls postDataToServer function */
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

    /** It calls onRequestNotificationData function of notification presenter */
    private fun postDataToServer(pageNo: Int, pageSize: Int)
    {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                notificationPresenter!!.onRequestNotificationData(
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

        /** It goes back to previous fragment or activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        }

        /** Call load more api when scrolling the recycler view */
        notiticationRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = notiticationRecycler!!.getChildCount()
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
                    this@NotificationActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(this@NotificationActivity)) {
                                loadMore()
                            }
                        }
                    })
                    // Do something
                    loading = true
                }

            }
        })

        /** It calls again getDataToPost api when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            resetValue()
            getDataToPost()
            pullToRefresh!!.isRefreshing = false
        }

    }

    /** It calls postDataToServerOnLoadMore function for loading more data */
    private fun loadMore() {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {

                postDataToServerOnLoadMore(++page)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls onRequestNotificationDataOnLoadMore function of notification presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int)
    {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {

                notificationPresenter!!.onRequestNotificationDataOnLoadMore(this, pageNo, PAGE_SIZE
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
        notiticationRecycler = findViewById(R.id.notificationRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        notiticationRecycler!!.setHasFixedSize(true)
        notiticationRecycler!!.layoutManager = linearLayoutManager


        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

    }

    /** It checks the notification data,
     *  if it is not empty then set NotificationAdapter
     *  else it show warning dialog
     */
    override fun setNotificationDataAdapter(notificationData: NotificationPojo) {


        if (notificationData.data.size == 0) {


            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "No notification found."

            try {
                val sweetAlertDialog = SweetAlertDialog(this , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "No notification found."
//                sweetAlertDialog.show()
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

            notificationArray!!.addAll(notificationData.data)

            println("notification list ${notificationData.data}")

            notificationAdapter = NotificationAdapter(this, notificationArray!!)
            notiticationRecycler!!.adapter = notificationAdapter


        }

    }

    /** It checks the notification data,
     *  if it is not empty then notify to NotificationAdapter
     */
    override fun setNotificationDataAdapterOnLoadMore(notificationData: NotificationPojo) {

        if (notificationData.data.size > 0) {

            notificationArray!!.addAll(notificationData.data)

            notificationAdapter!!.notifyDataSetChanged()
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
        notificationArray!!.clear()
    }
}
