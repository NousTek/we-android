package com.we.beyond.ui.issues.nearByIssue


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.adapter.CommentAdapter
import com.we.beyond.adapter.LinkedGatheringAdapter
import com.we.beyond.adapter.ResolvedIssueUserAdapter
import com.we.beyond.Interface.*
import com.we.beyond.R
import com.we.beyond.model.*
import com.we.beyond.presenter.comments.commentById.CommentsImpl
import com.we.beyond.presenter.comments.commentById.CommentsPresenter
import com.we.beyond.presenter.comments.createComment.CreateCommentImpl
import com.we.beyond.presenter.comments.createComment.CreateCommentPresenter
import com.we.beyond.presenter.issues.nearByIssues.NearByIssueByIdImpl
import com.we.beyond.presenter.issues.nearByIssues.NearByIssueByIdPresenter
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.presenter.reportResolved.MarkAsResolvedImpl
import com.we.beyond.presenter.reportResolved.MarkAsResolvedPresenter
import com.we.beyond.presenter.reportResolved.ReportResolvedImpl
import com.we.beyond.presenter.reportResolved.ReportResolvedPresenter
import com.we.beyond.ui.HeatMapActivity
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.MediaViewPagerActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.dashboard.NotificationActivity
import com.we.beyond.ui.dashboard.SummaryDetailsActivity
import com.we.beyond.ui.gathering.createGathering.CreateGatheringActivity
import com.we.beyond.ui.issues.submitIssue.SubmitAnIssueActivity
import com.we.beyond.ui.profile.MyIssuesActivity
import com.we.beyond.ui.reportResolved.ReportResolvedActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.Exception
import kotlin.collections.ArrayList

/**
 * It shows the issue details near by location which perform further actions
 */
