package com.we.beyond.ui.issues.submitIssue.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson

import com.we.beyond.R
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.LatLongPojo
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.model.NearByIssueByIdDetailsPojo
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList

/** It will select the location to add issue */
class IssueLocationFragment : Fragment() ,OnMapReadyCallback{

    /** initialize google map */
    private lateinit var mMap: GoogleMap

    /** init double*/
    var latitude :Double = 0.0
    var longitude :Double = 0.0

    /** init firebase database reference */
    lateinit var rootRef: DatabaseReference

    /** init array list */
    var latlongArray : ArrayList<LatLongSelectedPojo>?=null

    /** init image view */
    var recenterLocation : ImageView?=null

    /** init model */
    var issueData : NearByIssueByIdDetailsPojo?=null
    var updatedLocation : LatLng?=null



    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_location, container, false)

        /** array initialization */
        latlongArray = ArrayList()
        latlongArray!!.clear()

        /** ui initialization */
        recenterLocation = v.findViewById(R.id.img_recenter)

        /** It reset the location on google map */
        recenterLocation!!.setOnClickListener {
            try {

                val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, locationListener)
                var currentLocation = getLastKnownLocation()
//            latitude = currentLocation.latitude
//            longitude = currentLocation.longitude
                if (currentLocation != null) {
                    latitude = currentLocation.latitude
                    longitude = currentLocation.longitude

                    latlongArray!!.clear()
                    latlongArray!!.add(LatLongSelectedPojo(latitude,longitude))
                   // latlongArray!!.add(longitude)

                    val latlongSet  = HashSet<String>()

                    for(i in 0 until latlongArray!!.size) {
                        latlongSet.add(latlongArray!![i].toString())
                    }


                    //latlongArray!!.add(LatLongSelectedPojo(latitude,longitude))

                    val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
                    EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)

                    println("latlong set $latlongArray")
                    EasySP.init(context).putStringSet("latlong",latlongSet)

                    EasySP.init(ApplicationController.context).put("lat", latitude)
                    EasySP.init(ApplicationController.context).put("long", longitude)

                    getAddress(latitude, longitude)

                    onMapReady(mMap)

                }


            } catch (e: Exception) {

            }
        }

        /** it checks the current location using google places library
         * and shows the current location address to text view
         * if current location wants to change then we can search it
         * */
        if (!Places.isInitialized()) {
            Places.initialize(context!!.applicationContext, Constants.GOOGLE_API_KEY, Locale.ENGLISH)
        }


        // from
        val autocompleteSupportFragmentfrom =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment_from) as AutocompleteSupportFragment

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

                latlongArray!!.clear()
                //latlongArray!!.add(latitude)
                // //latlongArray!!.add(longitude)
                latlongArray!!.add(LatLongSelectedPojo(latitude,longitude))

                val latlongSet  = HashSet<String>()

                    for(i in 0 until latlongArray!!.size) {
                        latlongSet.add(latlongArray!![i].toString())
                    }


                val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
                EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)


                println("latlong set $latlongArray")
                EasySP.init(context).putStringSet("latlong",latlongSet)
                //EasySP.init(context).put("latlong",latlongArray!!)

                onMapReady(mMap)
                //Toast.makeText(this@DistanceActivity, "${p0.name}", Toast.LENGTH_LONG).show()
            }

            override fun onError(p0: Status) {
                Toast.makeText(context!!, "${p0.status}", Toast.LENGTH_LONG).show()
            }
        })


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        try {

            val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, locationListener)
            var currentLocation = getLastKnownLocation()
//            latitude = currentLocation.latitude
//            longitude = currentLocation.longitude
            if (currentLocation != null) {
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude

                latlongArray!!.clear()
                //latlongArray!!.add(latitude)
                ///latlongArray!!.add(longitude)

                val latlongSet  = HashSet<String>()

                for(i in 0 until latlongArray!!.size) {
                    latlongSet.add(latlongArray!![i].toString())
                }

                val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
                EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)

                println("latlong set $latlongArray")
                EasySP.init(context).putStringSet("latlong",latlongSet)

                EasySP.init(ApplicationController.context).put("lat", latitude)
                EasySP.init(ApplicationController.context).put("long", longitude)

                getAddress(latitude, longitude)

            }


        } catch (e: Exception) {

        }


        /** Get all stored data using intent and assign it respectively and call getAddress() */
        val getIntentData = arguments!!.getString("issueData")
        issueData = Gson().fromJson(getIntentData, NearByIssueByIdDetailsPojo::class.java)

        if(issueData!=null)
        {
            latitude = issueData!!.data.coordinates[0]
            longitude = issueData!!.data.coordinates[1]


            latlongArray!!.clear()
            //latlongArray!!.add(latitude)
            ///latlongArray!!.add(longitude)

            val latlongSet  = HashSet<String>()

            for(i in 0 until latlongArray!!.size) {
                latlongSet.add(latlongArray!![i].toString())
            }

            val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
            EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)

            println("latlong set $latlongArray")
            EasySP.init(context).putStringSet("latlong",latlongSet)

            EasySP.init(ApplicationController.context).put("lat", latitude)
            EasySP.init(ApplicationController.context).put("long", longitude)

            getAddress(latitude, longitude)

        }

        return  v
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
           /* mMap.addMarker(
                MarkerOptions().position(location).title(
                    getAddress(
                        latitude,
                        longitude
                    )
                ).icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(R.drawable.map_pin, 60, 60)))
            )*/
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))


            updatedLocation = location


            mMap.setOnCameraMoveListener {
                updatedLocation = mMap.cameraPosition.target


                latitude = mMap.cameraPosition.target.latitude
                longitude =  mMap.cameraPosition.target.longitude

                EasySP.init(ApplicationController.context).put("lat", mMap.cameraPosition.target.latitude)
                EasySP.init(ApplicationController.context).put("long", mMap.cameraPosition.target.longitude)
            }
            mMap.setOnCameraIdleListener {

                val json1 = Gson().toJson(LatLongSelectedPojo(latitude,longitude))
                EasySP.init(context).putString(ConstantEasySP.LATLONG_POJO,json1)

                getAddress(latitude,longitude)

            }
            getAddress(latitude,longitude)

            val cameraPosition = CameraPosition.builder()
            cameraPosition.target(location)
            cameraPosition.zoom(20f)
            cameraPosition.bearing(90f)
            cameraPosition.tilt(30f)
            cameraPosition.build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition.build()))

        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun resizeBitmap(mapPin: Int ,width : Int , height : Int): Bitmap?
    {
        val imageBitmap = BitmapFactory.decodeResource(resources,mapPin)
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    /** It returns the address text
     * which takes the latitude and longitude as a parameter
     * and converts to address string using geo coder
     */
    private fun getAddress(latitude: Double, longitude: Double): String?
    {
        var address: String = ""
        try {
            var geocoder: Geocoder = Geocoder(context, Locale.getDefault())
            var addresses: ArrayList<Address> =
                geocoder.getFromLocation(latitude, longitude, 1) as ArrayList<Address>

            address = addresses.get(0).getAddressLine(0)
            var title: String = addresses.get(0).featureName

            EasySP.init(context).put("locationAddress", address)
            EasySP.init(context).put("city", addresses[0].locality)


            val autocompleteSupportFragmentfrom =
                childFragmentManager.findFragmentById(R.id.autocomplete_fragment_from) as AutocompleteSupportFragment

            autocompleteSupportFragmentfrom.setText(address)





        }
        catch (e : Exception)
        {
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

            }

        }
    }


    /** It returns the last known location  */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        var mLocationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationManager =
            context!!.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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


}
