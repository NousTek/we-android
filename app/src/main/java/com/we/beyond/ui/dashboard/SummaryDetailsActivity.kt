package com.we.beyond.ui.dashboard

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
import com.we.beyond.adapter.NewInfoPublishedAdapter
import com.we.beyond.adapter.ResolvedIssuesAdapter
import com.we.beyond.adapter.UnresolvedIssuesAdapter
import com.we.beyond.adapter.UpcomingGatheringAdapter
import com.we.beyond.Interface.OnGoingListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.Interface.OnSubscribeListener
import com.we.beyond.R
import com.we.beyond.model.*
import com.we.beyond.presenter.dashboard.DashBoardSummaryDetailsPresenter
import com.we.beyond.presenter.dashboard.DashboardSummaryDetailsImpl
import com.we.beyond.presenter.going.going.GoingImpl
import com.we.beyond.presenter.going.going.GoingPresenter
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.presenter.subscribe.SubscribeImpl
import com.we.beyond.presenter.subscribe.SubscribePresenter
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP

/**
 * It will show all categories such as posted Issues, Issue Resolved, Gatherings near by , Published Articles  */
class SummaryDetailsActivity : AppCompatActivity(),
    DashBoardSummaryDetailsPresenter.IDashBoardSummaryDetailsView, LikePresenter.ILikeView,
    OnLikeDislikeListener, GoingPresenter.IGoingView, OnGoingListener,
    OnSubscribeListener, SubscribePresenter.ISubscribeView, OnReportAbuseListener,
    ReportAbusePresenter.IReportAbuseView {

    /** initialize respected implementors */
    var summaryPresenter: DashboardSummaryDetailsImpl? = null
    var likePresenter: LikeImpl? = null
    var subscribePresenter: SubscribeImpl? = null
    var reportAbusePresenter: ReportAbuseImpl? = null
    var goingPresenter: GoingImpl? = null
    var context: Context = this

    /** init image view */
    var back: ImageView? = null
    var close : ImageView?=null

    /** init text view */
    var title: TextView? = null
    var noContent : TextView?=null

    /** init button */
    var abuse : Button?=null

    /** init edit text */
    var abuseEditText : EditText?=null

    /** init recycler view */
    var summaryRecycler: RecyclerView? = null

    /** init progress layout */
    var progressBar: ProgressBar? = null

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

    /** init relative layout */
    var abuseLayout : RelativeLayout?=null

    /** pull to refresh layout */
    var pullToRefresh : SwipeRefreshLayout?=null

    /** init adapter */
    var unresolvedIssuesAdapter: UnresolvedIssuesAdapter? = null
    var resolvedIssuesAdapter: ResolvedIssuesAdapter? = null
    var upcomingGatheringAdapter: UpcomingGatheringAdapter? = null
    var newPublishedAdapter: NewInfoPublishedAdapter? = null

    companion object {
        val PAGE_SIZE = 5
    }

    /** init strings */
    var summeryType: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var typeSummary: String = ""
    var issueId  : String =""
    var abuseOn : String =""

    /** init array list */
    var unresolvedIssuesArrayList: ArrayList<NearByIssues>? = null
    var resolvedIssuesArrayList: ArrayList<NearByIssues>? = null
    var upcomingGatheringArrayList: ArrayList<GatheringData>? = null
    var newPublishedArrayList: ArrayList<ConnectList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary_details)

        /** It hides the keyboard */
        ConstantMethods.hideKeyBoard(this, this)

        /** array initialization */
        unresolvedIssuesArrayList = ArrayList()
        unresolvedIssuesArrayList!!.clear()
        resolvedIssuesArrayList = ArrayList()
        resolvedIssuesArrayList!!.clear()
        upcomingGatheringArrayList = ArrayList()
        upcomingGatheringArrayList!!.clear()
        newPublishedArrayList = ArrayList()
        newPublishedArrayList!!.clear()

        summaryPresenter = DashboardSummaryDetailsImpl(this)
        likePresenter = LikeImpl(this)
        subscribePresenter = SubscribeImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)
        goingPresenter = GoingImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()


        /** initialize onclick listener */
        initWithListener()


        /** get data to post */
        getDataToPost()


    }

    override fun onSuccess()
    {

    }


    /** It create json object
     * and call postDataToServerOnGoing function
     */
    override fun onGoing(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this)) {
                postDataToServerOnGoing(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It create json object
     * and call postDataToServerOnNotGoing function
     */
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
                ConstantMethods.showProgessDialog(this, "Please Wait...")
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
                    ConstantMethods.showProgessDialog(this, "Please Wait...")

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


    /** It create json object
     * and call postDataToServerOnSubscribe function
     */
    override fun onSubscribe(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                postDataToServerOnSubscribe(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It opens abuse layout with animation */
    override fun ReportAbuse(_id: String, type: String)
    {
        issueId = _id
        abuseOn = type
        abuseLayout!!.visibility = View.VISIBLE
        abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_in_up))

    }


    /** It takes the json object as input and send to onSubscribe function of subscribe presenter */
    private fun postDataToServerOnSubscribe(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                subscribePresenter!!.onSubscribe(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It assigns type to variable  */
    override fun summaryType(type: String) {
        typeSummary = type
    }


    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", typeSummary)
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                postDataToServerOnLike(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onLike function of like dislike  presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                likePresenter!!.onLike(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the id as input and call postDataToServerOnDislike function */
    override fun onDislike(_id: String) {

        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", typeSummary)
            jsonObject.addProperty("typeId", _id)


            try {
                postDataToServerOnDislike(jsonObject)

            } catch (e: Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It opens normal dialog to ask down vote and
     * It takes the json object as input and send to onLike function of like  presenter */
    private fun postDataToServerOnDislike(jsonObject: JsonObject) {

        try {
            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = ""
            sweetAlertDialog.contentText = "Do you want to DownVote?"
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()


                try {

                    if (ConstantMethods.checkForInternetConnection(this)) {
                        ConstantMethods.showProgessDialog(this, "Please Wait...")
                        likePresenter!!.onLike(this, jsonObject)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }


            sweetAlertDialog.setCancelClickListener {
                sweetAlertDialog.dismissWithAnimation()
            }


        } catch (e: Exception) {
                e.printStackTrace()

        }


    }

    /** It will get stored data and type, and call function depends on summary type of corresponding presenters
     */
    private fun getDataToPost() {

        summeryType = intent.getStringExtra(Constants.DASHBOARD_SUMMARY)
        latitude = EasySP.init(this).getString(ConstantEasySP.DASHBOARD_LATITUDE)
        longitude = EasySP.init(this).getString(ConstantEasySP.DASHBOARD_LONGITUDE)

        if (latitude != null && latitude.isNotEmpty()) {
            if (longitude != null && longitude.isNotEmpty()) {
                if (summeryType.equals(Constants.UNRESOLVED_ISSUES, ignoreCase = true)) {

                    title!!.text = "Issues Posted"
                    if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                            ConstantMethods.showProgessDialog(this, "Please Wait...")
                        summaryPresenter!!.getUnresolvedIssues(
                            this,
                            latitude,
                            longitude,
                            pageNo,
                            PAGE_SIZE
                        )
                    }
                } else if (summeryType.equals(Constants.RESOLVED_ISSUES, ignoreCase = true)) {
                    title!!.text = "Issues Resolved"
                    if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                            ConstantMethods.showProgessDialog(this, "Please Wait...")
                        summaryPresenter!!.getResolvedIssues(
                            this,
                            latitude,
                            longitude,
                            pageNo,
                            PAGE_SIZE
                        )
                    }
                } else if (summeryType.equals(Constants.UPCOMING_GATHERING, ignoreCase = true)) {
                    title!!.text = "Upcoming Gatherings"
                    if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                            ConstantMethods.showProgessDialog(this, "Please Wait...")
                        summaryPresenter!!.getUpcomingGathering(
                            this,
                            latitude,
                            longitude,
                            pageNo,
                            PAGE_SIZE
                        )
                    }
                } else if (summeryType.equals(Constants.PUBLISH_CONNECT, ignoreCase = true)) {
                    title!!.text = "Connect Published"
                    if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                            ConstantMethods.showProgessDialog(this, "Please Wait...")
                        summaryPresenter!!.getNewPublished(
                            this,
                            pageNo,
                            PAGE_SIZE
                        )
                    }
                }

            }
        } else {

        }


    }

    private fun initWithListener() {

        /** It goes back to previous fragment or activity */
        back!!.setOnClickListener {
            /* val intent = Intent(this, DashboardActivity::class.java)
             startActivity(intent)*/
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        /** It calls loadMore() when scrolling  */
        summaryRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = summaryRecycler!!.getChildCount()
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
                    this@SummaryDetailsActivity.runOnUiThread(object : Runnable {
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


        /** It closes more option layout with animation and */
        close!!.setOnClickListener {
            abuseLayout!!.visibility = View.GONE
            abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))
        }

        /** It closes the abuse layout with animation and call getDataToPostOnAbuse() */
        abuse!!.setOnClickListener {
            try {
                abuseLayout!!.visibility = View.GONE
                abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))

                if (ConstantMethods.checkForInternetConnection(this)) {
                    getDataToPostOnAbuse()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /** It calls again getDataToPost function when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            resetValue()
            getDataToPost()
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

                if (ConstantMethods.checkForInternetConnection(this)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", abuseOn)
                    jsonObject.addProperty("typeId",issueId )
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
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It calls load more functions with json object depends on summary type  */
    private fun loadMore() {
        if (summeryType.equals(Constants.UNRESOLVED_ISSUES, ignoreCase = true)) {
            if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                summaryPresenter!!.getUnresolvedIssuesOnLoadMore(
                    this, latitude, longitude, ++page,
                    PAGE_SIZE
                )
            }
        } else if (summeryType.equals(Constants.RESOLVED_ISSUES, ignoreCase = true)) {
            if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                summaryPresenter!!.getResolvedIssuesOnLoadMore(
                    this,
                    latitude,
                    longitude,
                    ++pageNo,
                    PAGE_SIZE
                )
            }
        } else if (summeryType.equals(Constants.UPCOMING_GATHERING, ignoreCase = true)) {
            if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                summaryPresenter!!.getUpcomingGatheringOnLoadMore(
                    this,
                    latitude,
                    longitude,
                    ++pageNo,
                    PAGE_SIZE
                )
            }
        } else if (summeryType.equals(Constants.PUBLISH_CONNECT, ignoreCase = true)) {
            if (ConstantMethods.checkForInternetConnection(this@SummaryDetailsActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                summaryPresenter!!.getNewPublishedOnLoadMore(
                    this,
                    ++pageNo,
                    PAGE_SIZE
                )
            }
        }

    }

    /** It checks the unresolvedIssue data,
     *  if it is not empty then set UnresolvedIssuesAdapter
     *  else show warning dialog
     */
    override fun setUnresolvedIssueData(unresolvedIssues: NearByIssuesPojo) {
        if (unresolvedIssues.data.size == 0) {
          /*  ConstantMethods.showWarning(
                this,
                "",
                "There are no issues found in this area in past 7 days."
            )*/

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no issues found in this area in past 30 days."

            try {
                val sweetAlertDialog = SweetAlertDialog(this , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "There are no issues found in this area in past 30 days."
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

            unresolvedIssuesArrayList!!.addAll(unresolvedIssues.data)


            unresolvedIssuesAdapter = UnresolvedIssuesAdapter(this, unresolvedIssuesArrayList!!)
            summaryRecycler!!.adapter = unresolvedIssuesAdapter

        }

    }


    /** It checks the unresolvedIssue data,
     *  if it is not empty then notify to UnresolvedIssuesAdapter
     */
    override fun setUnresolvedIssueDataOnLoadMore(unresolvedIssues: NearByIssuesPojo) {
        if (unresolvedIssues.data.size > 0) {

            unresolvedIssuesArrayList!!.addAll(unresolvedIssues.data)

            unresolvedIssuesAdapter!!.notifyDataSetChanged()
        }

    }

    /** It checks the resolvedIssue data,
     *  if it is not empty then set ResolvedIssuesAdapter
     *  else show warning dialog
     */
    override fun setResolvedIssueData(resolvedIssues: NearByIssuesPojo) {
        if (resolvedIssues.data.size == 0) {

           /* ConstantMethods.showWarning(
                this,
                "",
                "There are no issues resolved in this area in past 7 days."
            )*/

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no issues resolved in this area in past 30 days."

            try {
                val sweetAlertDialog = SweetAlertDialog(this , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "There are no issues resolved in this area in past 30 days."
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

            resolvedIssuesArrayList!!.addAll(resolvedIssues.data)


            resolvedIssuesAdapter = ResolvedIssuesAdapter(this, resolvedIssuesArrayList!!)
            summaryRecycler!!.adapter = resolvedIssuesAdapter

        }

    }

    /** It checks the resolvedIssue data,
     *  if it is not empty then notify to ResolvedIssuesAdapter
     */
    override fun setResolvedIssueDataOnLoadMore(resolvedIssues: NearByIssuesPojo) {
        if (resolvedIssues.data.size > 0) {

            resolvedIssuesArrayList!!.addAll(resolvedIssues.data)

            resolvedIssuesAdapter!!.notifyDataSetChanged()
        }

    }

    /** It checks the gatheringList data,
     *  if it is not empty then set UpcomingGatheringAdapter
     *  else show warning dialog
     */
    override fun setUpcomingGatheringData(gatheringList: GatheringListPojo) {

        if (gatheringList.data.size == 0) {
          /*  ConstantMethods.showWarning(
                this,
                "",
                "There are no gatherings found in this area in upcoming 7 days."
            )*/

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no gatherings found in this area in upcoming 30 days."


            try {
                val sweetAlertDialog = SweetAlertDialog(this , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "There are no gatherings found in this area in upcoming 30 days."
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

            upcomingGatheringArrayList!!.addAll(gatheringList.data)

            upcomingGatheringAdapter = UpcomingGatheringAdapter(this, upcomingGatheringArrayList!!)
            summaryRecycler!!.adapter = upcomingGatheringAdapter

        }

    }

    /** It checks the gatheringList data,
     *  if it is not empty then notify to UpcomingGatheringAdapter
     */
    override fun setUpcomingGatheringDataOnLoadMore(gatheringList: GatheringListPojo) {
        if (gatheringList.data.size > 0) {

            upcomingGatheringArrayList!!.addAll(gatheringList.data)


            upcomingGatheringAdapter!!.notifyDataSetChanged()
        }

    }


    /** It checks the publishList data,
     *  if it is not empty then set NewInfoPublishedAdapter
     *  else show warning dialog
     */
    override fun setNewPublishedData(newPublishedList: ConnectPojo) {

        if (newPublishedList.data.size == 0) {
          /*  ConstantMethods.showWarning(
                this,
                "",
                "There are no published connects found in this area in past 7 days."
            )
*/

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no published connects found in this area in past 30 days."



            try {
                val sweetAlertDialog = SweetAlertDialog(this , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "There are no published connects found in this area in past 30 days."
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

            newPublishedArrayList!!.addAll(newPublishedList.data)


            newPublishedAdapter = NewInfoPublishedAdapter(this, newPublishedArrayList!!)
            summaryRecycler!!.adapter = newPublishedAdapter

        }

    }

    /** It checks the publishList data,
     *  if it is not empty then notify to NewInfoPublishedAdapter
     */
    override fun setNewPublishedDataOnLoadMore(newPublishedList: ConnectPojo) {
        if (newPublishedList.data.size > 0) {

            newPublishedArrayList!!.addAll(newPublishedList.data)

            newPublishedAdapter!!.notifyDataSetChanged()
        }

    }


    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image view */
        back = findViewById(R.id.img_back)
        close = findViewById(R.id.img_close)


        /** ids of text view */
        title = findViewById(R.id.txt_title)
        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of button */

        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold

        /** ids of relative layout */
        abuseLayout = findViewById(R.id.reportAbuseLayout)

        /** ids of recycler view */
        summaryRecycler = findViewById(R.id.summeryDetailsRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        summaryRecycler!!.setHasFixedSize(true)
        summaryRecycler!!.layoutManager = linearLayoutManager


        /** ids of edit text */
        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)


    }

    /** It will notify respected adapter depends on conditions */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200 && resultCode == 201) {
            if (data!!.getStringExtra(Constants.DASHBOARD_SUMMARY).equals(
                    Constants.UNRESOLVED_ISSUES,
                    ignoreCase = true
                )
            ) {
                val deleteIssueList = ArrayList<NearByIssues>()
                deleteIssueList.addAll(unresolvedIssuesArrayList!!)

                val issueId = data.getStringExtra("issueId")

                for (i in 0 until deleteIssueList.size) {

                    if (deleteIssueList[i]._id == issueId) {
                        deleteIssueList.remove(deleteIssueList[i])
                        unresolvedIssuesArrayList!!.clear()
                        unresolvedIssuesArrayList!!.addAll(deleteIssueList)
                        unresolvedIssuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }
            } else if (data.getStringExtra(Constants.DASHBOARD_SUMMARY).equals(
                    Constants.RESOLVED_ISSUES,
                    ignoreCase = true
                )
            ) {
                val deleteIssueList = ArrayList<NearByIssues>()
                deleteIssueList.addAll(resolvedIssuesArrayList!!)

                val issueId = data.getStringExtra("issueId")

                for (i in 0 until deleteIssueList.size) {

                    if (deleteIssueList[i]._id == issueId) {
                        deleteIssueList.remove(deleteIssueList[i])
                        resolvedIssuesArrayList!!.clear()
                        resolvedIssuesArrayList!!.addAll(deleteIssueList)
                        resolvedIssuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }


            } else if (data.getStringExtra(Constants.DASHBOARD_SUMMARY).equals(
                    Constants.UPCOMING_GATHERING,
                    ignoreCase = true
                )
            ) {
                val deleteGatheringList = ArrayList<GatheringData>()
                deleteGatheringList.addAll(upcomingGatheringArrayList!!)

                val issueId = data.getStringExtra("gatheringId")

                for (i in 0 until deleteGatheringList.size) {

                    if (deleteGatheringList[i]._id == issueId) {
                        deleteGatheringList.remove(deleteGatheringList[i])
                        upcomingGatheringArrayList!!.clear()
                        upcomingGatheringArrayList!!.addAll(deleteGatheringList)
                        upcomingGatheringAdapter!!.notifyDataSetChanged()

                        break
                    }
                }

            } else if (data.getStringExtra(Constants.DASHBOARD_SUMMARY).equals(
                    Constants.PUBLISH_CONNECT,
                    ignoreCase = true
                )
            ) {
                val deleteConnectList = ArrayList<ConnectList>()
                deleteConnectList.addAll(newPublishedArrayList!!)

                val issueId = data.getStringExtra("connectId")

                for (i in 0 until deleteConnectList.size) {

                    if (deleteConnectList[i]._id == issueId) {
                        deleteConnectList.remove(deleteConnectList[i])
                        newPublishedArrayList!!.clear()
                        newPublishedArrayList!!.addAll(deleteConnectList)
                        newPublishedAdapter!!.notifyDataSetChanged()

                        break
                    }
                }

            } else if (requestCode == 200 && resultCode == 501) {
                val isLike = data.getBooleanExtra("isLikeByUser",false)
                val isDisLike = data.getBooleanExtra("isDislikeByUser",false)
                val issueId = data.getStringExtra("issueId")


                if(isLike) {
                    val likeIssueList = ArrayList<NearByIssues>()
                    likeIssueList.addAll(unresolvedIssuesArrayList!!)



                    for (i in 0 until likeIssueList.size) {

                        if (likeIssueList[i]._id == issueId) {
                            likeIssueList[i].likeByUser = isLike
                            unresolvedIssuesArrayList!!.clear()
                            unresolvedIssuesArrayList!!.addAll(likeIssueList)
                            unresolvedIssuesAdapter!!.notifyDataSetChanged()

                            break
                        }
                    }
                 }
                    else{
                    val likeIssueList = ArrayList<NearByIssues>()
                    likeIssueList.addAll(unresolvedIssuesArrayList!!)



                    for (i in 0 until likeIssueList.size) {

                        if (likeIssueList[i]._id == issueId) {
                            likeIssueList[i].likeByUser = !isDisLike
                            unresolvedIssuesArrayList!!.clear()
                            unresolvedIssuesArrayList!!.addAll(likeIssueList)
                            unresolvedIssuesAdapter!!.notifyDataSetChanged()

                            break
                        }
                    }
                }

            } else if (requestCode == 200 && resultCode == 502) {
                val isLike = data.getBooleanExtra("isLikeByUser",false)
                val isDisLike = data.getBooleanExtra("isDislikeByUser",false)
                val issueId = data.getStringExtra("issueId")


                if(isLike) {
                    val likeIssueList = ArrayList<NearByIssues>()
                    likeIssueList.addAll(resolvedIssuesArrayList!!)



                    for (i in 0 until likeIssueList.size) {

                        if (likeIssueList[i]._id == issueId) {
                            likeIssueList[i].likeByUser = isLike
                            resolvedIssuesArrayList!!.clear()
                            resolvedIssuesArrayList!!.addAll(likeIssueList)
                            resolvedIssuesAdapter!!.notifyDataSetChanged()

                            break
                        }
                    }
                }
                else{
                    val likeIssueList = ArrayList<NearByIssues>()
                    likeIssueList.addAll(resolvedIssuesArrayList!!)

                    for (i in 0 until likeIssueList.size) {

                        if (likeIssueList[i]._id == issueId) {
                            likeIssueList[i].likeByUser = !isDisLike
                            resolvedIssuesArrayList!!.clear()
                            resolvedIssuesArrayList!!.addAll(likeIssueList)
                            resolvedIssuesAdapter!!.notifyDataSetChanged()

                            break
                        }
                    }
                }
            }
        }
            else if (requestCode == 200 && resultCode == 503)
            {
                val gatheringId = data!!.getStringExtra("gatheringId")
                val isGathering = data.getBooleanExtra("isSubscribe",false)

                val subscribeGatheringList = ArrayList<GatheringData>()
                subscribeGatheringList.addAll(upcomingGatheringArrayList!!)

                for( i in 0 until subscribeGatheringList.size) {
                    if (subscribeGatheringList[i]._id == gatheringId) {
                        subscribeGatheringList[i].isSubscribe = isGathering
                        upcomingGatheringArrayList!!.clear()
                        upcomingGatheringArrayList!!.addAll(subscribeGatheringList)
                        upcomingGatheringAdapter!!.notifyDataSetChanged()

                        break
                    }

                }
        }

        else if(requestCode == 200 && resultCode== Activity.RESULT_OK) {
            val issueId = data!!.getStringExtra("issueId")
            val reportedResolution = data.getBooleanExtra("reportedResolution", false)
            val reportedResolutionCount = data.getIntExtra("reportedCount", 0)

            if (reportedResolution) {
                val likeIssueList = ArrayList<NearByIssues>()
                likeIssueList.addAll(unresolvedIssuesArrayList!!)



                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].resolved = false
                        likeIssueList[i].resolutionsCount = reportedResolutionCount
                        unresolvedIssuesArrayList!!.clear()
                        unresolvedIssuesArrayList!!.addAll(likeIssueList)
                        unresolvedIssuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }
            } else {
                val likeIssueList = ArrayList<NearByIssues>()
                likeIssueList.addAll(unresolvedIssuesArrayList!!)

                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].resolved = true
                        unresolvedIssuesArrayList!!.clear()
                        unresolvedIssuesArrayList!!.addAll(likeIssueList)
                        unresolvedIssuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }
            }
        }

        else if (requestCode == 200 && resultCode == 205)
        {

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
        page = 1
        pageNo = 1
        unresolvedIssuesArrayList!!.clear()
        resolvedIssuesArrayList!!.clear()
        upcomingGatheringArrayList!!.clear()
        newPublishedArrayList!!.clear()
    }


    /** It goes back to previous activity */
    override fun onBackPressed() {
        super.onBackPressed()
        /*val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)*/
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