class NearByIssueDetailsActivity : AppCompatActivity(),
    NearByIssueByIdPresenter.INearByIssueByIdView, CommentsPresenter.ICommentsByIdView,
    CreateCommentPresenter.ICreateCommentView, LikePresenter.ILikeView, OnLikeDislikeListener,
    OnIssuesResolvedListener, MarkAsResolvedPresenter.IMarkAsResolvedView, ReportResolvedPresenter.IReportResolvedView,
    OnItemClickListener, ReportAbusePresenter.IReportAbuseView, OnReportAbuseListener,
    OnEditListener {


    /** initialize respected implementors */
    var context: Context = this
    var nearByIssieIdPresenter: NearByIssueByIdImpl? = null
    var commentPresenter: CommentsImpl? = null
    var createCommentPresenter: CreateCommentImpl? = null
    var likeDislikePresenter: LikeImpl? = null
    var markAsResolvedPresenter: MarkAsResolvedImpl? = null
    var nearByIssuesData: NearByIssueByIdDetailsPojo? = null
    var reportResolvedPresenter: ReportResolvedImpl? = null
    var reportAbusePresenter: ReportAbuseImpl? = null


    /** init image view */
    var back: ImageView? = null
    var profilePic: CircleImageView? = null
    var issueImage: ImageView? = null
    var commentImage: ImageView? = null
    var sendComment: ImageView? = null
    var gatheringImage: ImageView? = null
    var close: ImageView? = null
    var more: ImageView? = null
    var like: ImageView? = null
    var dislike: ImageView? = null
    var resolved: ImageView? = null
    var unResolved: ImageView? = null
    var notResolved : ImageView?=null
    var closeAbuse: ImageView? = null
    var closeComment: ImageView? = null
    var play: ImageView? = null


    /** init text view */
    var title: TextView? = null
    var issueNumber: TextView? = null
    var issueTitle: TextView? = null
    var issueLocation: TextView? = null
    var issueDistance: TextView? = null
    var name: TextView? = null
    var time: TextView? = null
    var issueDescription: TextView? = null
    var commentCount: TextView? = null
    var reportResolvedCount: TextView? = null
    var linkedGatheringTitle: TextView? = null
    var categoryTitle: TextView? = null
    var resolvedFlag: TextView? = null
    var share : TextView?=null
    var moreTitle: TextView? = null
    var getDirections: TextView? = null
    var delete: TextView? = null
    var reportAbuse: TextView? = null
    var edit: TextView? = null
    var supportText: TextView? = null
    var resolvedText: TextView? = null
    var commentTitle: TextView? = null
    var gatheringTitle: TextView? = null
    var moreTitleText: TextView? = null


    /** init recycler view */
    var commentRecycler: RecyclerView? = null
    var gatheringRecycler: RecyclerView? = null
    var userTypeRecycler: RecyclerView? = null

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
    var linearLayoutManager1: LinearLayoutManager? = null
    var linearLayoutManager2: LinearLayoutManager? = null

    /** init adapters */
    var commentsAdapter: CommentAdapter? = null
    var userTypeAdapter: ResolvedIssueUserAdapter? = null

    /** init array list */
    var commentArray: ArrayList<CommentsData>? = null
    var userTypeList: ArrayList<String>? = null
    var userPojo: ArrayList<UserList>? = null
    var tagsList : ArrayList<String>?=null
    var tagUserName : ArrayList<String>?=null
    var gatheringDataArray: ArrayList<Gatherings>? = null

    /** init relative layout */
    var operationLayout: RelativeLayout? = null
    var commentLayout: RelativeLayout? = null
    var linkedGatheringLayout: RelativeLayout? = null
    var moreLayout: RelativeLayout? = null
    var mainLayout: RelativeLayout? = null
    var backgroundLayout: RelativeLayout? = null
    var reportAbuseLayout: RelativeLayout? = null
    var locationLayout : RelativeLayout?=null


    /** init scroll view */
    var scrollView: ScrollView? = null

    var linkedGatheringAdapter: LinkedGatheringAdapter? = null


    /* */
    /** init text input layout *//*
    var commentTextLayout : TextInputLayout?=null*/

    /** init edit text */
    var comment: EditText? = null
    var abuseEditText: EditText? = null

    /** init button */
    var abuse: Button? = null

    /** pull to refresh layout */
    var pullToRefresh : SwipeRefreshLayout?=null

    /** init strings */
    var issueId: String? = null
    var commentId: String =""
    var userId: String = ""
    var searchText : String =""
    var allText : String = ""
    var getDistance : String?=null

    /** init booleans */
    var commentIssue: Boolean = false
    var unresolvedIssue: Boolean = false
    var resolvedIssue: Boolean = false
    var isIssueResolved: Boolean = false
    var approvedResolution: Boolean = false
    var editComment: Boolean = false
    var myIssues: Boolean = false
    var likeByUser: Boolean = false
    var dislikeByUser : Boolean = false
    var heatMap : Boolean = false
    var notification : Boolean = false
    var reportedResolution : Boolean = false

    /** init int */
    var commentPosition: Int? = null
    var getIssueNumber: Int? = null
    var reportedResolutionCount : Int ?=null
    var count = 0

    /** init double */
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var rootRef: DatabaseReference

    companion object {
        val PAGE_SIZE = 5
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** It hides the keyboard */
        ConstantMethods.hideKeyBoard(this, this@NearByIssueDetailsActivity)

        setContentView(R.layout.activity_near_by_issue_details)

        /** array initialization */
        commentArray = ArrayList()
        commentArray!!.clear()

        userTypeList = ArrayList()
        userTypeList!!.clear()
        userPojo = ArrayList()

        gatheringDataArray = ArrayList()
        gatheringDataArray!!.clear()

        tagsList = ArrayList()
        tagUserName = ArrayList()

        /** initialize implementation */
        nearByIssieIdPresenter = NearByIssueByIdImpl(this)
        commentPresenter = CommentsImpl(this)
        createCommentPresenter = CreateCommentImpl(this)
        likeDislikePresenter = LikeImpl(this)
        markAsResolvedPresenter = MarkAsResolvedImpl(this)
        reportResolvedPresenter = ReportResolvedImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get data */
        getData()

        /** get current location */
        getLocation()

        /** Get all stored data using intent and assign it respectively */
        if (getIntent() != null && getIntent().getData() != null)
        {
            val data = getIntent().getData()
            val path =data!!.path
            issueId = path!!.replace("/issues/","",ignoreCase = true)


        }
        else {
            issueId = intent.getStringExtra("issueId")
        }
        getIssueNumber = intent.getIntExtra("issueNumber", 0)
        commentIssue = intent.getBooleanExtra("comment", false)
        unresolvedIssue = intent.getBooleanExtra("unresolvedIssue", false)
        resolvedIssue = intent.getBooleanExtra("resolvedIssue", false)
        myIssues = intent.getBooleanExtra("myIssues", false)
        heatMap = intent.getBooleanExtra("heatmap",false)
        notification = intent.getBooleanExtra("notification",false)
        getDistance = intent.getStringExtra("distance")

        /** It opens keyboard for comment  */
        if (commentIssue)
        {
            operationLayout!!.visibility = View.GONE
            commentLayout!!.visibility = View.VISIBLE
            comment!!.isCursorVisible = true
            comment!!.requestFocus()
            backgroundLayout!!.visibility = View.VISIBLE
            ConstantMethods.showKeyBoard(this)

        }

        /** If issue id is not empty then call onNearByIssueById function of near by issue id presenter
         * and onCommentsById function of comment presenter
         */
        if (issueId != null && issueId!!.isNotEmpty()) {
            resetValue()

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {

                    commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /** It checks the issue number if it is not zero then set title text to text view */
        if (getIssueNumber != null && getIssueNumber != 0) {
            title = findViewById(R.id.txt_title)
            title!!.text = "Issue #$getIssueNumber"
            title!!.typeface = ConstantFonts.raleway_semibold
        }


    }

    /** If issue id is not empty then call onNearByIssueById function of near by issue id presenter
     * and onCommentsById function of comment presenter
     */
    override fun goToNextScreen() {

        if (issueId != null && issueId!!.isNotEmpty()) {
            resetValue()

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    commentArray!!.clear()
                    commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    /** It show operation layout
     * hides keyboard
     * and call onCommentsById function of comment presenter
     */
    override fun onUpdateSuccessfully() {
        try {
            resetValue()

            operationLayout!!.visibility = View.VISIBLE
            commentLayout!!.visibility = View.GONE
            comment!!.isCursorVisible = true
            backgroundLayout!!.visibility = View.GONE
            userTypeRecycler!!.visibility = View.GONE


            ConstantMethods.hideKeyBoard(this, this@NearByIssueDetailsActivity)

            if (ConstantMethods.checkForInternetConnection(context)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** It is used to edit the comment */
    override fun OnEdit(_id: String, commentText: String) {
        operationLayout!!.visibility = View.GONE
        commentLayout!!.visibility = View.VISIBLE
        comment!!.isCursorVisible = true
        comment!!.requestFocus()
        backgroundLayout!!.visibility = View.VISIBLE
        ConstantMethods.showKeyBoard(this)
        comment!!.setText(commentText)

        editComment = true
        commentId = _id

    }

    /** It sets the comment text and add user name to tag user name array */
    override fun OnClick(userName: String, userId: String) {

        val editText = comment!!.text.toString()
        val newText = editText.replace(searchText,"")


        comment!!.setText("${newText}${userName.trim()}")
        comment!!.setSelection(comment!!.text.length)

        tagUserName!!.add(userName)
        tagsList!!.add(userId)

        userTypeRecycler!!.visibility = View.GONE


    }

    /** It show and hide the layout */
    fun getData() {
        println("comment id reply $commentId")

        if (commentId != null && commentId.isNotEmpty()) {
            operationLayout!!.visibility = View.GONE
            commentLayout!!.visibility = View.VISIBLE
            comment!!.isCursorVisible = true
            comment!!.requestFocus()
            backgroundLayout!!.visibility = View.VISIBLE
        }

    }

    /** If issue id is not empty then call onNearByIssueById function of near by issue id presenter
     * and onCommentsById function of comment presenter
     */
    override fun onResolved() {


        if (issueId != null && issueId!!.isNotEmpty()) {
            resetValue()

            approvedResolution = true

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {

                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun summaryType(type: String)
    {

    }

    /** It takes the id as input and call postDataToServerOnResolved function */
    override fun onApproved(_id: String) {
        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("resolutionId", _id)

            if (ConstantMethods.checkForInternetConnection(this@NearByIssueDetailsActivity)) {
                postDataToServerOnResolved(jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onMarkAsResolvedData function of mark as resolved presenter */
    private fun postDataToServerOnResolved(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                markAsResolvedPresenter!!.onMarkAsResolvedData(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "comment")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@NearByIssueDetailsActivity)) {
                postDataToServerOnLike(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** It takes the id as input and call postDataToServerOnDislike function */
    override fun onDislike(_id: String) {

        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "comment")
            jsonObject.addProperty("typeId", _id)


            try {

                if (ConstantMethods.checkForInternetConnection(context)) {
                    postDataToServerOnDislike(jsonObject)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It opens abuse layout with animation */
    override fun ReportAbuse(_id: String, s: String) {
        issueId = _id
        reportAbuseLayout!!.visibility = View.VISIBLE
        reportAbuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))

    }


    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity depends on conditions  when click on it */
        back!!.setOnClickListener {

            ConstantMethods.hideKeyBoard(this, this@NearByIssueDetailsActivity)

            if (approvedResolution) {

                val intent = Intent()
                intent.putExtra("approved", approvedResolution)
                intent.putExtra("issueId", issueId)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            else if (unresolvedIssue)
            {
                if(likeByUser)
                {
                    val intent = Intent(this, SummaryDetailsActivity::class.java)
                    intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UNRESOLVED_ISSUES)
                    intent.putExtra("isLikeByUser", likeByUser)
                    intent.putExtra("issueId", issueId)
                    setResult( 501,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                else if(dislikeByUser)
                {
                    val intent = Intent(this, SummaryDetailsActivity::class.java)
                    intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UNRESOLVED_ISSUES)
                    intent.putExtra("isDislikeByUser", dislikeByUser)
                    intent.putExtra("issueId", issueId)
                    setResult( 501,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                else
                {
                    val intent = Intent(this, SummaryDetailsActivity::class.java)
                    intent.putExtra("issueId", issueId)
                    setResult(205,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
            } else if (resolvedIssue)
            {
                if(likeByUser)
                {
                    val intent = Intent(this, SummaryDetailsActivity::class.java)
                    intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.RESOLVED_ISSUES)
                    intent.putExtra("isLikeByUser", likeByUser)
                    intent.putExtra("issueId", issueId)
                    setResult( 502,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                else if(dislikeByUser)
                {
                    val intent = Intent(this, SummaryDetailsActivity::class.java)
                    intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.RESOLVED_ISSUES)
                    intent.putExtra("isDislikeByUser", dislikeByUser)
                    intent.putExtra("issueId", issueId)
                    setResult( 502,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                else{
                    val intent = Intent(this, SummaryDetailsActivity::class.java)
                    intent.putExtra("issueId", issueId)
                    setResult(205,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
            }
            else if (myIssues)
            {
                if(likeByUser)
                {
                    val intent = Intent(this, MyIssuesActivity::class.java)
                    intent.putExtra("isLikeByUser", likeByUser)
                    intent.putExtra("issueId", issueId)
                    setResult( 501,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                else if(dislikeByUser)
                {
                    val intent = Intent(this, MyIssuesActivity::class.java)
                    intent.putExtra("isDislikeByUser", dislikeByUser)
                    intent.putExtra("issueId", issueId)
                    setResult( 501,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
                else{
                    val intent = Intent(this, MyIssuesActivity::class.java)
                    intent.putExtra("issueId", issueId)
                    setResult(205,intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
            }

            else if (heatMap)
            {
                val intent = Intent(this, HeatMapActivity::class.java)
                // startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if(notification)
            {
                val intent = Intent(this, NotificationActivity::class.java)
                // startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if(likeByUser)
            {
                val intent = Intent(this, NearByIssueActivity::class.java)
                intent.putExtra("comment", false)
                intent.putExtra("isLikeByUser", likeByUser)
                intent.putExtra("issueId", issueId)
                setResult( 501,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()


            }

            else if(dislikeByUser)
            {
                val intent = Intent(this, NearByIssueActivity::class.java)
                intent.putExtra("comment", false)
                intent.putExtra("isDislikeByUser", dislikeByUser)
                intent.putExtra("issueId", issueId)
                setResult( 501,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()


            }

            else if (reportedResolution)
            {
                val intent = Intent(this, NearByIssueActivity::class.java)
                intent.putExtra("comment", false)
                intent.putExtra("reportedResolution", reportedResolution)
                intent.putExtra("reportedCount", reportedResolutionCount)
                intent.putExtra("issueId", issueId)
                setResult( 501,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()

            }

            else  {
                val intent = Intent(this, NearByIssueActivity::class.java)
                intent.putExtra("comment", false)
                intent.putExtra("issueId", issueId)
                setResult(205,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }

        /** Call load more api when scrolling the recycler view */
        commentRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = commentRecycler!!.getChildCount()
                totalItemCount = linearLayoutManager!!.getItemCount()
                firstVisibleItem = linearLayoutManager!!.findFirstVisibleItemPosition()
                if (loading) {

                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }

                }
                if ((!loading && ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)))) {
                    // End has been reached
                    this@NearByIssueDetailsActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(this@NearByIssueDetailsActivity)) {
                                loadMore()
                            }
                        }
                    })
                    // Do something
                    loading = true
                }

            }
        })

        /** It opens comment layout and show keyboard */
        commentImage!!.setOnClickListener {
            operationLayout!!.visibility = View.GONE
            commentLayout!!.visibility = View.VISIBLE
            comment!!.requestFocus()

            comment!!.setText("")
            tagsList!!.clear()

            backgroundLayout!!.visibility = View.VISIBLE
            ConstantMethods.showKeyBoard(this)

        }

        /**
         * It reset the values
         * call getDataToPost function
         * and hides the keyboard
         */
        sendComment!!.setOnClickListener {
            try {

                if (ConstantMethods.checkForInternetConnection(this@NearByIssueDetailsActivity)) {
                    resetValue()
                    getDataToPost()

                    if (commentPosition != null)
                    {

                    }
                    else
                    {
                        scrollView!!.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS)

                    }
                }

                backgroundLayout!!.visibility = View.GONE
                comment!!.clearFocus()
                ConstantMethods.hideKeyBoard(this, this@NearByIssueDetailsActivity)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /**
         * If issue resolved, we can not create new gathering
         * else it sends gathering details to create gathering activity
         */
        gatheringImage!!.setOnClickListener {

            if(nearByIssuesData!!.data.resolved)
            {
                ConstantMethods.showWarning(context,"","You cannot create gathering on resolved issues.")
            }
            else {
                EasySP.init(this).putString(ConstantEasySP.GATHERING_DATE, "")
                EasySP.init(this).putString(ConstantEasySP.SELECTED_GATHERING_ADDRESS, "")
                EasySP.init(this).putString("gatheringTitle", "")
                EasySP.init(this).putString("gatheringDetails", "")

                val intent = Intent(this, CreateGatheringActivity::class.java)
                intent.putExtra("issue", true)
                intent.putExtra("issueId", issueId)
                EasySP.init(this).putString(ConstantEasySP.ISSUE_TITLE, nearByIssuesData!!.data.title)
                startActivityForResult(intent, 1)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        /**It opens more option layout with animation */
        more!!.setOnClickListener {
            moreLayout!!.visibility = View.VISIBLE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))

        }

        /** It closes the more option layout with animation */
        close!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_down))
        }

        /** It closes more option layout with animation and
         * opens google map to get direction using latitude and longitude  */
        getDirections!!.setOnClickListener {
            if (nearByIssuesData!!.data.coordinates != null)
            {
                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_down
                    )
                )

                val uri =
                    "http://maps.google.com/maps?saddr=" + "$latitude" + "," + "$longitude" + "&daddr=" + "${nearByIssuesData!!.data.coordinates[0]}" + "," + "${nearByIssuesData!!.data.coordinates[1]}"
                val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                startActivity(intent)
            }
        }


        /** It closes more option layout with animation and
         * If issue resolved, we can not delete issue
         * else it will show dialog to delete an issue and call postDataToServerOnDelete()
        */
        delete!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_down))

            val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
            if (userId == nearByIssuesData!!.data.user._id) {
                if (nearByIssuesData!!.data.resolved) {
                    ConstantMethods.showWarning(this, "", "You can not delete resolved issues.")

                } else {
                    println("user id ${userId} gathering user ${nearByIssuesData!!.data.user._id}")
                    try {
                        val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "Do you want to delete issue?"
                        sweetAlertDialog.confirmText = "Yes"
                        sweetAlertDialog.cancelText = "No"
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()


                            try {

                                val jsonObject = JsonObject()
                                jsonObject.addProperty("type", "disabled")
                                jsonObject.addProperty("issueId", nearByIssuesData!!.data._id)


                                try {
                                    postDataToServerOnDelete(jsonObject)

                                } catch (e: Exception) {
                                    e.printStackTrace()
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
            } else {
                ConstantMethods.showWarning(this, "", "You are not owner of this issue.")
            }


        }

        /** It changes the like to dislike image, text color and call postDataToServerOnLike() */
        like!!.setOnClickListener {
            likeByUser = true
            dislike!!.visibility = View.VISIBLE
            supportText!!.setTextColor(ContextCompat.getColor(this, R.color.button_background))
            like!!.visibility = View.GONE

            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "issue")
                jsonObject.addProperty("typeId", issueId)


                if (ConstantMethods.checkForInternetConnection(this@NearByIssueDetailsActivity)) {
                    postDataToServerOnLike(jsonObject)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        /** It changes the dislike to like image, text color and call postDataToServerOnDislike() */
        dislike!!.setOnClickListener {
            dislikeByUser = true


            dislike!!.visibility = View.GONE
            like!!.visibility = View.VISIBLE
            supportText!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "issue")
                jsonObject.addProperty("typeId", issueId)


                try {

                    if (ConstantMethods.checkForInternetConnection(context)) {
                        postDataToServerOnDislike(jsonObject)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /**It opens warning dialog */
        unResolved!!.setOnClickListener {
            ConstantMethods.showWarning(this, "Issue Resolved", "Issue already resolved")
        }

        /**It opens ReportResolvedActivity */
        resolved!!.setOnClickListener {
            val intent = Intent(context, ReportResolvedActivity::class.java)
            intent.putExtra("issueId", issueId)
            startActivityForResult(intent, 1)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        /**It opens ReportResolvedActivity */
        notResolved!!.setOnClickListener {
            val intent = Intent(context, ReportResolvedActivity::class.java)
            intent.putExtra("issueId", issueId)
            startActivityForResult(intent, 1)
            overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }


        /**It opens MediaViewPagerActivity */
        issueImage!!.setOnClickListener {

            if (nearByIssuesData!!.data.imageUrls != null && nearByIssuesData!!.data.imageUrls.isNotEmpty() || nearByIssuesData!!.data.videoUrls != null && nearByIssuesData!!.data.videoUrls.isNotEmpty()) {
                val intent = Intent(context, MediaViewPagerActivity::class.java)
                intent.putStringArrayListExtra("mediaImage", nearByIssuesData!!.data.imageUrls)
                intent.putStringArrayListExtra("mediaVideo", nearByIssuesData!!.data.videoUrls)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            } else {
                ConstantMethods.showWarning(this, "No Data", "Images not available for this issue.")
            }
        }


        /** It hides keyboard, comment layout and show operation layout */
        closeComment!!.setOnClickListener {

            ConstantMethods.hideKeyBoard(this, this@NearByIssueDetailsActivity)

            commentLayout!!.visibility = View.GONE
            operationLayout!!.visibility = View.VISIBLE
            backgroundLayout!!.visibility = View.GONE
            userTypeRecycler!!.visibility = View.GONE


        }

        /** It hides keyboard, comment layout and show operation layout */
        commentLayout!!.setOnClickListener {
            ConstantMethods.hideKeyBoard(this, this@NearByIssueDetailsActivity)

            commentLayout!!.visibility = View.GONE
            operationLayout!!.visibility = View.VISIBLE
            backgroundLayout!!.visibility = View.GONE
            userTypeRecycler!!.visibility = View.GONE


        }


        /** It will search the user from user list using '@' character
         * from OnRequestUserListOnSearch function of report resolved presenter  */
        comment!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {


            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                allText = p0.toString()

                allText = p0.toString().replace("(","")

                if(allText.endsWith(")",ignoreCase = true)) {
                    if (allText != null && allText.isNotEmpty()) {
                        val parts = allText.split(")")
                        val first = parts[0]
                        var second = parts[1]

                        allText = first.replace(first, "")
                    }
                }

                allText = allText.replace(")","")


                println("all text $allText")

                    if (p0!!.startsWith("@", ignoreCase = true) && p1 >= 3) {

                        searchText = allText.substring(allText.lastIndexOf("@") + 1)

                        if (searchText != null && searchText.isNotEmpty()) {
                            if (searchText.length >= 3) {
                                try {

                                    if (ConstantMethods.checkForInternetConnection(context)) {
                                        reportResolvedPresenter!!.OnRequestUserListOnSearch(
                                            context,
                                            searchText.trim()
                                        )
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()

                                }
                            }
                        }

                    } else {

                        allText = allText.replace("(","")

                        if(allText.endsWith(")",ignoreCase = true)) {
                            if (allText != null && allText.isNotEmpty()) {
                                val parts = allText.split(")")
                                val first = parts[0]
                                var second = parts[1]

                                allText = first.replace(first, "")
                            }
                        }

                        allText = allText.replace(")","")

                        println("all text $allText")


                        searchText = allText.substring(allText.lastIndexOf("@") + 1)

                        if (searchText != null && searchText.isNotEmpty()) {
                            if (searchText.length >= 3) {
                                try {

                                    if (ConstantMethods.checkForInternetConnection(context)) {
                                        reportResolvedPresenter!!.OnRequestUserListOnSearch(
                                            context,
                                            searchText.trim()
                                        )
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }


                }


                if(p0!!.length==0)
                {
                    tagsList!!.clear()
                    tagUserName!!.clear()
                    userTypeRecycler!!.visibility = View.GONE
                }

                else{
                    for(i in 0 until tagUserName!!.size) {
                        if(!p0.contains(tagUserName!![i],ignoreCase = true))
                        {
                            tagUserName!!.remove(tagUserName!![i])
                            tagsList!!.remove(tagsList!![i])

                            break

                        }
                    }

                }
            }

        })

        /** It closes the more layout with animation
         * get stored user id and check with near by issue user id, if matches then we cannot abuse the issue
         * and if issue is resolved then we cannot abuse the issue
         * else It will open report abuse layout  */
        reportAbuse!!.setOnClickListener {
            try {
                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )

                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == nearByIssuesData!!.data.user._id) {
                    ConstantMethods.showWarning(context, "", "You can not abuse your issue.")
                } else if (nearByIssuesData!!.data.resolved) {
                    ConstantMethods.showWarning(context, "", "You can not abuse resolved issues.")
                } else {

                    reportAbuseLayout!!.visibility = View.VISIBLE
                    reportAbuseLayout!!.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.slide_in_up
                        )
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It closes the abuse layout with animation and call getDataToPostOnAbuse() */
        abuse!!.setOnClickListener {
            try {
                reportAbuseLayout!!.visibility = View.GONE
                reportAbuseLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_down
                    )
                )


                if (ConstantMethods.checkForInternetConnection(this@NearByIssueDetailsActivity)) {
                    getDataToPostOnAbuse()
                }
                // }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /** It closes the report abuse layout with animation */
        closeAbuse!!.setOnClickListener {
            reportAbuseLayout!!.visibility = View.GONE
            reportAbuseLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )
        }

        /** It closes the more layout with animation
         * and opens SubmitAnIssueActivity to edit an issue */
        edit!!.setOnClickListener {

            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )

            val userId = EasySP.init(this).getString(ConstantEasySP.USER_ID)
            if (userId == nearByIssuesData!!.data.user._id) {


                val jsonString = Gson().toJson(nearByIssuesData)
                val intent = Intent(this, SubmitAnIssueActivity::class.java)
                intent.putExtra("issueData", jsonString)
                intent.putExtra("edit", true)
                startActivityForResult(intent, 200)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {

                ConstantMethods.showWarning(this, "", "You are not owner of this issue.")
            }

        }

        /** It closes the more layout with animation */
        moreLayout!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )
        }

        /** It calls again onNearByIssueById function of near by issue id presenter
         * and onCommentsById function of comment presenter when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            if (issueId != null && issueId!!.isNotEmpty()) {
                resetValue()

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {
                        ConstantMethods.showProgessDialog(this, "Please Wait...")
                        nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {

                        commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            pullToRefresh!!.isRefreshing = false
        }

        /**It opens LocationActivity */
        locationLayout!!.setOnClickListener {
            val intent = Intent(context, LocationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            EasySP.init(context).put("lat",nearByIssuesData!!.data.coordinates[0])
            EasySP.init(context).put("long",nearByIssuesData!!.data.coordinates[1])
            EasySP.init(context).putBoolean("resolved",nearByIssuesData!!.data.resolved)
        }


        /** It closes the more layout with animation
         * It will share issue through share intent*/
        share!!.setOnClickListener {

            try {

                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_down
                    )
                )

                val share = Intent(Intent.ACTION_SEND)
                share.setType("text/plain")
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                share.putExtra(Intent.EXTRA_SUBJECT, "WE")
                share.putExtra(
                    Intent.EXTRA_TEXT,
                    "${Constants.BASE_URL}issues/$issueId"
                )
                startActivity(Intent.createChooser(share, "Share Issue!"))
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }


    }

    /** It calls postDataToServerOnAbuse function with json object */
    private fun getDataToPostOnAbuse() {
        try {

            if (abuseEditText!!.text.trim() != null && abuseEditText!!.text.trim().isNotEmpty()) {
                var abuse = abuseEditText!!.text.trim()

                if (ConstantMethods.checkForInternetConnection(this@NearByIssueDetailsActivity)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "issue")
                    jsonObject.addProperty("typeId", issueId)
                    jsonObject.addProperty(
                        "data", "" + abuse
                    )

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerOnAbuse(jsonObject)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onReportAbuse function of report abuse presenter */
    private fun postDataToServerOnAbuse(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It checks the user data,
     *  if it is not empty then set ResolvedIssueUserAdapter
     */
    override fun setUserListAdapter(userList: ArrayList<UserList>) {

        println("user list $userList")
        try {
            userTypeList!!.clear()

            if (userList.isNotEmpty()) {
                for (i in 0 until userList.size) {
                    userTypeList!!.add(userList[i].name)
                    userPojo!!.addAll(userList)

                }
            }

            if (userTypeList!!.isNotEmpty()) {
                userTypeRecycler!!.visibility = View.VISIBLE
                userTypeAdapter = ResolvedIssueUserAdapter(this, userList, searchText.trim())
                userTypeRecycler!!.adapter = userTypeAdapter

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onLike function of like dislike  presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                likeDislikePresenter!!.onLike(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It opens normal dialog to ask down vote and
     * It takes the json object as input and send to onLike function of like dislike  presenter */
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

                    if (ConstantMethods.checkForInternetConnection(context)) {
                        ConstantMethods.showProgessDialog(this, "Please Wait...")
                        likeDislikePresenter!!.onLike(this, jsonObject)
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

    /** It takes the json object as input and send to onDelete function of near by issue id presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject) {

        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                nearByIssieIdPresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    /** It checks the edit comment boolean, if it is true then call postDataToServerUpdateComment()
     * else call postDataToServer() with required json object
     */
    fun getDataToPost() {
        try {
            if (editComment) {
                if (comment!!.text != null && comment!!.text.isNotEmpty()) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("text", (comment!!.text.toString()))
                    jsonObject.addProperty("commentId", commentId)
                    val jsonArray = JsonArray()

                    if(tagsList != null  &&  tagsList!!.isNotEmpty())
                    {
                        for(i in 0 until tagsList!!.size) {
                            jsonArray.add(tagsList!![i])
                        }
                        jsonObject.add("tags",jsonArray)
                    }

                    val jsonArrayName = JsonArray()

                    if(tagUserName != null  &&  tagUserName!!.isNotEmpty())
                    {
                        for(i in 0 until tagUserName!!.size) {
                            jsonArrayName.add(tagUserName!![i])
                        }
                        jsonObject.add("tagNames",jsonArrayName)
                    }


                    commentId = ""

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerUpdateComment(jsonObject)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    ConstantMethods.showWarning(
                        this,
                        "Empty Data",
                        "Please give us your comment."
                    )
                }

            } else {
                if (commentId != null && commentId.isNotEmpty()) {
                    if (comment!!.text != null && comment!!.text.isNotEmpty()) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("issueId", issueId!!)
                        jsonObject.addProperty("text", (comment!!.text.toString()))
                        jsonObject.addProperty("parentId", commentId)
                        commentId = ""

                        val jsonArray = JsonArray()
                        if(tagsList != null  &&  tagsList!!.isNotEmpty())
                        {
                            for(i in 0 until tagsList!!.size) {
                                jsonArray.add(tagsList!![i])
                            }
                            jsonObject.add("tags",jsonArray)
                        }

                        val jsonArrayName = JsonArray()

                        if(tagUserName != null  &&  tagUserName!!.isNotEmpty())
                        {
                            for(i in 0 until tagUserName!!.size) {
                                jsonArrayName.add(tagUserName!![i])
                            }
                            jsonObject.add("tagNames",jsonArrayName)
                        }



                        try {
                            if (ConstantMethods.checkForInternetConnection(context)) {

                                postDataToServer(jsonObject)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        ConstantMethods.showWarning(
                            this,
                            "Empty Data",
                            "Please give us your comment."
                        )
                    }
                } else {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("issueId", issueId)
                    jsonObject.addProperty("text", comment!!.text.toString().trim())

                    val jsonArray = JsonArray()
                    if(tagsList != null  &&  tagsList!!.isNotEmpty())
                    {
                        for(i in 0 until tagsList!!.size) {
                            jsonArray.add(tagsList!![i])
                        }
                        jsonObject.add("tags",jsonArray)
                    }

                    val jsonArrayName = JsonArray()

                    if(tagUserName != null  &&  tagUserName!!.isNotEmpty())
                    {
                        for(i in 0 until tagUserName!!.size) {
                            jsonArrayName.add(tagUserName!![i])
                        }
                        jsonObject.add("tagNames",jsonArrayName)
                    }


                    println("json object $jsonObject")
                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServer(jsonObject)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onCommentCreated function of create comment presenter */
    private fun postDataToServer(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                createCommentPresenter!!.onCommentCreated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onUpdateComment function of comment presenter */
    private fun postDataToServerUpdateComment(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                commentPresenter!!.onUpdateComment(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onNearByIssueById function of near by issue id presenter
     * and onCommentsById function of comment presenter
     * hide keyboard */
    override fun setCommentAdapter() {
        try {
            operationLayout!!.visibility = View.VISIBLE
            commentLayout!!.visibility = View.GONE
            backgroundLayout!!.visibility = View.GONE
            userTypeRecycler!!.visibility = View.GONE
            comment!!.setText("")
            tagsList!!.clear()

            ConstantMethods.hideKeyBoard(this, this@NearByIssueDetailsActivity)

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {

                    nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {

                    commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            ConstantMethods.hideKeyBoard(this, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls postDataToServerOnLoadMore() with json object  */
    private fun loadMore() {
        try {


            if (ConstantMethods.checkForInternetConnection(this@NearByIssueDetailsActivity)) {

                postDataToServerOnLoadMore(issueId!!, "issue", ++page, PAGE_SIZE)
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onCommentsByIdOnLoadMore function of comment presenter */
    private fun postDataToServerOnLoadMore(issueId: String?, s: String, i: Int, pageSize: Int) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                commentPresenter!!.onCommentsByIdOnLoadMore(this, issueId!!, s, i, pageSize)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** ui initialization */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        profilePic = findViewById(R.id.img_profile_pic)
        issueImage = findViewById(R.id.img_issue)
        commentImage = findViewById(R.id.img_comment)
        sendComment = findViewById(R.id.img_send_comment)
        gatheringImage = findViewById(R.id.img_gathering)
        more = findViewById(R.id.img_more)
        close = findViewById(R.id.img_close)
        like = findViewById(R.id.img_support)
        dislike = findViewById(R.id.img_dislike)
        resolved = findViewById(R.id.img_resolved)
        unResolved = findViewById(R.id.img_already_resolved)
        notResolved = findViewById(R.id.img_not_resolved)
        closeAbuse = findViewById(R.id.img_close_window)
        closeComment = findViewById(R.id.img_close_comment)
        play = findViewById(R.id.img_play)

        /** ids of text view */

        issueNumber = findViewById(R.id.txt_issue_number)
        //issueNumber!!.typeface = ConstantFonts.raleway_semibold

        issueTitle = findViewById(R.id.txt_issue_title)
        issueTitle!!.typeface = ConstantFonts.raleway_semibold

        issueLocation = findViewById(R.id.txt_issue_location)
        issueLocation!!.typeface = ConstantFonts.raleway_semibold

        issueDistance = findViewById(R.id.txt_issue_distance)
        issueDistance!!.typeface = ConstantFonts.raleway_semibold

        name = findViewById(R.id.txt_name)
        name!!.typeface = ConstantFonts.raleway_regular

        time = findViewById(R.id.txt_time)
        time!!.typeface = ConstantFonts.raleway_regular

        issueDescription = findViewById(R.id.txt_issue_description)
        issueDescription!!.typeface = ConstantFonts.raleway_medium

        commentCount = findViewById(R.id.txt_comments_count)
        commentCount!!.typeface = ConstantFonts.raleway_semibold

        reportResolvedCount = findViewById(R.id.txt_reported_resolution_Count)
        reportResolvedCount!!.typeface = ConstantFonts.raleway_semibold

        linkedGatheringTitle = findViewById(R.id.txt_linked_gathering_title)
        linkedGatheringTitle!!.typeface = ConstantFonts.raleway_semibold

        categoryTitle = findViewById(R.id.txt_category_title)
        categoryTitle!!.typeface = ConstantFonts.raleway_regular

        resolvedFlag = findViewById(R.id.txt_flag)
        resolvedFlag!!.typeface = ConstantFonts.raleway_regular

        moreTitle = findViewById(R.id.txt_more)
        moreTitle!!.typeface = ConstantFonts.raleway_regular

        share =findViewById(R.id.txt_share)
        share!!.typeface = ConstantFonts.raleway_semibold

        getDirections = findViewById(R.id.txt_get_direction)
        getDirections!!.typeface = ConstantFonts.raleway_semibold

        delete = findViewById(R.id.txt_delete)
        delete!!.typeface = ConstantFonts.raleway_semibold

        reportAbuse = findViewById(R.id.txt_report_abuse)
        reportAbuse!!.typeface = ConstantFonts.raleway_semibold

        edit = findViewById(R.id.txt_edit)
        edit!!.typeface = ConstantFonts.raleway_semibold

        supportText = findViewById(R.id.txt_support)
        supportText!!.typeface = ConstantFonts.raleway_regular


        resolvedText = findViewById(R.id.txt_resolved)
        resolvedText!!.typeface = ConstantFonts.raleway_regular

        commentTitle = findViewById(R.id.txt_comment)
        commentTitle!!.typeface = ConstantFonts.raleway_regular

        gatheringTitle = findViewById(R.id.txt_gathering)
        gatheringTitle!!.typeface = ConstantFonts.raleway_regular

        moreTitleText = findViewById(R.id.txt_more_title)
        moreTitleText!!.typeface = ConstantFonts.raleway_regular


        /** ids of recycler view */
        commentRecycler = findViewById(R.id.commentRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        commentRecycler!!.setHasFixedSize(true)
        commentRecycler!!.layoutManager = linearLayoutManager

        gatheringRecycler = findViewById(R.id.gatheringRecycler)
        linearLayoutManager1 = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        gatheringRecycler!!.setHasFixedSize(true)
        gatheringRecycler!!.layoutManager = linearLayoutManager1

        userTypeRecycler = findViewById(R.id.recycler_resolved_by)
        linearLayoutManager2 = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        userTypeRecycler!!.setHasFixedSize(true)
        userTypeRecycler!!.layoutManager = linearLayoutManager2


        /** ids of relative layout */
        operationLayout = findViewById(R.id.operationLayout)
        commentLayout = findViewById(R.id.commentLayout)
        linkedGatheringLayout = findViewById(R.id.linkedGatheringLayout)
        moreLayout = findViewById(R.id.moreLayout)
        mainLayout = findViewById(R.id.mainLayout)
        backgroundLayout = findViewById(R.id.backgroundLayout)
        reportAbuseLayout = findViewById(R.id.reportAbuseLayout)
        locationLayout = findViewById(R.id.issueDetailsLayout)

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

        /** ids of edit text */
        comment = findViewById(R.id.et_comment)
        comment!!.typeface = ConstantFonts.raleway_regular

        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of scroll view */
        scrollView = findViewById(R.id.nearByIssueDetailsScrollView)

        /** ids of button */
        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold


    }

    /** It checks the commentDetails data,
     *  if it is not empty then set CommentAdapter
     */
    override fun setCommentsByIdAdapter(commentsDetails: CommentsPojo) {

        if (commentsDetails.data.size == 0) {


        } else {

            commentArray!!.clear()

            commentArray!!.addAll(commentsDetails.data)

            if (commentPosition != null) {

                commentsAdapter = CommentAdapter(this, commentArray!!, userId, isIssueResolved)
                commentRecycler!!.adapter = commentsAdapter
                commentRecycler!!.smoothScrollToPosition(commentPosition!!)
                commentRecycler!!.scrollToPosition(commentPosition!! - 2)
                linearLayoutManager!!.scrollToPositionWithOffset(
                    commentPosition!!,
                    commentPosition!!
                )
            } else {
                commentsAdapter = CommentAdapter(this, commentArray!!, userId, isIssueResolved)
                commentRecycler!!.adapter = commentsAdapter
                commentRecycler!!.smoothScrollToPosition(0)
                linearLayoutManager!!.scrollToPositionWithOffset(0, 0)
            }


        }

    }

    /** It checks the commentsDetails data,
     *  if it is not empty then notify to CommentAdapter
     */
    override fun setCommentsByIdAdapterOnLoadMore(commentsDetails: CommentsPojo) {
        if (commentsDetails.data.size > 0) {

            commentArray!!.addAll(commentsDetails.data)

            commentsAdapter!!.notifyDataSetChanged()
        }

    }

    /** It checks the nearByIssueDetails data,
     *  if it is not empty then notify to NearByIssuesAdapter
     */
    override fun setNearByIssueByIdAdapter(nearByIssueDetails: NearByIssueByIdDetailsPojo) {


        try {
            if (nearByIssueDetails.data != null) {

                issueId = nearByIssueDetails.data._id

                nearByIssuesData = nearByIssueDetails

                userId = nearByIssueDetails.data.user._id

                if (nearByIssueDetails.data.likeByUser != null && nearByIssueDetails.data.likeByUser) {
                    like!!.visibility = View.GONE
                    dislike!!.visibility = View.VISIBLE
                    supportText!!.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.button_background
                        )
                    )
                }


                if (nearByIssueDetails.data.issueNumber != null) {
                    issueNumber!!.text = "# ${nearByIssueDetails.data.issueNumber}"
                }

                if (nearByIssueDetails.data.title != null) {
                    issueTitle!!.text = nearByIssueDetails.data.title
                }
                if (nearByIssueDetails.data.address != null) {
                    issueLocation!!.text = nearByIssueDetails.data.address
                }
                if (nearByIssueDetails.data.city != null) {
                    issueLocation!!.text = nearByIssueDetails.data.city
                }
                if (nearByIssueDetails.data.address != null && nearByIssueDetails.data.city != null) {
                    issueLocation!!.text =
                        nearByIssueDetails.data.address + ", ${nearByIssueDetails.data.city}"
                }


                if(getDistance !=null && getDistance!!.isNotEmpty()) {

                        issueDistance!!.text = getDistance + "Km Away"

                }

                if (nearByIssueDetails.data.user.userLoginType.userType.equals(
                        "individual",
                        ignoreCase = true
                    )
                ) {
                    name!!.text =
                        nearByIssueDetails.data.user.firstName + " ${nearByIssueDetails.data.user.lastName}"
                    name!!.typeface = ConstantFonts.raleway_regular
                } else {
                    name!!.text = nearByIssueDetails.data.user.organizationName
                    name!!.typeface = ConstantFonts.raleway_regular

                }


                if (nearByIssueDetails.data.createdAt != null && nearByIssueDetails.data.createdAt.isNotEmpty()) {
                    time!!.text =
                        ConstantMethods.convertStringToDateStringFull(nearByIssueDetails.data.createdAt)
                }

                if (nearByIssueDetails.data.description != null && nearByIssueDetails.data.description.isNotEmpty()) {
                    issueDescription!!.text = nearByIssueDetails.data.description
                }

                if (nearByIssueDetails.data.imageUrls != null && nearByIssueDetails.data.imageUrls.isNotEmpty()) {
                    Glide.with(this)
                        .load(nearByIssueDetails.data.imageUrls[0])
                        .placeholder(R.drawable.loading_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(issueImage!!)

                    play!!.visibility = View.GONE

                } else if (nearByIssueDetails.data.videoUrls != null && nearByIssueDetails.data.videoUrls.isNotEmpty()) {
                    Glide.with(this)
                        .load(nearByIssueDetails.data.videoUrlThumbnails[0])
                        .placeholder(R.drawable.processing_video)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(issueImage!!)

                    play!!.visibility = View.VISIBLE
                }
                else{
                    play!!.visibility = View.GONE
                    issueImage!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.placeholder))
                }

                if (nearByIssueDetails.data.user.profileUrl != null && nearByIssueDetails.data.user.profileUrl.isNotEmpty()) {
                    Picasso.with(this)
                        .load(nearByIssueDetails.data.user.profileUrl)
                        .placeholder(R.drawable.background)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(profilePic)
                }

                commentCount!!.text = "${nearByIssueDetails.data.commentsCount}  C o m m e n t s,"
                reportResolvedCount!!.text =
                    "   ${nearByIssueDetails.data.resolutionsCount}  R e p o r t e d   r e s o l u t i o n"

                if (nearByIssueDetails.data.gatherings != null && nearByIssueDetails.data.gatherings.isNotEmpty()) {
                    gatheringDataArray!!.clear()
                    gatheringDataArray!!.addAll(nearByIssueDetails.data.gatherings)

                    linkedGatheringAdapter =
                        LinkedGatheringAdapter(this, gatheringDataArray!!)
                    gatheringRecycler!!.adapter = linkedGatheringAdapter
                } else {
                    linkedGatheringLayout!!.visibility = View.GONE
                }


                if (nearByIssueDetails.data.category.name != null && nearByIssueDetails.data.category.name.isNotEmpty()) {
                    categoryTitle!!.text = nearByIssueDetails.data.category.name
                    categoryTitle!!.typeface = ConstantFonts.raleway_regular
                }

                if (nearByIssueDetails.data.resolved) {
                    resolvedFlag!!.visibility = View.VISIBLE
                    resolvedFlag!!.text = "Resolved"


                    resolved!!.visibility = View.GONE
                    unResolved!!.visibility = View.VISIBLE
                    notResolved!!.visibility = View.GONE

                    resolvedText!!.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.issue_resolved
                        )
                    )

                    isIssueResolved = true

                }
                else if (!nearByIssueDetails!!.data.resolved && nearByIssueDetails!!.data.resolutionsCount>0)
                {
                    reportedResolution = true
                    reportedResolutionCount = nearByIssueDetails!!.data.resolutionsCount

                    notResolved!!.visibility = View.VISIBLE
                    resolved!!.visibility = View.GONE
                    unResolved!!.visibility = View.GONE
                    resolvedFlag!!.visibility = View.GONE

                    resolvedText!!.setTextColor(ContextCompat.getColor(context,R.color.button_background))
                }

                else {
                    resolvedFlag!!.visibility = View.GONE

                    resolved!!.visibility = View.VISIBLE
                    unResolved!!.visibility = View.GONE
                    notResolved!!.visibility = View.GONE

                    resolvedText!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

                    isIssueResolved = false
                }


                profilePic!!.setOnClickListener {
                    val intent = Intent(context, BadgesActivity::class.java)
                    intent.putExtra(Constants.USER_ID, nearByIssueDetails.data.user._id)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }


                name!!.setOnClickListener {
                    val intent = Intent(context, BadgesActivity::class.java)
                    intent.putExtra(Constants.USER_ID, nearByIssueDetails.data.user._id)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }

                title = findViewById(R.id.txt_title)
                title!!.text = "Issue #${nearByIssueDetails.data.issueNumber}"
                title!!.typeface = ConstantFonts.raleway_semibold


            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        commentArray!!.clear()
    }

    /** It gets the current location using gps provider,
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {

            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f,
                locationListener
            )
            var currentLocation = getLastKnownLocation()
//            latitude = currentLocation.latitude
//            longitude = currentLocation.longitude
            if (currentLocation != null) {
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It returns the last known location  */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        var mLocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.getAccuracy() < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }


    /** It is listener which store the location data
     * to firebase database reference whenever location changes
     */
    private val locationListener = object : LocationListener {
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }

        override fun onLocationChanged(location: Location) {
            try {
                val deviceFcmToken: String =
                    EasySP.init(context).getString(ConstantEasySP.SP_FCM_DEVICE_TOKEN)
                if (deviceFcmToken != null && deviceFcmToken.length > 0) {
                    val latLongPojo =
                        LatLongPojo(location.latitude, location.longitude, deviceFcmToken)
                    rootRef.setValue(latLongPojo)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    /** It call below functions using corresponding presenters */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

                resetValue()
            val issueId = data!!.getStringExtra("issueId")
            val reportedResolution = data.getBooleanExtra("reportedResolution", false)
            val reportedResolutionCount = data.getIntExtra("reportedCount", 0)


            try {

                if (ConstantMethods.checkForInternetConnection(context)) {

                    gatheringDataArray!!.clear()
                    nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {

                    commentArray!!.clear()
                    commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }



        } else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            resetValue()
            val issueId = data!!.getStringExtra("issueId")

            try {

                if (ConstantMethods.checkForInternetConnection(context)) {

                    gatheringDataArray!!.clear()
                    nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {

                    commentArray!!.clear()
                    commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }



            println("start activity result")


        } else if (requestCode == 200 && resultCode == 201) {
            resetValue()

            val issueId = data!!.getStringExtra("issueId")
            val gatheringId = data.getStringExtra("gatheringId")

            if (issueId != null) {

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {

                        nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {
                        commentPresenter!!.onCommentsById(
                            this,
                            issueId,
                            "issue",
                            pageNo,
                            PAGE_SIZE
                        )
                        commentsAdapter!!.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                val deleteGatheringList = ArrayList<Gatherings>()
                deleteGatheringList.addAll(gatheringDataArray!!)

                linkedGatheringTitle!!.visibility = View.VISIBLE

                for (i in 0 until deleteGatheringList.size) {

                    if (deleteGatheringList[i]._id == gatheringId) {
                        deleteGatheringList.remove(deleteGatheringList[i])
                        gatheringDataArray!!.clear()
                        gatheringDataArray!!.addAll(deleteGatheringList)
                        linkedGatheringAdapter!!.notifyDataSetChanged()


                        break
                    }
                }

                if (gatheringDataArray!!.size == 0) {
                    linkedGatheringTitle!!.visibility = View.GONE
                }
            }

        }


    }

    /** It opens activity depends on condition
     */
    override fun setOnDelete() {

        if (approvedResolution) {
            println("approved resolution $approvedResolution")

            val intent = Intent()
            intent.putExtra("approved", approvedResolution)
            intent.putExtra("issueId", issueId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (unresolvedIssue) {
            val intent = Intent(this, SummaryDetailsActivity::class.java)
            intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UNRESOLVED_ISSUES)
            intent.putExtra("delete", true)
            intent.putExtra("issueId", issueId)
            setResult(201, intent)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (resolvedIssue) {
            val intent = Intent(this, SummaryDetailsActivity::class.java)
            intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.RESOLVED_ISSUES)
            intent.putExtra("delete", true)
            intent.putExtra("issueId", issueId)
            setResult(201, intent)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (myIssues) {
            val intent = Intent(this, MyIssuesActivity::class.java)
            intent.putExtra("delete", true)
            intent.putExtra("issueId", issueId)
            setResult(201, intent)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else {
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("comment", false)
            intent.putExtra("delete", true)
            intent.putExtra("issueId", issueId)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
            println("in else")

        }


    }


    /** It goes back to previous activity depends on condition   */
    override fun onBackPressed() {


        ConstantMethods.hideKeyBoard(this, this@NearByIssueDetailsActivity)


        if (approvedResolution) {
            println("approved resolution $approvedResolution")

            val intent = Intent()
            intent.putExtra("approved", approvedResolution)
            intent.putExtra("issueId", issueId)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        else if (unresolvedIssue) {
            if(likeByUser) {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UNRESOLVED_ISSUES)
                intent.putExtra("isLikeByUser", likeByUser)
                intent.putExtra("issueId", issueId)
                setResult( 501,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if(dislikeByUser){
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UNRESOLVED_ISSUES)
                intent.putExtra("isDislikeByUser", dislikeByUser)
                intent.putExtra("issueId", issueId)
                setResult( 501,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else{
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra("issueId", issueId)
                setResult(205,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        } else if (resolvedIssue) {
             if(likeByUser) {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.RESOLVED_ISSUES)
                intent.putExtra("isLikeByUser", likeByUser)
                intent.putExtra("issueId", issueId)
                setResult( 502,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if(dislikeByUser)
             {
                 val intent = Intent(this, SummaryDetailsActivity::class.java)
                 intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.RESOLVED_ISSUES)
                 intent.putExtra("isDislikeByUser", dislikeByUser)
                 intent.putExtra("issueId", issueId)
                 setResult( 502,intent)
                 overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                 finish()
             }
            else{
                 val intent = Intent(this, SummaryDetailsActivity::class.java)
                 intent.putExtra("issueId", issueId)
                 setResult(205,intent)
                 overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                 finish()
             }
        }
        else if (myIssues) {
            if(likeByUser) {
                val intent = Intent(this, MyIssuesActivity::class.java)
                intent.putExtra("isLikeByUser", likeByUser)
                intent.putExtra("issueId", issueId)
                setResult( 501,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if(dislikeByUser)
            {
                val intent = Intent(this, MyIssuesActivity::class.java)
                intent.putExtra("isDislikeByUser", dislikeByUser)
                intent.putExtra("issueId", issueId)
                setResult( 501,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else{
                val intent = Intent(this, MyIssuesActivity::class.java)
                intent.putExtra("issueId", issueId)
                setResult(205,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }

        else if (heatMap)
        {
            val intent = Intent(this, HeatMapActivity::class.java)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if(notification)
        {
            val intent = Intent(this, NotificationActivity::class.java)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if(likeByUser) {
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("comment", false)
            intent.putExtra("isLikeByUser", likeByUser)
            intent.putExtra("issueId", issueId)
            setResult( 501,intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()


        }

        else if(dislikeByUser) {
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("comment", false)
            intent.putExtra("isDislikeByUser", dislikeByUser)
            intent.putExtra("issueId", issueId)
            setResult( 501,intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()


        }
        else if (reportedResolution)
        {
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("comment", false)
            intent.putExtra("reportedResolution", reportedResolution)
            intent.putExtra("reportedCount", reportedResolutionCount)
            intent.putExtra("issueId", issueId)
            setResult( 501,intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        }

        else  {
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("comment", false)
            intent.putExtra("issueId", issueId)
            //setResult(205,intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()


        }

        super.onBackPressed()
    }




}
