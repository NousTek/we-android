package com.we.beyond.ui.profile

import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.model.ProfilePojo
import com.we.beyond.model.RegistrationPojo
import com.we.beyond.model.UpdateProfilePic
import com.we.beyond.presenter.profile.ProfileImpl
import com.we.beyond.presenter.profile.ProfilePresenter
import com.we.beyond.ui.AddLocationActivity
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import org.w3c.dom.Text
import java.util.*
import kotlin.collections.ArrayList

/** It will edit the profile of user */
class EditProfileActivity : AppCompatActivity(), ProfilePresenter.IProfileView {

    /** initialize implementors */
    var profilePresenter: ProfileImpl? = null
    var profileData: ProfilePojo? = null
    //var areaPojo : ArrayList<RegistrationPojo>?=null


    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var homeLocation : TextView?=null

    /** init autocomplete text view for spinner */
    //var city: AutoCompleteTextView? = null
    //var area: AutoCompleteTextView? = null
    var userType : AutoCompleteTextView?=null

    /** init text input edit text */
    var firstName: TextInputEditText? = null
    var secondName: TextInputEditText? = null
    var organizationName: TextInputEditText? = null


    /** init text input layout */
    var firstNameLayout: TextInputLayout? = null
    var secondNameLayout: TextInputLayout? = null
    var organizationNameLayout: TextInputLayout? = null
    //var cityLayout: TextInputLayout? = null
    //var areaLayout: TextInputLayout? = null
    var userTypeLayout: TextInputLayout? = null

    /** init relative layout */
    var locationLayout : RelativeLayout?=null

    /** init button */
    var update: Button? = null

    /** Array list */
    //var citiesList: ArrayList<String>? = null
    var userTypeList : ArrayList<String>?=null

    /** init strings */
    //var cityText : String =""
    //var areaText : String =""
    var userTypeId : String = ""
    var address : String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        /* citiesList = ArrayList()
         areaPojo = ArrayList()
         areaPojo!!.clear()*/

        /** array initialization */
        userTypeList = ArrayList()
        userTypeList!!.clear()

        profilePresenter = ProfileImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** getDataToPost */
        getDataToPost()

        /** set cities data to spinner */
        //setCities()

