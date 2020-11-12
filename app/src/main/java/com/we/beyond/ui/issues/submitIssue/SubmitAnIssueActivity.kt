package com.we.beyond.ui.issues.submitIssue


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.we.beyond.adapter.SubmitIssueFragmentPagerAdapter
import com.we.beyond.Interface.OnVideoDeleteSuccess
import com.we.beyond.R
import com.we.beyond.model.LatLongSelectedPojo
import com.we.beyond.model.MediaUploadingPojo
import com.we.beyond.model.NearByIssueByIdDetailsPojo
import com.we.beyond.presenter.issues.submitIssue.SubmitIssueImpl
import com.we.beyond.presenter.issues.submitIssue.SubmitIssuePresenter
import com.we.beyond.ui.issues.nearByIssue.NearByIssueActivity
import com.we.beyond.ui.issues.submitIssue.fragments.IssueCategoryFragment
import com.we.beyond.ui.issues.submitIssue.fragments.IssueDescriptionFragment
import com.we.beyond.ui.issues.submitIssue.fragments.IssueLocationFragment
import com.we.beyond.ui.issues.submitIssue.fragments.IssueMediaFragment
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import java.util.*
import kotlin.collections.ArrayList
/**
 *  This Activity is used to submit an issues with issue description
 */
class SubmitAnIssueActivity : AppCompatActivity(),
    SubmitIssuePresenter.ISubmitIssueView, OnVideoDeleteSuccess {

    var submitIssueAdapter: SubmitIssueFragmentPagerAdapter? = null
    var issuePresenter: SubmitIssueImpl? = null

    /** init image view */
    var back: ImageView? = null
    var categoryImage: ImageView? = null
    var descriptionImage: ImageView? = null
    var mediaImage: ImageView? = null
    var locationImage: ImageView? = null

    /** init text view */
    var submitIssueTitle: TextView? = null

    /** init view pager */
    var viewPager: ViewPager? = null

    /** init button */
    var next: Button? = null

    /** init fragment */
    var categoryFragment: IssueCategoryFragment? = null
    var descriptionFragment: IssueDescriptionFragment? = null
    var mediaFragment: IssueMediaFragment? = null
    var locationFragment: IssueLocationFragment? = null

    /** init booleans */
    var isEdit: Boolean = false
    var isMyIssue: Boolean = false
    var isDashboard: Boolean = false
    var isBackPressed = false

    /** init model */
    var issueDetailsData: NearByIssueByIdDetailsPojo? = null

    /** init int */
    var currentIndex = 0
    var count = 0

    private val previousIndex = Stack<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_submit_an_issue)

        /** fragment initialization */
        categoryFragment = IssueCategoryFragment()
        descriptionFragment =
            IssueDescriptionFragment()
        mediaFragment = IssueMediaFragment()
        locationFragment = IssueLocationFragment()

        issuePresenter = SubmitIssueImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** set fragment to view pager adapter */
        setFragment()

        /** initialize onclick listener */
        initWithListener()

        /** Get all stored data using intent and assign it respectively */
        val getIntentData = intent.getStringExtra("issueData")
        isEdit = intent.getBooleanExtra("edit", false)
        isDashboard = intent.getBooleanExtra("dashboard", false)
        issueDetailsData = Gson().fromJson(getIntentData, NearByIssueByIdDetailsPojo::class.java)


        /** Issue details data passes to the respective fragments
         * as argument  in the form of json
         */
        val jsonString = Gson().toJson(issueDetailsData)

        if (jsonString.isNotEmpty()) {

            val bundle = Bundle()
            bundle.putString("issueData", jsonString)
            categoryFragment!!.arguments = bundle
            descriptionFragment!!.arguments = bundle
            mediaFragment!!.arguments = bundle
            locationFragment!!.arguments = bundle
        }

    }


    override fun onVideoDelete() {
        mediaFragment!!.isVideoSelected = false

    }

    /** It set fragments to view pager adapter to swipe respective fragment */
    private fun setFragment() {
        submitIssueAdapter = SubmitIssueFragmentPagerAdapter(supportFragmentManager)

        submitIssueAdapter!!.addFragmentHolder(
            SubmitIssueFragmentPagerAdapter.FragmentHolder(
                mediaFragment!!,
                "Upload image / video"
            )
        )

        submitIssueAdapter!!.addFragmentHolder(
            SubmitIssueFragmentPagerAdapter.FragmentHolder(
                categoryFragment!!,
                "Select the Category"
            )
        )
        submitIssueAdapter!!.addFragmentHolder(
            SubmitIssueFragmentPagerAdapter.FragmentHolder(
                descriptionFragment!!,
                "Describe the issue"
            )
        )

        submitIssueAdapter!!.addFragmentHolder(
            SubmitIssueFragmentPagerAdapter.FragmentHolder(
                locationFragment!!,
                "Locate the area"
            )
        )


        viewPager!!.offscreenPageLimit = 3
        viewPager!!.isEnabled = false
        viewPager!!.adapter = submitIssueAdapter

    }

    private fun initWithListener() {

        /** It changes respective fragment */
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

                /** It hides the keyboard */
                ConstantMethods.hideKeyBoard(this@SubmitAnIssueActivity, this@SubmitAnIssueActivity)
            }

            override fun onPageSelected(position: Int) {

                /** It shows the selected fragment and set drawable images to image view */
                if (!isBackPressed) {
                    previousIndex.push(currentIndex)
                }

                categoryImage!!.setImageResource(R.drawable.categorylight)
                descriptionImage!!.setImageResource(R.drawable.describelight)
                mediaImage!!.setImageResource(R.drawable.imagelight)
                locationImage!!.setImageResource(R.drawable.locationlight)

                var imageView: ImageView? = null
                var resId = 0
                var nextButtonText = "Next"
                when (position) {

                    0 -> {
                        imageView = mediaImage
                        resId = R.drawable.image
                    }
                    1 -> {
                        imageView = categoryImage
                        resId = R.drawable.category
                    }
                    2 -> {
                        imageView = descriptionImage
                        resId = R.drawable.describe
                    }

                    3 -> {
                        if (isEdit) {

                            nextButtonText = "Update"
                            imageView = locationImage
                            resId = R.drawable.location

                        } else {

                            nextButtonText = "Submit"
                            imageView = locationImage
                            resId = R.drawable.location
                        }

                    }
                }
                imageView!!.setImageResource(resId)
                next!!.text = nextButtonText

                currentIndex = position
                isBackPressed = false
            }

        })


        /** It checks required fields are empty or not, if empty then shows the warning dialog
         * and get all required fields from user to
         * call the respective method to add or update data to the server
         * */
        next!!.setOnClickListener {
            val selectedPageIndex = viewPager!!.currentItem

            val categoryId = EasySP.init(this).getString("categoryId")
            val issueTitle = EasySP.init(this).getString("issueTitle")
            val issueDetails = EasySP.init(this).getString("issueDetails")
            val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
            val address = EasySP.init(this).getString("locationAddress")
            val city = EasySP.init(this).getString("city")
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)

            val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {

            }.type
            var mediaPojo = ArrayList<MediaUploadingPojo>()
            try {
                mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            println("media $media")
            println("media pojo $mediaPojo")

            if (selectedPageIndex < viewPager!!.adapter!!.count - 1) run {


                when (viewPager!!.currentItem) {
                    1 -> {
                        if (categoryId!!.isEmpty()) {
                            ConstantMethods.showWarning(this, "Category", "Please Select Category")
                        } else {
                            viewPager!!.currentItem = selectedPageIndex + 1
                        }

                    }
                    2 -> {
                        if (issueTitle!!.isEmpty()) {
                            ConstantMethods.showWarning(this, "Title", "Please enter title")
                        } else if (issueDetails!!.isEmpty()) {
                            ConstantMethods.showWarning(this, "Details", "Please enter details")
                        } else {
                            viewPager!!.currentItem = selectedPageIndex + 1
                        }

                    }
                    3 -> {

                        // viewPager!!.currentItem = selectedPageIndex + 1

                    }
                    0 -> {


                        if (media!!.isEmpty()) {
                            ConstantMethods.showWarning(
                                this,
                                "Please Upload Photo / Video",
                                "You need to attach at least 1 photo or video to this issue to post"
                            )
                        } else {

                            viewPager!!.currentItem = selectedPageIndex + 1
                        }
                    }

                }

            }
            else {
                println("position 3")
                if (latlong!!.isEmpty()) {
                    ConstantMethods.showWarning(this, "LatLong", "Please select location")
                } else if (address.isEmpty()) {
                    ConstantMethods.showWarning(this, "Address", "Please enter address")
                } else if (city.isEmpty()) {
                    ConstantMethods.showWarning(this, "City", "Please select city")
                } else if (categoryId!!.isEmpty()) {
                    ConstantMethods.showWarning(this, "Category", "Please select category")
                } else if (issueTitle!!.isEmpty()) {
                    ConstantMethods.showWarning(this, "Title", "Please enter title")
                } else if (issueDetails!!.isEmpty()) {
                    ConstantMethods.showWarning(this, "Details", "Please enter details")
                } else if (media!!.isEmpty()) {
                    ConstantMethods.showWarning(
                        this,
                        "Please Upload Photo / Video",
                        "You need to attach at least 1 photo or video to this issue to post"
                    )
                } else {
                    try {

                        if (isEdit) {

                            if (ConstantMethods.checkForInternetConnection(this)) {
                                getDataToPostUpdate()
                            }
                        } else {
                            if (ConstantMethods.checkForInternetConnection(this)) {
                                getDataToPost()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }


        /** It goes to previous fragment */
        back!!.setOnClickListener {

            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        }

        /** It sets to drawable image to image view
         * and set to count to view pager
         */
        categoryImage!!.setOnClickListener {
            categoryImage!!.setImageResource(R.drawable.category)
            viewPager!!.currentItem = 1
        }

        descriptionImage!!.setOnClickListener {
            descriptionImage!!.setImageResource(R.drawable.describe)
            viewPager!!.currentItem = 2
        }

        mediaImage!!.setOnClickListener {
            mediaImage!!.setImageResource(R.drawable.image)
            viewPager!!.currentItem = 0
        }

        locationImage!!.setOnClickListener {
            locationImage!!.setImageResource(R.drawable.location)
            viewPager!!.currentItem = 3
        }

    }

    /** It converts required data to json object and
     * call postDataToServerUpdate function with json object as parameter
     */
    private fun getDataToPostUpdate() {
        try {
            val categoryId = EasySP.init(this).getString("categoryId")
            val issueTitle = EasySP.init(this).getString("issueTitle")
            val issueDetails = EasySP.init(this).getString("issueDetails")
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
            val address = EasySP.init(this).getString("locationAddress")
            val city = EasySP.init(this).getString("city")

            val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {

            }.type
            var mediaPojo = ArrayList<MediaUploadingPojo>()
            try {
                mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val latLongPojo: LatLongSelectedPojo =
                Gson().fromJson(latlong, LatLongSelectedPojo::class.java)


            println("latlong pojo in activity ${latLongPojo.latitude}")
            val latlongArray = ArrayList<String>()
            latlongArray.add(latLongPojo.latitude.toString())
            latlongArray.add(latLongPojo.longitude.toString())


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
                    ConstantMethods.showError(this, "", "Please wait while media is uploading.")
                }
            }



            println("latlongArray array  $latlongArray")
            println("address $address city $city")

            if (ConstantMethods.checkForInternetConnection(this@SubmitAnIssueActivity)) {

                val jsonObject = JsonObject()

                jsonObject.addProperty("issueId", issueDetailsData!!.data._id)

                if (!issueTitle.equals(issueDetailsData!!.data.title)) {
                    jsonObject.addProperty("title", issueTitle)
                }

                if (!issueDetails.equals(issueDetailsData!!.data.description)) {
                    jsonObject.addProperty("description", issueDetails)
                }
                if (!categoryId.equals(issueDetailsData!!.data.category._id)) {
                    jsonObject.addProperty("category", categoryId)
                }

                if (!address.equals(issueDetailsData!!.data.address)) {
                    jsonObject.addProperty("city", city)
                    jsonObject.addProperty("address", address)
                    //latlong array
                    val latlongJsonArray = JsonArray()

                    for (i in 0 until latlongArray.size) {


                        latlongJsonArray.add(latlongArray[i])


                    }
                    jsonObject.add("coordinates", latlongJsonArray)
                }

                //image array
//                    if(!imageArray.size.equals(issueDetailsData!!.data.imageUrls.size)) {
//                        val imageJsonArray = JsonArray()
//
//                        for (i in 0 until imageArray.size) {
//
//                            imageJsonArray.add(imageArray[i])
//
//                        }
//
//                        println("images json $imageArray")
//                        jsonObject.add("imageUrls", imageJsonArray)
//
//                    }

                val imageJsonArray = JsonArray()

                for (i in 0 until imageArray.size) {

                    imageJsonArray.add(imageArray[i])

                }

                println("images json $imageArray")
                jsonObject.add("imageUrls", imageJsonArray)

//                    if(!imageArray.size.equals(issueDetailsData!!.data.imageUrls.size)) {
//                        val imageJsonArray = JsonArray()
//
//                        for (i in 0 until imageArray.size) {
//
//                            imageJsonArray.add(imageArray[i])
//
//                        }
//
//                        println("images json $imageArray")
//                        jsonObject.add("imageUrls", imageJsonArray)
//
//                    }

//                    if(!videoArray.size.equals(issueDetailsData!!.data.videoUrls.size)) {
//                        //video array
//                        val videoJsonArray = JsonArray()
//                        for (i in 0 until videoArray.size) {
//
//                            videoJsonArray.add(videoArray[i])
//
//                        }
//                        println("video json $videoArray")
//                        jsonObject.add("videoUrls", videoJsonArray)
//                    }

                val videoJsonArray = JsonArray()
                for (i in 0 until videoArray.size) {

                    videoJsonArray.add(videoArray[i])

                }
                println("video json $videoArray")
                jsonObject.add("videoUrls", videoJsonArray)

//                    if(!videoArray.size.equals(issueDetailsData!!.data.videoUrls.size)) {
//                        //video array
//
//                    }


                if (count == mediaPojo.size) {
                    postDataToServerUpdate(jsonObject)
                } else {

                    count = 0

                    ConstantMethods.showWarning(
                        this,
                        "",
                        "Please wait while media is uploading."
                    )
                }



                println("post data $jsonObject")


            }


        } catch (e: Exception) {
            println("catch exception ${e.printStackTrace()}")
        }
    }

    /** It converts required data to json object and
     * call postDataToServer function with json object as parameter
     * */
    private fun getDataToPost() {
        try {
            val categoryId = EasySP.init(this).getString("categoryId")
            val issueTitle = EasySP.init(this).getString("issueTitle")
            val issueDetails = EasySP.init(this).getString("issueDetails")
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
            val address = EasySP.init(this).getString("locationAddress")
            val city = EasySP.init(this).getString("city")

            val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {

            }.type
            var mediaPojo = ArrayList<MediaUploadingPojo>()
            try {
                mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val latLongPojo: LatLongSelectedPojo =
                Gson().fromJson(latlong, LatLongSelectedPojo::class.java)


            val latlongArray = ArrayList<String>()
            latlongArray.add(latLongPojo.latitude.toString())
            latlongArray.add(latLongPojo.longitude.toString())


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
                    ConstantMethods.showError(this, "", "Please wait while media is uploading.")
                }
            }


            if (categoryId.isNotEmpty() && issueTitle.isNotEmpty() && issueDetails!!.isNotEmpty() && media.isNotEmpty() && latlongArray.isNotEmpty() && address!!.isNotEmpty() && city!!.isNotEmpty() && imageArray.isNotEmpty() && videoArray.isNotEmpty()) {
                if (ConstantMethods.checkForInternetConnection(this@SubmitAnIssueActivity)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("title", issueTitle)
                    jsonObject.addProperty("description", issueDetails)
                    jsonObject.addProperty("categoryId", categoryId)
                    jsonObject.addProperty("city", city)
                    jsonObject.addProperty("address", address)

                    //image array
                    val imageJsonArray = JsonArray()

                    for (i in 0 until imageArray.size) {

                        imageJsonArray.add(imageArray[i])

                    }

                    jsonObject.add("imageUrls", imageJsonArray)

                    //video array
                    val videoJsonArray = JsonArray()
                    for (i in 0 until videoArray.size) {

                        videoJsonArray.add(videoArray[i])

                    }

                    jsonObject.add("videoUrls", videoJsonArray)


                    //latlong array
                    val latlongJsonArray = JsonArray()

                    for (i in 0 until latlongArray.size) {


                        latlongJsonArray.add(latlongArray[i])


                    }
                    jsonObject.add("coordinates", latlongJsonArray)


                    if (count == mediaPojo.size) {
                        postDataToServer(jsonObject)
                    } else {

                        count = 0

                        ConstantMethods.showWarning(
                            this,
                            "",
                            "Please wait while media is uploading."
                        )
                    }

                }
            } else if (categoryId.isNotEmpty() && issueTitle.isNotEmpty() && issueDetails!!.isNotEmpty() && media.isNotEmpty() && latlongArray.isNotEmpty() && address!!.isNotEmpty() && city!!.isNotEmpty() && imageArray.isNotEmpty()) {
                if (ConstantMethods.checkForInternetConnection(this@SubmitAnIssueActivity)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("title", issueTitle)
                    jsonObject.addProperty("description", issueDetails)
                    jsonObject.addProperty("categoryId", categoryId)
                    jsonObject.addProperty("city", city)
                    jsonObject.addProperty("address", address)

                    //image array
                    val imageJsonArray = JsonArray()

                    for (i in 0 until imageArray.size) {
                        imageJsonArray.add(imageArray[i])
                    }

                    jsonObject.add("imageUrls", imageJsonArray)


                    //latlong array
                    val latlongJsonArray = JsonArray()

                    for (i in 0 until latlongArray.size) {


                        latlongJsonArray.add(latlongArray[i])


                    }
                    jsonObject.add("coordinates", latlongJsonArray)


                    if (count == mediaPojo.size) {
                        postDataToServer(jsonObject)
                    } else {

                        count = 0

                        ConstantMethods.showWarning(
                            this,
                            "",
                            "Please wait while media is uploading."
                        )
                    }

                }
            } else if (categoryId.isNotEmpty() && issueTitle.isNotEmpty() && issueDetails!!.isNotEmpty() && media.isNotEmpty() && latlongArray.isNotEmpty() && address!!.isNotEmpty() && city!!.isNotEmpty() && videoArray.isNotEmpty()) {
                if (ConstantMethods.checkForInternetConnection(this@SubmitAnIssueActivity)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("title", issueTitle)
                    jsonObject.addProperty("description", issueDetails)
                    jsonObject.addProperty("categoryId", categoryId)
                    jsonObject.addProperty("city", city)
                    jsonObject.addProperty("address", address)


                    //video array
                    val videoJsonArray = JsonArray()
                    for (i in 0 until videoArray.size) {
                        videoJsonArray.add(videoArray[i])
                    }
                    jsonObject.add("videoUrls", videoJsonArray)


                    //latlong array
                    val latlongJsonArray = JsonArray()

                    for (i in 0 until latlongArray.size) {

                        latlongJsonArray.add(latlongArray[i])

                    }
                    jsonObject.add("coordinates", latlongJsonArray)


                    if (count == mediaPojo.size) {
                        postDataToServer(jsonObject)
                    } else {

                        count = 0

                        ConstantMethods.showWarning(
                            this,
                            "",
                            "Please wait while media is uploading."
                        )
                    }



                    println("post data $jsonObject")


                }
            } else if (categoryId.isNotEmpty() && issueTitle.isNotEmpty() && issueDetails!!.isNotEmpty() && latlongArray.isNotEmpty() && address!!.isNotEmpty() && city!!.isNotEmpty() && mediaPojo.size == 0) {
                if (ConstantMethods.checkForInternetConnection(this@SubmitAnIssueActivity)) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("title", issueTitle)
                    jsonObject.addProperty("description", issueDetails)
                    jsonObject.addProperty("categoryId", categoryId)
                    jsonObject.addProperty("city", city)
                    jsonObject.addProperty("address", address)

                    //latlong array
                    val latlongJsonArray = JsonArray()

                    for (i in 0 until latlongArray.size) {

                        latlongJsonArray.add(latlongArray[i])
                    }


                    jsonObject.add("coordinates", latlongJsonArray)
                    postDataToServer(jsonObject)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }

    }

    /** It takes the json object as input and send to onIssueCreated function of issue presenter */
    private fun postDataToServer(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this@SubmitAnIssueActivity, "Please Wait...")
                issuePresenter!!.onIssueCreated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onIssueUpdated function of issue presenter */
    private fun postDataToServerUpdate(jsonObject: JsonObject) {
        try {


            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this@SubmitAnIssueActivity, "Please Wait...")
                issuePresenter!!.onIssueUpdated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It opens the respective activity on condition */
    override fun goToNextScreen() {
        EasySP.init(this).putString("issueTitle", "")
        EasySP.init(this).putString("issueDetails", "")
        if (isEdit) {

            println("is edit")
            val intent = Intent()
            intent.putExtra("issueId", issueDetailsData!!.data._id)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

            count = 0
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
        } else if (isDashboard) {
            val intent = Intent(this, NearByIssueActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

            count = 0
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
        } else {
            val intent = Intent(this, NearByIssueActivity::class.java)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

            count = 0
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
        }
    }


    /** ui initialization  */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        categoryImage = findViewById(R.id.img_category_step_icon)
        descriptionImage = findViewById(R.id.img_description_step_icon)
        mediaImage = findViewById(R.id.img_media_step_icon)
        locationImage = findViewById(R.id.img_location_step_icon)

        /** ids of text view */
        submitIssueTitle = findViewById(R.id.txt_submit_issue_title)
        submitIssueTitle!!.typeface = ConstantFonts.raleway_semibold

        /** ids of view pager */
        viewPager = findViewById(R.id.viewpager)

        /** ids of button */
        next = findViewById(R.id.btn_next)
        next!!.typeface = ConstantFonts.raleway_semibold

    }


    private fun shouldShowConfirmationAlert(): Boolean
    {
        val categoryId = EasySP.init(this).getString("categoryId")
        val issueTitle = EasySP.init(this).getString("issueTitle")
        val issueDetails = EasySP.init(this).getString("issueDetails")
        val latlong = EasySP.init(this).getString(ConstantEasySP.LATLONG_POJO)
        val address = EasySP.init(this).getString("locationAddress")
        val city = EasySP.init(this).getString("city")
        val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
        return  (!categoryId!!.isEmpty() || !issueTitle!!.isEmpty() || !issueDetails!!.isEmpty() || !latlong!!.isEmpty() || !address!!.isEmpty() || !city!!.isEmpty() ||  ! media!!.isEmpty())
    }
    /** It goes back to previous activity of fragment   */
    override fun onBackPressed() {
        if(shouldShowConfirmationAlert())
        {
            ConstantMethods.showAlert(this, "", "Are you sure you want to discard this issue?")
        }
        else {
            super.onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}
