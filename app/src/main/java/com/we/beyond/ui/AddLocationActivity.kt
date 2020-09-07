package com.we.beyond.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.interceptor.ApplicationController.Companion.context
import com.we.beyond.model.LatLongPojo
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.ui.campaign.createCampaign.CreateCampaignActivity
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.ui.gathering.createGathering.CreateGatheringActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueActivity
import com.we.beyond.ui.profile.EditProfileActivity
import com.we.beyond.ui.registration.RegistrationActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.activity_add_location.*
import java.util.*
import kotlin.collections.ArrayList

/** It is used to add the location */
class AddLocationActivity : AppCompatActivity() ,OnMapReadyCallback
{
    /** initialize google map */
    private lateinit var mMap: GoogleMap

    /** init double*/
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    /** init firebase database reference */
    lateinit var rootRef: DatabaseReference

    /** init strings */
    var location: String? = null
    var city: String? = null

    /** init image view */
    var back : ImageView?=null
    var recenter: ImageView?=null

    /** init text view */
    var title : TextView?=null

    var updatedLocation : LatLng?=null

    /** init booleans */
    var isSelected : Boolean = false
    var isSelectedLocation : Boolean = false
    var isSelectedCampaignLocation : Boolean = false
    var isDashboard : Boolean = false
    var isHomeLocation : Boolean = false
    var isUpdateHomeLocation : Boolean = false

