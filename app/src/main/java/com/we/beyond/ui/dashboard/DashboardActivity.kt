package com.we.beyond.ui.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.*
import android.location.LocationListener
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.card.MaterialCardView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.DashboardPojo
import com.we.beyond.model.LatLongPojo
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.presenter.dashboard.DashboardImpl
import com.we.beyond.presenter.dashboard.DashboardPresenter
import com.we.beyond.ui.HeatMapActivity
import com.we.beyond.ui.campaign.campaignDetails.CampaignDetailsActivity
import com.we.beyond.ui.connect.connectIssue.ConnectDetailsActivity
import com.we.beyond.ui.connect.connectIssue.ConnectIssueActivity
import com.we.beyond.ui.connect.publishConnect.PublishConnectActivity
import com.we.beyond.ui.gathering.gathering.GatheringActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.ui.issues.submitIssue.SubmitAnIssueActivity
import com.we.beyond.ui.leaderBoard.LeaderBoardActivity
import com.we.beyond.ui.nearByMe.NearByMeActivity
import com.we.beyond.ui.profile.UserProfileActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import java.util.*
import kotlin.collections.ArrayList


class DashboardActivity : AppCompatActivity() , DashboardPresenter.IDashboardView{

    /** initialize implementors */
    var dashboardPresenter : DashboardImpl?=null
    var context : Context = this

    /** init image view */
    var notify : ImageView?=null
    var notifyGif : ImageView?=null
    var notification : ImageView?=null

    /** init relative layout */
    var issuesLayout: RelativeLayout? = null
    var issuesPostedLayout: RelativeLayout? = null
    var gatheringLayout: RelativeLayout? = null
    var gatheringAllLayout: RelativeLayout? = null
    var connectLayout: RelativeLayout? = null
    var connectOverAllLayout: RelativeLayout? = null
    var heatmapLayout: RelativeLayout? = null
    var leaderboardLayout: RelativeLayout? = null
    var profileLayout: RelativeLayout? = null
    var locationLayout : RelativeLayout?=null
    var nearByMeLayout : RelativeLayout?=null


    /** init linear layout */
    var issuePosted : LinearLayout?=null
    var issueResolved : LinearLayout?=null
    var gatheringNearby : LinearLayout?=null
    var newInfoPublished : LinearLayout?=null

    /** init text view */
    var dashboardTitle: TextView? = null
    var issues: TextView? = null
    var issuePostedText : TextView?=null
    var gathering: TextView? = null
    var gatheringOverAll: TextView? = null
    var connect: TextView? = null
    var connectOverAll: TextView? = null
    var heatMap: TextView? = null
    var leaderBoard: TextView? = null
    var profile: TextView? = null
    var issuePostedCount : TextView?=null
    var issuePostedTitle : TextView?=null
    var issuePostedKm : TextView?=null
    var issueResolvedCount : TextView?=null
    var issueResolvedTitle : TextView?=null
    var issueResolvedKm : TextView?=null
    var gatheringsNearByCount : TextView?=null
    var gatheringsNearByTitle : TextView?=null
    var gatheringsNearByKm : TextView?=null
    var newInfoPublishedCount : TextView?=null
    var newInfoPublishedTitle : TextView?=null
    var newInfoPublishedKm : TextView?=null
    var allIssuesCount : TextView?=null
    var allGatheringCount : TextView?=null
    var hello : TextView?=null
    var name : TextView?=null
    var nearByMe : TextView?=null
    // var locationTitle : TextView?=null

    /** pull to refresh layout */
    var pullToRefresh : SwipeRefreshLayout?=null

    /** init material card view */
    /* var issuePosted : MaterialCardView?=null
     var issueResolved : MaterialCardView?=null
     var gatheringNearby : MaterialCardView?=null
     var newInfoPublished : MaterialCardView?=null*/

    var summaryCard : MaterialCardView?=null


    var selectedCategotyId = HashSet<String>()

    /** init button */
    var submitIssue: Button? = null
    var createGathering: Button? = null
    var publishConnect: Button? = null

    var REQUEST_ID_MULTIPLE_PERMISSIONS = 1
    var REQUEST_ID_ENABLE_SETTINGS = 2
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var rootRef: DatabaseReference

