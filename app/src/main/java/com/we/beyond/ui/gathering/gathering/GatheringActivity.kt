package com.we.beyond.ui.gathering.gathering

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.JsonObject
import com.we.beyond.adapter.GatheringAdapter
import com.we.beyond.adapter.GatheringCriteriaAdapter
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnGoingListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.Interface.OnSubscribeListener
import com.we.beyond.R
import com.we.beyond.model.GatheringData
import com.we.beyond.model.GatheringListPojo
import com.we.beyond.presenter.gathering.gatheringByIssueId.GatheringImpl
import com.we.beyond.presenter.gathering.gatheringByIssueId.GatheringPresenter
import com.we.beyond.presenter.going.going.GoingImpl
import com.we.beyond.presenter.going.going.GoingPresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.presenter.subscribe.SubscribeImpl
import com.we.beyond.presenter.subscribe.SubscribePresenter
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/** This Activity shows the future gathering details  */
class GatheringActivity : AppCompatActivity(), GatheringPresenter.IGatheringView,
    GoingPresenter.IGoingView, OnGoingListener, OnDeleteListener, OnSubscribeListener, SubscribePresenter.ISubscribeView
    , OnReportAbuseListener, ReportAbusePresenter.IReportAbuseView{

    /** initialize respected implementors */
    var context: Context = this
    var gatheringPresenter: GatheringImpl? = null
    var goingPresenter: GoingImpl? = null
    var subscribePresenter : SubscribeImpl?=null
    var reportAbusePresenter : ReportAbuseImpl?=null


    /** init image view */
    var back: ImageView? = null
    var close : ImageView?=null


    /** init text view */
    var title: TextView? = null
    var noContent : TextView?=null

    /** init button */
    var create: Button? = null
    var abuse : Button?=null

    /** init edit text */
    var abuseEditText : EditText?=null

    /** init relative layout */
    var abuseLayout : RelativeLayout?=null


    /** init recycler view and its adapter */
    var gatheringCriteriaRecycler: RecyclerView? = null
    var gatheringRecycler: RecyclerView? = null

    var linearLayoutManager: LinearLayoutManager? = null
    var gatheringCriteriaAdapter: GatheringCriteriaAdapter? = null
    var gatheringAdapter: GatheringAdapter? = null


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
    var currentDate: String = ""
    var startDate: String = ""
    var endDate: String = ""
    var gatheringId: String = ""

    var isCreate : Boolean = false

    companion object {
        val PAGE_SIZE = 5
    }

    /** initialize array list */
    var gatheringArray: ArrayList<GatheringData>? = null
    var gatheringCriteria: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gathering)

        /** It hides the keyboard */
        ConstantMethods.hideKeyBoard(this, this)

        /** array initialization */
        gatheringArray = ArrayList()
        gatheringArray!!.clear()

        gatheringCriteria = ArrayList()
        gatheringCriteria!!.add(" All ")
        gatheringCriteria!!.add("Upcoming")
        gatheringCriteria!!.add("Past")
        // gatheringCriteria!!.add("Next month")

        /** initialize implementation */
        gatheringPresenter = GatheringImpl(this)
        goingPresenter = GoingImpl(this)
        subscribePresenter = SubscribeImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** set connect category adapter */
        setGatheringCriteria()
