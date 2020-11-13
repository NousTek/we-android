package com.we.beyond.ui.campaign.campaignDetails

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.adapter.CampaignGoingAdapter
import com.we.beyond.Interface.OnGoingListener
import com.we.beyond.R
import com.we.beyond.interceptor.ApplicationController.Companion.context
import com.we.beyond.model.*
import com.we.beyond.presenter.campaign.campaignById.CampaignImpl
import com.we.beyond.presenter.campaign.campaignById.CampaignPresenter
import com.we.beyond.presenter.chechin.CheckInImpl
import com.we.beyond.presenter.chechin.CheckInPresenter
import com.we.beyond.presenter.going.going.GoingImpl
import com.we.beyond.presenter.going.going.GoingPresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.MediaViewPagerActivity
import com.we.beyond.ui.campaign.createCampaign.CreateCampaignActivity
import com.we.beyond.ui.dashboard.NotificationActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.ui.profile.MyCampaignActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList

/**
 * It shows the campaign details perform further actions
 */
class CampaignDetailsActivity : AppCompatActivity(), CampaignPresenter.ICampaignByIdView,CheckInPresenter.ICheckInView,
    GoingPresenter.IGoingView, OnGoingListener, ReportAbusePresenter.IReportAbuseView {

    /** initialize respected implementors */
    var campaignPresenter : CampaignImpl? = null
    var checkInPresenter : CheckInImpl?=null
    var goingPresenter : GoingImpl?=null
    var reportAbusePresenter : ReportAbuseImpl?=null

    /** init image view */
    var back: ImageView? = null
    var campaignImage: ImageView? = null
    var checkIn : ImageView?=null
    var alreadyCheckedIn : ImageView?=null
    var going : ImageView?=null
    var notGoing : ImageView?=null
    var directions : ImageView?=null
    var more : ImageView?=null
    var close: ImageView? = null
    var closeAbuse : ImageView?=null
    var play : ImageView?=null

    /** init text view */
    var title: TextView? = null
    var gatheringNumber: TextView? = null
    var gatheringNumberTitle : TextView?=null
    var campaignTitle: TextView? = null
    var campaignLocation: TextView? = null
    var campaignDate: TextView? = null
    var campaignMonth : TextView?=null
    var campaignDay : TextView?=null
    var campaignDescription: TextView? = null
    var delete : TextView?=null
    var campaignGoingCount : TextView?=null
    var reportAbuse : TextView?=null
    var edit : TextView?=null
    var goingTitle : TextView?=null
    var directionTitle : TextView?=null
    var chechInTitle : TextView?=null
    var moreTitle : TextView?=null

    var campaignData : CampaignPojo?=null

    /** init strings */
    var campaignId : String ?=null
    var gatheringId : String ?=null

    /** init double */
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var rootRef: DatabaseReference

    /** init relative layout */
    var moreLayout : RelativeLayout?=null
    var campaignGoingLayout : RelativeLayout?=null
    var reportAbuseLayout : RelativeLayout?=null
    var locationLayout : RelativeLayout?=null

    var myCampaign : Boolean = false

    var linearLayoutManager: LinearLayoutManager? = null

    /** init recycler view */
    var campaignGoingRecycler : RecyclerView?=null

    /** init adapter */
    var campaignGoingAdapter : CampaignGoingAdapter?=null

    /** init edit text */
    var abuseEditText : EditText?=null

    /** init button */
    var abuse : Button?=null

    /** init booleans */
    var isEdit : Boolean = false
    var notification : Boolean = false

    /** init array list */
    var goingArrayList : ArrayList<CamapignGoingList>?=null

    /** init calender instance */
    var myCalendar: Calendar? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campaign_details)

        /** calender initialization */
        myCalendar = Calendar.getInstance()

        /** array initialization */
        goingArrayList = ArrayList()
        goingArrayList!!.clear()

        /** initialize implementation */
        campaignPresenter = CampaignImpl(this)
        checkInPresenter = CheckInImpl(this)
        goingPresenter = GoingImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get current location */
        getLocation()

        /** Get all stored data using intent and assign it respectively
         * and call onCampaignById function of campaign presenter */
        campaignId = intent.getStringExtra("campaignId")
        gatheringId = intent.getStringExtra("gatheringId")
        myCampaign = intent.getBooleanExtra("myCampaign",false)
        notification = intent.getBooleanExtra("notification",false)


        if (campaignId != null && campaignId!!.isNotEmpty()) {

            try {
                if (ConstantMethods.checkForInternetConnection(this)) {

                        ConstantMethods.showProgessDialog(this, "Please Wait...")
                    campaignPresenter!!.onCampaignById(this, campaignId!!)
                }

            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

        }


    }

    /** It call onCampaignById function of campaign presenter */
    override fun onSuccess()
    {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                campaignPresenter!!.onCampaignById(this, campaignId!!)
            }

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It create json object
     * and call postDataToServerOnGoing function
     */
    override fun onGoing(_id: String)
    {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
                postDataToServerOnGoing(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It create json object
     * and call postDataToServerOnNotGoing function
     */
    override fun onNoGoing(_id: String)
    {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            try {
                postDataToServerOnNotGoing(jsonObject)

            } catch (e:Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** It takes the json object as input and send to onGoing function of going presenter */
    private fun postDataToServerOnGoing(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
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
                if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
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

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity depends on conditions  when click on it */
        back!!.setOnClickListener {
           /* val intent = Intent(this, GatheringDetailsActivity::class.java)
            intent.putExtra("gatheringId",gatheringId)
            startActivity(intent)*/
            if(isEdit)
            {
                if(isEdit) {
                    val intent = Intent()
                    intent.putExtra("gatheringId", gatheringId)
                    setResult(201, intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
            }
            else if(notification)
            {
                val intent = Intent(this, NotificationActivity::class.java)
                // startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }

        /** If image or video url is not empty then opens MediaViewPagerActivity to see the image and video
         * else show warning dialog  */
        campaignImage!!.setOnClickListener {
            if(campaignData!!.data.imageUrls !=null && campaignData!!.data.imageUrls.isNotEmpty() || campaignData!!.data.videoUrls !=null && campaignData!!.data.videoUrls.isNotEmpty()) {
                val intent = Intent(this, MediaViewPagerActivity::class.java)
                intent.putStringArrayListExtra("mediaImage", campaignData!!.data.imageUrls)
                intent.putStringArrayListExtra("mediaVideo", campaignData!!.data.videoUrls)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            else{
                ConstantMethods.showWarning(this,"No Data","Images not available for this campaign.")
            }
        }


        /** It show and hide checkIn, alrreadyCheckIn image view , change text color
         * and call postDataToServerOnCheckIn() with json object */
        checkIn!!.setOnClickListener {

            alreadyCheckedIn!!.visibility = View.VISIBLE
            checkIn!!.visibility = View.GONE

            chechInTitle!!.setTextColor(ContextCompat.getColor(this,R.color.button_background))

            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "campaign")
                jsonObject.addProperty("typeId", campaignData!!.data._id)


                if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
                    postDataToServerOnCheckIn(jsonObject)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It show and hide checkIn, alrreadyCheckIn image view , change text color
         * and call postDataToServerOnAlreadyCheckedIn() with json object */
        alreadyCheckedIn!!.setOnClickListener {

            checkIn!!.visibility = View.VISIBLE
            alreadyCheckedIn!!.visibility = View.GONE


            chechInTitle!!.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))



            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "campaign")
                jsonObject.addProperty("typeId", campaignData!!.data._id)


                try {
                    postDataToServerOnAlreadyCheckedIn(jsonObject)

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /** It generate current date and check with gathering creation date
         * if current date is greater then show warning dialog
         * else show and hide going , notGoing image view , change text color
         * and call postDataToServerOnGoing() with json object */
        going!!.setOnClickListener {

            var simpleDateFormat = SimpleDateFormat("dd-M-yyyy")
            val currentDate = simpleDateFormat.format(myCalendar!!.time)

            if(campaignData!!.data.createdAt < ConstantMethods.convertDateStringToServerDateFull(currentDate))
            {
                ConstantMethods.showWarning(context,"","The campaign date is gone.Unless you are time traveller and wand to visit the campaign in past.")
            }
            else {
                notGoing!!.visibility = View.VISIBLE
                going!!.visibility = View.GONE

                goingTitle!!.setTextColor(ContextCompat.getColor(this, R.color.button_background))

                try {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "campaign")
                    jsonObject.addProperty("typeId", campaignData!!.data._id)


                    if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
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

            goingTitle!!.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))

            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "campaign")
                jsonObject.addProperty("typeId", campaignData!!.data._id)


                try {
                    postDataToServerOnNotGoing(jsonObject)

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It closes more option layout with animation and
         * opens google map to get direction using latitude and longitude  */
        directions!!.setOnClickListener {
            if(campaignData!!.data.location.coordinates!=null) {
                val uri =
                    "http://maps.google.com/maps?saddr=" + "$latitude" + "," + "$longitude" + "&daddr=" + "${campaignData!!.data.location.coordinates[0]}" + "," + "${campaignData!!.data.location.coordinates[1]}"
                val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                startActivity(intent)
            }
        }

        /** It closes more option layout with animation */
        more!!.setOnClickListener {
            moreLayout!!.visibility = View.VISIBLE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_in_up))

        }

        /** It closes more option layout with animation */
        close!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))
        }

        /**  It will show dialog to delete an campaign and call postDataToServerOnDelete() with json object
         */
        delete!!.setOnClickListener {
            val userId = EasySP.init(this).getString(ConstantEasySP.USER_ID)
            if(userId == campaignData!!.data.user._id) {
                println("user id ${userId} campaign user ${campaignData!!.data.user._id}")
                try {
                    val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "Do you want to delete campaign?"
                    sweetAlertDialog.confirmText = "Yes"
                    sweetAlertDialog.cancelText = "No"
                    sweetAlertDialog.show()
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.setConfirmClickListener {
                        sweetAlertDialog.dismissWithAnimation()


                        try {

                            val jsonObject = JsonObject()
                            jsonObject.addProperty("type", "disabled")
                            jsonObject.addProperty("campaignId", campaignData!!.data._id)


                            try {
                                postDataToServerOnDelete(jsonObject)

                            } catch (e: java.lang.Exception) {

                            }


                        } catch (e: java.lang.Exception) {
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
            else{
                ConstantMethods.showWarning(this,"","You are not owner of this campaign.")
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

                val userId = EasySP.init(this).getString(ConstantEasySP.USER_ID)
                if (userId == campaignData!!.data.user._id) {
                    ConstantMethods.showWarning(this,"","You can not abuse your campaign.")
                }

                else {

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
            try{
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
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }

        /** It closes the report abuse layout with animation */
        closeAbuse!!.setOnClickListener {
            reportAbuseLayout!!.visibility = View.GONE
            reportAbuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))
        }


        /** It closes the more layout with animation
         * get stored user id and check with gathering user id, if matches then  opens CreateCampaignActivity to edit a campaign
         * else show warning dialog */
        edit!!.setOnClickListener {

            isEdit = true

            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )

            val userId = EasySP.init(this).getString(ConstantEasySP.USER_ID)
            if (userId == campaignData!!.data.user._id) {


                val jsonString = Gson().toJson(campaignData)
                val intent = Intent(this, CreateCampaignActivity::class.java)
                intent.putExtra("campaignData", jsonString)
                intent.putExtra("edit", true)
                startActivityForResult(intent,200)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {

                ConstantMethods.showWarning(this,"","You are not owner of this campaign.")
            }

        }

        moreLayout!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )
        }

        /**It opens LocationActivity */
        locationLayout!!.setOnClickListener {
            val intent = Intent(context, LocationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            EasySP.init(context).put("lat",campaignData!!.data.location.coordinates[0])
            EasySP.init(context).put("long",campaignData!!.data.location.coordinates[1])
            EasySP.init(context).putBoolean("resolved",campaignData!!.data.issue.resolved)
        }


        /** It opens GatheringDetailsActivity  */
        gatheringNumber!!.setOnClickListener {
            val intent = Intent(this,GatheringDetailsActivity::class.java)
            intent.putExtra("gatheringId",campaignData!!.data.gathering._id)
            startActivity(intent)

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
                    ConstantMethods.showProgessDialog(
                        this,
                        "Please Wait..."
                    )
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "campaign")
                    jsonObject.addProperty("typeId",campaignId )
                    jsonObject.addProperty(
                        "data", ""+abuse
                    )

                    try {
                        if (ConstantMethods.checkForInternetConnection(this)) {
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
            if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }



    /** It takes the json object as input and send to onDelete function of campaign presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                campaignPresenter!!.onDelete(this, jsonObject)
            }


        } catch (e: Exception)
        {
            e.printStackTrace()
        }


    }


    /** It takes the json object as input and send to onCheckIn function of checkIn presenter */
    private fun postDataToServerOnCheckIn(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                checkInPresenter!!.onCheckIn(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    /** It takes the json object as input and send to onCheckIn function of checkIn presenter */
    private fun postDataToServerOnAlreadyCheckedIn(jsonObject: JsonObject) {

        try {

            try {
                if (ConstantMethods.checkForInternetConnection(this@CampaignDetailsActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    checkInPresenter!!.onCheckIn(this, jsonObject)
                }

            } catch (e: Exception)
            {
                e.printStackTrace()
            }


        } catch (e: Exception)

        {
            e.printStackTrace()


        }


    }

    /** ui initialization */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        campaignImage = findViewById(R.id.img_campaign)
        checkIn = findViewById(R.id.img_checkIn)
        alreadyCheckedIn = findViewById(R.id.img_checkedIn)
        going = findViewById(R.id.img_resolved)
        notGoing = findViewById(R.id.img_already_resolved)
        directions = findViewById(R.id.img_directions)
        more = findViewById(R.id.img_more)
        close = findViewById(R.id.img_close)
        closeAbuse = findViewById(R.id.img_close_window)
        play = findViewById(R.id.img_play)


        /** ids of text view */

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        gatheringNumber = findViewById(R.id.txt_gathering_id)
        //gatheringNumber!!.typeface = ConstantFonts.raleway_semibold

        gatheringNumberTitle = findViewById(R.id.txt_gathering_title)
        gatheringNumberTitle!!.typeface = ConstantFonts.raleway_semibold

        campaignLocation = findViewById(R.id.txt_campaign_location)
        campaignLocation!!.typeface = ConstantFonts.raleway_semibold

        campaignDate = findViewById(R.id.txt_campaign_date)
        campaignDate!!.typeface = ConstantFonts.raleway_regular

        campaignTitle = findViewById(R.id.txt_campaign_title)
        campaignTitle!!.typeface = ConstantFonts.raleway_semibold

        campaignMonth = findViewById(R.id.txt_month_title)
        campaignMonth!!.typeface = ConstantFonts.raleway_regular

        campaignDay = findViewById(R.id.txt_date_title)
        campaignDay!!.typeface = ConstantFonts.raleway_regular


        campaignDescription = findViewById(R.id.txt_campaign_description)
        campaignDescription!!.typeface = ConstantFonts.raleway_regular

        delete = findViewById(R.id.txt_delete)
        delete!!.typeface = ConstantFonts.raleway_semibold

        campaignGoingCount = findViewById(R.id.txt_going_count)
        campaignGoingCount!!.typeface = ConstantFonts.raleway_regular

        reportAbuse = findViewById(R.id.txt_report_abuse)
        reportAbuse!!.typeface = ConstantFonts.raleway_semibold

        edit = findViewById(R.id.txt_edit)
        edit!!.typeface = ConstantFonts.raleway_semibold

        goingTitle = findViewById(R.id.txt_resolved_title)
        goingTitle!!.typeface = ConstantFonts.raleway_regular

        directionTitle = findViewById(R.id.txt_direction_title)
        directionTitle!!.typeface = ConstantFonts.raleway_regular


        chechInTitle = findViewById(R.id.txt_checkIn_title)
        chechInTitle!!.typeface = ConstantFonts.raleway_regular


        moreTitle = findViewById(R.id.txt_more_title)
        moreTitle!!.typeface = ConstantFonts.raleway_regular



        /** ids of relative layout */
        moreLayout = findViewById(R.id.moreLayout)
        campaignGoingLayout = findViewById(R.id.campaignGoingLayout)
        reportAbuseLayout = findViewById(R.id.reportAbuseLayout)
        locationLayout = findViewById(R.id.campaignDetailsLayout)



        /** ids of recycler view */
        campaignGoingRecycler = findViewById(R.id.campaignGoingRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        campaignGoingRecycler!!.setHasFixedSize(true)
        campaignGoingRecycler!!.layoutManager = linearLayoutManager

        /** ids of edit text */

        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of button */
        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold




    }

    /** It checks the campaignDetails data,
     *  if it is not empty then set data to respective views
     */
    override fun setCampaignByIdAdapter(campaignDetails: CampaignPojo) {
        try {
            if (campaignDetails.data != null) {

                campaignData = campaignDetails

                if (campaignDetails.data.gathering.gatheringNumber != null) {
                    gatheringNumber!!.text = "# ${campaignDetails.data.gathering.gatheringNumber}"
                }
                if(campaignDetails.data.gathering._id != null)
                {
                    gatheringId = campaignDetails.data.gathering._id
                }

                if(campaignDetails.data.campaignNumber !=null)
                {
                    title!!.text = "Campaign #${campaignDetails.data.campaignNumber}"
                }
                if (campaignDetails.data.title != null) {
                    campaignTitle!!.text = campaignDetails.data.title
                }
                if (campaignDetails.data.address != null) {
                    campaignLocation!!.text = campaignDetails.data.address
                }
                if (campaignDetails.data.city != null) {
                    campaignLocation!!.text = campaignDetails.data.city
                }
                if (campaignDetails.data.address != null && campaignDetails.data.city != null) {
                    campaignLocation!!.text =
                        campaignDetails.data.address + ", ${campaignDetails.data.city}"
                }


                if (campaignDetails.data.campaignDate != null && campaignDetails.data.campaignDate.isNotEmpty()) {
                    campaignDate!!.text = ConstantMethods.convertStringToDateStringFull(campaignDetails.data.campaignDate)
                    campaignMonth!!.text = ConstantMethods.convertStringToMonthFull(campaignDate!!.text.toString())
                    campaignDay!!.text = ConstantMethods.convertStringToDateFull(campaignDate!!.text.toString())
                }


                if (campaignDetails.data.description != null && campaignDetails.data.description.isNotEmpty()) {
                    campaignDescription!!.text = campaignDetails.data.description
                }

                if (campaignDetails.data.imageUrls != null && campaignDetails.data.imageUrls.isNotEmpty()) {
                    Glide.with(this)
                        .load(campaignDetails.data.imageUrls[0])
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.loading_image)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(campaignImage!!)

                    play!!.visibility = View.GONE
                }
                else if(campaignDetails.data.videoUrls != null && campaignDetails.data.videoUrls.isNotEmpty())
                {
                    Glide
                        .with(this)
                        .load(campaignDetails.data.videoUrlThumbnails[0])
                        .placeholder(R.drawable.processing_video)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(campaignImage!!)

                    play!!.visibility = View.VISIBLE

                }
                else{
                    play!!.visibility = View.GONE
                    campaignImage!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.placeholder))
                }

                if(campaignDetails.data.isCheckedIn !=null)
                {
                    if(campaignDetails.data.isCheckedIn)
                    {
                        alreadyCheckedIn!!.visibility = View.VISIBLE
                        checkIn!!.visibility = View.GONE
                    }
                }
                else{
                    alreadyCheckedIn!!.visibility = View.GONE
                    checkIn!!.visibility = View.VISIBLE
                }


                if(campaignDetails.data.isGoing !=null)
                {
                    if(campaignDetails.data.isGoing)
                    {
                        notGoing!!.visibility = View.VISIBLE
                        going!!.visibility = View.GONE

                        goingTitle!!.setTextColor(ContextCompat.getColor(this,R.color.button_background))
                    }
                }
                else{
                    notGoing!!.visibility = View.GONE
                    going!!.visibility = View.VISIBLE

                    goingTitle!!.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
                }



                if(campaignDetails.data.isCheckedIn !=null)
                {
                    if(campaignDetails.data.isCheckedIn)
                    {
                        alreadyCheckedIn!!.visibility = View.VISIBLE
                        checkIn!!.visibility = View.GONE

                        chechInTitle!!.setTextColor(ContextCompat.getColor(this,R.color.button_background))
                    }
                }
                else{
                    alreadyCheckedIn!!.visibility = View.GONE
                    checkIn!!.visibility = View.VISIBLE

                    chechInTitle!!.setTextColor(ContextCompat.getColor(this,R.color.colorPrimary))
                }


                if(campaignDetails.data.goingList != null && campaignDetails.data.goingList.isNotEmpty())
                {
                    println("going list ${campaignDetails.data.goingList}")
                    campaignGoingCount!!.visibility = View.VISIBLE
                    goingArrayList!!.clear()
                    goingArrayList!!.addAll(campaignDetails.data.goingList)
                    campaignGoingLayout!!.visibility = View.VISIBLE
                    campaignGoingAdapter = CampaignGoingAdapter(this, goingArrayList!!,campaignDetails.data)
                    campaignGoingRecycler!!.adapter = campaignGoingAdapter

                    if(campaignDetails.data.goingListCount!=null && campaignDetails.data.goingListCount!=0)
                    {
                        campaignGoingCount!!.text = "${campaignDetails.data.goingListCount} people are going to the campaign"
                    }
                }
                else {
                    goingArrayList!!.clear()
                    campaignGoingCount!!.visibility = View.GONE
                    campaignGoingAdapter!!.notifyDataSetChanged()
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


        } catch (e:Exception) {
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
                    EasySP.init(this@CampaignDetailsActivity).getString(ConstantEasySP.SP_FCM_DEVICE_TOKEN)
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
    /** It opens activity depends on condition
     */
    override fun setOnDelete()
    {
        if(myCampaign)
        {
            val intent = Intent(this, MyCampaignActivity::class.java)
            intent.putExtra("delete", true)
            intent.putExtra("campaignId", campaignData!!.data._id)
            setResult(201, intent)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else {

            val intent = Intent(this, GatheringDetailsActivity::class.java)
            intent.putExtra("delete", true)
            intent.putExtra("campaignId", campaignData!!.data._id)
            setResult(201, intent)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

    }


    /** It call below functions using corresponding presenters */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == 201)

        {

            val campaignId = data!!.getStringExtra("campaignId")

            if (campaignId != null && campaignId.isNotEmpty()) {

                try {
                    if (ConstantMethods.checkForInternetConnection(this)) {
                           // ConstantMethods.showProgessDialog(this, "Please Wait...")
                        campaignPresenter!!.onCampaignById(this, campaignId)
                    }

                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }

            }


        }

    }

    /** It goes back to previous activity depends on condition   */
    override fun onBackPressed() {

       /* val intent = Intent(this, GatheringDetailsActivity::class.java)
        intent.putExtra("gatheringId",gatheringId)
        startActivity(intent)*/

        if(isEdit) {
            val intent = Intent()
            intent.putExtra("gatheringId", gatheringId)
            setResult(201, intent)
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
        else{
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        }
        super.onBackPressed()

    }

}

