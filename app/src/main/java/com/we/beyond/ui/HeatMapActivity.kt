package com.we.beyond.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.we.beyond.R
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.presenter.heatMap.HeatMapImpl
import com.we.beyond.presenter.heatMap.HeatMapPresenter
import com.we.beyond.presenter.issues.nearByIssues.NearByIssueByIdImpl
import com.we.beyond.presenter.issues.nearByIssues.NearByIssueByIdPresenter
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import java.util.*
import kotlin.collections.ArrayList

/** It will show issues on map with different types of maps  */
class HeatMapActivity : AppCompatActivity(), OnMapReadyCallback, HeatMapPresenter.IHeatMapView,
    NearByIssueByIdPresenter.INearByIssueByIdView,
    GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter {

    override fun setOnDelete() {

    }

    var context: Context = this

    /** initialize respected implementors */
    var heatMapPresenter: HeatMapImpl? = null
    var newByIssuePresenter: NearByIssueByIdImpl? = null
    var heatMapData: HeatMapData? = null

    /** initialize google map */
    private lateinit var mMap: GoogleMap

    /** init double*/
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    /** init firebase database reference */
    lateinit var rootRef: DatabaseReference


    var location: String? = null
    var city: String? = null

    /** init image view */
    var back: ImageView? = null
    var recenter: ImageView? = null

    /** init text view */
    var title: TextView? = null

    /** init button */
    var isSelected: Boolean = false
    var mapType : Button?=null
    var hybridType : Button?=null
    var satelliteType : Button?=null

    var mapViewType : Int?=null

    /** init relative layout */
    // var issueDecriptionLayout : RelativeLayout?=null

    var imageUrl: String = ""
    var jsonString: String = ""
    var issueId: String = ""


    /** init array list and model */
    var snippetArray: ArrayList<String>? = null
    var locationArrayList: ArrayList<HeatMapData>? = null
    var markerDataList: NearByIssueByIdDetailsPojo? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heat_map)

        /** array initialization */
        locationArrayList = ArrayList()
        locationArrayList!!.clear()
        snippetArray = ArrayList()
        snippetArray!!.clear()


        /** initialize implementation */
        heatMapPresenter = HeatMapImpl(this)
        newByIssuePresenter = NearByIssueByIdImpl(this)

        /** ui initialization */
        back = findViewById(R.id.img_back)
        recenter = findViewById(R.id.img_recenter)

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        // issueDecriptionLayout = findViewById(R.id.issueDescriptionLayout)

        mapType = findViewById(R.id.btn_map)
        mapType!!.typeface = ConstantFonts.raleway_regular

        hybridType = findViewById(R.id.btn_hybrid)
        hybridType!!.typeface = ConstantFonts.raleway_regular

        satelliteType = findViewById(R.id.btn_satellite)
        satelliteType!!.typeface = ConstantFonts.raleway_regular

        mapViewType = GoogleMap.MAP_TYPE_NORMAL

        /** It select map type and change background and text color and call onMapReady()*/
        mapType!!.setOnClickListener {
            mapViewType = GoogleMap.MAP_TYPE_NORMAL
            mapType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.button_background)
            mapType!!.setTextColor(ContextCompat.getColor(this, R.color.white))

            hybridType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.white)
            hybridType!!.setTextColor(ContextCompat.getColor(this, R.color.issue_posted_color))

            satelliteType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.white)
            satelliteType!!.setTextColor(ContextCompat.getColor(this, R.color.issue_posted_color))

            onMapReady(mMap)
        }

        /** It select map type and change background and text color and call onMapReady()*/
        hybridType!!.setOnClickListener {
            mapViewType = GoogleMap.MAP_TYPE_HYBRID
            mapType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.white)
            mapType!!.setTextColor(ContextCompat.getColor(this, R.color.issue_posted_color))

            hybridType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.button_background)
            hybridType!!.setTextColor(ContextCompat.getColor(this, R.color.white))

            satelliteType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.white)
            satelliteType!!.setTextColor(ContextCompat.getColor(this, R.color.issue_posted_color))

            onMapReady(mMap)
        }

        /** It select map type and change background and text color and call onMapReady()*/
        satelliteType!!.setOnClickListener {
            mapViewType = GoogleMap.MAP_TYPE_SATELLITE
            mapType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.white)
            mapType!!.setTextColor(ContextCompat.getColor(this, R.color.issue_posted_color))

            hybridType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.white)
            hybridType!!.setTextColor(ContextCompat.getColor(this, R.color.issue_posted_color))

            satelliteType!!.backgroundTintList=ContextCompat.getColorStateList(this,R.color.button_background)
            satelliteType!!.setTextColor(ContextCompat.getColor(this, R.color.white))

            onMapReady(mMap)
        }


        /** It goes back to DashboardActivity */
        back!!.setOnClickListener {

            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        }

        /** It reset the location on google map */
        recenter!!.setOnClickListener {
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

                    EasySP.init(ApplicationController.context).put("lat", latitude)
                    EasySP.init(ApplicationController.context).put("long", longitude)

                    val json1 = Gson().toJson(LatLongSelectedPojo(latitude, longitude))
                    EasySP.init(ApplicationController.context)
                        .putString(ConstantEasySP.LATLONG_POJO, json1)

                    getAddress(latitude, longitude)

                    //onMapReady(mMap)
                    heatMapPresenter!!.OnRequestHeatMapList(
                        this,
                        latitude.toString(),
                        longitude.toString()
                    )


                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



        /** it checks the current location using google places library
         * and shows the current location address to text view
         * if current location wants to change then we can search it
         * */
        if (!Places.isInitialized()) {
            Places.initialize(
                ApplicationController.context.applicationContext,
                Constants.GOOGLE_API_KEY, Locale.ENGLISH
            )
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        try {

            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f,
                locationListener
            )

            val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
            if (latlong != null && latlong.isNotEmpty()) {
                val latLongPojo: LatLongSelectedPojo =
                    Gson().fromJson(latlong, LatLongSelectedPojo::class.java)

                latitude = latLongPojo.latitude
                longitude = latLongPojo.longitude

                getAddress(latitude, longitude)
            }
            else{

                var currentLocation = getLastKnownLocation()
//
                latitude = currentLocation!!.latitude
                longitude = currentLocation.longitude

                getAddress(latitude, longitude)

            }

            heatMapPresenter!!.OnRequestHeatMapList(
                this,
                latitude.toString(),
                longitude.toString()
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It calls onNearByIssueById function of newByIssue presenter  */
    override fun onInfoWindowClick(p0: Marker?) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                newByIssuePresenter!!.onNearByIssueById(this, p0!!.id)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** It will set nearByIssueDetails to markerDataList and call onMapReady() */
    override fun setNearByIssueByIdAdapter(nearByIssueDetails: NearByIssueByIdDetailsPojo) {
        if (nearByIssueDetails != null) {
            markerDataList = nearByIssueDetails

            onMapReady(mMap)
        }
    }

    /** It will set locationList to locationArrayList and call onMapReady() */
    override fun setLocationListAdapter(locationList: ArrayList<HeatMapData>) {
        if (locationList != null) {
            locationArrayList = locationList

            onMapReady(mMap)
        }
    }

    /** It will ready the google map with animation */
    override fun onMapReady(googleMap: GoogleMap?) {

        try {
            mMap = googleMap!!
            mMap.clear()
            mMap.mapType=mapViewType!!


            for (i in 0 until locationArrayList!!.size) {

                val height = 100
                val width = 100
                val bitmapdraw = getResources().getDrawable(R.drawable.green_flag) as BitmapDrawable
                val b = bitmapdraw.getBitmap()
                val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

                val height1 = 100
                val width1 = 100
                val bitmapdrawRed =
                    getResources().getDrawable(R.drawable.red_flag) as BitmapDrawable
                val b1 = bitmapdrawRed.getBitmap()
                val smallMarkerRed = Bitmap.createScaledBitmap(b1, width1, height1, false)


                jsonString = Gson().toJson(locationArrayList!![i])

                if (locationArrayList!![i].resolved) {
                    mMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                locationArrayList!![i].coordinates[0],
                                locationArrayList!![i].coordinates[1]
                            )
                        ).title(

                            locationArrayList!![i].title


                        ).icon(

                            BitmapDescriptorFactory.fromBitmap(smallMarker)
                        ).snippet(
                            jsonString
                        )
                    ).isInfoWindowShown


                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                latitude,
                                longitude
                            ), 12f
                        )
                    )

                } else {
                    mMap.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                locationArrayList!![i].coordinates[0],
                                locationArrayList!![i].coordinates[1]
                            )
                        ).title(

                            locationArrayList!![i].title
                        ).icon(

                            BitmapDescriptorFactory.fromBitmap(smallMarkerRed)
                        ).snippet(
                            jsonString
                        )
                    ).isInfoWindowShown


                    val location = LatLng(latitude, longitude)

                    val cameraPosition = CameraPosition.builder()
                    cameraPosition.target(location)
                    cameraPosition.zoom(12f)
                    cameraPosition.bearing(90f)
                    cameraPosition.tilt(30f)
                    cameraPosition.build()

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))


                }


            }


            /*  mMap.setOnInfoWindowClickListener {



                  val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                  intent.putExtra("issueId", issueId)
                  intent.putExtra("heatmap",true)
                  startActivity(intent)
                  overridePendingTransition(
                      R.anim.slide_in_right,
                      R.anim.slide_out_left
                  )

              }

             */

            mMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                override fun onMarkerClick(p0: Marker?): Boolean {
                    try {



                        /*   p0!!.showInfoWindow()
                           mMap.setInfoWindowAdapter(this@HeatMapActivity)
   */
                        val heatMapData = Gson().fromJson(p0!!.snippet, HeatMapData::class.java)


                        issueId = heatMapData._id


                        val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                        intent.putExtra("issueId", issueId)
                        intent.putExtra("heatmap",true)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )



                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    return true

                }


            })


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It returns view */
    override fun getInfoContents(p0: Marker?): View {
        val v = View.inflate(this@HeatMapActivity, R.layout.custom_marker_info_window, null)
        /*  val v = View.inflate(this@HeatMapActivity, R.layout.custom_marker_info_window, null)


          val heatMapData = Gson().fromJson(p0!!.snippet,HeatMapData::class.java)


          issueId = heatMapData._id


               v.setBackgroundColor(ContextCompat.getColor(this,R.color.button_background))


                  val progressBar = v.findViewById<ProgressBar>(R.id.progressBar)

                  // p0!!.title= locationArrayList!![0].title

                  val issueTitle = v.findViewById<TextView>(R.id.txt_issue_title)
                  issueTitle!!.typeface = ConstantFonts.raleway_regular
                  issueTitle.text = p0.title

                  val distance = v.findViewById<TextView>(R.id.txt_issue_distance)
                  distance!!.text = " ${heatMapData.distance} Km away"
                  distance.typeface = ConstantFonts.raleway_regular


                  val issueImage = v.findViewById<ImageView>(R.id.img_issue)


          if(heatMapData.imageUrls.isNotEmpty() && heatMapData.imageUrls!=null) {

              imageUrl = heatMapData.imageUrls[0]
              println("image url $imageUrl")

          progressBar.visibility = View.VISIBLE
                  Picasso.with(context)
                      .load(imageUrl)
                      //.placeholder(R.drawable.progress_animation)
                      //.memoryPolicy(MemoryPolicy.NO_CACHE)
                      //.networkPolicy(NetworkPolicy.NO_CACHE)
                      //.resize(400, 400)  // optional
                      .into(issueImage, object : Callback {
                          override fun onSuccess() {
                              progressBar.visibility = View.GONE
                          }

                          override fun onError() {
                          }

                      })
             }
          else{
                      issueImage!!.setImageResource(R.drawable.background)
          }

          return v
  */


        return v
    }

    /** It returns view and set heat map id to issue id */
    override fun getInfoWindow(p0: Marker?): View? {

        val v = View.inflate(this@HeatMapActivity, R.layout.custom_marker_info_window, null)


        val heatMapData = Gson().fromJson(p0!!.snippet, HeatMapData::class.java)


        issueId = heatMapData._id


        /* //v.setBackgroundColor(ContextCompat.getColor(this,R.color.button_background))
         v.background = (ContextCompat.getDrawable(this, R.drawable.bgorange))


         val progressBar = v.findViewById<ProgressBar>(R.id.progressBar)

         // p0!!.title= locationArrayList!![0].title

         val issueTitle = v.findViewById<TextView>(R.id.txt_issue_title)
         issueTitle!!.typeface = ConstantFonts.raleway_regular
         issueTitle.text = p0.title

         val distance = v.findViewById<TextView>(R.id.txt_issue_distance)

         val play = v.findViewById<ImageView>(R.id.img_play)

         val issueImage = v.findViewById<ImageView>(R.id.img_issue)



         distance!!.text = " ${String.format("%2f",heatMapData.distance)} Km away"
         distance.typeface = ConstantFonts.raleway_regular




         if (heatMapData.imageUrls != null && heatMapData.imageUrls.isNotEmpty()) {

             println("image url ${heatMapData.imageUrls}")

             Glide.with(context)
                 .load(heatMapData.imageUrls[0])
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                 .placeholder(R.drawable.loading_image)
                 .into(issueImage)

             play.visibility = View.GONE

         } else if (heatMapData.videoUrlThumbnails != null && heatMapData.videoUrlThumbnails.isNotEmpty()) {
             Glide
                 .with(context)
                 .load(heatMapData.videoUrlThumbnails[0])
                 .diskCacheStrategy(DiskCacheStrategy.ALL)
                 .into(issueImage)

             play.visibility = View.VISIBLE

         }


         else {
             issueImage!!.setImageResource(R.drawable.placeholder)
         }*/

        return v


    }


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
            var geocoder: Geocoder = Geocoder(ApplicationController.context, Locale.getDefault())
            var addresses: ArrayList<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as ArrayList<Address>

            address = addresses.get(0).getAddressLine(0)
            var title: String = addresses.get(0).featureName

            location = addresses[0].featureName
            city = addresses[0].locality + " " + addresses[0].postalCode

            println("location $location")


        } catch (e: Exception) {

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
                    EasySP.init(ApplicationController.context)
                        .getString(ConstantEasySP.SP_FCM_DEVICE_TOKEN)
                if (deviceFcmToken != null && deviceFcmToken.length > 0) {
                    val latLongPojo =
                        LatLongPojo(location.latitude, location.longitude, deviceFcmToken)
                    rootRef.setValue(latLongPojo)
                }

            } catch (e: Exception) {

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

    /** It goes back to previous activity */
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()


    }


}
