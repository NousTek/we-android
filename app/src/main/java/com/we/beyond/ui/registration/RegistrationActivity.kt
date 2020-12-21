package com.we.beyond.ui.registration

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.model.UserTypes
import com.we.beyond.presenter.registration.RegistrationImpl
import com.we.beyond.presenter.registration.RegistrationPresenter
import com.we.beyond.ui.AddLocationActivity
import com.we.beyond.ui.login.LoginActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import org.apache.commons.codec.digest.DigestUtils
import java.util.regex.Pattern

/** It is used to register the user */
class RegistrationActivity : AppCompatActivity(), RegistrationPresenter.IRegistrationView {


    var context: Context? = null
    var registrationPresenter: RegistrationImpl? = null
    var registrationPojo: ArrayList<UserTypes>? = null
    //var areaPojo : ArrayList<RegistrationPojo>?=null

    var isExternalUserRegistration: Boolean?=false
    var socialMediaType: String?=null
    /** init image view */
    var back: ImageView? = null
    var close: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var tvPasswordHint: TextView? = null
    var checkBoxText: TextView? = null
    var homeLocation: TextView? = null

    /** init relative layout */
    var moreInfoLayout: RelativeLayout? = null
    var homeLocationLayout: RelativeLayout? = null

    /** init web view */
    var webView: WebView? = null

    /** init autocomplete text view for spinner */
    var userType: AutoCompleteTextView? = null
    //var city: AutoCompleteTextView? = null
    //var area: AutoCompleteTextView? = null

    /** init text input edit text */
    var firstName: TextInputEditText? = null
    var secondName: TextInputEditText? = null
    var organizationName: TextInputEditText? = null
    var email: TextInputEditText? = null
    var mobileNumber: TextInputEditText? = null
    var password: TextInputEditText? = null
    var retypePassword: TextInputEditText? = null

    /** init text input layout */
    var userTypeLayout: TextInputLayout? = null
    var firstNameLayout: TextInputLayout? = null
    var secondNameLayout: TextInputLayout? = null
    var organizationNameLayout: TextInputLayout? = null
    //var cityLayout: TextInputLayout? = null
    //var areaLayout: TextInputLayout? = null
    var emailLayout: TextInputLayout? = null
    var mobileNumberLayout: TextInputLayout? = null
    var passwordLayout: TextInputLayout? = null
    var retypePasswordLayout: TextInputLayout? = null


    /** init button */
    var register: Button? = null

    /** init checkbox */
    var checkBox: CheckBox? = null

    /** Array list */
    var userTypeList: ArrayList<String>? = null
    // var citiesList: ArrayList<String>? = null

    var userTypeId: String = ""
    var address: String? = null
    var itemPosition = 0
    var REQUEST_ID_MULTIPLE_PERMISSIONS = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        isExternalUserRegistration=intent.getBooleanExtra(Constants.IS_EXTERNAL_USER, false)
        socialMediaType=intent.getStringExtra(Constants.SOCIAL_MEDIA_TYPE)
        userTypeList = ArrayList()
        //citiesList = ArrayList()
        registrationPojo = ArrayList()
        /*areaPojo = ArrayList()
        areaPojo!!.clear()*/


        registrationPresenter = RegistrationImpl(this)

        checkAndRequestPermissions()
        displayLocationSettingsRequest(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** set user type data to spinner */
        setUserType()

        /** set cities data to spinner */
        //setCities()

        /** initialize onclick listener */
        initWithListener()

    }


    /*   private fun setCities() {
           try {

                   if (ConstantMethods.checkForInternetConnection(this)) {
                          // ConstantMethods.showProgessDialog(this, "Please Wait...")
                       registrationPresenter!!.getCities(this)
                   }
           }
           catch (e : Exception)
           {
                   e.printStackTrace()
           }
       }*/