    /** init button */
    var addLocation : Button?=null




    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_location)


        /** Get stored data using intent and assign it */
        isSelectedLocation = intent.getBooleanExtra("gatheringLocation",false)
        isSelectedCampaignLocation = intent.getBooleanExtra("campaignLocation",false)
        isSelected = intent.getBooleanExtra("gathering",false)
        isDashboard = intent.getBooleanExtra("dashboard",false)
        isHomeLocation = intent.getBooleanExtra("homeLocation",false)
        isUpdateHomeLocation = intent.getBooleanExtra("updateHomeLocation",false)


        back = findViewById(R.id.img_back)
        recenter = findViewById(R.id.img_recenter)

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        addLocation = findViewById(R.id.btn_add_location)

        /** It goes back to previous activity depends on condition */
        back!!.setOnClickListener {

            if(isSelectedLocation){
                val intent = Intent(this, CreateGatheringActivity::class.java)
                intent.putExtra("gatheringLocation",false)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if(isSelectedCampaignLocation) {
                val intent = Intent(this, CreateCampaignActivity::class.java)
                intent.putExtra("campaignLocation", false)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }

            else if(isSelected){
                val intent = Intent(this, NearByIssueActivity::class.java)
                intent.putExtra("gathering",true)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if (isDashboard)
            {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("dashboard",true)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if (isHomeLocation)
            {
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.putExtra("homeLocation",false)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if (isUpdateHomeLocation)
            {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("updateHomeLocation",false)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else {
                val intent = Intent(this, NearByIssueActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }

        /** It opens corresponding activity depends on condition */
        addLocation!!.setOnClickListener {

            if (isSelectedLocation) {
                val intent = Intent(this, CreateGatheringActivity::class.java)
                intent.putExtra("gatheringLocation", false)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else if (isSelectedCampaignLocation) {
                val intent = Intent(this, CreateCampaignActivity::class.java)
                intent.putExtra("campaignLocation", false)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else if (isSelected) {
                val intent = Intent(this, NearByIssueActivity::class.java)
                intent.putExtra("gathering", true)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if (isDashboard)
            {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("dashboard",true)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if (isHomeLocation)
            {
                val intent = Intent(this, RegistrationActivity::class.java)
                intent.putExtra("homeLocation",false)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if (isUpdateHomeLocation)
            {
                val intent = Intent(this, EditProfileActivity::class.java)
                intent.putExtra("updateHomeLocation",false)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else {
                val intent = Intent(this, NearByIssueActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
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

                    EasySP.init(context).put("lat", latitude)
                    EasySP.init(context).put("long", longitude)

                    val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
                    EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)

                    getAddress(latitude, longitude)

                    onMapReady(mMap)
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
            Places.initialize(context.applicationContext, Constants.GOOGLE_API_KEY, Locale.ENGLISH)
            //Places.initialize(context.applicationContext, this.resources.getString(R.string.google_api_key), Locale.ENGLISH)
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

                val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
                EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)


                onMapReady(mMap)

            }

            override fun onError(p0: Status) {
                //Toast.makeText(context, "${p0.status}", Toast.LENGTH_LONG).show()
            }
        })


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)



        val getLatitude = EasySP.init(this).getString("lat").trim()
        val getLongitude = EasySP.init(this).getString("long").trim()



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

                    EasySP.init(context).put("lat", latitude)
                    EasySP.init(context).put("long", longitude)

                    val json1 = Gson().toJson(LatLongSelectedPojo(latitude, longitude))
                    EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO, json1)

                    getAddress(latitude, longitude)


                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /** It will ready the google map with animation
     * get slider value and create circle according to it with animation  */
    override fun onMapReady(googleMap: GoogleMap?) {

        try {

            mMap = googleMap!!
            mMap.clear()
            mMap.mapType=GoogleMap.MAP_TYPE_HYBRID

            // Add a marker in Sydney and move the camera
            val location = LatLng(latitude, longitude)
            updatedLocation = location


            mMap.setOnCameraMoveListener {
                updatedLocation = mMap.cameraPosition.target


                latitude = mMap.cameraPosition.target.latitude
                longitude =  mMap.cameraPosition.target.longitude

                EasySP.init(context).put("lat", mMap.cameraPosition.target.latitude)
                EasySP.init(context).put("long", mMap.cameraPosition.target.longitude)

            }


            mMap.setOnCameraIdleListener {

                getAddress(latitude,longitude)

                val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
                EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)
            }

            getAddress(latitude,longitude)


            val cameraPosition = CameraPosition.builder()
            cameraPosition.target(updatedLocation)
            cameraPosition.zoom(20f)
            cameraPosition.bearing(90f)
            cameraPosition.tilt(30f)
            cameraPosition.build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))


        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            var geocoder: Geocoder = Geocoder(context, Locale.getDefault())
            var addresses: ArrayList<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as ArrayList<Address>

            address = addresses.get(0).getAddressLine(0)
            var title: String = addresses.get(0).featureName

            location = addresses[0].featureName
            city = addresses[0].locality + " "+addresses[0].postalCode


            EasySP.init(context).put("location", location)
            EasySP.init(context).put("city", addresses[0].locality)

            println("locality ${city}")

            EasySP.init(context).put(ConstantEasySP.SELECTED_GATHERING_ADDRESS,address)
            EasySP.init(context).put(ConstantEasySP.SELECTED_CAMPAIGN_ADDRESS,address)
            EasySP.init(context).put(ConstantEasySP.HOME_LOCATION_ADDRESS,address)

            println("location $location")


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

    /** It goes back to previous activity depends on condition */
    override fun onBackPressed() {
        super.onBackPressed()
        if(isSelectedLocation){
            val intent = Intent(this, CreateGatheringActivity::class.java)
            intent.putExtra("gatheringLocation",false)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if (isSelectedCampaignLocation)
        {
            val intent = Intent(this, CreateCampaignActivity::class.java)
            intent.putExtra("campaignLocation",false)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        else  if(isSelected){
            val intent = Intent(this, NearByIssueActivity::class.java)
            intent.putExtra("gathering",true)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if (isDashboard)
        {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("dashboard",true)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if (isHomeLocation)
        {
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra("homeLocation",false)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if (isUpdateHomeLocation)
        {
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("updateHomeLocation",false)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else {
            val intent = Intent(this, NearByIssueActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }


    }

}