//        gatheringCriteriaAdapter!!.setSelected(0)
//        (gatheringCriteriaRecycler!!.adapter).setS
        /** post Data to server */
        getCurrentDateGathering()

        /** initialize onclick listener */
        initWithListener()


    }

    /** It opens abuse layout with animation */
    override fun ReportAbuse(_id: String, s: String)
    {
        gatheringId = _id
        abuseLayout!!.visibility = View.VISIBLE
        abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_in_up))

    }


    override fun onSuccess()
    {

    }

    /** It takes the id as input and call postDataToServerOnSubscribe function */
    override fun onSubscribe(_id: String)
    {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@GatheringActivity)) {
                postDataToServerOnSubscribe(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onSubscribe function of subscribe presenter */
    private fun postDataToServerOnSubscribe(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                subscribePresenter!!.onSubscribe(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** It takes id as input and delete gathering data from array and notify to adapter
     */
    override fun setOnDelete(_id: String)
    {
        val deleteGatheringList = ArrayList<GatheringData>()
        deleteGatheringList.addAll(gatheringArray!!)

        val gatheringId = _id
        for( i in 0 until deleteGatheringList.size)
        {

            if(deleteGatheringList[i]._id == gatheringId)
            {
                deleteGatheringList.remove(deleteGatheringList[i])
                gatheringArray!!.clear()
                gatheringArray!!.addAll(deleteGatheringList)
                gatheringAdapter!!.notifyDataSetChanged()

                break
            }
        }

    }

    /** It calls postDataToServerOnDelete function with json object as parameter
     */
    override fun onDelete(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@GatheringActivity)) {
                postDataToServerOnDelete(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onDelete function of gathering presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")

                gatheringPresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception)

        {
            e.printStackTrace()
        }


    }




    /** It takes the id as input and call postDataToServerOnGoing function */
    override fun onGoing(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@GatheringActivity)) {
                postDataToServerOnGoing(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the id as input and call postDataToServerOnNotGoing function */
    override fun onNoGoing(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            try {
                postDataToServerOnNotGoing(jsonObject)

            } catch (e: Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onGoing function of going presenter */
    private fun postDataToServerOnGoing(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                goingPresenter!!.onGoing(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onGoing function of going presenter */
    private fun postDataToServerOnNotGoing(jsonObject: JsonObject) {

        try {

            try {
                if (ConstantMethods.checkForInternetConnection(this)) {
                   // ConstantMethods.showProgessDialog(this, "Please Wait...")

                    goingPresenter!!.onGoing(this, jsonObject)
                }
            } catch (e: Exception)

            {
                e.printStackTrace()
            }


        } catch (e: Exception) {

            e.printStackTrace()
        }


    }


    /** get current date from system and calls postDataToServer() */
    fun getCurrentDateGathering() {
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
        val d = Date()
        var dayOfTheWeek = sdf.format(d)
        val startDateRange=sdf.parse("1950-01-01 00:00:00");
        var startDayOfWeek=sdf.format(startDateRange)
        println("date of week $dayOfTheWeek")

        //today
        currentDate = ConstantMethods.convertDateStringToServerDateTodayFull(startDayOfWeek)
        startDate=currentDate
        println("current date $currentDate")

        postDataToServer(pageNo, currentDate)


    }

    /** set gathering criteria adapter */
    private fun setGatheringCriteria() {
        gatheringCriteriaAdapter = GatheringCriteriaAdapter(context, gatheringCriteria!!)
        gatheringCriteriaRecycler!!.adapter = gatheringCriteriaAdapter
    }


    /** get date and time from selection and convert to server formatted date and time
     * and calls postDataToServerWithCriteria()
     */
    fun getDateAndTime() {
        try {
            println("gathering id $gatheringId")
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            when (gatheringId) {

                "All" -> {
                    val d = Date()
                    val startDateRange=sdf.parse("1950-01-01");
                    var dayOfTheWeek = sdf.format(d)
                    var startDayOfWeek=sdf.format(startDateRange)
                    val calendar = Calendar.getInstance()

                    println("date of week $dayOfTheWeek")

                    //today
                    /*startDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeek + " 00:00:00")
                    endDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeek + " 23:59:59")*/
                    startDate= ConstantMethods.convertDateStringToServerDateTodayFull(startDayOfWeek + " 00:00:00")
                    endDate=""
                    println("start date today  $startDate $endDate")

                    postDataToServerWithCriteria(pageNo, startDate, endDate)
                    gatheringAdapter!!.notifyDataSetChanged()

                }
                "Upcoming" -> {

                    //tomorrow
                    val calendar = Calendar.getInstance()
                    val today = calendar.time
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                    val tomorrow = calendar.time

                    var dayOfTheWeekTomorrow = sdf.format(tomorrow)
                    val startDateRange=sdf.parse("1950-01-01");
                    var startDayOfWeek=sdf.format(startDateRange)
                    println("date of week $dayOfTheWeekTomorrow")
                    val d = Date()
                    var dayOfTheWeek = sdf.format(d)

                    startDate=ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeek + " 00:00:00")
                    /*startDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeekTomorrow + " 00:00:00")*/
                    /*endDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeekTomorrow + " 23:59:59")*/
                    endDate=""

                    println("start date tomorrow $startDate $endDate")

                    postDataToServerWithCriteria(pageNo, startDate, endDate)
                    gatheringAdapter!!.notifyDataSetChanged()


                }
                /* "Next month" -> {
                     //next month
                     val calendarNextMonth = Calendar.getInstance()
                     calendarNextMonth.time = Date()
                     calendarNextMonth.add(Calendar.MONTH, 1)
                     calendarNextMonth.add(
                         Calendar.DAY_OF_MONTH,
                         calendarNextMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
                     )
                     calendarNextMonth.set(Calendar.DAY_OF_MONTH, 1)
                     val nextMonth = calendarNextMonth.time

                     var dayOfTheWeekNextMonth = sdf.format(nextMonth)

                     println("date of week $dayOfTheWeekNextMonth")


                      endDate =
                         ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeekNextMonth + " 00:00:00")


                     val calendarNextMonthStart = Calendar.getInstance()
                     calendarNextMonthStart.time = Date()
                     calendarNextMonthStart.add(Calendar.MONTH, 1)
                     calendarNextMonthStart.set(Calendar.DAY_OF_MONTH, 1)
                     val nextMonthStart = calendarNextMonthStart.time

                     var dayOfTheWeekNextMonthStrat = sdf.format(nextMonthStart)

                     println("date of week $dayOfTheWeekNextMonthStrat")


                      startDate=
                         ConstantMethods.convertDateStringToServerDateTodayFull(
                             dayOfTheWeekNextMonthStrat + " 00:00:00"
                         )

                     println("start date tomorrow $startDate $endDate")

                     postDataToServerWithCriteria(pageNo, startDate, endDate)


                 }*/
                "Past" -> {
                    //week end
                    val calendarNextWeekStart = Calendar.getInstance()
                    calendarNextWeekStart.time = Date()
                    calendarNextWeekStart.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
                    val weekStart = calendarNextWeekStart.time

                    var dayOfTheWeekStart = sdf.format(weekStart)

                    println("date of week $dayOfTheWeekStart")

                    /*startDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeekStart + " 00:00:00")*/

                    println("week end $startDate")


                    val calendarNextWeekEnd = Calendar.getInstance()
                    calendarNextWeekEnd.time = Date()
                    calendarNextWeekEnd.add(Calendar.WEEK_OF_MONTH, 1)
                    calendarNextWeekEnd.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                    val weekEnd = calendarNextWeekEnd.time
                    val d = Date()
                    var dayOfTheWeek = sdf.format(d)
                    var dayOfTheWeekEnd = sdf.format(weekEnd)
                    val startDateRange=sdf.parse("1950-01-01");
                    var startDayOfWeek=sdf.format(startDateRange)
                    println("date of week $dayOfTheWeekEnd")

                   /* endDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeekEnd + " 00:00:00")*/
                    startDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(startDayOfWeek + " 00:00:00")
                    endDate=ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeek + " 00:00:00")

                    println("week end $endDate")

                    println("ween end $startDate $endDate")

                    postDataToServerWithCriteria(pageNo, startDate, endDate)
                    gatheringAdapter!!.notifyDataSetChanged()

                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes below input and send to getGatheringList function of gathering presenter */
    private fun postDataToServer(pageNo: Int, startDate: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                gatheringPresenter!!.getGatheringList(this, pageNo, PAGE_SIZE, startDate)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes below input and send to getGatheringWithCriteriaList function of gathering presenter */
    private fun postDataToServerWithCriteria(pageNo: Int, startDate: String, endDate: String?) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                gatheringPresenter!!.getGatheringWithCriteriaList(
                    this,
                    pageNo,
                    PAGE_SIZE,
                    startDate,
                    endDate!!
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes below input and send to getGatheringListOnLoadMore function of gathering presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int, startDate: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                gatheringPresenter!!.getGatheringListOnLoadMore(
                    this, pageNo,
                    PAGE_SIZE, startDate
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes below input and send to postDataToServerWithCriteriaOnLoadMore function of gathering presenter */
    private fun postDataToServerWithCriteriaOnLoadMore(
        pageNo: Int,
        startDate: String,
        endDate: String?
    ) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {

                gatheringPresenter!!.getGatheringWithCriteriaListOnLoadMore(
                    this,
                    pageNo,
                    PAGE_SIZE,
                    startDate,
                    endDate!!
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It checks the gathering  data,
     *  if it is not empty then set GatheringAdapter
     *  else show warning dialog
     */
    override fun setGatheringAdapter(gatheringPojo: GatheringListPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (gatheringPojo.data.size == 0) {

           /* ConstantMethods.showWarning(
                this,
                "",
                "There are no upcoming gatherings currently in this area."
            )*/

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no upcoming gatherings currently in this area."



            try {
                val sweetAlertDialog = SweetAlertDialog(this , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "There are no upcoming gatherings currently in this area."
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

            gatheringArray!!.addAll(gatheringPojo.data)

            println("near by issue $gatheringArray")

            gatheringAdapter = GatheringAdapter(this, gatheringArray!!)
            gatheringRecycler!!.adapter = gatheringAdapter


        }

    }

    /** It checks the gathering data,
     *  if it is not empty then notify to GatheringAdapter
     */
    override fun setGatheringLoadMoreAdapter(gatheringPojo: GatheringListPojo) {

        if (gatheringPojo.data.size > 0) {
            println("size of near by issue ${gatheringPojo.data.size}")


            gatheringArray!!.addAll(gatheringPojo.data)

            println("near by issue $gatheringArray")

            gatheringAdapter!!.notifyDataSetChanged()
        }


    }

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        }


        /** Call load more api when scrolling the recycler view */
        gatheringRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = gatheringRecycler!!.getChildCount()
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
                    this@GatheringActivity.runOnUiThread(object : Runnable {
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

        /**It opens NearByIssueActivity */
        create!!.setOnClickListener {
            isCreate = true
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("gathering", true)
            startActivityForResult(intent,1)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)


        }

        /** It closes the abuse layout with animation */
        close!!.setOnClickListener {
            abuseLayout!!.visibility = View.GONE
            abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))
        }

        /** It closes the abuse layout with animation and
         * calls the getDataToPostOnAbuse function
         */
        abuse!!.setOnClickListener {
            try {
                abuseLayout!!.visibility = View.GONE
                abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))

                if (ConstantMethods.checkForInternetConnection(this@GatheringActivity)) {
                    getDataToPostOnAbuse()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It calls again getCurrentDateGathering api when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            resetValue()
//            getCurrentDateGathering()
            postDataToServerWithCriteriaOnLoadMore(page, startDate, endDate)
            pullToRefresh!!.isRefreshing = false
        }




    }

    /** It calls postDataToServerOnAbuse function with json object */
    private fun getDataToPostOnAbuse()
    {
        try{

            if(abuseEditText!!.text.trim() !=null && abuseEditText!!.text.trim().isNotEmpty())
            {
                var abuse = abuseEditText!!.text.trim()

                if (ConstantMethods.checkForInternetConnection(this@GatheringActivity)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "gathering")
                    jsonObject.addProperty("typeId",gatheringId )
                    jsonObject.addProperty(
                        "data", ""+abuse
                    )

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerOnAbuse(jsonObject)
                        }
                    }
                    catch (e : Exception)
                    {
                        e.printStackTrace()
                    }

                    println("post data $jsonObject")


                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onReportAbuse function of report abuse presenter */
    private fun postDataToServerOnAbuse(jsonObject: JsonObject)
    {
        try{
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It calls postDataToServerWithCriteriaOnLoadMore function for loading data */
    private fun loadMore() {
        try {

            println("gathering id $gatheringId")

            when (gatheringId) {

                "All" -> {


                    postDataToServerWithCriteriaOnLoadMore(++page, startDate, endDate)

                }
                "Upcoming" -> {

                    postDataToServerWithCriteriaOnLoadMore(++page, startDate, endDate)


                }
                "Next month" -> {
                    //next month

                    postDataToServerWithCriteriaOnLoadMore(++page, startDate, endDate)


                }
                "Past" -> {
                    //week end

                    postDataToServerWithCriteriaOnLoadMore(++page, startDate, endDate)

                }
                else -> {

                    postDataToServerOnLoadMore(++page, currentDate)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**It goes back to DashboardActivity */
    override fun goToPreviousScreen() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    /** ui initialization */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        close = findViewById(R.id.img_close)

        /** ids of text view */
        title = findViewById(R.id.txt_gathering_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of button */
        create = findViewById(R.id.btn_create_gathering)
        create!!.typeface = ConstantFonts.raleway_semibold

        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold

        /** ids of recycler view */
        gatheringCriteriaRecycler = findViewById(R.id.gatheringCriteriaRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        gatheringCriteriaRecycler!!.setHasFixedSize(true)
        gatheringCriteriaRecycler!!.layoutManager = linearLayoutManager


        gatheringRecycler = findViewById(R.id.gatheringRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        gatheringRecycler!!.setHasFixedSize(true)
        gatheringRecycler!!.layoutManager = linearLayoutManager

        /** ids of relative layout */
        abuseLayout = findViewById(R.id.reportAbuseLayout)

        /** ids of edit text */
        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

    }

    /** It get image url which get from camera or gallery */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == 201)

        {
            println("gathering lost ${gatheringArray!!.size}")
            val deleteGatheringList = ArrayList<GatheringData>()
            deleteGatheringList.addAll(gatheringArray!!)


            println("gathering list ${gatheringArray!!.size}")
            for( i in 0 until deleteGatheringList.size)
            {

                if(deleteGatheringList[i]._id == gatheringId)
                {
                    deleteGatheringList.remove(deleteGatheringList[i])
                    gatheringArray!!.clear()
                    gatheringArray!!.addAll(deleteGatheringList)
                    gatheringAdapter!!.notifyDataSetChanged()

                    break
                }
            }

        }
        else if (requestCode ==1 && resultCode== Activity.RESULT_OK)
        {
            try{
                getCurrentDateGathering()
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }

        else if (requestCode == 200 && resultCode == 503)
        {
            val gatheringId = data!!.getStringExtra("gatheringId")
            val isGathering = data.getBooleanExtra("isSubscribe",false)

            val subscribeGatheringList = ArrayList<GatheringData>()
            subscribeGatheringList.addAll(gatheringArray!!)

            for( i in 0 until subscribeGatheringList.size) {
                if (subscribeGatheringList[i]._id == gatheringId) {
                    subscribeGatheringList[i].isSubscribe = isGathering
                    gatheringArray!!.clear()
                    gatheringArray!!.addAll(subscribeGatheringList)
                    gatheringAdapter!!.notifyDataSetChanged()

                    break
                }

            }
        }

        else if(requestCode == 200 && resultCode == 203)
        {
            val goingGatheringList = ArrayList<GatheringData>()
            goingGatheringList.addAll(gatheringArray!!)


            var isGoing = data!!.getBooleanExtra("gatheringGoing",false)
            var isNotGoing = data!!.getBooleanExtra("gatheringIsNotGoing",false)
            println("is going $isGoing")

            val gatheringId = data.getStringExtra("gatheringId")

            if(isGoing) {
                for (i in 0 until gatheringArray!!.size) {
                    if (gatheringArray!![i]._id == gatheringId) {
                        goingGatheringList[i].isGoing = true
                        gatheringArray!!.clear()
                        gatheringArray!!.addAll(goingGatheringList)
                        gatheringAdapter!!.notifyDataSetChanged()

                        println("gathering list after going ${gatheringArray!!.size}")
                        break
                    }
                }
            }

            if(isNotGoing){
                for (i in 0 until gatheringArray!!.size) {
                    if (gatheringArray!![i]._id == gatheringId) {
                        goingGatheringList[i].isGoing = false
                        gatheringArray!!.clear()
                        gatheringArray!!.addAll(goingGatheringList)
                        gatheringAdapter!!.notifyDataSetChanged()

                        println("gathering list after not going ${gatheringArray!!.size}")
                        break
                    }
                }
            }
        }




    }


    /** It resumes the adapter */
    override fun onResume() {


       if(isCreate)
       {

            resetValue()

            /** set connect category adapter */
            setGatheringCriteria()

            /** post Data to server */
            getCurrentDateGathering()
        }
        else{

        }

        super.onResume()

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
        EasySP.init(this).remove("gatheringId")
        gatheringArray!!.clear()

    }

    /** It goes back to previous activity or fragment   */
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }
}