    private fun setUserType() {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                registrationPresenter!!.getUserTypes(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initElementsWithIds() {

        /** ids of image */
        back = findViewById(R.id.img_back)
        close = findViewById(R.id.img_close)

        /** ids of web View */
        webView = findViewById(R.id.webView)

        /** ids of text view */
        title = findViewById(R.id.txt_title)
        tvPasswordHint = findViewById(R.id.tvPasswordHint)
        title!!.typeface = ConstantFonts.raleway_semibold
        tvPasswordHint!!.typeface = ConstantFonts.raleway_medium

        checkBoxText = findViewById(R.id.txt_check_box)
        checkBoxText!!.typeface = ConstantFonts.raleway_medium
        checkBoxText!!.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)

        homeLocation = findViewById(R.id.txt_location_title)
        homeLocation!!.typeface=ConstantFonts.raleway_regular

        /** ids of relative layout */
        moreInfoLayout = findViewById(R.id.moreInfoLayout)
        homeLocationLayout = findViewById(R.id.setHomeLocationLayout)


        /** ids of AutoCompleteText View (spinner) */
        userType = findViewById(R.id.dropdown_registration_for)
        userType!!.typeface = ConstantFonts.raleway_regular

        /*  city = findViewById(R.id.dropdown_city)
          city!!.typeface = ConstantFonts.raleway_semibold

          area = findViewById(R.id.dropdown_area)
          area!!.typeface = ConstantFonts.raleway_semibold*/

        /** ids of edit text */
        firstName = findViewById(R.id.et_first_name)
        firstName!!.typeface = ConstantFonts.raleway_regular

        secondName = findViewById(R.id.et_second_name)
        secondName!!.typeface = ConstantFonts.raleway_regular

        organizationName = findViewById(R.id.et_organization_name)
        organizationName!!.typeface = ConstantFonts.raleway_regular

        email = findViewById(R.id.et_email)
        email!!.typeface = ConstantFonts.raleway_regular

        mobileNumber = findViewById(R.id.et_mobile_number)
        mobileNumber!!.typeface = ConstantFonts.raleway_regular

        password = findViewById(R.id.et_password)
        password!!.typeface = ConstantFonts.raleway_regular

        retypePassword = findViewById(R.id.et_retype_password)
        retypePassword!!.typeface = ConstantFonts.raleway_regular

        register = findViewById(R.id.btn_register)
        register!!.typeface = ConstantFonts.raleway_semibold

        checkBox = findViewById(R.id.checkBox)
        checkBox!!.typeface = ConstantFonts.raleway_semibold


        /** ids of input  text  layout*/
        userTypeLayout = findViewById(R.id.registrationForLayout)
        userTypeLayout!!.typeface=ConstantFonts.raleway_regular
        firstNameLayout = findViewById(R.id.firstNameLayout)
        firstNameLayout!!.typeface=ConstantFonts.raleway_regular
        secondNameLayout = findViewById(R.id.secondNameLayout)
        secondNameLayout!!.typeface=ConstantFonts.raleway_regular
        organizationNameLayout = findViewById(R.id.organizationNameLayout)
        organizationNameLayout!!.typeface=ConstantFonts.raleway_regular
        /* cityLayout = findViewById(R.id.cityLayout)
         areaLayout = findViewById(R.id.areaLayout)*/
        emailLayout = findViewById(R.id.EmailLayout)
        emailLayout!!.typeface=ConstantFonts.raleway_regular
        mobileNumberLayout = findViewById(R.id.MobileNumberLayout)
        mobileNumberLayout!!.typeface=ConstantFonts.raleway_regular
        passwordLayout = findViewById(R.id.PasswordLayout)
        passwordLayout!!.typeface=ConstantFonts.raleway_regular
        retypePasswordLayout = findViewById(R.id.RetypePasswordLayout)
        retypePasswordLayout!!.typeface=ConstantFonts.raleway_regular
        if(isExternalUserRegistration!!)
        {
            val emailId=intent.getStringExtra(Constants.EXTERNAL_USER_EMAIL)
            val userFirstName=intent.getStringExtra(Constants.EXTERNAL_USER_FIRST_NAME)
            val userLastName=intent.getStringExtra(Constants.EXTERNAL_USER_LAST_NAME)
            if(emailId!=null && emailId.isNotEmpty())
            {
                email!!.setText(emailId)
                email!!.isEnabled=false
            }
            if(userFirstName!=null && userFirstName.isNotEmpty())
            {
                firstName!!.setText(userFirstName)
            }
            if(userLastName!=null && userLastName.isNotEmpty())
            {
                secondName!!.setText(userLastName)
            }
        }

    }

    private fun initWithListener() {

        /** go to previous activity */
        back!!.setOnClickListener {
            registrationPresenter!!.onBackClick()
        }
        password!!.setOnFocusChangeListener(object:View.OnFocusChangeListener{
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus){
                    tvPasswordHint!!.visibility = View.VISIBLE
                }else{
                    tvPasswordHint!!.visibility = View.GONE
                }
            }

        })