    var mPermissionsGranted = false

    var notificationBell : Boolean = false
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        firebaseAnalytics = Firebase.analytics
        /** hide the keyboard when this activity open up */
        ConstantMethods.hideKeyBoard(this,this)

        /** initialize dashboard implementation */
        dashboardPresenter = DashboardImpl(this)


        /** check runtime app permission from user  */
        checkAndRequestPermissions()

        /** check runtime permission to get the current location */
//        displayLocationSettingsRequest(this)

        /** initialize ids of elements */
        initElementsWithIds()



        val getLatitude = EasySP.init(this).getString("lat").trim()
        val getLongitude = EasySP.init(this).getString("long").trim()

        /** it checks the current location using google places library
         * and shows the current location address to text view
         * if current location wants to change then we can search it
         * */
        if (!Places.isInitialized()) {
            Places.initialize(ApplicationController.context.applicationContext, Constants.GOOGLE_API_KEY, Locale.ENGLISH)
        }
        // from
        val autocompleteSupportFragmentfrom =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_from) as AutocompleteSupportFragment

        autocompleteSupportFragmentfrom.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )
        autocompleteSupportFragmentfrom.setHint("Type location to search")
        autocompleteSupportFragmentfrom.setOnPlaceSelectedListener(object :
            PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                val location = p0.latLng
                latitude = location!!.latitude
                longitude = location.longitude

                EasySP.init(context).put("lat", latitude)
                EasySP.init(context).put("long", longitude)
                EasySP.init(context).put(ConstantEasySP.DASHBOARD_LATITUDE,latitude)
                EasySP.init(context).put(ConstantEasySP.DASHBOARD_LONGITUDE,longitude)

                val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
                EasySP.init(ApplicationController.context).putString(ConstantEasySP.LATLONG_POJO,json1)

            }

            override fun onError(p0: Status) {
//                Toast.makeText(context, "${p0.status}", Toast.LENGTH_LONG).show()
            }
        })


        /** It checks the stored latitude and longitude are available or not
         * if available then calling getAddress function using those parameter
         * else get current location's latitude and longitude and call
         * address api with this parameter
         * */
        if (getLatitude != null && getLatitude.isNotEmpty() && getLatitude.length > 0) {
            latitude = getLatitude.toDouble()
            longitude = getLongitude.toDouble()

            getAddress(latitude, longitude)

        } /*else {
            *//** get current location *//*
            getLocation()
        }*/

        val getNotificationCount = EasySP.init(this).getInt("")
        if(getNotificationCount !=null)
        {

        }
        else{

        }

        /** clear all data in shared */
        clearData()

        /** initialize ui onclick listeners */
        initWithListener()


    }

    /** It gets the current location using gps provider,
     * call the getAddress function with latitude and longitude of current location as a parameter
     * and get the dashboard summary details using summary api with these parameter
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {

            var currentLocation = getLastKnownLocation()
            if (currentLocation != null) {
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude

                EasySP.init(context).put(ConstantEasySP.DASHBOARD_LATITUDE,latitude)
                EasySP.init(context).put(ConstantEasySP.DASHBOARD_LONGITUDE,longitude)

                getAddress(latitude, longitude)

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {

                        EasySP.init(context).remove(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)
                        EasySP.init(context).put("lat", latitude)
                        EasySP.init(context).put("long", longitude)
                        dashboardPresenter!!.getSummary(
                            this,
                            latitude.toString(),
                            longitude.toString()
                        )
                    }
                }
                catch ( e : Exception)
                {
                    e.printStackTrace()
                }

            }
            else
            {
                val locationManager =
                    this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    6000,
                    1f,
                    locationListener
                )
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It returns the address text
     * which takes the latitude and longitude as a parameter
     * and converts to address string using geo coder
     */
    private fun getAddress(latitude: Double, longitude: Double): String? {
        var address: String = ""
        try {
            var geocoder: Geocoder = Geocoder(ApplicationController.context, Locale.getDefault())
            var addresses: ArrayList<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as ArrayList<Address>

            address = addresses.get(0).getAddressLine(0)
            var title: String = addresses.get(0).featureName

            //locationTitle!!.text = address

            val autocompleteSupportFragmentfrom =
                supportFragmentManager.findFragmentById(R.id.autocomplete_fragment_from) as AutocompleteSupportFragment

            autocompleteSupportFragmentfrom.setPlaceFields(
                Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG
                )
            )

            autocompleteSupportFragmentfrom.setText(address)


        } catch (e: Exception) {
            e.printStackTrace()
        }
        return address
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
                latitude = location.latitude
                longitude = location.longitude

                EasySP.init(context).put(ConstantEasySP.DASHBOARD_LATITUDE,latitude)
                EasySP.init(context).put(ConstantEasySP.DASHBOARD_LONGITUDE,longitude)

                getAddress(latitude, longitude)

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {

                        EasySP.init(context).remove(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)

                        dashboardPresenter!!.getSummary(
                            this@DashboardActivity,
                            latitude.toString(),
                            longitude.toString()
                        )
                        ConstantMethods.showProgessDialog(this@DashboardActivity, "Please wait...")
                        dashboardPresenter!!.getSummaryOnResume(
                            this@DashboardActivity,
                            latitude.toString(),
                            longitude.toString()
                        )
                    }
                }
                catch ( e : Exception)
                {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    /** It returns the last known location  */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        var mLocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationManager =
            this.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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

    /** It have summary data as a input to set data to text views
     * and store the category array
     */
    override fun setSummaryData(summary: DashboardPojo)
    {
        if(summary != null)
        {
            issuePostedCount!!.text = summary.data.unresolvedIssueCount.toString()
            issueResolvedCount!!.text = summary.data.resolvedIssueCount.toString()
            gatheringsNearByCount!!.text = summary.data.upcomingGatheringCount.toString()
            newInfoPublishedCount!!.text = summary.data.publishedConnectCount.toString()
            allIssuesCount!!.text = summary.data.allIssuesCount!!.toString()
            allGatheringCount!!.text= summary.data.allGatheringsCount!!.toString()
            selectedCategotyId.addAll(summary.data.user.categories)


            EasySP.init(context).putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID,selectedCategotyId)
            EasySP.init(context).putInt(ConstantEasySP.SELECTED_FILTER_CATEGORY_SIZE,selectedCategotyId.size)
            EasySP.init(context).putString(ConstantEasySP.USER_ID,summary.data.user._id)

            if (summary.data.user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {

                name!!.text = summary.data.user.firstName + " ${summary.data.user.lastName}"
                name!!.typeface = ConstantFonts.raleway_regular
            }
            else{
                name!!.text = summary.data.user.organizationName
                name!!.typeface = ConstantFonts.raleway_regular
            }


        }
    }

    /** It have summary data as a input to set data to text views after resume
     * and remove previous stored summary data and store the category array again
     */
    override fun setSummeryDataOnResume(summary: DashboardPojo) {

        if(summary != null)
        {
            issuePostedCount!!.text = summary.data.unresolvedIssueCount.toString()
            issueResolvedCount!!.text = summary.data.resolvedIssueCount.toString()
            gatheringsNearByCount!!.text = summary.data.upcomingGatheringCount.toString()
            newInfoPublishedCount!!.text = summary.data.publishedConnectCount.toString()
            allIssuesCount!!.text = summary.data.allIssuesCount!!.toString()
            allGatheringCount!!.text= summary.data.allGatheringsCount!!.toString()
            selectedCategotyId.addAll(summary.data.user.categories)

            EasySP.init(context).remove(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)
            EasySP.init(context).putStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID,selectedCategotyId)
            EasySP.init(context).remove(ConstantEasySP.SELECTED_FILTER_CATEGORY_SIZE)
            EasySP.init(context).putInt(ConstantEasySP.SELECTED_FILTER_CATEGORY_SIZE,selectedCategotyId.size)
            EasySP.init(context).remove(ConstantEasySP.USER_ID)
            EasySP.init(context).putString(ConstantEasySP.USER_ID,summary.data.user._id)

            if (summary.data.user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {

                name!!.text = summary.data.user.firstName + " ${summary.data.user.lastName}"
                name!!.typeface = ConstantFonts.raleway_regular
            }
            else{
                name!!.text = summary.data.user.organizationName
                name!!.typeface = ConstantFonts.raleway_regular
            }


            // var mp = MediaPlayer()
            if(summary.data.user.isNotficationReceived)
            {
                try {

                    notification!!.visibility = View.VISIBLE
                    notifyGif!!.visibility= View.VISIBLE

                    //notify!!.startAnimation(AnimationUtils.loadAnimation(context,R.anim.shake))

                    Glide.with(this)
                        .load(R.raw.bell_animation)
                        .into(notifyGif!!)




                    /*  mp = MediaPlayer.create(context,R.raw.bellring)
                      mp.start()
  */
                    //handler for delay to hold the splash screen
                    Handler().postDelayed(object : Runnable {
                        override fun run() {
                            notify!!.visibility= View.VISIBLE
                            notifyGif!!.clearAnimation()
                            notifyGif!!.visibility= View.GONE
                        }
                    }, 5000.toLong())

                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }

            }
            else{
                notification!!.visibility = View.GONE

                notifyGif!!.clearAnimation()
                notify!!.visibility= View.VISIBLE
                notifyGif!!.visibility= View.GONE
                //mp.release()
            }

        }
    }

    /** It clears the stored data  */
    private fun clearData() {
        EasySP.init(this).put("categoryId", "")
        EasySP.init(this).put("issueTitle", "")
        EasySP.init(this).put("issueDetails", "")
        EasySP.init(this).putStringSet("image", null)
        EasySP.init(this).putStringSet("latlong", null)
        EasySP.init(this).put("locationAddress", "")
        EasySP.init(this).put("city", "")
        EasySP.init(this).put("lat", "")
        EasySP.init(this).put("long", "")
        EasySP.init(this).put("gatheringTitle","")
        EasySP.init(this).put("gatheringDetails","")
        EasySP.init(this).put("campaignTitle","")
        EasySP.init(this).put("campaignDetails","")
        EasySP.init(this).put("location","")
        EasySP.init(this).put(ConstantEasySP.SELECTED_GATHERING_ADDRESS,"")
        EasySP.init(this).put(ConstantEasySP.SELECTED_CAMPAIGN_ADDRESS,"")
        EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA,"")
        EasySP.init(this).put(ConstantEasySP.GATHERING_DATE,"")
        EasySP.init(this).put(ConstantEasySP.ISSUE_ID,"")

    }

    /** ui listeners */
    private fun initWithListener() {

        /** It opens SubmitAnIssueActivity when click on it */
        submitIssue!!.setOnClickListener {

            val intent = Intent(this, SubmitAnIssueActivity::class.java)
            intent.putExtra("dashboard",true)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /** It opens SubmitAnIssueActivity when click on it */
        issuesLayout!!.setOnClickListener {

            val intent = Intent(this, SubmitAnIssueActivity::class.java)
            intent.putExtra("dashboard",true)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            firebaseAnalytics.logEvent("submit_issue") {
                param("button_name", "Submit issue")
                param("action_triggered", "user wants to submit issue")
            }
        }

        /** It opens NearByIssueActivity when click on it */
        issuesPostedLayout!!.setOnClickListener {

            EasySP.init(this).put("lat",latitude)
            EasySP.init(this).put("long",longitude)

            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("gathering",false)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /** It opens PublishConnectActivity when click on it */
        publishConnect!!.setOnClickListener {
            val intent = Intent(this, PublishConnectActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }
        /** It opens PublishConnectActivity when click on it */
        connectLayout!!.setOnClickListener {
            val intent = Intent(this, PublishConnectActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

        /** It opens ConnectIssueActivity when click on it */
        connectOverAllLayout!!.setOnClickListener {
            val intent = Intent(this, ConnectIssueActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

        /** It opens CreateGatheringActivity when click on it */
        createGathering!!.setOnClickListener {

            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("gathering",true)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /** It opens CreateGatheringActivity when click on it */
        gatheringLayout!!.setOnClickListener {
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("gathering",true)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            firebaseAnalytics.logEvent("share_image") {
                param("button_name", "Create gathering")
                param("action_triggered", "user wants to create gatherings")
            }
        }

        /** It opens GatheringActivity when click on it */
        gatheringAllLayout!!.setOnClickListener {
            val intent = Intent(this, GatheringActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

        /** It checks the issue posted count and if count is not zero then
         * It opens SummaryDetailsActivity when click on it
         * else shows the warning dialog
         * */
        issuePosted!!.setOnClickListener {
            if(issuePostedCount!!.text == "0")
            {
                ConstantMethods.showToast(this,"No issues posted in past 30 days.")
            }
            else {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UNRESOLVED_ISSUES)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)


            }
        }

        /** It checks the issue resolved count and if count is not zero then
         * It opens SummaryDetailsActivity when click on it
         * else shows the warning dialog
         * */
        issueResolved!!.setOnClickListener {
            if(issueResolvedCount!!.text == "0")
            {
                ConstantMethods.showToast(this,"No issues resolved in past 30 days.")
            }
            else {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.RESOLVED_ISSUES)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            }
        }

        /** It checks the gathering near by count and if count is not zero then
         * It opens SummaryDetailsActivity when click on it
         * else shows the warning dialog
         * */
        gatheringNearby!!.setOnClickListener {
            if(gatheringsNearByCount!!.text == "0")
            {
                ConstantMethods.showToast(this,"No gathering arranged in past 30 days.")
            }
            else {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UPCOMING_GATHERING)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            }
        }

        /** It check the new info publish count and if count is not zero then
         * It opens SummaryDetailsActivity when click on it
         * else shows the warning dialog
         * */
        newInfoPublished!!.setOnClickListener {
            if(newInfoPublishedCount!!.text == "0")
            {
                ConstantMethods.showToast(this,"No new info published in past 30 days.")
            }
            else {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.PUBLISH_CONNECT)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            }
        }

        /** It opens UserProfileActivity when click on it */
        profileLayout!!.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        /** It opens NearByMeActivity when click on it */
        nearByMeLayout!!.setOnClickListener {
            val intent = Intent(this, NearByMeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        /** It opens HeatMapActivity when click on it */
        heatmapLayout!!.setOnClickListener {
            val intent = Intent(this, HeatMapActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        /** It opens LeaderBoardActivity when click on it */
        leaderboardLayout!!.setOnClickListener {
            val intent = Intent(this, LeaderBoardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        /** It clears the notification animation and
         * opens NotificationActivity when click on it
         */
        notify!!.setOnClickListener {
            EasySP.init(context).put("notification",false)
            notification!!.visibility = View.GONE

            notifyGif!!.clearAnimation()
            notify!!.visibility= View.VISIBLE
            notifyGif!!.visibility= View.GONE
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /** It calls again summary api when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please wait...")

                    val coarseLocation =
                        ContextCompat.checkSelfPermission(
                            this@DashboardActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    val fineLocation =
                        ContextCompat.checkSelfPermission(
                            this@DashboardActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    if(fineLocation == PackageManager.PERMISSION_GRANTED && coarseLocation == PackageManager.PERMISSION_GRANTED && locationEnabled())
                    {
                       getLocation()
                    }
                    else {
                        dashboardPresenter!!.getSummaryOnResume(
                            this,
                            latitude.toString(),
                            longitude.toString()
                        )
                    }
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

            pullToRefresh!!.isRefreshing = false
        }

        /* locationLayout!!.setOnClickListener {
             val intent = Intent(this, AddLocationActivity::class.java)
             intent.putExtra("dashboard",true)
             startActivity(intent)
             overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
             finish()
         }*/

    }

    /** ui initialization
     * set fonts to text views
     * */
    private fun initElementsWithIds() {

        /** ids of image view */
        notify = findViewById(R.id.img_notify)
        notifyGif = findViewById(R.id.img_notify_gif)
        notification = findViewById(R.id.img_notification)

        /** ids of text view */
        dashboardTitle = findViewById(R.id.txt_dashboard_title)
        dashboardTitle!!.typeface = ConstantFonts.raleway_semibold

        issues = findViewById(R.id.txt_issue)
        issues!!.typeface = ConstantFonts.raleway_semibold

        issuePostedText = findViewById(R.id.txt_issue_posted)
        issuePostedText!!.typeface = ConstantFonts.raleway_semibold

        gathering = findViewById(R.id.txt_gathering)
        gathering!!.typeface = ConstantFonts.raleway_semibold

        gatheringOverAll = findViewById(R.id.txt_gathering_oveall)
        gatheringOverAll!!.typeface = ConstantFonts.raleway_semibold

        connect = findViewById(R.id.txt_connect)
        connect!!.typeface = ConstantFonts.raleway_semibold

        connectOverAll = findViewById(R.id.txt_connect_overAll)
        connectOverAll!!.typeface = ConstantFonts.raleway_semibold

        heatMap = findViewById(R.id.txt_heatmap)
        heatMap!!.typeface = ConstantFonts.raleway_semibold

        leaderBoard = findViewById(R.id.txt_leaderboard)
        leaderBoard!!.typeface = ConstantFonts.raleway_semibold

        profile = findViewById(R.id.txt_profile)
        profile!!.typeface = ConstantFonts.raleway_semibold

        issuePostedCount = findViewById(R.id.txt_issue_posted_count)
        issuePostedCount!!.typeface = ConstantFonts.raleway_semibold

        issuePostedTitle = findViewById(R.id.txt_issue_posted_title)
        issuePostedTitle!!.typeface = ConstantFonts.raleway_regular

        issuePostedKm = findViewById(R.id.txt_issue_posted_km)
        issuePostedKm!!.typeface = ConstantFonts.raleway_semibold

        issueResolvedCount = findViewById(R.id.txt_issue_resolved_count)
        issueResolvedCount!!.typeface = ConstantFonts.raleway_semibold

        issueResolvedTitle = findViewById(R.id.txt_issue_resolved_title)
        issueResolvedTitle!!.typeface = ConstantFonts.raleway_regular

        issueResolvedKm = findViewById(R.id.txt_issue_resolved_km)
        issueResolvedKm!!.typeface = ConstantFonts.raleway_semibold

        gatheringsNearByCount = findViewById(R.id.txt_gathering_nearby_count)
        gatheringsNearByCount!!.typeface = ConstantFonts.raleway_semibold

        gatheringsNearByTitle = findViewById(R.id.txt_gathering_nearby_title)
        gatheringsNearByTitle!!.typeface = ConstantFonts.raleway_regular

        gatheringsNearByKm = findViewById(R.id.txt_gathering_nearby_km)
        gatheringsNearByKm!!.typeface = ConstantFonts.raleway_semibold

        newInfoPublishedCount = findViewById(R.id.txt_new_info_published_count)
        newInfoPublishedCount!!.typeface = ConstantFonts.raleway_semibold

        newInfoPublishedTitle = findViewById(R.id.txt_new_info_published_title)
        newInfoPublishedTitle!!.typeface = ConstantFonts.raleway_regular

        newInfoPublishedKm = findViewById(R.id.txt_new_info_published_km)
        newInfoPublishedKm!!.typeface = ConstantFonts.raleway_semibold
        allIssuesCount =findViewById(R.id.txtIssueCnt)
        allGatheringCount=findViewById(R.id.txtGatheringCnt)
        allIssuesCount!!.typeface = ConstantFonts.raleway_semibold
        allGatheringCount!!.typeface = ConstantFonts.raleway_semibold
        hello = findViewById(R.id.txt_hello)
        hello!!.typeface = ConstantFonts.raleway_semibold

        name = findViewById(R.id.txt_name)
        name!!.typeface = ConstantFonts.raleway_regular

        nearByMe = findViewById(R.id.txt_nearByMe)
        nearByMe!!.typeface = ConstantFonts.raleway_semibold

        /* locationTitle = findViewById(R.id.txt_location)
         locationTitle!!.typeface = ConstantFonts.raleway_regular*/

        /** ids of relative layout */
        issuesLayout = findViewById(R.id.issuesLayout)
        issuesPostedLayout = findViewById(R.id.issuesPostedLayout)
        gatheringLayout = findViewById(R.id.gatheringLayout)
        gatheringAllLayout = findViewById(R.id.gatheringOverallLayout)
        connectLayout = findViewById(R.id.connectLayout)
        connectOverAllLayout = findViewById(R.id.connectOverAllLayout)
        heatmapLayout = findViewById(R.id.heatMapLayout)
        leaderboardLayout = findViewById(R.id.leaderboardLayout)
        profileLayout = findViewById(R.id.profileLayout)
        locationLayout = findViewById(R.id.locationLayout)
        nearByMeLayout = findViewById(R.id.nearByMeLayout)

        /** ids of button */
        submitIssue = findViewById(R.id.btn_submit)
        submitIssue!!.typeface = ConstantFonts.raleway_semibold

        createGathering = findViewById(R.id.btn_create)
        createGathering!!.typeface = ConstantFonts.raleway_semibold

        publishConnect = findViewById(R.id.btn_publish)
        publishConnect!!.typeface = ConstantFonts.raleway_semibold

        /** ids of material card view */
        summaryCard = findViewById(R.id.summaryCardLayout)

        issuePosted = findViewById(R.id.issuePostedLayout)
        issueResolved = findViewById(R.id.issueResolvedLayout)
        gatheringNearby = findViewById(R.id.gatheringsNearbyLayout)
        newInfoPublished = findViewById(R.id.newInfoPublishedLayout)

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)
    }


    /** It is runtime permission result
     * and call the getLocation method
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mPermissionsGranted = true

        val isAccessLocationPermission:Boolean=grantResults[0]== PackageManager.PERMISSION_GRANTED
        val isCourseLocationPermission:Boolean=grantResults[1]== PackageManager.PERMISSION_GRANTED

        if(isAccessLocationPermission && isCourseLocationPermission)
        {
            displayLocationSettingsRequest(this@DashboardActivity)
        }
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                // Check the result of each permission granted
                for (grantResult in grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionsGranted = false
                    }
                }
            }
        }
    }


    /** check the runtime permissions  */
    private fun checkAndRequestPermissions(): Boolean {
        val coarseLocation =
            ContextCompat.checkSelfPermission(
                this@DashboardActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        val fineLocation =
            ContextCompat.checkSelfPermission(
                this@DashboardActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        var camera = ContextCompat.checkSelfPermission(
            this@DashboardActivity,
            Manifest.permission.CAMERA
        )

        var read = ContextCompat.checkSelfPermission(
            this@DashboardActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        var write = ContextCompat.checkSelfPermission(
            this@DashboardActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val listPermissionsNeeded: ArrayList<Any> = ArrayList()
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA)
        }
         if(fineLocation == PackageManager.PERMISSION_GRANTED && fineLocation == PackageManager.PERMISSION_GRANTED)
         {
             displayLocationSettingsRequest(this@DashboardActivity)
         }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (write != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this@DashboardActivity,
                listPermissionsNeeded.toArray(arrayOfNulls(listPermissionsNeeded.size)),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )

            return false
        }

        return true

    }


    /**  */
    private fun displayLocationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
        val locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(10000)
        locationRequest.setFastestInterval(10000 / 2)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback(object : ResultCallback<LocationSettingsResult> {
            override fun onResult(result: LocationSettingsResult) {
                val status = result.getStatus()
                when (status.getStatusCode()) {
                    LocationSettingsStatusCodes.SUCCESS -> {
                        getLocation()
                        Log.i(
                        "error",
                        "All location settings are satisfied."
                    )
                    }
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            "error",
                            "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                        )
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(
                                this@DashboardActivity,
                                REQUEST_ID_ENABLE_SETTINGS
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.i("error", "PendingIntent unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                        "error",
                        "Location settings are inadequate, and cannot be fixed here. Dialog not created."
                    )
                }
            }
        })
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_ID_ENABLE_SETTINGS)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                getLocation()
            }
        }
    }

    private fun startLocationUpdates() {

    }


    /** It resumes the notification animation and summary data
     * and open respective activity according to type and type id
     */
    override fun onResume() {

        super.onResume()
        try {
            val getLatitude = EasySP.init(this).getString("lat").trim()
            val getLongitude = EasySP.init(this).getString("long").trim()
            if (getLatitude != null && getLatitude.isNotEmpty() && getLatitude.length > 0) {
                latitude = getLatitude.toDouble()
                longitude = getLongitude.toDouble()

                getAddress(latitude, longitude)

            }
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA,"")

            notificationBell = EasySP.init(this).getBoolean("notification")

            println("notification $notificationBell")

            if(notificationBell)
            {
                notification!!.visibility = View.VISIBLE
                notifyGif!!.visibility= View.VISIBLE
                notify!!.visibility = View.GONE

                Glide.with(this)
                    .load(R.raw.bell_animation)
                    .into(notifyGif!!)

                Handler().postDelayed(object : Runnable {
                    override fun run() {
                        notify!!.visibility= View.VISIBLE
                        notifyGif!!.clearAnimation()
                        notifyGif!!.visibility= View.GONE
                    }
                }, 5000.toLong())

                if (ConstantMethods.checkForInternetConnection(context) && (!"0.0".equals(latitude.toString())) && (!"0.0".equals(longitude.toString()))) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    dashboardPresenter!!.getSummaryOnResume(
                        this,
                        latitude.toString(),
                        longitude.toString()
                    )
                }

            }
            else{
                if (ConstantMethods.checkForInternetConnection(context) && (!"0.0".equals(latitude.toString())) && (!"0.0".equals(longitude.toString()))) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    dashboardPresenter!!.getSummaryOnResume(
                        this,
                        latitude.toString(),
                        longitude.toString()
                    )
                }
            }


            val type = intent.getStringExtra("type")
            val typeId = intent.getStringExtra(Constants.COMMENT_ID)
            val issue = intent.getStringExtra("issueId")
            val gathering = intent.getStringExtra("gatheringId")
            val connect = intent.getStringExtra("connectCategoryId")

            if(type !=null) {
                println("type $type id $typeId")

                when (type) {
                    "issue" -> {
                        val intent = Intent(this, NearByIssueDetailsActivity::class.java)
                        intent.putExtra("issueId", typeId)
                        intent.putExtra("notification", true)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()


                    }

                    "resolution" -> {
                        val intent = Intent(this, NearByIssueDetailsActivity::class.java)
                        intent.putExtra("issueId", issue)
                        intent.putExtra("notification", true)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                    }

                    "gathering" -> {
                        val intent = Intent(this, GatheringDetailsActivity::class.java)
                        intent.putExtra("gatheringId", typeId)
                        intent.putExtra("notification", true)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        //finish()
                    }


                    "issue-campaign" -> {
                        val intent = Intent(this, CampaignDetailsActivity::class.java)
                        intent.putExtra("campaignId", typeId)
                        intent.putExtra("notification", true)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                    }


                    "gathering-campaign" -> {
                        val intent = Intent(this, CampaignDetailsActivity::class.java)
                        intent.putExtra("campaignId", typeId)
                        intent.putExtra("notification", true)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                    }

                    "campaign" -> {
                        val intent = Intent(this, CampaignDetailsActivity::class.java)
                        intent.putExtra("campaignId", typeId)
                        intent.putExtra("notification", true)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                    }


                    "connect" -> {
                        val intent = Intent(this, ConnectDetailsActivity::class.java)
                        intent.putExtra("connectCategoryId", typeId)
                        intent.putExtra("notification", true)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                    }


                    "comment" -> {
                        if (issue != null) {
                            //val intent = Intent(context, CommentActivity::class.java)
                            val intent = Intent(this, NearByIssueDetailsActivity::class.java)
                            intent.putExtra(Constants.COMMENT_ID, typeId)
                            intent.putExtra("issueId", issue)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                            println("issue")

                        } else if (connect != null) {
                            //val intent = Intent(context, ConnectCommentActivity::class.java)
                            val intent = Intent(this, ConnectDetailsActivity::class.java)
                            intent.putExtra("connectCategoryId", connect)
                            intent.putExtra(Constants.COMMENT_ID, typeId)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()

                        } else if (gathering != null) {
                            //val intent = Intent(context, ConnectCommentActivity::class.java)
                            val intent = Intent(this, GatheringDetailsActivity::class.java)
                            intent.putExtra("gatheringId", gathering)
                            intent.putExtra(Constants.COMMENT_ID, typeId)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()

                        }

                    }


                    else -> {

                        if (ConstantMethods.checkForInternetConnection(context) && (!"0.0".equals(latitude.toString())) && (!"0.0".equals(longitude.toString()))) {
                            ConstantMethods.showProgessDialog(this, "Please wait...")
                            dashboardPresenter!!.getSummaryOnResume(
                                this,
                                latitude.toString(),
                                longitude.toString()
                            )
                        }
                    }
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }


    private fun locationEnabled(): Boolean{
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gpsStatus
    }
}