        /** initialize onclick listener */
        initWithListener()

    }

    override fun setUserProfilePic(userProfilePic: UpdateProfilePic) {

    }

    /** On successfully update data, it go back to the activity */
    override fun onUpdateSuccessfully() {
        try {
           /* overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()*/
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    /* override fun setCitiesAdapter(cities: ArrayList<RegistrationPojo>) {

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

     private fun setCities() {
         try {

             if (ConstantMethods.checkForInternetConnection(this)) {
                     //ConstantMethods.showProgessDialog(this, "Please Wait...")
                 profilePresenter!!.getCities(this)
             }
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }

 */
    /** ui  listeners */
    private fun initWithListener() {

        /** It goes back to previous activity when click on it */
        back!!.setOnClickListener {

            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }


        /** It will take all required fields and call onUpdateProfile function of profile presenter */
        update!!.setOnClickListener {

            try {

                /*  if(city!!.text.isEmpty() && area!!.text.isEmpty()) {
                      cityText = city!!.hint.toString()
                      areaText = area!!.hint.toString()
                  }
                  else if(city!!.text.isEmpty() && area!!.text.isNotEmpty())
                  {
                      cityText = city!!.hint.toString()
                      areaText = area!!.text.toString()
                  }
                  else if(city!!.text.isNotEmpty() && area!!.text.isEmpty())
                  {
                      cityText = city!!.text.toString()
                      areaText = area!!.hint.toString()
                  }
                  else{
                      cityText = city!!.text.toString()
                      areaText = area!!.text.toString()
                  }*/

                val jsonObject = JsonObject()

                if(firstName!!.text!!.isNotEmpty() && firstName!!.text !=null ) {

                    if (!firstName!!.text.toString().equals(
                            profileData!!.data.firstName,
                            ignoreCase = true
                        )
                    ) {
                        jsonObject.addProperty("firstName", firstName!!.text.toString().trim())
                    }
                }

                if(secondName!!.text!!.isNotEmpty() && secondName!!.text !=null ) {

                    if (!secondName!!.text.toString().equals(
                            profileData!!.data.lastName,
                            ignoreCase = true
                        )
                    ) {

                        jsonObject.addProperty("lastName", secondName!!.text.toString().trim())
                    }
                }

                if(organizationName!!.text!!.isNotEmpty() && organizationName!!.text !=null ) {
                    if (!organizationName!!.text.toString().equals(
                            profileData!!.data.organizationName,
                            ignoreCase = true
                        )
                    ) {

                        jsonObject.addProperty(
                            "organizationName",
                            organizationName!!.text.toString().trim()
                        )
                    }

                }

                if(homeLocation!!.text!!.isNotEmpty() && homeLocation!!.text !=null ) {
                    if(profileData!!.data.location.coordinates.isNotEmpty()) {
                        if (!homeLocation!!.text.toString().equals(
                                getAddress(
                                    profileData!!.data.location.coordinates[0].toDouble(),
                                    profileData!!.data.location.coordinates[1].toDouble()
                                ),
                                ignoreCase = true
                            )
                        ) {

                            val latlong =
                                EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
                            var latLongPojo: LatLongSelectedPojo =
                                Gson().fromJson(latlong, LatLongSelectedPojo::class.java)

                            val latlongArray = ArrayList<String>()
                            latlongArray.add(latLongPojo.latitude.toString())
                            latlongArray.add(latLongPojo.longitude.toString())

                            val locationJsonArray = JsonArray()

                            for (i in 0 until latlongArray.size) {

                                locationJsonArray.add(latlongArray[i])

                            }
                            jsonObject.add("location", locationJsonArray)
                        }
                    }
                    else{
                        val latlong =
                            EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
                        var latLongPojo: LatLongSelectedPojo =
                            Gson().fromJson(latlong, LatLongSelectedPojo::class.java)

                        val latlongArray = ArrayList<String>()
                        latlongArray.add(latLongPojo.latitude.toString())
                        latlongArray.add(latLongPojo.longitude.toString())

                        val locationJsonArray = JsonArray()

                        for (i in 0 until latlongArray.size) {

                            locationJsonArray.add(latlongArray[i])

                        }
                        jsonObject.add("location", locationJsonArray)
                    }

                }



                /* if (!cityText.equals(
                         profileData!!.data.city,
                         ignoreCase = true
                     )
                 ) {

                     jsonObject.addProperty("city", city!!.text.toString().trim())
                 }


                 if (!areaText.equals(
                         profileData!!.data.area,
                         ignoreCase = true
                     )
                 ) {

                     jsonObject.addProperty("area", area!!.text.toString().trim())
                 }
 */

                if(userTypeId!=null && userTypeId.isNotEmpty())
                {
                    jsonObject.addProperty("userLoginTypeId",userTypeId)
                }


                if(jsonObject !=null && jsonObject.size() != 0) {
                    if (ConstantMethods.checkForInternetConnection(this)) {
                        ConstantMethods.showProgessDialog(this, "Please Wait...")


                        profilePresenter!!.onUpdateProfile(this, jsonObject)

                    }
                }
                else{
                    Toast.makeText(this,"no change",Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /* city!!.setOnItemClickListener(object : AdapterView.OnItemClickListener{
             override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                 val id = p2

                 println("id $id")

                 area!!.setHint("Select Area")
                 area!!.setHintTextColor(ContextCompat.getColor(this@EditProfileActivity, R.color.black))

                 setAreaAdapter(id)

             }

         })
 */

        /** It will select the user type from spinner */
        userType!!.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if(profileData !=null) {
                    userTypeId = profileData!!.data.userLoginTypes[p2]._id

                }
            }

        })

        /** It opens AddLocationActivity */
        locationLayout!!.setOnClickListener {

            val intent = Intent(this, AddLocationActivity::class.java)
            intent.putExtra("updateHomeLocation", true)
            startActivityForResult(intent,5)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }


    }

    /*  private fun setAreaAdapter(id : Int) {
          val areasAdapter = ArrayAdapter<String>(
              this,
              R.layout.spinner_popup_item,
              areaPojo!![id].areas
          )
          areasAdapter.setDropDownViewResource(R.layout.spinner_popup_item)
          area!!.setAdapter(areasAdapter)
      }*/

    /** It will call onRequestUserProfileData function of profile presenter */
    private fun getDataToPost() {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                profilePresenter!!.onRequestUserProfileData(this)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It checks the user data and set data to respective views */
    override fun setUserProfileDataAdapter(userData: ProfilePojo) {
        if (userData != null) {
            profileData = userData
        }

        println("user data $userData")

        if (userData.data.userLoginType.userType.equals(
                "individual",
                ignoreCase = true
            )
        ) {
            firstName!!.setText(userData.data.firstName)
            secondName!!.setText(userData.data.lastName)

        } else {
            firstNameLayout!!.visibility = View.GONE
            secondNameLayout!!.visibility = View.GONE
            organizationNameLayout!!.visibility = View.VISIBLE
            organizationName!!.setText(userData.data.organizationName)

        }

        if(userData.data.location!=null ) {
            if (userData.data.location.coordinates != null && userData.data.location.coordinates.isNotEmpty()) {
                address = getAddress(
                    userData.data.location.coordinates[0].toDouble(),
                    userData.data.location.coordinates[1].toDouble()
                )

                EasySP.init(this).putString("lat", userData.data.location.coordinates[0])
                EasySP.init(this).putString("long", userData.data.location.coordinates[1])

                homeLocation!!.text = address
                homeLocation!!.typeface = ConstantFonts.raleway_regular
            }
        }

        /*  city!!.setHint(userData.data.city)
          city!!.setHintTextColor(ContextCompat.getColor(this, R.color.black))
          area!!.setHint(userData.data.area)
          area!!.setHintTextColor(ContextCompat.getColor(this, R.color.black))*/

        userType!!.setHint(userData.data.userLoginType.name)
        userType!!.setHintTextColor(ContextCompat.getColor(this, R.color.black))

        try {
            userTypeList!!.clear()

            if (userData.data.userLoginTypes !=null && userData.data.userLoginTypes.isNotEmpty()) {
                for (i in 0 until userData.data.userLoginTypes.size) {
                    userTypeList!!.add(userData.data.userLoginTypes[i].name)

                }
            }


            if (userTypeList!!.isNotEmpty()) {
                for (i in 0 until userTypeList!!.size) {
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


    /** It sets address to text view */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 5)
        {
            address = EasySP.init(this).getString(ConstantEasySP.HOME_LOCATION_ADDRESS)

            if (address != null && address!!.isNotEmpty()) {
                homeLocation = findViewById(R.id.txt_location_title)
                homeLocation!!.text = address
                homeLocation!!.typeface = ConstantFonts.raleway_regular
            }


        }
    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image */
        back = findViewById(R.id.img_back)

        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        homeLocation = findViewById(R.id.txt_location_title)


        /** ids of edit text */
        firstName = findViewById(R.id.et_first_name)
        firstName!!.typeface = ConstantFonts.raleway_regular

        secondName = findViewById(R.id.et_second_name)
        secondName!!.typeface = ConstantFonts.raleway_regular

        organizationName = findViewById(R.id.et_organization_name)
        organizationName!!.typeface = ConstantFonts.raleway_regular

        /** ids of relative layout */
        locationLayout = findViewById(R.id.setHomeLocationLayout)

        /** ids of input  text  layout*/
        firstNameLayout = findViewById(R.id.firstNameLayout)
        firstNameLayout!!.typeface=ConstantFonts.raleway_regular
        secondNameLayout = findViewById(R.id.secondNameLayout)
        secondNameLayout!!.typeface=ConstantFonts.raleway_regular
        organizationNameLayout = findViewById(R.id.organizationNameLayout)
        organizationNameLayout!!.typeface=ConstantFonts.raleway_regular
        /*cityLayout = findViewById(R.id.cityLayout)
        areaLayout = findViewById(R.id.areaLayout)*/
        userTypeLayout = findViewById(R.id.registrationForLayout)

        /** ids of AutoCompleteText View (spinner) */

        /* city = findViewById(R.id.dropdown_city)
         city!!.typeface = ConstantFonts.raleway_semibold

         area = findViewById(R.id.dropdown_area)
         area!!.typeface = ConstantFonts.raleway_semibold*/

        userType = findViewById(R.id.dropdown_registration_for)
        userType!!.typeface = ConstantFonts.raleway_regular

        /** ids of button */
        update = findViewById(R.id.btn_update)
    }

    /** It opens UserProfileActivity */
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
