package com.we.beyond.ui.campaign.createCampaign

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.we.beyond.adapter.MediaAdapter
import com.we.beyond.Interface.OnVideoDeleteSuccess
import com.we.beyond.R
import com.we.beyond.RealPathUtils
import com.we.beyond.model.CampaignPojo
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.model.MediaUploadingPojo
import com.we.beyond.presenter.campaign.createCampaign.CreateCampaignImpl
import com.we.beyond.presenter.campaign.createCampaign.CreateCampaignPresenter
import com.we.beyond.presenter.mediaUpload.MediaImpl
import com.we.beyond.ui.AddLocationActivity
import com.we.beyond.ui.campaign.campaignDetails.CampaignDetailsActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.FileUtils
import com.white.easysp.EasySP
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * This activity creates the campaign with respected details
 */
class CreateCampaignActivity : AppCompatActivity(), CreateCampaignPresenter.ICreateCampaignView,
    OnVideoDeleteSuccess {

    /** initialize respected implementors */
    val context: Context = this
    var createCampaignPresenter: CreateCampaignImpl? = null
    var mediaPresenter: MediaImpl? = null

    /** init image view */
    var back: ImageView? = null
    var closeGallery: ImageView? = null
    var close: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var locationTitle: TextView? = null
    var addLocation: TextView? = null
    var mediaTitle: TextView? = null
    var or: TextView? = null
    var mediaOr: TextView? = null
    var mediaOrOption: TextView? = null

    /** init edit text */
    var campaignTitle: EditText? = null
    var campaignDetails: EditText? = null
    var dateTime: EditText? = null

    /** init text input edit text */
    var linkGatheringTitle: TextInputEditText? = null

    /** init button */
    var create: Button? = null
    var useCamera: Button? = null
    var useGallery: Button? = null
    var image: Button? = null
    var video: Button? = null
    var imageGallery: Button? = null
    var videoGalley: Button? = null

    var gatheringId: String? = null
    var campaignId: String? = null
    var gatheringTitle: String? = null
    var address: String? = null
    var city: String? = null
    var campaignDate: String = ""
    var selectedDate: String = ""
    var selectedTime: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var connectTitleText: String = ""
    var connectDetailsText: String = ""
    var getCampaignDate: String = ""

    /** init linear layout */
    var progressLayout: LinearLayout? = null


    /** init date and time picker */
    var date: DatePickerDialog.OnDateSetListener? = null
    var myCalendar: Calendar? = null

    /** init recycler view */
    var mediaRecycler: RecyclerView? = null

    var mMediaUri: Uri? = null

    /** init array list */
    var mimeTypeArray: ArrayList<String>? = null
    var mediaArray: ArrayList<String>? = null
    var mediaStatusArray: ArrayList<MediaUploadingPojo>? = null

    /** init relative layout */
    var optionLayout: RelativeLayout? = null
    var galleryOptionLayout: RelativeLayout? = null

    /** init adapter */
    var mediaAdapter: MediaAdapter? = null

    /** init variables */
    var file: File? = null
    var fileUri: String? = null
    var picturePath: String? = null

    var CAMERA = 1
    var VIDEO = 2
    var GALLERY_IMAGE = 3
    var GALLERY_VIDEO = 4
    var count = 0
    var RequestPermissionCode = 1

    var isEdit: Boolean = false
    var isMyCampaign: Boolean = false
    var isVideoSelected: Boolean = false
    var isCamapign: Boolean = false
    var campaignDetailsData: CampaignPojo? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_campaign)

        /** calender initialization */
        myCalendar = Calendar.getInstance()

        /** array initialization */
        mediaArray = ArrayList()
        mimeTypeArray = ArrayList()
        mediaStatusArray = ArrayList()

        /** initialize implementation */
        createCampaignPresenter = CreateCampaignImpl(this)
        //mediaPresenter = MediaImpl(this)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get Shared data */
        getSharedData()

        /* if (ConstantMethods.checkPermission(context)) {
             val mydir =
                 File(Environment.getExternalStorageDirectory().toString() + resources.getString(R.string.app_name))
             if (!mydir.exists()) {
                 mydir.mkdirs()
             }
         } else {
             ConstantMethods.requestPermission(context)
         }
 */

        /** It get stored data using intent and checks the gathering details
         * if not null then set data to edit text
         * else call getSharedData()
         */
        val getIntentData = intent.getStringExtra("campaignData")
        isEdit = intent.getBooleanExtra("edit", false)
        campaignDetailsData = Gson().fromJson(getIntentData, CampaignPojo::class.java)
        isMyCampaign = intent.getBooleanExtra("myCampaign", false)
        isCamapign = intent.getBooleanExtra("campaign", false)


        if (isEdit) {
            create!!.text = "update"

        } else {
            create!!.text = "create"
        }

        println("campaign data $isEdit")

        if (campaignDetailsData != null) {

            gatheringId = campaignDetailsData!!.data.gathering._id
            campaignId = campaignDetailsData!!.data._id
            linkGatheringTitle = findViewById(R.id.et_link_gathering_title)
            linkGatheringTitle!!.setText(campaignDetailsData!!.data.gathering.title)
            linkGatheringTitle!!.typeface = ConstantFonts.raleway_semibold
            campaignTitle!!.setText(campaignDetailsData!!.data.title)
            city = campaignDetailsData!!.data.city
            latitude = campaignDetailsData!!.data.location.coordinates[0].toString()
            longitude = campaignDetailsData!!.data.location.coordinates[1].toString()
            campaignDetails!!.setText(campaignDetailsData!!.data.description)
            dateTime!!.setText(ConstantMethods.convertStringToDateStringFull(campaignDetailsData!!.data.campaignDate))
            locationTitle = findViewById(R.id.txt_location_title)
            locationTitle!!.text = campaignDetailsData!!.data.address
            locationTitle!!.typeface = ConstantFonts.raleway_semibold


            if (campaignDetailsData!!.data.imageUrls != null && campaignDetailsData!!.data.imageUrls.isNotEmpty()) {
                for (i in 0 until campaignDetailsData!!.data.imageUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            campaignDetailsData!!.data.imageUrls[i],
                            "image",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }

            if (campaignDetailsData!!.data.videoUrls != null && campaignDetailsData!!.data.videoUrls.isNotEmpty()) {
                for (i in 0 until campaignDetailsData!!.data.videoUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            campaignDetailsData!!.data.videoUrls[i],
                            "video",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }


        } else {

        }

    }


    override fun onVideoDelete() {
        isVideoSelected = false

    }

    /** It get stored data using easysp and checks the gathering details
     * if not null then set data to text view
     * else call getSharedData()
     */
    private fun getSharedData() {
        gatheringId = EasySP.init(this).getString(ConstantEasySP.GATHERING_ID)
        gatheringTitle = EasySP.init(this).getString(ConstantEasySP.GATHERING_TITLE)
        address = EasySP.init(this).getString(ConstantEasySP.SELECTED_CAMPAIGN_ADDRESS)
        city = EasySP.init(this).getString("city")
        latitude = EasySP.init(this).getString("lat")
        longitude = EasySP.init(this).getString("long")
        connectTitleText = EasySP.init(this).getString("campaignTitle")
        connectDetailsText = EasySP.init(this).getString("campaignDetails")
        getCampaignDate = EasySP.init(this).getString("campaignDate")


        if (gatheringTitle != null && gatheringTitle!!.isNotEmpty()) {
            linkGatheringTitle = findViewById(R.id.et_link_gathering_title)
            linkGatheringTitle!!.setText(gatheringTitle)
            linkGatheringTitle!!.typeface = ConstantFonts.raleway_semibold
        }

        if (address != null && address!!.isNotEmpty()) {
            locationTitle = findViewById(R.id.txt_location_title)
            locationTitle!!.text = address
            locationTitle!!.typeface = ConstantFonts.raleway_semibold
        }

        campaignTitle = findViewById(R.id.et_campaign_title)
        campaignTitle!!.setText(connectTitleText)

        campaignDetails = findViewById(R.id.et_campaign_details)
        campaignDetails!!.setText(connectDetailsText)

        dateTime = findViewById(R.id.et_date_time)
        dateTime!!.setText(getCampaignDate)

    }

    /** convert date to string */
    fun convertDateStringToShow(dateString: String): String {
        var serverDate = ""

        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        val FORMAT_DATETIME = "dd-MM-yyyy hh:mm a"
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        // sdf2.setTimeZone(tz)
        try {
            date = sdf.parse(dateString)
            serverDate = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }


        return serverDate

    }

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity */
        back!!.setOnClickListener {
            /* val intent = Intent(this, DashboardActivity::class.java)
             startActivity(intent)*/
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

            EasySP.init(this).putString(ConstantEasySP.CAMPAIGN_DATE, "")
            EasySP.init(this).putString(ConstantEasySP.SELECTED_CAMPAIGN_ADDRESS, "")
            EasySP.init(this).putString("city", "")
            EasySP.init(this).putString("lat", "")
            EasySP.init(this).putString("long", "")
            EasySP.init(this).putString("campaignTitle", "")
            EasySP.init(this).putString("campaignDetails", "")
            EasySP.init(this).putString("campaignDate", "")
        }

        dateTime!!.setOnClickListener {
            val datePicker = DatePickerDialog(
                context, date, myCalendar!!.get(Calendar.YEAR), myCalendar!!.get(Calendar.MONTH),
                myCalendar!!.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        /** It selects the date and time using date picker and time picker and set it to text view */
        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            selectedDate = "$dayOfMonth-${(monthOfYear + 1)}-$year"

            println("year===$selectedDate")

            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    myCalendar!!.set(Calendar.HOUR_OF_DAY, hour)
                    myCalendar!!.set(Calendar.MINUTE, minute)

                    selectedTime = SimpleDateFormat("K:mm a").format(myCalendar!!.time)
                    campaignDate = "$selectedDate $selectedTime"
                    println("date and time $campaignDate")

                    dateTime!!.setText(convertDateStringToShow(campaignDate))

                    println(
                        "server date and time ${ConstantMethods.convertDateStringToServerDateFull(
                            dateTime!!.text.toString()
                        )}"
                    )

                    EasySP.init(this)
                        .putString(ConstantEasySP.CAMPAIGN_DATE, dateTime!!.text.toString().trim())

                },
                myCalendar!!.get(Calendar.HOUR_OF_DAY),
                myCalendar!!.get(Calendar.MINUTE),
                false
            ).show()

        }

        /** It opens AddLocationActivity  */
        addLocation!!.setOnClickListener {
            val intent = Intent(context, AddLocationActivity::class.java)
            intent.putExtra("campaignLocation", true)
            startActivityForResult(intent, 5)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            EasySP.init(this).putString("campaignTitle", campaignTitle!!.text.toString())
            EasySP.init(this).putString("campaignDetails", campaignDetails!!.text.toString())

        }

        /** It opens camera intent to capture image */
        useCamera!!.setOnClickListener {
            try {
                optionLayout!!.visibility = View.VISIBLE
                galleryOptionLayout!!.visibility = View.GONE
                optionLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_in_up
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /** It opens option layout with animation */
        useGallery!!.setOnClickListener {
            try {
                optionLayout!!.visibility = View.GONE
                galleryOptionLayout!!.visibility = View.VISIBLE
                galleryOptionLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_in_up
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It opens camera intent to capture image */
        image!!.setOnClickListener {

            try {
                mMediaUri = ConstantMethods.getMediaOutputUri(context)
                if (mMediaUri == null) {
                    // error
                    Toast.makeText(
                        context,
                        "There was a problem accessing your device's storage.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    println("Mediya Url:- $mMediaUri")
                    EasySP.init(this).putString("campaignTitle", campaignTitle!!.text.toString())
                    EasySP.init(this)
                        .putString("campaignDetails", campaignDetails!!.text.toString())

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri)
                    startActivityForResult(intent, CAMERA)
                }
                optionLayout!!.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It opens camera intent to capture only one video
         *  else it will show dialog
         */
        video!!.setOnClickListener {

            try {
                optionLayout!!.visibility = View.GONE
                if (!isVideoSelected) {
                    EasySP.init(this).putString("campaignTitle", campaignTitle!!.text.toString())
                    EasySP.init(this)
                        .putString("campaignDetails", campaignDetails!!.text.toString())

                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    startActivityForResult(intent, VIDEO)


                } else {
                    ConstantMethods.showWarning(
                        context!!,
                        "",
                        "You can not upload more than 1 video."
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /** It opens gallery to select image from gallery */
        imageGallery!!.setOnClickListener {

            try {
                EasySP.init(this).putString("campaignTitle", campaignTitle!!.text.toString())
                EasySP.init(this).putString("campaignDetails", campaignDetails!!.text.toString())

                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, GALLERY_IMAGE)

                galleryOptionLayout!!.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It opens gallery intent to select only one video
         *  else it will show dialog
         */
        videoGalley!!.setOnClickListener {

            try {
                galleryOptionLayout!!.visibility = View.GONE
                if (!isVideoSelected) {
                    EasySP.init(this).putString("campaignTitle", campaignTitle!!.text.toString())
                    EasySP.init(this)
                        .putString("campaignDetails", campaignDetails!!.text.toString())

                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setType("video/*")
                    startActivityForResult(intent, GALLERY_VIDEO)


                } else {
                    ConstantMethods.showWarning(
                        context!!,
                        "",
                        "You can not upload more than 1 video."
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /** It closes the layout with animation */
        close!!.setOnClickListener {
            optionLayout!!.visibility = View.GONE
            optionLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.slide_out_down
                )
            )

        }

        /** It closes the layout with animation */
        closeGallery!!.setOnClickListener {
            galleryOptionLayout!!.visibility = View.GONE
            galleryOptionLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.slide_out_down
                )
            )
        }


        /** It checks required fields are empty or not, if empty then shows the warning dialog
         * and get all required fields from user to
         * call the respective method to add or update data to the server
         * */
        create!!.setOnClickListener {
            try {

                if (isEdit) {
                    if (ConstantMethods.checkForInternetConnection(this@CreateCampaignActivity)) {

                        getDataToPostUpdate()
                    }
                } else {
                    if (ConstantMethods.checkForInternetConnection(this@CreateCampaignActivity)) {

                        getDataToPost()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    /** It converts required data to json object and
     * call postDataToServerUpdate function with json object as parameter
     */
    private fun getDataToPostUpdate() {
        try {
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val details = campaignDetails!!.text.toString().trim()
            val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
            getCampaignDate = EasySP.init(this).getString(ConstantEasySP.CAMPAIGN_DATE)

            val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {

            }.type
            var mediaPojo = ArrayList<MediaUploadingPojo>()
            try {
                mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val imageArray = ArrayList<String>()
            val videoArray = ArrayList<String>()

            for (i in 0 until mediaPojo.size) {
                if (mediaPojo[i].isUpload) {
                    count++

                    if (mediaPojo[i].mimeType.contains("image")) {
                        imageArray.add(mediaPojo[i].serverUrl)
                    } else {
                        videoArray.add(mediaPojo[i].serverUrl)
                    }

                } else {
                    ConstantMethods.showError(this, "Please wait", "Please wait media uploading.")
                }
            }


            var latLongPojo: LatLongSelectedPojo =
                Gson().fromJson(latlong, LatLongSelectedPojo::class.java)


            println("latlong pojo in activity ${latLongPojo.latitude}")

            val latlongArray = ArrayList<String>()
            latlongArray.add(latLongPojo.latitude.toString())
            latlongArray.add(latLongPojo.longitude.toString())


            val jsonObject = JsonObject()
            jsonObject.addProperty("campaignId", campaignId)
            if (!campaignTitle!!.text.equals(campaignDetailsData!!.data.title)) {
                jsonObject.addProperty("title", campaignTitle!!.text.toString().trim())
            }
            if (!campaignDetails!!.text.equals(campaignDetailsData!!.data.description)) {

                jsonObject.addProperty(
                    "description",
                    campaignDetails!!.text.toString().trim()
                )
            }
            if (!ConstantMethods.convertStringToDateStringFull(campaignDetailsData!!.data.campaignDate)
                    .equals(dateTime!!.text.toString(), ignoreCase = true)
            ) {
                jsonObject.addProperty(
                    "campaignDate",
                    ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                )
            }
            if (!locationTitle!!.text.equals(campaignDetailsData!!.data.address)) {
                jsonObject.addProperty("address", locationTitle!!.text.toString().trim())
                jsonObject.addProperty("city", city)


                val locationJsonArray = JsonArray()

                for (i in 0 until latlongArray.size) {

                    locationJsonArray.add(latlongArray[i])

                }
                jsonObject.add("location", locationJsonArray)
            }

            //image array
            val imageJsonArray = JsonArray()

            for (i in 0 until imageArray.size) {

                imageJsonArray.add(imageArray[i])

            }

            println("images json $imageArray")
            jsonObject.add("imageUrls", imageJsonArray)

            //video array
            val videoJsonArray = JsonArray()
            for (i in 0 until videoArray.size) {

                videoJsonArray.add(videoArray[i])

            }
            println("video json $videoArray")
            jsonObject.add("videoUrls", videoJsonArray)





            if (count == mediaPojo.size) {

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {
                        postDataToServerUpdate(jsonObject)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                count = 0

                ConstantMethods.showWarning(
                    this,
                    "Please wait",
                    "Please wait media uploading."
                )
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onUpdateCampaign function of create campaign presenter */
    private fun postDataToServerUpdate(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                createCampaignPresenter!!.onUpdateCampaign(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It converts required data to json object and
     * call postDataToServer function with json object as parameter
     * */
    private fun getDataToPost() {
        try {
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val details = campaignDetails!!.text.toString().trim()
            val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
            getCampaignDate = EasySP.init(this).getString(ConstantEasySP.CAMPAIGN_DATE)

            var mediaPojo = ArrayList<MediaUploadingPojo>()
            val imageArray = ArrayList<String>()
            val videoArray = ArrayList<String>()

            if (mediaPojo != null) {

                val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {

                }.type

                try {
                    mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                }



                for (i in 0 until mediaPojo.size) {
                    if (mediaPojo[i].isUpload) {
                        count++

                        if (mediaPojo[i].mimeType.contains("image")) {
                            imageArray.add(mediaPojo[i].serverUrl)
                        } else {
                            videoArray.add(mediaPojo[i].serverUrl)
                        }

                    } else {
                        ConstantMethods.showError(
                            this,
                            "Please wait",
                            "Please wait media uploading."
                        )
                    }
                }

            }

            if (campaignTitle!!.text.isEmpty()) {
                ConstantMethods.showWarning(this, "Title", "Please Enter Title")
            } else if (campaignDetails!!.text.isEmpty()) {
                ConstantMethods.showWarning(this, "Details", "Please Enter Campaign Details")
            } else if (dateTime!!.text.isEmpty()) {
                ConstantMethods.showWarning(this, "Date And Time", "Please Select Date And Time")
            } else if (locationTitle!!.text.isEmpty()) {
                ConstantMethods.showWarning(this, "Location", "Please Select Campaign Location")
            } else {

                var latLongPojo: LatLongSelectedPojo =
                    Gson().fromJson(latlong, LatLongSelectedPojo::class.java)


                println("latlong pojo in activity ${latLongPojo.latitude}")

                val latlongArray = ArrayList<String>()
                latlongArray.add(latLongPojo.latitude.toString())
                latlongArray.add(latLongPojo.longitude.toString())


                if (details.isNotEmpty() && campaignTitle!!.text.isNotEmpty() && campaignDetails!!.text.isNotEmpty() && gatheringId!!.isNotEmpty() && getCampaignDate.isNotEmpty() && latlongArray.isNotEmpty() && imageArray.isEmpty() && videoArray.isEmpty()) {
                    if (ConstantMethods.checkForInternetConnection(this@CreateCampaignActivity)) {

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("gatheringId", gatheringId)
                        jsonObject.addProperty("title", campaignTitle!!.text.toString().trim())
                        jsonObject.addProperty(
                            "description",
                            campaignDetails!!.text.toString().trim()
                        )
                        jsonObject.addProperty(
                            "campaignDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                        jsonObject.addProperty("address", locationTitle!!.text.toString().trim())
                        jsonObject.addProperty("city", city)

                        val locationJsonArray = JsonArray()

                        for (i in 0 until latlongArray.size) {

                            locationJsonArray.add(latlongArray[i])

                        }
                        jsonObject.add("location", locationJsonArray)

                        try {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                postDataToServer(jsonObject)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        println("post data $jsonObject")


                    }
                } else if (details.isNotEmpty() && campaignTitle!!.text.isNotEmpty() && campaignDetails!!.text.isNotEmpty() && gatheringId!!.isNotEmpty() && getCampaignDate.isNotEmpty() && latlongArray.isNotEmpty() && imageArray.isNotEmpty() && videoArray.isNotEmpty()) {

                    if (ConstantMethods.checkForInternetConnection(this@CreateCampaignActivity)) {

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("gatheringId", gatheringId)
                        jsonObject.addProperty("title", campaignTitle!!.text.toString().trim())
                        jsonObject.addProperty(
                            "description",
                            campaignDetails!!.text.toString().trim()
                        )
                        jsonObject.addProperty(
                            "campaignDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                        jsonObject.addProperty("address", locationTitle!!.text.toString().trim())
                        jsonObject.addProperty("city", city)

                        val locationJsonArray = JsonArray()

                        for (i in 0 until latlongArray.size) {

                            locationJsonArray.add(latlongArray[i])

                        }
                        jsonObject.add("location", locationJsonArray)

                        //image array
                        val imageJsonArray = JsonArray()

                        for (i in 0 until imageArray.size) {

                            imageJsonArray.add(imageArray[i])

                        }

                        println("images json $imageArray")
                        jsonObject.add("imageUrls", imageJsonArray)

                        //video array
                        val videoJsonArray = JsonArray()
                        for (i in 0 until videoArray.size) {

                            videoJsonArray.add(videoArray[i])

                        }
                        println("video json $videoArray")
                        jsonObject.add("videoUrls", videoJsonArray)





                        if (count == mediaPojo.size) {

                            try {
                                if (ConstantMethods.checkForInternetConnection(context)) {
                                    postDataToServer(jsonObject)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {

                            count = 0

                            ConstantMethods.showWarning(
                                this,
                                "Please wait",
                                "Please wait media uploading."
                            )
                        }


                    }

                } else if (details.isNotEmpty() && campaignTitle!!.text.isNotEmpty() && campaignDetails!!.text.isNotEmpty() && gatheringId!!.isNotEmpty() && getCampaignDate.isNotEmpty() && latlongArray.isNotEmpty() && imageArray.isNotEmpty()) {

                    if (ConstantMethods.checkForInternetConnection(this@CreateCampaignActivity)) {

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("gatheringId", gatheringId)
                        jsonObject.addProperty("title", campaignTitle!!.text.toString().trim())
                        jsonObject.addProperty(
                            "description",
                            campaignDetails!!.text.toString().trim()
                        )
                        jsonObject.addProperty(
                            "campaignDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                        jsonObject.addProperty("address", locationTitle!!.text.toString().trim())
                        jsonObject.addProperty("city", city)

                        val locationJsonArray = JsonArray()

                        for (i in 0 until latlongArray.size) {

                            locationJsonArray.add(latlongArray[i])

                        }
                        jsonObject.add("location", locationJsonArray)

                        //image array
                        val imageJsonArray = JsonArray()

                        for (i in 0 until imageArray.size) {

                            imageJsonArray.add(imageArray[i])

                        }

                        println("images json $imageArray")
                        jsonObject.add("imageUrls", imageJsonArray)


                        if (count == mediaPojo.size) {

                            try {
                                if (ConstantMethods.checkForInternetConnection(context)) {
                                    postDataToServer(jsonObject)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {

                            count = 0

                            ConstantMethods.showWarning(
                                this,
                                "Please wait",
                                "Please wait media uploading."
                            )
                        }


                    }

                } else if (details.isNotEmpty() && campaignTitle!!.text.isNotEmpty() && campaignDetails!!.text.isNotEmpty() && gatheringId!!.isNotEmpty() && getCampaignDate.isNotEmpty() && latlongArray.isNotEmpty() && videoArray.isNotEmpty()) {

                    if (ConstantMethods.checkForInternetConnection(this@CreateCampaignActivity)) {

                        val jsonObject = JsonObject()
                        jsonObject.addProperty("gatheringId", gatheringId)
                        jsonObject.addProperty("title", campaignTitle!!.text.toString().trim())
                        jsonObject.addProperty(
                            "description",
                            campaignDetails!!.text.toString().trim()
                        )
                        jsonObject.addProperty(
                            "campaignDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                        jsonObject.addProperty("address", locationTitle!!.text.toString().trim())
                        jsonObject.addProperty("city", city)

                        val locationJsonArray = JsonArray()

                        for (i in 0 until latlongArray.size) {

                            locationJsonArray.add(latlongArray[i])

                        }
                        jsonObject.add("location", locationJsonArray)


                        //video array
                        val videoJsonArray = JsonArray()
                        for (i in 0 until videoArray.size) {

                            videoJsonArray.add(videoArray[i])

                        }
                        println("video json $videoArray")
                        jsonObject.add("videoUrls", videoJsonArray)





                        if (count == mediaPojo.size) {


                            try {
                                if (ConstantMethods.checkForInternetConnection(context)) {
                                    postDataToServer(jsonObject)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {

                            count = 0

                            ConstantMethods.showWarning(
                                this,
                                "Please wait",
                                "Please wait media uploading."
                            )
                        }


                    }

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onCampaignCreated function of create campaign presenter */
    private fun postDataToServer(jsonObject: JsonObject) {

        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                createCampaignPresenter!!.onCampaignCreated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image view */
        back = findViewById(R.id.img_back)

        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        locationTitle = findViewById(R.id.txt_location_title)
        locationTitle!!.text = ""
        locationTitle!!.hint = resources.getString(R.string.campaign_issue_location_hint)
        locationTitle!!.typeface = ConstantFonts.raleway_semibold

        addLocation = findViewById(R.id.txt_add_location)
        addLocation!!.typeface = ConstantFonts.raleway_semibold

        mediaTitle = findViewById(R.id.txt_media_title)
        mediaTitle!!.typeface = ConstantFonts.raleway_semibold


        or = findViewById(R.id.txt_or)
        or!!.typeface = ConstantFonts.raleway_semibold

        mediaOr = findViewById(R.id.txt_media_or)
        mediaOr!!.typeface = ConstantFonts.raleway_regular

        mediaOrOption = findViewById(R.id.txt_media_or_option)
        mediaOrOption!!.typeface = ConstantFonts.raleway_regular


        /** ids of edit text */
        campaignTitle = findViewById(R.id.et_campaign_title)
        campaignTitle!!.typeface = ConstantFonts.raleway_semibold

        campaignDetails = findViewById(R.id.et_campaign_details)
        campaignDetails!!.typeface = ConstantFonts.raleway_semibold

        dateTime = findViewById(R.id.et_date_time)
        dateTime!!.typeface = ConstantFonts.raleway_semibold

        /** ids of button */
        create = findViewById(R.id.btn_create)
        create!!.typeface = ConstantFonts.raleway_semibold

        useCamera = findViewById(R.id.btn_use_camera)
        useCamera!!.typeface = ConstantFonts.raleway_semibold

        useGallery = findViewById(R.id.btn_browse_file)
        useGallery!!.typeface = ConstantFonts.raleway_semibold

        image = findViewById(R.id.btn_image)
        image!!.typeface = ConstantFonts.raleway_semibold

        video = findViewById(R.id.btn_video)
        video!!.typeface = ConstantFonts.raleway_semibold

        imageGallery = findViewById(R.id.btn_image_option)
        imageGallery!!.typeface = ConstantFonts.raleway_semibold

        videoGalley = findViewById(R.id.btn_video_option)
        videoGalley!!.typeface = ConstantFonts.raleway_semibold

        /** ids of recycler view */
        mediaRecycler = findViewById(R.id.recycler_media)
        mediaRecycler!!.layoutManager = GridLayoutManager(this, 3)

        /** ids of relative layout*/
        optionLayout = findViewById(R.id.captureOptionLayout)
        galleryOptionLayout = findViewById(R.id.galleryOptionLayout)

        /** ids of image view */
        close = findViewById(R.id.img_close)
        closeGallery = findViewById(R.id.img_close_option)

        /** ids of linear layout */
        progressLayout = findViewById(R.id.progressLayout)


    }

    /** It opens respected activity */
    override fun goToNextScreen() {

        if (isEdit) {
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
            count = 0

            val intent = Intent(this, CampaignDetailsActivity::class.java)
            intent.putExtra("campaignId", campaignId)
            intent.putExtra("isEdit", true)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (isCamapign) {
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
            count = 0

            val intent = Intent()
            intent.putExtra("campaignId", campaignId)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

        } else {

            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
            count = 0

            val intent = Intent(this, GatheringDetailsActivity::class.java)
            intent.putExtra("gatheringId", gatheringId)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

    }

    /** It get image url which selected from camera or gallery and set it to media adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap


        if (requestCode == GALLERY_IMAGE) {
            try {
                mMediaUri = data!!.data
                /* mMediaUri = data!!.getData()
                 var imageStream: InputStream? = null
                 try {
                     imageStream = context!!.getContentResolver().openInputStream(mMediaUri!!)

                     var file1 = File(mMediaUri!!.getPath())
                     var bt: Bitmap
                     bt = BitmapFactory.decodeStream(imageStream)
                     bt = ConstantMethods.imageOrientationValidator(bt, file1.absolutePath)
                     file1 = ConstantMethods.compressImage(context, bt)*/

                //val path = RealPathUtils.getPath(context!!,mMediaUri!!)
                val path = FileUtils().getRealPath(context, mMediaUri!!)

                var file1: File? = null
                if (path != null) {
                    var compressedPath = ConstantMethods.getCompressImage(context, path)
                    if (compressedPath != null) {
                        file1 = File(compressedPath)
                    } else {
                        file1 = RealPathUtils.getFile(context, mMediaUri!!)
                    }

                } else {
                    file1 = RealPathUtils.getFile(context, mMediaUri!!)

                }


                var length: Long? = file1!!.length()
                length = length!! / 1024

                //5mb in kb
                /* if (length > 5120) run {
                     ConstantMethods.showError(
                         context,
                         "Large Image Size",
                         "Maximum image allowed is 5 MB"
                     )

                 }
                 else {
 */
                mediaStatusArray!!.add(
                    MediaUploadingPojo(
                        file1.absolutePath,
                        "",
                        "image",
                        false
                    )
                )


                mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                mediaRecycler!!.adapter = mediaAdapter
                //}


            } catch (e: Exception) {
                e.printStackTrace()
            }


        } else if (requestCode == GALLERY_VIDEO) {
            try {

                val uri: Uri = Uri.parse(data!!.data.toString())

                if (uri != null) {
                    // val path = RealPathUtils.getPath(context!!, uri)
                    val path = FileUtils().getRealPath(context!!, uri)

                    val mimeType = ""
                    if (path != null) {
                        file = File(path)
//                    mimeType = ConstantsMethods.getMimeType(path)
                    } else {
                        file = RealPathUtils.getFile(context!!, uri)!!
//                    mimeType = ConstantsMethods.getMimeType(file.getPath())
                    }
                    var length: Long? = file!!.length()
                    length = length!! / 1024

                    //decodeFile(picturePath);
                    //100mb in kb
                    if (length!! > 102400) {
                        ConstantMethods.showError(
                            context!!,
                            "Large Video Size",
                            "Maximum video allowed is 100 MB"
                        )

                    } else {
                        if (file != null) {
                            fileUri = file!!.path

                            println("gallery video path $fileUri")

                            mediaStatusArray!!.add(
                                MediaUploadingPojo(
                                    fileUri!!,
                                    "",
                                    "video",
                                    false
                                )
                            )

                            mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!, false)
                            mediaRecycler!!.adapter = mediaAdapter
                        }
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == CAMERA) {
            if (mMediaUri != null) {
                // try {
                /* var file = File(mMediaUri!!.getPath())

                 bitmap = BitmapFactory.decodeFile(mMediaUri!!.getPath())

                 bitmap =
                     ConstantMethods.imageOrientationValidator(bitmap, mMediaUri!!.getPath())

                 file = ConstantMethods.compressImage(context, bitmap)
 */
                /*val sourceDirectory = File(
                    Environment.getExternalStorageDirectory().toString() + "/" + resources.getString(
                        R.string.app_name
                    ) + "/"
                )

                if (!sourceDirectory.exists()) {
                    sourceDirectory.mkdirs()
                }

                val targetFilePath =
                    Environment.getExternalStorageDirectory().toString() + "/" + resources.getString(
                        R.string.app_name
                    ) + "/"


                val finalPath = ConstantMethods.copyFile(file.path, file.name, targetFilePath)

                println("file path intenal $finalPath")*/


                // val path = RealPathUtils.getPath(context!!,mMediaUri!!)
                val path = FileUtils().getRealPath(context!!, mMediaUri!!)

                var file1: File? = null
                if (path != null) {
                    var compressedPath = ConstantMethods.getCompressImage(context!!, path)
                    if (compressedPath != null) {
                        file1 = File(compressedPath)
                    } else {
                        file1 = RealPathUtils.getFile(context!!, mMediaUri!!)
                    }

                } else {
                    file1 = RealPathUtils.getFile(context!!, mMediaUri!!)

                }

                var length: Long? = file1!!.length()
                length = length!! / 1024

                //decodeFile(picturePath);
                //5mb in kb
                /* if (length > 5120) {
                     ConstantMethods.showError(
                         context,
                         "Large Image Size",
                         "Maximum image allowed is 5 MB"
                     )


                 } else {*/


                mediaStatusArray!!.add(
                    MediaUploadingPojo(
                        file1!!.absolutePath,
                        "",
                        "image",
                        false
                    )
                )

                mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                mediaRecycler!!.adapter = mediaAdapter

                EasySP.init(context).putString("image", fileUri)

                // mediaPresenter!!.onFileUpload(context!!, file.absolutePath!!)
                // }

                /* } catch (e: Exception) {
                     e.printStackTrace()
                 }
 */

            }
        } else if (requestCode == VIDEO) {
            // if (mMediaUri != null) {
            try {
                mMediaUri = data!!.getData()
                println("mmendia$mMediaUri")
                val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
                Log.e("path", filePathColumn[0])

                val cursor = context!!.getContentResolver().query(
                    mMediaUri!!,
                    filePathColumn, null, null, null
                )
                cursor!!.moveToFirst()

                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                picturePath = cursor.getString(columnIndex)
                cursor.close()
                // Log.e("picture path",picturePath);


                /*  val ffmpeg = FFmpeg.getInstance(context)
                  ffmpeg.loadBinary(object : LoadBinaryResponseHandler() {

                  })

                  val targetFile = Environment.getExternalStorageDirectory().toString() + "/"
                  val fileTarget = File(targetFile, "We.mp4")

                  val file = File(picturePath)


                  val command = arrayOf(
                      "-y",
                      "-i",
                      file.path,
                      "-s",
                      "720x480",
                      "-r",
                      "30",
                      "-c:v",
                      "libx264",
                      "-preset",
                      "ultrafast",
                      "-c:a",
                      "copy",
                      "-me_method",
                      "zero",
                      "-tune",
                      "fastdecode",
                      "-tune",
                      "zerolatency",
                      "-strict",
                      "-2",
                      "-pix_fmt",
                      "yuv420p",
                      fileTarget.toString()
                  )

                  ffmpeg!!.execute(command, object : ExecuteBinaryResponseHandler() {

                      override fun onStart() {
                          super.onStart()

                          progressLayout!!.visibility = View.VISIBLE

                      }

                      override fun onFinish() {
                          super.onFinish()

                          isVideoSelected = true

                          progressLayout!!.visibility = View.GONE

                          var length: Long? = fileTarget.length()
                          length = length!! / 1024

                          //decodeFile(picturePath);
                          //100mb in kb
                          if (length!! > 102400) {
                              ConstantMethods.showError(
                                  context!!,
                                  "Large Video Size",
                                  "Maximum video allowed is 100 MB"
                              )

                          } else {

                              mediaStatusArray!!.add(
                                  MediaUploadingPojo(
                                      fileTarget.absolutePath,
                                      "",
                                      "video",
                                      false
                                  )
                              )


                              val videosSet = java.util.HashSet<String>()
                              for (i in 0 until mediaArray!!.size) {
                                  videosSet.add(mediaArray!![i])
                              }

                              // mimeTypeArray!!.add("video")

                              mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!)
                              mediaRecycler!!.adapter = mediaAdapter

                              EasySP.init(context!!).putString("video", fileUri)

                          }
                      }


                  })

  */

                val targetFile = Environment.getExternalStorageDirectory().toString() + "/"
                val fileTarget = File(targetFile, "We${System.currentTimeMillis()}.mp4")

                val file = File(picturePath)


                // val r = FFmpeg.execute("-i ${file.path} -c:v mpeg4 ${fileTarget}")
                var command = "-i ${file.path} -c:v mpeg4 ${fileTarget}"
                //val r = FFmpeg.execute("-y -i ${file.path} -s 720x480 -r 30 -c:v libx264 -preset ultrafast -c:a copy -me_method zero -tune fastdecode -tune zerolatency -strict -2 -pix_fmt yuv420p ${fileTarget}")

                progressLayout!!.visibility = View.VISIBLE

                try {

                    AsyncSingleFFmpegExecuteTask(command, progressLayout, fileTarget).execute()
                } catch (e: Exception) {
                    e.printStackTrace()
                }


                /*  progressLayout!!.visibility = View.VISIBLE
                  val command = arrayOf(
                      "-y",
                      "-i",
                      file.path,
                      "-s",
                      "720x480",
                      "-r",
                      "30",
                      "-c:v",
                      "libx264",
                      "-preset",
                      "ultrafast",
                      "-c:a",
                      "copy",
                      "-me_method",
                      "zero",
                      "-tune",
                      "fastdecode",
                      "-tune",
                      "zerolatency",
                      "-strict",
                      "-2",
                      "-pix_fmt",
                      "yuv420p",
                      fileTarget.toString()
                  )



                  val r = FFmpeg.execute(command)

                  if(r == 0) {
                      isVideoSelected = true

                      progressLayout!!.visibility = View.GONE

                      var length: Long? = fileTarget.length()
                      length = length!! / 1024

                      //decodeFile(picturePath);
                      //100mb in kb
                      if (length!! > 102400) {
                          ConstantMethods.showError(
                              context!!,
                              "Large Video Size",
                              "Maximum video allowed is 100 MB"
                          )

                      } else {

                          mediaStatusArray!!.add(
                              MediaUploadingPojo(
                                  fileTarget.absolutePath,
                                  "",
                                  "video",
                                  false
                              )
                          )


                          val videosSet = java.util.HashSet<String>()
                          for (i in 0 until mediaArray!!.size) {
                              videosSet.add(mediaArray!![i])
                          }

                          // mimeTypeArray!!.add("video")

                          mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!)
                          mediaRecycler!!.adapter = mediaAdapter

                          EasySP.init(context!!).putString("video", fileUri)

                      }
                  }

*/

            } catch (e: Exception) {
                e.printStackTrace()
            }

            //}

        } else if (requestCode == 5) {
            getSharedData()
        }


    }

    /** This class is run in background to check file size of video
     * if size is greater than 102400 then it will show error dialog
     * else set it to media adapter
     */
    inner class AsyncSingleFFmpegExecuteTask(
        private val command: String,
        val progressLayout: LinearLayout?,
        var fileTarget: File
    ) :
        AsyncTask<String, Int, Int>() {

        override fun doInBackground(vararg arguments: String): Int? {


            Log.d("start", "start")
            var rc = FFmpeg.execute(command)
            return rc
        }

        override fun onPostExecute(rc: Int?) {
            Log.d("complete", rc.toString())
            progressLayout!!.visibility = View.GONE
            if (rc == 0) {
                isVideoSelected = true

                progressLayout!!.visibility = View.GONE

                var length: Long? = fileTarget.length()
                length = length!! / 1024

                //decodeFile(picturePath);
                //100mb in kb
                if (length!! > 102400) {
                    ConstantMethods.showError(
                        context!!,
                        "Large Video Size",
                        "Maximum video allowed is 100 MB"
                    )

                } else {

                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            fileTarget.absolutePath,
                            "",
                            "video",
                            false
                        )
                    )


                    val videosSet = java.util.HashSet<String>()
                    for (i in 0 until mediaArray!!.size) {
                        videosSet.add(mediaArray!![i])
                    }

                    // mimeTypeArray!!.add("video")

                    mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter

                    EasySP.init(context!!).putString("video", fileUri)

                }
            } else {
                progressLayout!!.visibility = View.GONE

                Toast.makeText(context, "Error File not uploaded $rc.", Toast.LENGTH_LONG).show()
            }
        }


    }


    /** It is runtime permission result
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            RequestPermissionCode -> if (grantResults.size > 0) {
                val StoragePermission = (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                if (StoragePermission) {
                    Toast.makeText(
                        context, "Permission Granted",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context, "Permission Denied ", Toast.LENGTH_LONG).show()

                }
            }
        }
    }

    /** It goes back to previous activity of fragment   */
    override fun onBackPressed() {
        super.onBackPressed()

        EasySP.init(this).putString(ConstantEasySP.CAMPAIGN_DATE, "")
        EasySP.init(this).putString(ConstantEasySP.SELECTED_CAMPAIGN_ADDRESS, "")
        EasySP.init(this).putString("city", "")
        EasySP.init(this).putString("lat", "")
        EasySP.init(this).putString("long", "")
        EasySP.init(this).putString("campaignTitle", "")
        EasySP.init(this).putString("campaignDetails", "")
        EasySP.init(this).putString("campaignDate", "")

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        //finish()
    }
}
