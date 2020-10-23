package com.we.beyond.ui.issues.nearByIssue

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.Html
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.maps.GoogleMap
import com.google.android.libraries.places.api.Places
import com.google.firebase.database.DatabaseReference
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.we.beyond.adapter.NearByIssuesAdapter
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.R
import com.we.beyond.model.*
import com.we.beyond.presenter.issues.nearByIssues.NearByIssueImpl
import com.we.beyond.presenter.issues.nearByIssues.NearByIssuePresenter
import com.we.beyond.ui.AddLocationActivity
import com.we.beyond.ui.filter.FilterActivity
import com.we.beyond.ui.issues.submitIssue.SubmitAnIssueActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import java.util.*
import kotlin.collections.ArrayList
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter

/**
 * This Activity shows the all issue details
 * */

class NearByIssueActivity : AppCompatActivity(), NearByIssuePresenter.INearByIssueView,
    LikePresenter.ILikeView, OnLikeDislikeListener , OnDeleteListener,OnReportAbuseListener ,ReportAbusePresenter.IReportAbuseView{


    val context: Context = this

    var recyclerViewDate : Parcelable?=null

    /** initialize respected implementors */
    var nearByIssuePresenter: NearByIssueImpl? = null
    var likePresenter: LikeImpl? = null
    var reportAbusePresenter : ReportAbuseImpl?=null


    /** init image view */
    var filter: ImageView? = null
    var back: ImageView? = null
    var close : ImageView?=null
    var searchView : SearchView?=null

    /** init text view */
    var locationName: TextView? = null
    var title: TextView? = null
    var delete : TextView?=null
    var noContent : TextView?=null


    /** init button */
    var submitIssue: Button? = null
    var abuse : Button?=null

    /** init edit text */
    var abuseEditText : EditText?=null

    /** init relative layout */
    var searchLocationLayout: RelativeLayout? = null
    var abuseLayout : RelativeLayout?=null

    /** init recycler view */
    var nearByIssuesRecycler: RecyclerView? = null

    var nearByIssuesAdapter: NearByIssuesAdapter? = null
    var nearByIssueArray: ArrayList<NearByIssues>? = null

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
    var linearLayoutManager: LinearLayoutManager? = null

    /** initialize strings */
    var location: String? = null
    var city: String? = null
    var issueId : String =""

    /** initialize booleans */
    var isSelected: Boolean = false
    var isSearch : Boolean = false

    private lateinit var mMap: GoogleMap
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var rootRef: DatabaseReference


    companion object {
        val PAGE_SIZE = 5
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_near_by_issue)

        /** It hides the keyboard */
        ConstantMethods.hideKeyBoard(this,this)

        /** array initialization */
        nearByIssueArray = ArrayList()
        nearByIssueArray!!.clear()

        /** initialize implementation */
        nearByIssuePresenter = NearByIssueImpl(this)
        likePresenter = LikeImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)


        /** it checks the current location using google places library
         * and shows the current location address to text view
         * if current location wants to change then we can search it
         */
        if (!Places.isInitialized()) {
            Places.initialize(this, Constants.GOOGLE_API_KEY, Locale.ENGLISH)
        }

        /*// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)*/

        val getLatitude = EasySP.init(this).getString("lat").trim()
        val getLongitude = EasySP.init(this).getString("long").trim()


        /** It checks the stored latitude and longitude are available or not
         * if available then calling getAddress function using those parameter
         * else get current location's latitude and longitude and call
         * address api with this parameter
         * */
        if (getLatitude != null && getLatitude.isNotEmpty() && getLatitude.length > 0) {
            latitude = getLatitude.toDouble()
            longitude = getLongitude.toDouble()

            println("lat $latitude")
            getAddress(latitude, longitude)
        } else {

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

                    /* EasySP.init(context).put("lat", latitude)
                EasySP.init(context).put("long", longitude)*/

                    getAddress(latitude, longitude)

                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        /** initialize ids of elements */
        initElementsWithIds()

        /** get data to post */
        getDataToPost()

        /** initialize onclick listener */
        initWithListener()



        isSelected = intent.getBooleanExtra("gathering", false)
        println("is selected gathering $isSelected")

        /** It changes submit issue button visibility and title text depends on condition */
        if (isSelected) {
            title!!.text = "Link An Issue"
            submitIssue!!.visibility = View.GONE
            nearByIssuesRecycler!!.setPadding(0, 0, 0, 0)
        } else {
            title!!.text = "Issues"
            submitIssue!!.visibility = View.VISIBLE
            nearByIssuesRecycler!!.setPadding(0, 0, 0, 50)
        }


    }


    /* override fun onMapReady(googleMap: GoogleMap?) {
         try {
             mMap = googleMap!!
             mMap.clear()
             // Add a marker in Sydney and move the camera
             val location = LatLng(latitude, longitude)
             mMap.addMarker(
                 MarkerOptions().position(location).title(
                     getAddress(
                         latitude,
                         longitude
                     )
                 ).icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(R.drawable.map_pin, 60, 60)))
             )
             mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10.8f))


         } catch (e: Exception) {

         }
     }*/

    private fun resizeBitmap(mapPin: Int, width: Int, height: Int): Bitmap? {
        val imageBitmap = BitmapFactory.decodeResource(resources, mapPin)
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    /** It returns the address text
     * which takes the latitude and longitude as a parameter
     * and converts to address string using geo coder
     */
    private fun getAddress(latitude: Double, longitude: Double): String? {
        var address: String = ""
        try {
            var geocoder: Geocoder = Geocoder(context, Locale.getDefault())
            var addresses: ArrayList<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as ArrayList<Address>

            address = addresses.get(0).getAddressLine(0)
            var title: String = addresses.get(0).featureName

            location = addresses[0].featureName
            city = addresses[0].locality + " " + addresses[0].postalCode

            println("area name $location")

            //val getNewLocation  = EasySP.init(this).getString("location")
            // val getNewCity = EasySP.init(this).getString("city")

            // if(getNewLocation.isEmpty()) {
            locationName = findViewById(R.id.txt_location)
            locationName!!.text = "$location, $city"
            locationName!!.typeface = ConstantFonts.raleway_medium
            /* }
             else{
                 locationName = findViewById(R.id.txt_location)
                 locationName!!.text = "$getNewLocation, $getNewCity"
                 locationName!!.typeface = ConstantFonts.raleway_regular
             }*/

            EasySP.init(context).put("location", location)
            EasySP.init(context).put("city", addresses[0].locality)




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

    /** It opens the abuse layout with animation */
    override fun ReportAbuse(_id: String, s: String)
    {
        issueId = _id
        abuseLayout!!.visibility = View.VISIBLE
        abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_in_up))

    }

    /** It delete the specific near by issue object from array and notify to adapter */
    override fun setOnDelete(_id: String)
    {
        /*try{
            connectArray!!.clear()
            getDataToPost()
            connectAdapter!!.notifyDataSetChanged()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }*/
        val deleteIssueList = ArrayList<NearByIssues>()
        deleteIssueList.addAll(nearByIssueArray!!)

        val issueId = _id
        for( i in 0 until deleteIssueList.size)
        {

            if(deleteIssueList[i]._id == issueId)
            {
                deleteIssueList.remove(deleteIssueList[i])
                nearByIssueArray!!.clear()
                nearByIssueArray!!.addAll(deleteIssueList)
                nearByIssuesAdapter!!.notifyDataSetChanged()

                break
            }
        }

    }

    /** It calls postDataToServerOnDelete function with json object as parameter
     */
    override fun onDelete(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                postDataToServerOnDelete(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onDelete function of near by issue presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                nearByIssuePresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception)
        {
            e.printStackTrace()
        }


    }




    override fun summaryType(type: String) {

    }

    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "issue")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                postDataToServerOnLike(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onLike function of like presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                // ConstantMethods.showProgessDialog(this, "Please Wait...")
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
            jsonObject.addProperty("type", "issue")
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

    /** It takes the json object as input and send to onLike function of like presenter */
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
                    if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                        //ConstantMethods.showProgessDialog(this, "Please Wait...")
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


    /** it generate json object with required fields and send to postDataToServer function */
    private fun getDataToPost() {

        try {
            // val categoriesValue = EasySP.init(this).getStringSet("selectedFilterCategory")
            var sortByValue = EasySP.init(this).getString("sortBy")
            var radiusValue = EasySP.init(this).getString("radius")
            var radius = 0.0
            if (radiusValue.isNotEmpty()) {
                radius = radiusValue.toDouble()
                println("radius $radius")
            }
            //val lat = EasySP.init(this).getInt("lat")
            //val long = EasySP.init(this).getInt("long")
            val selectedCategoryIds =
                EasySP.init(this).getStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)

            if (sortByValue.isEmpty()) {
                sortByValue = "Newest"
            }
            if (radiusValue.isEmpty()) {
                radiusValue = "5.0"
                radius = 5.0
            }


            val selectedIdsArray = ArrayList<String>()
            selectedIdsArray.addAll(selectedCategoryIds)


            if (sortByValue!!.isNotEmpty() && selectedCategoryIds.isNotEmpty() && radiusValue != null && latitude != null && longitude != null) {
                if (selectedCategoryIds.isNotEmpty()) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("lat", latitude)
                    jsonObject.addProperty("long", longitude)
                    jsonObject.addProperty("radius", radius)
                    val jsonArray = JsonArray()
                    for (i in 0 until selectedCategoryIds.size) {

                        if (selectedIdsArray.isNotEmpty()) {

                            jsonArray.add(selectedIdsArray[i])

                        }

                    }

                    jsonObject.add("categories", jsonArray)
                    jsonObject.addProperty("sortBy", sortByValue)
                    jsonObject.addProperty(
                        "size",
                        PAGE_SIZE
                    )
                    jsonObject.addProperty("pageNo", pageNo)
                    println("json object $jsonObject")
                    if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                        postDataToServer(jsonObject)
                    }
                } else {

                    ConstantMethods.showWarning(
                        this,
                        "Category",
                        "Please select at least one category"
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It takes the json object as input and send to onNearByIssue function of near by Issue presenter */
    private fun postDataToServer(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                resetValue()
                nearByIssuePresenter!!.onNearByIssue(this, jsonObject)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onNearByIssueOnLoadMore function of near by issue presenter */
    private fun postDataToServerOnLoadMore(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                nearByIssuePresenter!!.onNearByIssueOnLoadMore(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the  as input and send to onLike function of near by like presenter */
    private fun postDataToServerOnSearch(text : String ,pageNo : Int,size : Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                resetValue()
                nearByIssuePresenter!!.onSearch(this, text,pageNo,size)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the required fields as input and send to onSearchLoadMore function of near by issue presenter */
    private fun postDataToServerOnSearchLoadMore(text : String ,pageNo : Int,size : Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                nearByIssuePresenter!!.onSearchLoadMore(this, text,pageNo,size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** ui listeners */
    private fun initWithListener() {

        /** It opens FilterActivity when click on it */
        filter!!.setOnClickListener {
            if (isSelected) {
                val intent = Intent(this, FilterActivity::class.java)
                intent.putExtra("gathering", true)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            } else {
                val intent = Intent(this, FilterActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }
        }

        /** It opens SubmitAnIssueActivity when click on it */
        submitIssue!!.setOnClickListener {

            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA,"")

            val intent = Intent(this, SubmitAnIssueActivity::class.java)
            startActivityForResult(intent,2)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            //finish()
        }

        /** It opens AddLocationActivity when click on it */
        searchLocationLayout!!.setOnClickListener {
            if (isSelected) {
                val intent = Intent(this, AddLocationActivity::class.java)
                intent.putExtra("gathering", true)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            } else {
                val intent = Intent(this, AddLocationActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()

            }

        }

        /** It goes back to previous fragment or activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            //finish()
        }

        /** Call load more api when scrolling the recycler view */
        nearByIssuesRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                visibleItemCount = nearByIssuesRecycler!!.getChildCount()
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
                    this@NearByIssueActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(context)) {

                                if(isSearch)
                                {
                                    loadMoreSearch()
                                }
                                else {
                                    loadMore()
                                }
                            }
                        }
                    })
                    // Do something
                    loading = true
                }
                println("loading")
            }
        })


        /** It closes the abuse layout with animation */
        close!!.setOnClickListener {
            abuseLayout!!.visibility = View.GONE
            abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))
        }

        /** It calls the getDataToPostOnAbuse function */
        abuse!!.setOnClickListener {
            try {
                abuseLayout!!.visibility = View.GONE
                abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))

                if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                    getDataToPostOnAbuse()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /** It calls again getDataToPost api when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            resetValue()
            getDataToPost()
            pullToRefresh!!.isRefreshing = false
        }

        /** It calls the postDataToServerOnSearch function  */
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchView!!.clearFocus()
                if(query!!.toString().length > 2)
                {
                    try{

                        isSearch = true
                        postDataToServerOnSearch(query.toString().trim(),pageNo, PAGE_SIZE)
                    }

                    catch (e : Exception)
                    {
                        e.printStackTrace()
                    }
                }

                else{
                    Toast.makeText(context,"short length",Toast.LENGTH_LONG).show()
                }

                searchView!!.clearFocus()
                ConstantMethods.hideKeyBoard(context,this@NearByIssueActivity)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.toString().length== 0)
                {
                    ConstantMethods.hideKeyBoard(context,this@NearByIssueActivity)

                    searchView!!.clearFocus()
                    isSearch = false
                    resetValue()
                    getDataToPost()
                }
                return false
            }

        })




    }

    /** It calls postDataToServerOnAbuse function with json object */
    private fun getDataToPostOnAbuse()
    {
        try{

            if(abuseEditText!!.text.trim() !=null && abuseEditText!!.text.trim().isNotEmpty())
            {
                var abuse = abuseEditText!!.text.trim()

                if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "issue")
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
            if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It calls postDataToServerOnSearchLoadMore function for loading searched data */
    private fun loadMoreSearch()
    {
        try{

            postDataToServerOnSearchLoadMore(searchView!!.query.toString().trim(),++page, PAGE_SIZE)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It calls postDataToServerOnLoadMore() with json object  */
    private fun loadMore() {
        try {

            var sortByValue = EasySP.init(this).getString("sortBy")
            var radiusValue = EasySP.init(this).getString("radius")
            var radius = 0.0
            if (sortByValue.isEmpty()) {
                sortByValue = "Newest"
            }
            if (radiusValue.isEmpty()) {
                radiusValue = "5.0"
                radius = 5.0
            }
            else{
                radius = radiusValue.toDouble()
            }


            println("radius $radius")
            //val lat = EasySP.init(this).getInt("lat")
            //val long = EasySP.init(this).getInt("long")
            val selectedCategoryIds =
                EasySP.init(this).getStringSet(ConstantEasySP.ISSUE_SELECTED_CATEGORY_ID)


            val selectedIdsArray = ArrayList<String>()
            selectedIdsArray.addAll(selectedCategoryIds)


            if (sortByValue!!.isNotEmpty() && selectedCategoryIds.isNotEmpty() && radiusValue != null && latitude != null && longitude != null) {
                if (selectedCategoryIds.isNotEmpty()) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("lat", latitude)
                    jsonObject.addProperty("long", longitude)
                    jsonObject.addProperty("radius", radius)
                    val jsonArray = JsonArray()
                    for (i in 0 until selectedCategoryIds.size) {

                        if (selectedIdsArray.isNotEmpty()) {

                            jsonArray.add(selectedIdsArray[i])

                        }

                    }

                    jsonObject.add("categories", jsonArray)
                    jsonObject.addProperty("sortBy", sortByValue)
                    jsonObject.addProperty("size", PAGE_SIZE)
                    jsonObject.addProperty("pageNo", ++page)
                    println("json object $jsonObject")

                    if (ConstantMethods.checkForInternetConnection(this@NearByIssueActivity)) {
                        postDataToServerOnLoadMore(jsonObject)
                    }
                } else {

                    ConstantMethods.showWarning(this, "Category", "Please select at least one category")
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image view */
        filter = findViewById(R.id.img_filter)
        back = findViewById(R.id.img_back)
        close = findViewById(R.id.img_close)
        searchView = findViewById(R.id.img_search)
        searchView!!.queryHint = (Html.fromHtml("<font color = #000000>" + "Search Issue"+ "</font>"))




        /** ids of text view */
        locationName = findViewById(R.id.txt_location)
        locationName!!.typeface = ConstantFonts.raleway_regular

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of recycler view */
        nearByIssuesRecycler = findViewById(R.id.nearByIssueRecycler)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        nearByIssuesRecycler!!.setHasFixedSize(true)
        nearByIssuesRecycler!!.layoutManager = linearLayoutManager



        /** ids of button */
        submitIssue = findViewById(R.id.btn_submit_an_issue)
        submitIssue!!.typeface = ConstantFonts.raleway_semibold

        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold

        /** ids of relative layout */
        searchLocationLayout = findViewById(R.id.locationLayout)
        abuseLayout = findViewById(R.id.reportAbuseLayout)

        /** ids of edit text */
        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)




    }


    /** It checks the nearByIssueDetails data,
     *  if it is not empty then set NearByIssuesAdapter
     *  else show warning dialog
     */
    override fun setNearByIssueAdapter(nearByIssueDetails: NearByIssuesPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (nearByIssueDetails.data.size == 0) {


            /*ConstantMethods.showWarning(
                this,
                "",
                "There are no issues found in this area for selected categories."
            )*/

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no issues found in this area for selected categories."


        } else {
            noContent!!.visibility = View.GONE

            nearByIssueArray!!.addAll(nearByIssueDetails.data)


            nearByIssuesAdapter = NearByIssuesAdapter(this, nearByIssueArray!!)
            nearByIssuesRecycler!!.adapter = nearByIssuesAdapter

        }


    }

    /** It checks the nearByIssueDetails data,
     *  if it is not empty then notify to NearByIssuesAdapter
     */
    override fun setNearByIssueAdapterOnLoadMore(nearByIssueDetails: NearByIssuesPojo) {

        if (nearByIssueDetails.data.size > 0) {

            nearByIssueArray!!.addAll(nearByIssueDetails.data)
            nearByIssuesAdapter!!.notifyDataSetChanged()
        }
    }


    /** It checks the nearByIssueDetails data,
     *  if it is not empty then set NearByIssuesAdapter
     *  else show warning dialog
     */
    override fun setNearByIssueOnSearchAdapter(nearByIssueDetails: NearByIssuesPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (nearByIssueDetails.data.size == 0) {


            /*ConstantMethods.showWarning(
                this,
                "",
                "There are no issues found in this area for selected categories."
            )*/

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no issues found in this area for selected categories."


        } else {
            noContent!!.visibility = View.GONE

            nearByIssueArray!!.addAll(nearByIssueDetails.data)


            nearByIssuesAdapter = NearByIssuesAdapter(this, nearByIssueArray!!)
            nearByIssuesRecycler!!.adapter = nearByIssuesAdapter

        }


    }

    /** It checks the nearByIssueDetails data,
     *  if it is not empty then notify NearByIssuesAdapter
     */
    override fun setNearByIssueOnSearchAdapterOnLoadMore(nearByIssueDetails: NearByIssuesPojo) {

        if (nearByIssueDetails.data.size > 0) {

            nearByIssueArray!!.addAll(nearByIssueDetails.data)


            nearByIssuesAdapter!!.notifyDataSetChanged()
        }


    }


    override fun goToNextScreen() {

    }

    override fun goToPreviousScreen() {

    }


    /** It checks the respective data,
     *  if it is not empty then notify to respective adapter
     *  else show warning dialog
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == 201)

        {
            val deleteIssueList = ArrayList<NearByIssues>()
            deleteIssueList.addAll(nearByIssueArray!!)

            val issueId = data!!.getStringExtra("issueId")

            for( i in 0 until deleteIssueList.size)
            {

                if(deleteIssueList[i]._id == issueId)
                {
                    deleteIssueList.remove(deleteIssueList[i])
                    nearByIssueArray!!.clear()
                    nearByIssueArray!!.addAll(deleteIssueList)
                    nearByIssuesAdapter!!.notifyDataSetChanged()

                    break
                }
            }

            if(nearByIssueArray!!.size == 0)
            {
                /*ConstantMethods.showWarning(
                    this,
                    "",
                    "There are no issues found in this area for selected categories."
                )*/

                noContent!!.visibility = View.VISIBLE
                noContent!!.text = "There are no issues found in this area for selected categories."


            }

        }
        else if (requestCode == 2 && resultCode == 201)
        {
            getDataToPost()
        }

        else if (requestCode == 200 && resultCode == 501)
        {
            val isLike = data!!.getBooleanExtra("isLikeByUser",false)
            var isDisLike = data!!.getBooleanExtra("isDislikeByUser",false)
            val issueId = data!!.getStringExtra("issueId")


            if(isLike) {
                val likeIssueList = ArrayList<NearByIssues>()
                likeIssueList.addAll(nearByIssueArray!!)



                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].likeByUser = isLike
                        nearByIssueArray!!.clear()
                        nearByIssueArray!!.addAll(likeIssueList)
                        nearByIssuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }
            }

            else{
                val likeIssueList = ArrayList<NearByIssues>()
                likeIssueList.addAll(nearByIssueArray!!)

                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].likeByUser = false
                        nearByIssueArray!!.clear()
                        nearByIssueArray!!.addAll(likeIssueList)
                        nearByIssuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }
            }

        }
        else if(requestCode == 200 && resultCode== Activity.RESULT_OK) {
            val issueId = data!!.getStringExtra("issueId")
            val reportedResolution = data!!.getBooleanExtra("reportedResolution", false)
            val reportedResolutionCount = data!!.getIntExtra("reportedCount", 0)

            if (reportedResolution) {
                val likeIssueList = ArrayList<NearByIssues>()
                likeIssueList.addAll(nearByIssueArray!!)



                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].resolved = false
                        likeIssueList[i].resolutionsCount = reportedResolutionCount
                        nearByIssueArray!!.clear()
                        nearByIssueArray!!.addAll(likeIssueList)
                        nearByIssuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }
            } else {
                val likeIssueList = ArrayList<NearByIssues>()
                likeIssueList.addAll(nearByIssueArray!!)

                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].resolved = true
                        nearByIssueArray!!.clear()
                        nearByIssueArray!!.addAll(likeIssueList)
                        nearByIssuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }
            }
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
        nearByIssueArray!!.clear()
    }


    /* override fun onResume() {

         *//** get data to post *//*
        getDataToPost()

        super.onResume()

    }
*/

    /** It goes back to previous activity of fragment   */
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        // finish()
    }
}
