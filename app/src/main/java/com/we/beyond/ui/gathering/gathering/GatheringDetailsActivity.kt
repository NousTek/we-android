package com.we.beyond.ui.gathering.gathering

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.we.beyond.adapter.*
import com.we.beyond.Interface.*
import com.we.beyond.R
import com.we.beyond.model.*
import com.we.beyond.presenter.comments.commentById.CommentsImpl
import com.we.beyond.presenter.comments.commentById.CommentsPresenter
import com.we.beyond.presenter.comments.createComment.CreateCommentImpl
import com.we.beyond.presenter.comments.createComment.CreateCommentPresenter
import com.we.beyond.presenter.gathering.gatheringByIssueId.GatheringByIdImpl
import com.we.beyond.presenter.gathering.gatheringByIssueId.GatheringByIdPresenter
import com.we.beyond.presenter.going.going.GoingImpl
import com.we.beyond.presenter.going.going.GoingPresenter
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.presenter.reportResolved.ReportResolvedImpl
import com.we.beyond.presenter.reportResolved.ReportResolvedPresenter
import com.we.beyond.presenter.subscribe.SubscribeImpl
import com.we.beyond.presenter.subscribe.SubscribePresenter
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.MediaViewPagerActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.campaign.createCampaign.CreateCampaignActivity
import com.we.beyond.ui.dashboard.NotificationActivity
import com.we.beyond.ui.dashboard.SummaryDetailsActivity
import com.we.beyond.ui.gathering.createGathering.CreateGatheringActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.ui.profile.MyGatheringActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList

/**
 * It shows the gathering details perform further actions
 */