        /** select the type of user for e.g, individual or organization */
        userTypeId = ""
        userType!!.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {


                itemPosition = position

                println("position $position")

                if (registrationPojo!![position].userType.equals("individual", ignoreCase = true)) {
                    println("type individual")

                    firstNameLayout!!.visibility = View.VISIBLE
                    secondNameLayout!!.visibility = View.VISIBLE
                    organizationNameLayout!!.visibility = View.GONE
                    if(!isExternalUserRegistration!!) {
                        firstName!!.text!!.clear()
                        secondName!!.text!!.clear()
                    }

                } else {
                    println("type other")
                    firstNameLayout!!.visibility = View.GONE
                    secondNameLayout!!.visibility = View.GONE
                    organizationNameLayout!!.visibility = View.VISIBLE
                    organizationName!!.text!!.clear()

                    organizationName!!.setHint(registrationPojo!![position].name + " Name")


                }

            }
        })


        /** call the function which has api */
        register!!.setOnClickListener {
            try {

                if (ConstantMethods.checkForInternetConnection(this@RegistrationActivity)) {
                    getDataToPost()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /** open more info layout with animation which has
         *  privacy policy page in web view
         */
        checkBoxText!!.setOnClickListener {
            moreInfoLayout!!.visibility = View.VISIBLE
            moreInfoLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))
//            webView!!.loadUrl("http://www.dropouts.in/privacypolicy/privacyweapp.html")
            webView!!.loadUrl("https://legal.weapp.mobi/terms.html")
        }

        /** close the layout with animation */
        close!!.setOnClickListener {
            moreInfoLayout!!.visibility = View.GONE
            moreInfoLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )
        }

        /** get current location if all permissions are granted by user
         * if permission is denied then request the permission by user
         */
        homeLocationLayout!!.setOnClickListener {

            println("check request permission ${checkAndRequestPermissions()}")
            if(checkAndRequestPermissions()) {
                val intent = Intent(this, AddLocationActivity::class.java)
                intent.putExtra("homeLocation", true)
                startActivityForResult(intent, 5)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            else{
                checkAndRequestPermissions()
                displayLocationSettingsRequest(this)
            }

        }

        /*  city!!.setOnItemClickListener(object : AdapterView.OnItemClickListener{
              override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                  val id = p2

                 setAreaAdapter(id)

              }

          })
  */
    }

    /* private fun setAreaAdapter(id : Int) {
         val areasAdapter = ArrayAdapter<String>(
             this,
             R.layout.spinner_popup_item,
             areaPojo!![id].areas
         )
         areasAdapter.setDropDownViewResource(R.layout.spinner_popup_item)
         area!!.setAdapter(areasAdapter)
     }*/

    /**
     * check runtime permission for app
     */
    private fun checkAndRequestPermissions(): Boolean {
        val coarseLocation =
            ContextCompat.checkSelfPermission(
                this@RegistrationActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        val fineLocation =
            ContextCompat.checkSelfPermission(
                this@RegistrationActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )


        val listPermissionsNeeded: ArrayList<Any> = ArrayList()
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                this@RegistrationActivity,
                listPermissionsNeeded.toArray(arrayOfNulls(listPermissionsNeeded.size)),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )

            return false
        }

        return true

    }

    /** get current location runtime permission */
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
                    LocationSettingsStatusCodes.SUCCESS -> Log.i(
                        "error",
                        "All location settings are satisfied."
                    )
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        Log.i(
                            "error",
                            "Location settings are not satisfied. Show the user a dialog to upgrade location settings "
                        )
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(
                                this@RegistrationActivity,
                                REQUEST_ID_MULTIPLE_PERMISSIONS
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

    /** get all data from views  with validation and call the api */
    private fun getDataToPost() {
        try {
            val userTypeText = userType!!.text.toString()
            val firstNameText = firstName!!.text.toString()
            val secondNameText = secondName!!.text.toString()
            val organizationText = organizationName!!.text.toString()
            val locationText = homeLocation!!.text.toString()
            /*  val cityText = city!!.text.toString()
              val areaText = area!!.text.toString()*/
            val emailText = email!!.text.toString().toLowerCase()
            val mobileNumberText = mobileNumber!!.text.toString()
            val passwordText = password!!.text.toString()
            val retypePasswordText = retypePassword!!.text.toString()


            if (userTypeText.isEmpty()) {
                userTypeLayout!!.error = "Please Select User Type"
            } else {
                userTypeLayout!!.isErrorEnabled = false
            }
            if (firstNameText.isEmpty()) {
                firstNameLayout!!.error = "Please Fill First Name"
            } else {
                firstNameLayout!!.isErrorEnabled = false
            }
            if (secondNameText.isEmpty()) {
                secondNameLayout!!.error = "Please Fill Second Name"
            } else {
                secondNameLayout!!.isErrorEnabled = false
            }
            if (organizationText.isEmpty()) {
                organizationNameLayout!!.error = "Please Fill Organization Name"
            } else {
                organizationNameLayout!!.isErrorEnabled = false
            }
            if (locationText.isEmpty()) {
                ConstantMethods.showToast(this,  "Please select home location")
            } else {

            }

            if (emailText.isEmpty()) {
                emailLayout!!.error = "Please Fill Email Address"
            } else {
                emailLayout!!.isErrorEnabled = false
            }
            if (mobileNumberText.isEmpty()) {
                mobileNumberLayout!!.error = "Please Fill Mobile Number"
            } else {
                mobileNumberLayout!!.isErrorEnabled = false
            }
            if (passwordText.isEmpty()) {
                passwordLayout!!.error = "Please Fill Password"
            } else {
                passwordLayout!!.isErrorEnabled = false
            }
            if (retypePasswordText.isEmpty()) {
                retypePasswordLayout!!.error = "Please Fill Re-Type Password"
            } else {
                retypePasswordLayout!!.isErrorEnabled = false
            }
            if (checkBox!!.isChecked) {

            } else {
                Toast.makeText(
                    this,
                    "Please accept terms & conditions",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (!isPasswordValid(passwordText)) {

                passwordLayout!!.error = "Invalid password format"

            } else {
                passwordLayout!!.isErrorEnabled = false
            }

            if (userType!!.text!!.isNotEmpty() && firstName!!.text!!.isNotEmpty() && secondName!!.text!!.isNotEmpty() || organizationName!!.text!!.isNotEmpty() && email!!.text!!.isNotEmpty() && mobileNumber!!.text!!.isNotEmpty() && password!!.text!!.isNotEmpty() && retypePassword!!.text!!.isNotEmpty() && homeLocation!!.text!!.isNotEmpty()) {
                if (ConstantMethods.isValidEmail(emailText)) {
                    if (mobileNumberText.length > 1) {
                        if (passwordText.equals(retypePasswordText, true)) {
                            if (locationText.isNotEmpty()) {
                                if (checkBox!!.isChecked) {
                                    if (isPasswordValid(password!!.text!!.toString())) {
                                        val userTypeId = registrationPojo!![itemPosition]._id

                                        val jsonObject = JsonObject()
                                        if (registrationPojo!![itemPosition].userType.equals(
                                                "individual",
                                                ignoreCase = true
                                            )
                                        ) {

                                            jsonObject.addProperty("firstName", firstNameText)
                                            jsonObject.addProperty("lastName", secondNameText)
                                        } else {
                                            jsonObject.addProperty(
                                                "organizationName",
                                                organizationText
                                            )
                                        }
                                        jsonObject.addProperty("mobile", mobileNumberText)
                                        jsonObject.addProperty("email", emailText)
                                        //encrypt password here
                                        val hex = DigestUtils.sha256(passwordText)
                                        val hash:String = ConstantMethods.bytesToHex(hex)

                                        if (hash != null && hash.length > 0){
                                            jsonObject.addProperty("password", hash)
                                        }
                                        /* jsonObject.addProperty("city", cityText)
                                        jsonObject.addProperty("area", areaText)*/
                                        if (userTypeId.isNotEmpty()) {
                                            jsonObject.addProperty("userLoginTypeId", userTypeId)
                                        }

                                        val latlong =
                                            EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
                                        var latLongPojo: LatLongSelectedPojo =
                                            Gson().fromJson(
                                                latlong,
                                                LatLongSelectedPojo::class.java
                                            )

                                        val latlongArray = ArrayList<String>()
                                        latlongArray.add(latLongPojo.latitude.toString())
                                        latlongArray.add(latLongPojo.longitude.toString())

                                        val locationJsonArray = JsonArray()

                                        for (i in 0 until latlongArray.size) {

                                            locationJsonArray.add(latlongArray[i])

                                        }
                                        jsonObject.add("location", locationJsonArray)

                                        if (ConstantMethods.checkForInternetConnection(this@RegistrationActivity)) {

                                            postDataToServer(jsonObject)
                                        }
                                    } else {
                                        ConstantMethods.showToast(
                                            this,
                                            "Password must contain at least 6 characters including UPPERCASE, Lower case, numbers and special characters."
                                        )
                                    }
                                } else {
                                    ConstantMethods.showToast(
                                        this,
                                        "Please select home location"
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Please accept terms & conditions",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            passwordLayout!!.error = "Password Mismatch"
                            retypePasswordLayout!!.error = "Password Mismatch"
                        }
                    } else {
                        ConstantMethods.showToast(this,  "Please enter mobile number")
                    }
                } else {
                    ConstantMethods.showToast(
                        this,
                        "Please enter valid email address"
                    )
                }
            } else {
                ConstantMethods.showToast(
                    this,
                    "Please enter all the details for successful registration."
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * check password is valid or not
     * returns true if password is valid
     * returns false if password is invalid
     */
    fun isPasswordValid(pawd: String): Boolean {
        val pattern = Pattern.compile("^(?=.*\\d)(?=.*[!@#\$%^&*_])(?=.*[a-z])(?=.*[A-Z]).{8,20}\$")
        val matcher = pattern.matcher(pawd)
        if (matcher.matches())
        {
            return true
        }
        return false

    }

    /**
     * this function gives json object as parameter and
     * pass to passes to getDataToPost method of registration presenter
     */
    private fun postDataToServer(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                registrationPresenter!!.getDataToPost(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** this function set adapter of user type   */
    override fun setUserTypesAdapter(userTypes: ArrayList<UserTypes>) {
        try {
            userTypeList!!.clear()

            if (userTypes.isNotEmpty()) {
                for (i in 0 until userTypes.size) {
                    userTypeList!!.add(userTypes[i].name)
                    registrationPojo!!.addAll(userTypes)
                }
            }

            if (userTypeList!!.isNotEmpty()) {
                for (i in 0 until userTypeList!!.size) {
                    /*val userTypeAdapter =
                        ArrayAdapter<String>(
                            this,
                            R.layout.spinner_popup_item,
                            userTypeList!!
                        )
                    userTypeAdapter.setDropDownViewResource(R.layout.spinner_popup_item)
                    userType!!.setAdapter(userTypeAdapter)*/
                    val adapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item, userTypeList!!
                    ) {
                        override fun getView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            val view = super.getView(position, convertView, parent)
                            val text =
                                view.findViewById<View>(android.R.id.text1) as TextView
                            text.typeface = ConstantFonts.raleway_regular
                            return view
                        }
                    }
                    userType!!.setAdapter(adapter)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*  override fun setCitiesAdapter(cities: ArrayList<RegistrationPojo>) {

          try {

              areaPojo!!.clear()
              areaPojo!!.addAll(cities)


              citiesList!!.clear()

              if (cities.isNotEmpty()) {
                  for (i in 0 until cities.size) {
                      citiesList!!.add(cities[i].name)
                  }
              }

              if (citiesList!!.isNotEmpty()) {
                  for (i in 0 until citiesList!!.size) {
                      val citiesAdapter =
                          ArrayAdapter<String>(
                              this,
                              R.layout.spinner_popup_item,
                              citiesList!!
                          )
                      citiesAdapter.setDropDownViewResource(R.layout.spinner_popup_item)
                      city!!.setAdapter(citiesAdapter)


                  }
              }
          } catch (e: Exception) {
              e.printStackTrace()
          }

      }
  */


    /**  go to next activity  */
    override fun goToNextScreen() {
        try {
            if(isExternalUserRegistration!! && socialMediaType!=null)
            {
                EasySP.init(this).putString(Constants.SOCIAL_MEDIA_TYPE, socialMediaType)
            }
            if(socialMediaType.equals("Google"))
            {
                performGoogleSignOut()
            }
            else if(socialMediaType.equals("Facebook"))
            {
                performFacebookSignOut()
            }
            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** go to previous activity  */
    override fun goToPreviousScreen() {
        try {

            intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 5) {
            address = EasySP.init(this).getString(ConstantEasySP.HOME_LOCATION_ADDRESS)

            if (address != null && address!!.isNotEmpty()) {
                homeLocation = findViewById(R.id.txt_location_title)
                homeLocation!!.text = address
                homeLocation!!.typeface = ConstantFonts.raleway_regular
            }


        }
    }


    /**  go to login activity when system back button pressed */

    override fun onBackPressed() {
        super.onBackPressed()
        intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()

    }

    private fun performGoogleSignOut()
    {
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestEmail()
            .requestProfile()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this@RegistrationActivity, gso)
        mGoogleSignInClient.signOut()
    }

    private fun performFacebookSignOut()
    {
        LoginManager.getInstance().logOut()
    }
}
