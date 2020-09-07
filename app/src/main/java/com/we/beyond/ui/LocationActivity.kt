package com.we.beyond.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.LatLongPojo
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import java.util.*

/** It will show location on map */
class LocationActivity : AppCompatActivity(), OnMapReadyCallback {
    var context: Context = this

    /** initialize google map */
    private lateinit var mMap: GoogleMap

    /** init double*/
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    /** init firebase database reference */
    lateinit var rootRef: DatabaseReference

    /** init image view */
    var back: ImageView? = null
    var recenter: ImageView? = null

    /** init text view */
    var title: TextView? = null

    /** init boolean */
    var resolved: Boolean = false

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        /** ui initialization
         * get stored data using easysp
         * ui listeners
         * */
        val getLatitude = EasySP.init(this).getString("lat").trim()
        val getLongitude = EasySP.init(this).getString("long").trim()

        if (getLatitude != null && getLatitude.isNotEmpty() && getLatitude.length > 0) {
            latitude = getLatitude.toDouble()
            longitude = getLongitude.toDouble()
        }
        resolved = EasySP.init(this).getBoolean("resolved")

        back = findViewById(R.id.img_back)
        recenter = findViewById(R.id.img_recenter)

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold


        /** It goes back to previous activity */
        back!!.setOnClickListener {

            onBackPressed()

        }

        /** It reset the location on google map */
        recenter!!.setOnClickListener {
            try {

                onMapReady(mMap)

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


    }

    /** It will ready the google map with animation */
    override fun onMapReady(googleMap: GoogleMap?) {

        try {
            mMap = googleMap!!
            mMap.clear()
            mMap.mapType = GoogleMap.MAP_TYPE_HYBRID


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



            if (resolved) {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            latitude,
                            longitude
                        )
                    ).icon(

                        BitmapDescriptorFactory.fromBitmap(smallMarker)
                    )
                )

                val location = LatLng(latitude, longitude)

                val cameraPosition = CameraPosition.builder()
                cameraPosition.target(location)
                cameraPosition.zoom(12f)
                cameraPosition.bearing(90f)
                cameraPosition.tilt(30f)
                cameraPosition.build()

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))

            } else {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            latitude,
                            longitude
                        )
                    ).icon(

                        BitmapDescriptorFactory.fromBitmap(smallMarkerRed)
                    )
                )

                val location = LatLng(latitude, longitude)

                val cameraPosition = CameraPosition.builder()
                cameraPosition.target(location)
                cameraPosition.zoom(12f)
                cameraPosition.bearing(90f)
                cameraPosition.tilt(30f)
                cameraPosition.build()

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
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

    /** It will call previous activity */
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()

    }

}