class GatheringDetailsActivity : AppCompatActivity(), GatheringByIdPresenter.IGatheringByIdView
    , GoingPresenter.IGoingView, OnGoingListener, SubscribePresenter.ISubscribeView,
    ReportAbusePresenter.IReportAbuseView, CommentsPresenter.ICommentsByIdView, OnItemClickListener,
    CreateCommentPresenter.ICreateCommentView, OnEditListener,
    ReportResolvedPresenter.IReportResolvedView, LikePresenter.ILikeView,
    OnLikeDislikeListener, OnReportAbuseListener {

    /** initialize respected implementors */
    var gatheringIdPresenter: GatheringByIdImpl? = null
    var goingPresenter: GoingImpl? = null
    var subscribePresenter: SubscribeImpl? = null
    var reportAbusePresenter: ReportAbuseImpl? = null
    var commentPresenter: CommentsImpl? = null
    var createCommentPresenter: CreateCommentImpl? = null
    var reportResolvedPresenter: ReportResolvedImpl? = null
    var likeDislikePresenter: LikeImpl? = null
    var context: Context = this


    /** init image view */
    var back: ImageView? = null
    var commentImage: ImageView? = null
    var gatheringImage: ImageView? = null
    var campaign: ImageView? = null
    var more: ImageView? = null
    var going: ImageView? = null
    var notGoing: ImageView? = null
    var close: ImageView? = null
    var subscribe: ImageView? = null
    var notSubscribe: ImageView? = null
    var closeAbuse: ImageView? = null
    var closeComment: ImageView? = null
    var sendComment: ImageView? = null
    var divider3:View? =null
    var divider4:View?=null
    /** init text view */
    var title: TextView? = null
    var issueNumber: TextView? = null
    var issueNumberTitle: TextView? = null
    var gatheringTitle: TextView? = null
    var commentTitle: TextView? = null
    var gatheringLocation: TextView? = null
    var gatheringDate: TextView? = null
    var gatheringMonth: TextView? = null
    var gatheringDay: TextView? = null
    var hostedByTitle: TextView? = null
    var hostedByName: TextView? = null
    var gatheringDescription: TextView? = null
    var linkedCampaignTitle: TextView? = null
    var delete: TextView? = null
    var gatheringGoingCount: TextView? = null
    var reportAbuse: TextView? = null
    var edit: TextView? = null
    var goingTitle: TextView? = null
    var subscribeTitle: TextView? = null
    var campaignTitle: TextView? = null
    var moreTitle: TextView? = null
    var share: TextView? = null
    var directions: TextView? = null

    /** init edit text */
    var abuseEditText: EditText? = null
    var comment: EditText? = null

    /** init button */
    var abuse: Button? = null

    /** init relative layout */
    var linkedCampaignLayout: RelativeLayout? = null
    var moreLayout: RelativeLayout? = null
    var gatheringGoingLayout: RelativeLayout? = null
    var reportAbuseLayout: RelativeLayout? = null
    var locationLayout: RelativeLayout? = null
    var backgroundLayout: RelativeLayout? = null
    var operationLayout: LinearLayout? = null
    var commentLayout: RelativeLayout? = null

    /** init recycler view */
    var campaignRecycler: RecyclerView? = null
    var gatheringGoingRecycler: RecyclerView? = null
    var commentRecycler: RecyclerView? = null
    var userTypeRecycler: RecyclerView? = null

    /** init strings */
    var gatheringId: String? = null
    var commentId: String = ""
    var searchText: String = ""
    var allText: String = ""

    /** init layout manager  */
    var linearLayoutManager: LinearLayoutManager? = null
    var linearLayoutManager1: LinearLayoutManager? = null
    var linearLayoutManager2: LinearLayoutManager? = null

    /** init array list */
    var userTypeList: ArrayList<String>? = null
    var userPojo: ArrayList<UserList>? = null
    var userTypeAdapter: ResolvedIssueUserAdapter? = null
    var tagsList: ArrayList<String>? = null
    var tagUserName: ArrayList<String>? = null
    var campaignDataArray: ArrayList<Campaigns>? = null
    var goingArrayList: ArrayList<GatheringGoingList>? = null

    /** init adapter */
    var campaignAdapter: CampaignAdapter? = null
    var gatheringGoingAdapter: GatheringGoingAdapter? = null
    var commentArray: ArrayList<CommentsData>? = null
    var commentsAdapter: GatheringCommentAdapter? = null

    /** pull to refresh layout */
    var pullToRefresh: SwipeRefreshLayout? = null

    var gatheringData: GatheringDetails? = null

    /** init double */
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var rootRef: DatabaseReference

    /** init booleans */
    var nearByGathering: Boolean = false
    var myGathering: Boolean = false
    var isGoing: Boolean = false
    var isNotGoing: Boolean = false
    var isGatheringLinked: Boolean = false
    var isSubscribe: Boolean = false
    var notification: Boolean = false
    var commentIssue: Boolean = false
    var editComment: Boolean = false

    /** init calender instance */
    var myCalendar: Calendar? = null

    /** init int */
    var commentPosition: Int? = null


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
        val PAGE_SIZE = 5
    }

    var scrollView: ScrollView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gathering_details)

        /** It hides the keyboard */
        ConstantMethods.hideKeyBoard(this, this@GatheringDetailsActivity)

        /** calender initialization */
        myCalendar = Calendar.getInstance()

        /** array initialization */
        commentArray = ArrayList()
        commentArray!!.clear()


        campaignDataArray = ArrayList()
        campaignDataArray!!.clear()

        goingArrayList = ArrayList()
        goingArrayList!!.clear()

        userTypeList = ArrayList()
        userTypeList!!.clear()
        userPojo = ArrayList()

        tagsList = ArrayList()

        tagUserName = ArrayList()

        /** initialize implementation */
        commentPresenter = CommentsImpl(this)
        createCommentPresenter = CreateCommentImpl(this)
        gatheringIdPresenter = GatheringByIdImpl(this)
        goingPresenter = GoingImpl(this)
        subscribePresenter = SubscribeImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)
        likeDislikePresenter = LikeImpl(this)
        reportResolvedPresenter = ReportResolvedImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get data */
        getData()

        /** get current location */
        getLocation()

        /** Get all stored data using intent and assign it respectively */
        if (getIntent() != null && getIntent().getData() != null) {
            val data = getIntent().getData()
            val path = data!!.path
            gatheringId = path!!.replace("/gatherings/", "", ignoreCase = true)

        } else {
            gatheringId = intent.getStringExtra("gatheringId")
        }


        println("gathering id $gatheringId")

        nearByGathering = intent.getBooleanExtra("upcomingGathering", false)
        myGathering = intent.getBooleanExtra("myGathering", false)
        isGatheringLinked = intent.getBooleanExtra("linkedGathering", false)
        notification = intent.getBooleanExtra("notification", false)

    }

    /** It opens abuse layout with animation */
    override fun ReportAbuse(_id: String, s: String) {
        gatheringId = _id
        reportAbuseLayout!!.visibility = View.VISIBLE
        reportAbuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))

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


            ConstantMethods.hideKeyBoard(this, this)

            if (ConstantMethods.checkForInternetConnection(context)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                commentPresenter!!.onCommentsById(
                    this,
                    gatheringId!!,
                    "gathering",
                    pageNo,
                    PAGE_SIZE
                )
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
        val newText = editText.replace(searchText, "")


        comment!!.setText("${newText}${userName.trim()}")
        comment!!.setSelection(comment!!.text.length)

        tagUserName!!.add(userName)
        tagsList!!.add(userId)

        userTypeRecycler!!.visibility = View.GONE


    }

    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "comment")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
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

    override fun summaryType(type: String) {

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
            sweetAlertDialog.confirmText = "Yes"
            sweetAlertDialog.cancelText = "No"
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

    /** It create json object
     * and call postDataToServerOnGoing function
     */
    override fun onGoing(_id: String) {
        try {


            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
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
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")

                goingPresenter!!.onGoing(this, jsonObject)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It calls onGatheringById function of gathering presenter */
    override fun onSuccess() {
        try {
            campaignDataArray!!.clear()
            if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                gatheringIdPresenter!!.onGatheringById(this, gatheringId!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity depends on conditions  when click on it */
        back!!.setOnClickListener {

            if (isGoing) {
                println("approved resolution $isGoing")

                val intent = Intent()
                intent.putExtra("gatheringGoing", isGoing)
                intent.putExtra("gatheringId", gatheringId)
                setResult(203, intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else if (isNotGoing) {
                println("approved resolution $isNotGoing")

                val intent = Intent()
                intent.putExtra("gatheringIsNotGoing", isNotGoing)
                intent.putExtra("gatheringId", gatheringId)
                setResult(203, intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else if (nearByGathering) {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra("gatheringId", gatheringId)
                intent.putExtra("isSubscribe", isSubscribe)
                setResult(503, intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else if (myGathering) {
                val intent = Intent(this, MyGatheringActivity::class.java)
                intent.putExtra("gatheringId", gatheringId)
                intent.putExtra("isSubscribe", isSubscribe)
                setResult(503, intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else if (notification) {
                val intent = Intent(this, NotificationActivity::class.java)
                // startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else {
                val intent = Intent()
                intent.putExtra("gatheringId", gatheringId)
                intent.putExtra("isSubscribe", isSubscribe)
                setResult(503, intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }

        /**
         * If issue resolved, we can not create new campaign
         * else it sends campaign details to create campaign activity
         */
        campaign!!.setOnClickListener {

            if (gatheringData!!.data.issue.resolved) {
                ConstantMethods.showToast(
                    context,
                    "You cannot create campaign on resolved issues."
                )

            } else {
                EasySP.init(this).putString(ConstantEasySP.CAMPAIGN_DATE, "")
                EasySP.init(this).putString(ConstantEasySP.SELECTED_CAMPAIGN_ADDRESS, "")
                EasySP.init(this).putString(ConstantEasySP.UPLOADED_MEDIA, "")
                EasySP.init(this).putString("city", "")
                EasySP.init(this).putString("lat", "")
                EasySP.init(this).putString("long", "")
                EasySP.init(this).putString("campaignTitle", "")
                EasySP.init(this).putString("campaignDetails", "")
                EasySP.init(this).putString("campaignDate", "")

                val intent = Intent(this, CreateCampaignActivity::class.java)
                intent.putExtra("campaign", true)
                startActivityForResult(intent, 200)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                // finish()
            }
        }

        /** It closes more option layout with animation and
         * opens google map to get direction using latitude and longitude  */
        directions!!.setOnClickListener {

            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_down))

            if (gatheringData!!.data.location.coordinates != null) {
                val uri =
                    "http://maps.google.com/maps?saddr=" + "$latitude" + "," + "$longitude" + "&daddr=" + "${gatheringData!!.data.location.coordinates[0]}" + "," + "${gatheringData!!.data.location.coordinates[1]}"
                val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                startActivity(intent)
            }
        }


        /** If image or video url is not empty then opens MediaViewPagerActivity to see the image and video
         * else show warning dialog  */
        gatheringImage!!.setOnClickListener {
            if (gatheringData!!.data.imageUrls != null && gatheringData!!.data.imageUrls.isNotEmpty() || gatheringData!!.data.videoUrls != null && gatheringData!!.data.videoUrls.isNotEmpty()) {
                val intent = Intent(context, MediaViewPagerActivity::class.java)
                intent.putStringArrayListExtra("mediaImage", gatheringData!!.data.imageUrls)
                intent.putStringArrayListExtra("mediaVideo", gatheringData!!.data.videoUrls)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            } else {
                ConstantMethods.showToast(
                    this,
                    "Images not available for this gathering."
                )
            }
        }

        /** It generate current date and check with gathering creation date
         * if current date is greater then show warning dialog
         * else show and hide going , notGoing image view , change text color
         * and call postDataToServerOnGoing() with json object */
        going!!.setOnClickListener {


            var simpleDateFormat = SimpleDateFormat("dd-M-yyyy")
            val currentDate = simpleDateFormat.format(myCalendar!!.time)

            println(
                "created date ${gatheringData!!.data.createdAt} current date ${ConstantMethods.convertDateStringToServerDateFull(
                    currentDate
                )}"
            )
            if (gatheringData!!.data.createdAt < ConstantMethods.convertDateStringToServerDateFull(
                    currentDate
                )
            ) {
                ConstantMethods.showToast(
                    context,
                    "The gathering date is gone.Unless you are time traveller and want to visit the gathering in past."
                )
            } else {

                notGoing!!.visibility = View.VISIBLE
                going!!.visibility = View.GONE

                goingTitle!!.setTextColor(ContextCompat.getColor(this, R.color.button_background))

                isGoing = true

                try {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "gathering")
                    jsonObject.addProperty("typeId", gatheringData!!.data._id)


                    if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
                        postDataToServerOnGoing(jsonObject)
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }


        }

        /** It show and hide going , notGoing image view , change text color
         * and call postDataToServerOnNotGoing() with json object */
        notGoing!!.setOnClickListener {

            going!!.visibility = View.VISIBLE
            notGoing!!.visibility = View.GONE

            goingTitle!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))

            isNotGoing = true
            //onGoingListener!!.onNoGoing(gatheringData!!.data._id)


            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "gathering")
                jsonObject.addProperty("typeId", gatheringData!!.data._id)


                try {
                    postDataToServerOnNotGoing(jsonObject)

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        /** It show and hide subscribe, notSubscribe image view, change text color
         * and call postDataToServerOnSubscribe() with json object */
        subscribe!!.setOnClickListener {

            notSubscribe!!.visibility = View.VISIBLE
            subscribe!!.visibility = View.GONE

            subscribeTitle!!.setTextColor(ContextCompat.getColor(this, R.color.button_background))


            isSubscribe = true

            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "gathering")
                jsonObject.addProperty("typeId", gatheringData!!.data._id)


                if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
                    postDataToServerOnSubscribe(jsonObject)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It show and hide subscribe, notSubscribe image view, change text color
         * and call postDataToServerOnSubscribe() with json object */
        notSubscribe!!.setOnClickListener {


            isSubscribe = false

            subscribe!!.visibility = View.VISIBLE
            notSubscribe!!.visibility = View.GONE

            subscribeTitle!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))


            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "gathering")
                jsonObject.addProperty("typeId", gatheringData!!.data._id)


                try {
                    postDataToServerOnSubscribe(jsonObject)

                } catch (e: Exception) {

                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It closes more option layout with animation */
        more!!.setOnClickListener {
            moreLayout!!.visibility = View.VISIBLE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))

        }

        /** It closes more option layout with animation */
        close!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_down))
        }


        /**  It will show dialog to delete an gathering and call postDataToServerOnDelete() with json object
         */
        delete!!.setOnClickListener {
            val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
            if (userId == gatheringData!!.data.user._id) {
                println("user id ${userId} gathering user ${gatheringData!!.data.user._id}")
                try {
                    val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "Do you want to delete gathering?"
                    sweetAlertDialog.confirmText = "Yes"
                    sweetAlertDialog.cancelText = "No"
                    sweetAlertDialog.show()
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.setConfirmClickListener {
                        sweetAlertDialog.dismissWithAnimation()


                        try {

                            val jsonObject = JsonObject()
                            jsonObject.addProperty("type", "disabled")
                            jsonObject.addProperty("gatheringId", gatheringData!!.data._id)


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
            } else {
                ConstantMethods.showToast(this, "You are not the owner of this gathering.")
            }


        }


        /** It closes the more layout with animation
         * get stored user id and check with gathering user id, if matches then we cannot abuse the gathering
         * else It will open report abuse layout  */
        reportAbuse!!.setOnClickListener {
            try {
                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_down
                    )
                )

                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == gatheringData!!.data.user._id) {
                    ConstantMethods.showToast(
                        context,
                        "You can not abuse your gathering."
                    )
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


                /* val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                 if (userId == nearByIssuesData!!.data.user._id) {
                     ConstantMethods.showWarning(context,"","You can not abuse your issue.")
                 }
                 else {
 */

                if (ConstantMethods.checkForInternetConnection(this)) {
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
         * get stored user id and check with gathering user id, if matches then  opens CreateGatheringActivity to edit a gathering
         * else show warning dialog */
        edit!!.setOnClickListener {

            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )


            val userId = EasySP.init(this).getString(ConstantEasySP.USER_ID)
            if (userId == gatheringData!!.data.user._id) {


                val jsonString = Gson().toJson(gatheringData)
                val intent = Intent(this, CreateGatheringActivity::class.java)
                intent.putExtra("gatheringData", jsonString)
                intent.putExtra("edit", true)
                startActivityForResult(intent, 200)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {

                ConstantMethods.showToast(this,  "You are not the owner of this gathering.")
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


        /** It opens NearByIssueDetailsActivity  */
        issueNumber!!.setOnClickListener {
            val intent = Intent(this, NearByIssueDetailsActivity::class.java)
            intent.putExtra("issueId", gatheringData!!.data.issue._id)
            startActivity(intent)

        }

        /** It calls again onGatheringById function of gathering id presenter
         * and onCommentsById function of comment presenter when pull it from top */
        pullToRefresh!!.setOnRefreshListener {


            try {
                if (nearByGathering) {
                    title!!.text = "Upcoming Gatherings"
                } else {
                    title!!.text = "Gathering"
                }

                campaignDataArray!!.clear()


                if (gatheringId != null && gatheringId!!.isNotEmpty()) {

                    resetValue()
                    try {
                        if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
                            ConstantMethods.showProgessDialog(this, "Please Wait...")
                            gatheringIdPresenter!!.onGatheringById(this, gatheringId!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            commentPresenter!!.onCommentsById(
                                this, gatheringId!!, "gathering", pageNo,
                                PAGE_SIZE
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            pullToRefresh!!.isRefreshing = false
        }


        /**It opens LocationActivity */
        locationLayout!!.setOnClickListener {
            val intent = Intent(context, LocationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            EasySP.init(context).put("lat", gatheringData!!.data.location.coordinates[0])
            EasySP.init(context).put("long", gatheringData!!.data.location.coordinates[1])
            EasySP.init(context).putBoolean("resolved", gatheringData!!.data.issue.resolved)
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
                    "${Constants.BASE_URL}gatherings/$gatheringId"
                )
                startActivity(Intent.createChooser(share, "Share Gathering!"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It calls loadMore() when scrolling  */
        commentRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = commentRecycler!!.getChildCount()
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
                    this@GatheringDetailsActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
                                loadMore()
                            }
                        }
                    })
                    // Do something
                    loading = true
                }
                println("loading comments")
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

                if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
                    resetValue()
                    getDataToPost()
                    //println("comment position $commentPosition")
                    if (commentPosition != null) {

                    } else {
                        scrollView!!.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS)


                    }
                }

                backgroundLayout!!.visibility = View.GONE
                comment!!.clearFocus()
                ConstantMethods.hideKeyBoard(this, this@GatheringDetailsActivity)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It hides keyboard, comment layout and show operation layout */
        closeComment!!.setOnClickListener {

            ConstantMethods.hideKeyBoard(this, this@GatheringDetailsActivity)

            commentLayout!!.visibility = View.GONE
            operationLayout!!.visibility = View.VISIBLE
            backgroundLayout!!.visibility = View.GONE


        }

        /** It hides keyboard, comment layout and show operation layout */
        commentLayout!!.setOnClickListener {
            ConstantMethods.hideKeyBoard(this, this@GatheringDetailsActivity)

            commentLayout!!.visibility = View.GONE
            operationLayout!!.visibility = View.VISIBLE
            backgroundLayout!!.visibility = View.GONE


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

                allText = p0.toString().replace("(", "")

                if (allText.endsWith(")", ignoreCase = true)) {
                    if (allText != null && allText.isNotEmpty()) {
                        val parts = allText.split(")")
                        val first = parts[0]
                        var second = parts[1]

                        allText = first.replace(first, "")
                    }
                }

                allText = allText.replace(")", "")


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
                    allText = allText.replace("(", "")

                    if (allText.endsWith(")", ignoreCase = true)) {
                        if (allText != null && allText.isNotEmpty()) {
                            val parts = allText.split(")")
                            val first = parts[0]
                            var second = parts[1]

                            allText = first.replace(first, "")
                        }
                    }

                    allText = allText.replace(")", "")

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





                if (p0.length == 0) {
                    tagsList!!.clear()
                    tagUserName!!.clear()
                    userTypeRecycler!!.visibility = View.GONE
                } else {
                    for (i in 0 until tagUserName!!.size) {
                        if (!p0.contains(tagUserName!![i], ignoreCase = true)) {
                            tagUserName!!.remove(tagUserName!![i])
                            tagsList!!.remove(tagsList!![i])

                            break

                        }
                    }

                }
            }

        })


    }


    /** It checks the user data,
     *  if it is not empty then set ResolvedIssueUserAdapter
     */
    override fun setUserListAdapter(userList: ArrayList<UserList>) {
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
                userTypeAdapter = ResolvedIssueUserAdapter(this, userList, "")
                userTypeRecycler!!.adapter = userTypeAdapter

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

                    if (tagsList != null && tagsList!!.isNotEmpty()) {
                        for (i in 0 until tagsList!!.size) {
                            jsonArray.add(tagsList!![i])
                        }
                        jsonObject.add("tags", jsonArray)
                    }

                    val jsonArrayName = JsonArray()

                    if (tagUserName != null && tagUserName!!.isNotEmpty()) {
                        for (i in 0 until tagUserName!!.size) {
                            jsonArrayName.add(tagUserName!![i])
                        }
                        jsonObject.add("tagNames", jsonArrayName)
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
                    ConstantMethods.showToast(
                        this,
                        "Please give us your comment."
                    )
                }

            } else {
                if (commentId != null && commentId.isNotEmpty()) {
                    if (comment!!.text != null && comment!!.text.isNotEmpty()) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("gatheringId", gatheringId!!)
                        jsonObject.addProperty("text", (comment!!.text.toString()))
                        jsonObject.addProperty("parentId", commentId)

                        val jsonArray = JsonArray()

                        if (tagsList != null && tagsList!!.isNotEmpty()) {
                            for (i in 0 until tagsList!!.size) {
                                jsonArray.add(tagsList!![i])
                            }
                            jsonObject.add("tags", jsonArray)
                        }


                        val jsonArrayName = JsonArray()

                        if (tagUserName != null && tagUserName!!.isNotEmpty()) {
                            for (i in 0 until tagUserName!!.size) {
                                jsonArrayName.add(tagUserName!![i])
                            }
                            jsonObject.add("tagNames", jsonArrayName)
                        }
                        commentId = ""

                        try {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                postDataToServer(jsonObject)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        ConstantMethods.showToast(
                            this,
                            "Please give us your comment."
                        )
                    }
                } else {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("gatheringId", gatheringId)
                    jsonObject.addProperty("text", comment!!.text.toString().trim())
                    val jsonArray = JsonArray()

                    if (tagsList != null && tagsList!!.isNotEmpty()) {
                        for (i in 0 until tagsList!!.size) {
                            jsonArray.add(tagsList!![i])
                        }
                        jsonObject.add("tags", jsonArray)
                    }

                    val jsonArrayName = JsonArray()

                    if (tagUserName != null && tagUserName!!.isNotEmpty()) {
                        for (i in 0 until tagUserName!!.size) {
                            jsonArrayName.add(tagUserName!![i])
                        }
                        jsonObject.add("tagNames", jsonArrayName)
                    }
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
     * and onGatheringById function of gathering presenter
     * hide keyboard */
    override fun setCommentAdapter() {
        try {
            operationLayout!!.visibility = View.VISIBLE
            commentLayout!!.visibility = View.GONE
            backgroundLayout!!.visibility = View.GONE
            comment!!.setText("")
            tagsList!!.clear()

            ConstantMethods.hideKeyBoard(this, this@GatheringDetailsActivity)

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    gatheringIdPresenter!!.onGatheringById(this, gatheringId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {

                    commentPresenter!!.onCommentsById(
                        this,
                        gatheringId!!,
                        "gathering",
                        pageNo,
                        PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            ConstantMethods.hideKeyBoard(this, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls postDataToServerOnLoadMore() with below inputs  */
    private fun loadMore() {
        try {


            if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {

                postDataToServerOnLoadMore(gatheringId!!, "gathering", ++page, PAGE_SIZE)
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


    /** It calls postDataToServerOnAbuse function with json object */
    private fun getDataToPostOnAbuse() {
        try {

            if (abuseEditText!!.text.trim() != null && abuseEditText!!.text.trim().isNotEmpty()) {
                var abuse = abuseEditText!!.text.trim()

                if (ConstantMethods.checkForInternetConnection(this)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "gathering")
                    jsonObject.addProperty("typeId", gatheringId)
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

                    println("post data $jsonObject")


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

    /** It takes the json object as input and send to onDelete function of gathering presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject) {

        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                gatheringIdPresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** ui initialization */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        commentImage = findViewById(R.id.img_comment)
        gatheringImage = findViewById(R.id.img_gathering)
        campaign = findViewById(R.id.img_create_campaign)
        going = findViewById(R.id.img_resolved)
        notGoing = findViewById(R.id.img_already_resolved)
        more = findViewById(R.id.img_more)
        close = findViewById(R.id.img_close)
        subscribe = findViewById(R.id.img_subscribe)
        notSubscribe = findViewById(R.id.img_already_subscribe)
        closeAbuse = findViewById(R.id.img_close_window)
        sendComment = findViewById(R.id.img_send_comment)
        closeComment = findViewById(R.id.img_close_comment)

        /** ids of text view */

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        issueNumber = findViewById(R.id.txt_issue_id)
        //issueNumber!!.typeface = ConstantFonts.raleway_semibold

        issueNumberTitle = findViewById(R.id.txt_issue_title)
        issueNumberTitle!!.typeface = ConstantFonts.raleway_semibold

        gatheringLocation = findViewById(R.id.txt_gathering_location)
        gatheringLocation!!.typeface = ConstantFonts.raleway_semibold

        gatheringDate = findViewById(R.id.txt_gathering_date)
        gatheringDate!!.typeface = ConstantFonts.raleway_regular

        gatheringTitle = findViewById(R.id.txt_gathering_title)
        gatheringTitle!!.typeface = ConstantFonts.raleway_semibold

        gatheringMonth = findViewById(R.id.txt_month_title)
        gatheringMonth!!.typeface = ConstantFonts.raleway_regular

        gatheringDay = findViewById(R.id.txt_date_title)
        gatheringDay!!.typeface = ConstantFonts.raleway_regular

        hostedByTitle = findViewById(R.id.txt_hosted_by_title)
        hostedByTitle!!.typeface = ConstantFonts.raleway_regular

        hostedByName = findViewById(R.id.txt_hosted_by_name)
        hostedByName!!.typeface = ConstantFonts.raleway_regular

        gatheringDescription = findViewById(R.id.txt_gathering_description)
        gatheringDescription!!.typeface = ConstantFonts.raleway_medium

        linkedCampaignTitle = findViewById(R.id.txt_linked_campaign_title)
        linkedCampaignTitle!!.typeface = ConstantFonts.raleway_semibold

        delete = findViewById(R.id.txt_delete)
        delete!!.typeface = ConstantFonts.raleway_semibold

        gatheringGoingCount = findViewById(R.id.txt_going_count)
        gatheringGoingCount!!.typeface = ConstantFonts.raleway_regular

        reportAbuse = findViewById(R.id.txt_report_abuse)
        reportAbuse!!.typeface = ConstantFonts.raleway_semibold

        edit = findViewById(R.id.txt_edit)
        edit!!.typeface = ConstantFonts.raleway_semibold

        goingTitle = findViewById(R.id.txt_resolve_title)
        goingTitle!!.typeface = ConstantFonts.raleway_regular

        subscribeTitle = findViewById(R.id.txt_subscribe_title)
        subscribeTitle!!.typeface = ConstantFonts.raleway_regular

        commentTitle = findViewById(R.id.txt_comment_title)
        commentTitle!!.typeface = ConstantFonts.raleway_regular

        campaignTitle = findViewById(R.id.txt_campaign_title)
        campaignTitle!!.typeface = ConstantFonts.raleway_regular

        moreTitle = findViewById(R.id.txt_more_title)
        moreTitle!!.typeface = ConstantFonts.raleway_regular

        share = findViewById(R.id.txt_share)
        share!!.typeface = ConstantFonts.raleway_semibold

        directions = findViewById(R.id.txt_get_direction)
        directions!!.typeface = ConstantFonts.raleway_semibold


        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)


        /** ids of relative layout */
        linkedCampaignLayout = findViewById(R.id.linkedCampaignLayout)
        moreLayout = findViewById(R.id.moreLayout)
        gatheringGoingLayout = findViewById(R.id.gatheringGoingLayout)
        reportAbuseLayout = findViewById(R.id.reportAbuseLayout)
        locationLayout = findViewById(R.id.gatheringDetailsLayout)
        backgroundLayout = findViewById(R.id.backgroundLayout)
        commentLayout = findViewById(R.id.commentLayout)

        /** ids of linear layout */
        operationLayout = findViewById(R.id.operationLayout)

        /** ids of edit text */
        comment = findViewById(R.id.et_comment)
        comment!!.typeface = ConstantFonts.raleway_regular

        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of button */
        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold

        /** ids of recycler view */
        campaignRecycler = findViewById(R.id.campaignRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        campaignRecycler!!.setHasFixedSize(true)
        campaignRecycler!!.layoutManager = linearLayoutManager


        gatheringGoingRecycler = findViewById(R.id.gatheringGoingRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        gatheringGoingRecycler!!.setHasFixedSize(true)
        gatheringGoingRecycler!!.layoutManager = linearLayoutManager

        commentRecycler = findViewById(R.id.commentRecycler)
        linearLayoutManager1 = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        commentRecycler!!.setHasFixedSize(true)
        commentRecycler!!.layoutManager = linearLayoutManager1

        userTypeRecycler = findViewById(R.id.recycler_resolved_by)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        userTypeRecycler!!.setHasFixedSize(true)
        userTypeRecycler!!.layoutManager = linearLayoutManager


        scrollView = findViewById(R.id.gatheringDetailsScrollView)


    }

    /** It checks the gatheringDetails data,
     *  if it is not empty then set data to respective views
     */
    override fun setGatheringByIdAdapter(gatheringDetails: GatheringDetails) {
        try {

            if (gatheringId != null && gatheringId!!.isNotEmpty()) {

                resetValue()
                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {
                        commentPresenter!!.onCommentsById(
                            this, gatheringId!!, "gathering", pageNo,
                            PAGE_SIZE
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            if (gatheringDetails.data != null) {

                gatheringData = gatheringDetails
                val loggedInUserId = EasySP.init(this).getString(ConstantEasySP.USER_ID)
                if (loggedInUserId == gatheringDetails!!.data.user._id) {
                    edit!!.visibility=View.VISIBLE
                    divider3=findViewById(R.id.view3)
                    divider3!!.visibility=View.VISIBLE
                    delete!!.visibility=View.VISIBLE
                    divider4=findViewById(R.id.view4)
                    divider4!!.visibility=View.VISIBLE
                }
                if (gatheringDetails.data.issue.issueNumber != null) {
                    issueNumber!!.text = "# ${gatheringDetails.data.issue.issueNumber}"
                }

                if (gatheringDetails.data.gatheringNumber != null) {
                    title!!.text = "Gathering #${gatheringDetails.data.gatheringNumber}"
                }
                if (gatheringDetails.data.title != null) {
                    gatheringTitle!!.text = gatheringDetails.data.title
                }
                if (gatheringDetails.data.address != null) {
                    gatheringLocation!!.text = gatheringDetails.data.address
                }
                if (gatheringDetails.data.city != null) {
                    gatheringLocation!!.text = gatheringDetails.data.city
                }
                if (gatheringDetails.data.address != null && gatheringDetails.data.city != null) {
                    gatheringLocation!!.text =
                        gatheringDetails.data.address + ", ${gatheringDetails.data.city}"
                }


                if (gatheringDetails.data.user.firstName != null && gatheringDetails.data.user.firstName.isNotEmpty()) {
                    hostedByName!!.text = gatheringDetails.data.user.firstName
                }

                if (gatheringDetails.data.user.lastName != null && gatheringDetails.data.user.lastName.isNotEmpty()) {
                    hostedByName!!.text = gatheringDetails.data.user.lastName
                }

                if (gatheringDetails.data.user.firstName != null && gatheringDetails.data.user.lastName != null) {
                    hostedByName!!.text =
                        gatheringDetails.data.user.firstName + " ${gatheringDetails.data.user.lastName}"
                }

                if (gatheringDetails.data.user.organizationName != null) {
                    hostedByName!!.text = gatheringDetails.data.user.organizationName
                }

                if (gatheringDetails.data.createdAt != null && gatheringDetails.data.createdAt.isNotEmpty()) {
                    gatheringDate!!.text =
                        ConstantMethods.convertStringToDateStringFull(gatheringDetails.data.gatheringDate)
                    gatheringMonth!!.text =
                        ConstantMethods.convertStringToMonthFull(gatheringDate!!.text.toString())
                    gatheringDay!!.text =
                        ConstantMethods.convertStringToDateFull(gatheringDate!!.text.toString())
                }


                if (gatheringDetails.data.description != null && gatheringDetails.data.description.isNotEmpty()) {
                    gatheringDescription!!.text = gatheringDetails.data.description
                }

                if (gatheringDetails.data.imageUrls != null && gatheringDetails.data.imageUrls.isNotEmpty()) {
                    Glide.with(this)
                        .load(gatheringDetails.data.imageUrls[0])
//                        .placeholder(R.drawable.loading_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(gatheringImage!!)
                } else if (gatheringDetails.data.videoUrls != null && gatheringDetails.data.videoUrls.isNotEmpty()) {
                    Glide.with(context)
                        .load(gatheringDetails.data.videoUrlThumbnails[0])
                        .placeholder(R.drawable.processing_video)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(gatheringImage!!)

                } else {

                    gatheringImage!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.placeholder
                        )
                    )
                }


                if (gatheringDetails.data.campaigns != null && gatheringDetails.data.campaigns.isNotEmpty()) {
                    campaignDataArray!!.addAll(gatheringDetails.data.campaigns)

                    campaignAdapter = CampaignAdapter(this, campaignDataArray!!)
                    campaignRecycler!!.adapter = campaignAdapter
                } else {
                    linkedCampaignLayout!!.visibility = View.GONE
                }

                EasySP.init(this).putString(ConstantEasySP.GATHERING_ID, gatheringDetails.data._id)
                EasySP.init(this)
                    .putString(ConstantEasySP.GATHERING_TITLE, gatheringDetails.data.title)


                if (gatheringDetails.data.isGoing != null) {
                    if (gatheringDetails.data.isGoing) {
                        notGoing!!.visibility = View.VISIBLE
                        going!!.visibility = View.GONE

                        goingTitle!!.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.button_background
                            )
                        )
                    }
                } else {
                    notGoing!!.visibility = View.GONE
                    going!!.visibility = View.VISIBLE

                    goingTitle!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                }


                if (gatheringDetails.data.isSubscribe != null) {
                    if (gatheringDetails.data.isSubscribe) {
                        notSubscribe!!.visibility = View.VISIBLE
                        subscribe!!.visibility = View.GONE

                        subscribeTitle!!.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.button_background
                            )
                        )
                    }
                } else {
                    notSubscribe!!.visibility = View.GONE
                    subscribe!!.visibility = View.VISIBLE

                    subscribeTitle!!.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.colorPrimary
                        )
                    )

                }

                if (gatheringDetails.data.goingList != null && gatheringDetails.data.goingList.isNotEmpty()) {
                    println("going list ${gatheringDetails.data.goingList}")

                    goingArrayList!!.clear()
                    gatheringGoingCount!!.visibility = View.VISIBLE
                    goingArrayList!!.addAll(gatheringDetails!!.data.goingList)
                    gatheringGoingLayout!!.visibility = View.VISIBLE
                    gatheringGoingAdapter =
                        GatheringGoingAdapter(this, goingArrayList!!, gatheringDetails.data)
                    gatheringGoingRecycler!!.adapter = gatheringGoingAdapter

                    if (gatheringDetails.data.goingListCount != null && gatheringDetails.data.goingListCount != 0) {
                        gatheringGoingCount!!.text =
                            "${gatheringDetails.data.goingListCount} people are going to the gathering"
                    }
                } else {
                    goingArrayList!!.clear()
                    gatheringGoingCount!!.visibility = View.GONE
                    gatheringGoingAdapter!!.notifyDataSetChanged()

                }

                if (gatheringDetails.data.isSubscribe) {
                    notSubscribe!!.visibility = View.VISIBLE
                    subscribe!!.visibility = View.GONE
                } else {
                    subscribe!!.visibility = View.VISIBLE
                    notSubscribe!!.visibility = View.GONE
                }

                hostedByName!!.setOnClickListener {
                    val intent = Intent(context, BadgesActivity::class.java)
                    intent.putExtra(Constants.USER_ID, gatheringDetails.data.user._id)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    /** It calls  onGatheringById function of gathering id presenter
     * and onCommentsById function of comment presenter when click on it */
    override fun goToNextScreen() {

        if (gatheringId != null && gatheringId!!.isNotEmpty()) {
            resetValue()

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    gatheringIdPresenter!!.onGatheringById(this, gatheringId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    //ConstantMethods.showProgessDialog(this, "Please Wait...")
                    commentArray!!.clear()
                    commentPresenter!!.onCommentsById(
                        this, gatheringId!!, "gathering", pageNo,
                        PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    /** It checks the commentDetails data,
     *  if it is not empty then set CommentAdapter
     */
    override fun setCommentsByIdAdapter(commentsDetails: CommentsPojo) {

        if (commentsDetails.data.size == 0) {

        } else {

            commentArray!!.clear()

            commentArray!!.addAll(commentsDetails.data)

            println("comment array size ${commentArray!!.size}")
            if (commentPosition != null) {

                commentsAdapter = GatheringCommentAdapter(this, commentArray!!, gatheringData!!)
                commentRecycler!!.adapter = commentsAdapter
                commentRecycler!!.smoothScrollToPosition(commentPosition!!)
                commentRecycler!!.scrollToPosition(commentPosition!! - 2)
                linearLayoutManager!!.scrollToPositionWithOffset(
                    commentPosition!!,
                    commentPosition!!
                )
            } else {
                commentsAdapter = GatheringCommentAdapter(this, commentArray!!, gatheringData!!)
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

    /** It opens activity depends on condition
     */
    override fun setOnDelete() {
        if (nearByGathering) {
            val intent = Intent(this, SummaryDetailsActivity::class.java)
            intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UPCOMING_GATHERING)
            //startActivity(intent)
            intent.putExtra("delete", true)
            intent.putExtra("gatheringId", gatheringId)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (myGathering) {
            val intent = Intent(this, MyGatheringActivity::class.java)
            intent.putExtra("delete", true)
            intent.putExtra("gatheringId", gatheringId)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (isGatheringLinked) {
            val intent = Intent(this, NearByIssueDetailsActivity::class.java)
            intent.putExtra("delete", true)
            intent.putExtra("gatheringId", gatheringId)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else {
            val intent = Intent(this, GatheringActivity::class.java)
            intent.putExtra("delete", true)
            intent.putExtra("gatheringId", gatheringId)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }


    }

    /** It call below functions using corresponding presenters */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == 201) {

            val isEdit = data!!.getBooleanExtra("isEdit", false)
            val gatheringId = data.getStringExtra("gatheringId")

            if (isEdit) {
                if (gatheringId != null && gatheringId.isNotEmpty()) {
                    resetValue()

                    try {
                        if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
                            //ConstantMethods.showProgessDialog(this, "Please Wait...")
                            gatheringIdPresenter!!.onGatheringById(this, gatheringId)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            //ConstantMethods.showProgessDialog(this, "Please Wait...")
                            commentPresenter!!.onCommentsById(
                                this, gatheringId!!, "gathering", pageNo,
                                PAGE_SIZE
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }

            }
            val deleteCampaignList = ArrayList<Campaigns>()
            deleteCampaignList.addAll(campaignDataArray!!)

            val issueId = data.getStringExtra("campaignId")

            for (i in 0 until deleteCampaignList.size) {

                if (deleteCampaignList[i]._id == issueId) {
                    deleteCampaignList.remove(deleteCampaignList[i])
                    campaignDataArray!!.clear()
                    campaignDataArray!!.addAll(deleteCampaignList)
                    campaignAdapter!!.notifyDataSetChanged()


                    break
                }
            }

            /*  if (campaignDataArray!!.size == 0) {
                  campaignDataArray!!.clear()
                  campaignAdapter!!.notifyDataSetChanged()
              }*/

        }

    }

    /** It goes back to previous activity depends on condition   */
    override fun onBackPressed() {

        if (isGoing) {
            println("approved resolution $isGoing")

            val intent = Intent()
            intent.putExtra("gatheringGoing", isGoing)
            intent.putExtra("gatheringId", gatheringId)
            setResult(203, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (isNotGoing) {
            println("approved resolution $isNotGoing")

            val intent = Intent()
            intent.putExtra("gatheringIsNotGoing", isNotGoing)
            intent.putExtra("gatheringId", gatheringId)
            setResult(203, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (nearByGathering) {
            val intent = Intent(this, SummaryDetailsActivity::class.java)
            intent.putExtra("gatheringId", gatheringId)
            intent.putExtra("isSubscribe", isSubscribe)
            setResult(503, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (myGathering) {
            val intent = Intent(this, MyGatheringActivity::class.java)
            intent.putExtra("gatheringId", gatheringId)
            intent.putExtra("isSubscribe", isSubscribe)
            setResult(503, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (notification) {
            val intent = Intent(this, NotificationActivity::class.java)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else {
            val intent = Intent()
            intent.putExtra("gatheringId", gatheringId)
            intent.putExtra("isSubscribe", isSubscribe)
            setResult(503, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        super.onBackPressed()

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
        campaignDataArray!!.clear()
    }


    /** When resumes the activity it will call onGatheringById function of gathering presenter */
    override fun onResume() {
        super.onResume()

        try {
            if (nearByGathering) {
                title!!.text = "Upcoming Gatherings"
            } else {
                title!!.text = "Gathering"
            }

            campaignDataArray!!.clear()


            if (gatheringId != null && gatheringId!!.isNotEmpty()) {

                resetValue()

                try {
                    if (ConstantMethods.checkForInternetConnection(this@GatheringDetailsActivity)) {
                        ConstantMethods.showProgessDialog(this, "Please Wait...")
                        gatheringIdPresenter!!.onGatheringById(this, gatheringId!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
