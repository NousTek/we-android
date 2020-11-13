package com.we.beyond.ui.reportResolved

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.we.beyond.adapter.MediaAdapter
import com.we.beyond.adapter.ResolvedIssueUserAdapter
import com.we.beyond.Interface.OnItemClickListener
import com.we.beyond.Interface.OnVideoDeleteSuccess
import com.we.beyond.R
import com.we.beyond.RealPathUtils
import com.we.beyond.model.*
import com.we.beyond.presenter.mediaUpload.MediaImpl
import com.we.beyond.presenter.reportResolved.ReportResolvedImpl
import com.we.beyond.presenter.reportResolved.ReportResolvedPresenter
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.FileUtils
import com.white.easysp.EasySP
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.activity_report_resolved.*
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/** It is used to resolve the issue with require details */
class ReportResolvedActivity : AppCompatActivity(), ReportResolvedPresenter.IReportResolvedView,
    OnItemClickListener, OnVideoDeleteSuccess {

    /** init context */
    var context: Context = this
    private var projection =
        arrayOf(MediaStore.MediaColumns.DATA)
    /** init adapter */
    var mediaAdapter: MediaAdapter? = null
    var userTypeAdapter: ResolvedIssueUserAdapter? = null

    /** init implementors and array list */
    var mediaPresenter: MediaImpl? = null
    var reportResolvedPresenter: ReportResolvedImpl? = null
    var userPojo: ArrayList<UserList>? = null

    /** init of image view */
    var back: ImageView? = null
    var closeGallery: ImageView? = null
    var close: ImageView? = null

    /** init of text view */
    var title: TextView? = null
    var mediaTitle: TextView? = null
    var or: TextView? = null
    var mediaOr: TextView? = null
    var mediaOrOption: TextView? = null

    /** init edit text */
    var dateTime: EditText? = null
    var resolvedDetails: EditText? = null
    var resolvedBy: EditText? = null

    /** button */
    var resolved: Button? = null
    var useCamera: Button? = null
    var useGallery: Button? = null
    var image: Button? = null
    var video: Button? = null
    var imageGallery: Button? = null
    var videoGalley: Button? = null

    /** init linear layout */
    var progressLayout: LinearLayout? = null

    /** recycler view and layout manager  */
    var mediaRecycler: RecyclerView? = null
    var userTypeRecycler: RecyclerView? = null
    var linearLayoutManager: LinearLayoutManager? = null

    var mMediaUri: Uri? = null

    /** tag layout library */
    var chipGroup: ChipGroup? = null

    /** Array list */
    var mimeTypeArray: ArrayList<String>? = null
    var mediaArray: ArrayList<String>? = null
    var userTypeList: ArrayList<String>? = null
    var userNameList: ArrayList<String>? = null
    var userSelectedId: ArrayList<String>? = null
    var imageArrayList: ArrayList<String>? = null
    var videoArrayList: ArrayList<String>? = null
    var mediaStatusArray: ArrayList<MediaUploadingPojo>? = null


    /** init relative layout */
    var optionLayout: RelativeLayout? = null
    var galleryOptionLayout: RelativeLayout? = null

    /** init variables */
    var CAMERA = 1
    var VIDEO = 2
    var GALLERY_IMAGE = 3
    var GALLERY_VIDEO = 4

    var file: File? = null
    var fileUri: String? = null
    var selectedDate: String = ""
    var selectedTime: String = ""
    var resolvedDate: String = ""
    var picturePath: String? = null
    var issueId: String = ""
    var resoltionId: String = ""


    var count = 0
    var RequestPermissionCode = 1

    var isEdit: Boolean = false
    var isMyComment: Boolean = false
    var isVideoSelected: Boolean = false

    /** init models */
    var commentDetailsData: CommentDetailsPojo? = null
    var resolutionDetailsData: Resolution? = null
    var commentData: CommentsData? = null


    /** init date and time picker */
    var date: DatePickerDialog.OnDateSetListener? = null
    var myCalendar: Calendar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_resolved)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        count = 0

        /** initialize calender and array list */
        myCalendar = Calendar.getInstance()
        mediaArray = ArrayList()
        mediaArray = ArrayList()

        userTypeList = ArrayList()
        userTypeList!!.clear()
        userPojo = ArrayList()

        mimeTypeArray = ArrayList()
        mediaArray = ArrayList()
        mediaStatusArray = ArrayList()

        userNameList = ArrayList()
        userNameList!!.clear()

        userSelectedId = ArrayList()
        userSelectedId!!.clear()

        videoArrayList = ArrayList()
        videoArrayList!!.clear()

        imageArrayList = ArrayList()
        imageArrayList!!.clear()

        /** initialize implementation */
        //mediaPresenter = MediaImpl(this)
        reportResolvedPresenter = ReportResolvedImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()

        /** set user types data to spinner */
        //setUserTypes()

        /** initialize onclick listener */
        initWithListener()

        /*  if (ConstantMethods.checkPermission(context)) {
              val mydir =
                  File(Environment.getExternalStorageDirectory().toString() + resources.getString(R.string.app_name))
              if (!mydir.exists()) {
                  mydir.mkdirs()
              }
          } else {
              ConstantMethods.requestPermission(context)
          }*/

        /** It get stored data using intent and checks the comment details
         * if not null then set data to media adapter and chip view using condition
         */

        issueId = intent.getStringExtra("issueId")

        val getIntentData = intent.getStringExtra("resolutionData")
        val getUserIntentData = intent.getStringExtra("userResolutionData")
        val getCommentDetailsIntentData = intent.getStringExtra("resolutionDetailsData")
        isEdit = intent.getBooleanExtra("edit", false)
        commentData = Gson().fromJson(getIntentData, CommentsData::class.java)
        commentDetailsData =
            Gson().fromJson(getCommentDetailsIntentData, CommentDetailsPojo::class.java)
        resolutionDetailsData = Gson().fromJson(getUserIntentData, Resolution::class.java)
        // isMyComment = intent.getBooleanExtra("myCampaign",false)

        println("user resolution  }")

        if (isEdit) {
            resolved!!.text = "update"

            resoltionId = intent.getStringExtra("resolutionId")

        } else {
            resolved!!.text = "resolved"
        }

        println("campaign data $isEdit")

        if (commentData != null) {

            resolvedDetails!!.setText(commentData!!.text)
            dateTime!!.setText(ConstantMethods.convertStringToDateStringFull(commentData!!.resolutionDate))


            if (commentData!!.imageUrls != null && commentData!!.imageUrls.isNotEmpty()) {

                imageArrayList!!.addAll(commentData!!.imageUrls)

                for (i in 0 until commentData!!.imageUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            commentData!!.imageUrls[i],
                            "image",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }

            if (commentData!!.videoUrls != null && commentData!!.videoUrls.isNotEmpty()) {

                videoArrayList!!.addAll(commentData!!.videoUrls)

                for (i in 0 until commentData!!.videoUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            commentData!!.videoUrls[i],
                            "video",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }

            if (commentData!!.resolutionUsersData != null && commentData!!.resolutionUsersData.isNotEmpty()) {
                for (i in 0 until commentData!!.resolutionUsersData.size) {

                    userSelectedId!!.add(commentData!!.resolutionUsersData[i]._id)

                    userNameList!!.add(commentData!!.resolutionUsersData[i].name)

                    val resolvedTag = Chip(this)
                    resolvedTag.text = commentData!!.resolutionUsersData[i].name
                    //resolvedTag.chipIcon = ContextCompat.getDrawable(this, )
                    resolvedTag.isCloseIconVisible = true
                    resolvedTag.setTextAppearanceResource(R.style.ChipTextStyle)
                    resolvedTag.isCheckable = false
                    resolvedTag.isClickable = true

                    chipGroup!!.addView(resolvedTag as View)
                    chipGroup!!.setChipSpacing(20)
                    //et_resolved_by.setText("")
                    userTypeRecycler!!.visibility = View.GONE


                    resolvedTag.setOnCloseIconClickListener {
                        chipGroup!!.removeView(resolvedTag as View)
                        //et_resolved_by.setText("")
                        userNameList!!.remove(commentData!!.resolutionUsersData[i].name)
                        userSelectedId!!.remove(commentData!!.resolutionUsersData[i]._id)
                        commentData!!.resolutionUsers.remove(commentData!!.resolutionUsersData[i]._id)


                    }


                }
            }

        } else if (commentDetailsData != null) {

            resolvedDetails!!.setText(commentDetailsData!!.data.text)
            dateTime!!.setText(ConstantMethods.convertStringToDateStringFull(commentDetailsData!!.data.resolutionDate))


            if (commentDetailsData!!.data.imageUrls != null && commentDetailsData!!.data.imageUrls.isNotEmpty()) {

                imageArrayList!!.addAll(commentDetailsData!!.data!!.imageUrls)

                for (i in 0 until commentDetailsData!!.data!!.imageUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            commentDetailsData!!.data!!.imageUrls[i],
                            "image",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }

            if (commentDetailsData!!.data!!.videoUrls != null && commentDetailsData!!.data!!.videoUrls.isNotEmpty()) {

                videoArrayList!!.addAll(commentDetailsData!!.data!!.videoUrls)

                for (i in 0 until commentDetailsData!!.data!!.videoUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            commentDetailsData!!.data!!.videoUrls[i],
                            "video",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }

            if (commentDetailsData!!.data.resolutionUsersData != null && commentDetailsData!!.data.resolutionUsersData.isNotEmpty()) {
                for (i in 0 until commentDetailsData!!.data.resolutionUsersData.size) {

                    userSelectedId!!.add(commentDetailsData!!.data.resolutionUsersData[i]._id)

                    userNameList!!.add(commentDetailsData!!.data.resolutionUsersData[i].name)

                    val resolvedTag = Chip(this)
                    resolvedTag.text = commentDetailsData!!.data.resolutionUsersData[i].name
                    //resolvedTag.chipIcon = ContextCompat.getDrawable(this, )
                    resolvedTag.isCloseIconVisible = true
                    resolvedTag.setTextAppearanceResource(R.style.ChipTextStyle)
                    resolvedTag.isCheckable = false
                    resolvedTag.isClickable = true

                    chipGroup!!.addView(resolvedTag as View)
                    chipGroup!!.setChipSpacing(20)
                    //et_resolved_by.setText("")
                    userTypeRecycler!!.visibility = View.GONE


                    resolvedTag.setOnCloseIconClickListener {
                        chipGroup!!.removeView(resolvedTag as View)
                        //et_resolved_by.setText("")
                        userNameList!!.remove(commentDetailsData!!.data.resolutionUsersData[i].name)
                        userSelectedId!!.remove(commentDetailsData!!.data.resolutionUsersData[i]._id)
                        commentDetailsData!!.data!!.resolutionUsers.remove(commentDetailsData!!.data.resolutionUsersData[i]._id)


                    }
                }
            }

        } else if (resolutionDetailsData != null) {

            resolvedDetails!!.setText(resolutionDetailsData!!.text)
            dateTime!!.setText(ConstantMethods.convertStringToDateStringFull(resolutionDetailsData!!.resolutionDate))


            if (resolutionDetailsData!!.imageUrls != null && resolutionDetailsData!!.imageUrls.isNotEmpty()) {

                imageArrayList!!.addAll(resolutionDetailsData!!.imageUrls)

                for (i in 0 until resolutionDetailsData!!.imageUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            resolutionDetailsData!!.imageUrls[i],
                            "image",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }

            if (resolutionDetailsData!!.videoUrls != null && resolutionDetailsData!!.videoUrls.isNotEmpty()) {

                videoArrayList!!.addAll(resolutionDetailsData!!.videoUrls)

                for (i in 0 until resolutionDetailsData!!.videoUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            resolutionDetailsData!!.videoUrls[i],
                            "video",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }

            if (resolutionDetailsData!!.resolutionUsersData != null && resolutionDetailsData!!.resolutionUsersData.isNotEmpty()) {
                for (i in 0 until resolutionDetailsData!!.resolutionUsersData.size) {
                    userSelectedId!!.add(resolutionDetailsData!!.resolutionUsersData[i]._id)

                    userNameList!!.add(resolutionDetailsData!!.resolutionUsersData[i].name)

                    val resolvedTag = Chip(this)
                    resolvedTag.text = resolutionDetailsData!!.resolutionUsersData[i].name
                    //resolvedTag.chipIcon = ContextCompat.getDrawable(this, )
                    resolvedTag.isCloseIconVisible = true
                    resolvedTag.setTextAppearanceResource(R.style.ChipTextStyle)
                    resolvedTag.isCheckable = false
                    resolvedTag.isClickable = true

                    chipGroup!!.addView(resolvedTag as View)
                    chipGroup!!.setChipSpacing(20)
                    //et_resolved_by.setText("")
                    userTypeRecycler!!.visibility = View.GONE


                    resolvedTag.setOnCloseIconClickListener {
                        chipGroup!!.removeView(resolvedTag as View)
                        //et_resolved_by.setText("")
                        userNameList!!.remove(resolutionDetailsData!!.resolutionUsersData[i].name)
                        userSelectedId!!.remove(resolutionDetailsData!!.resolutionUsersData[i]._id)
                        resolutionDetailsData!!.resolutionUsers.remove(resolutionDetailsData!!.resolutionUsersData[i]._id)


                    }


                }
            }
        } else {

        }


    }

    override fun onVideoDelete() {
        isVideoSelected = false

    }

    /** It sets the comment text and add user name to chip view */
    override fun OnClick(userName: String, userId: String) {

        et_resolved_by.setText(userName)
        et_resolved_by.setSelection(et_resolved_by.text.length)
        userSelectedId!!.add(userId)

        userNameList!!.add(userName)

        val resolvedTag = Chip(this)
        resolvedTag.text = userName
        //resolvedTag.chipIcon = ContextCompat.getDrawable(this, )
        resolvedTag.isCloseIconVisible = true
        resolvedTag.setTextAppearanceResource(R.style.ChipTextStyle)
        resolvedTag.isCheckable = false
        resolvedTag.isClickable = true

        chipGroup!!.addView(resolvedTag as View)
        chipGroup!!.setChipSpacing(20)
        et_resolved_by.setText("")
        userTypeRecycler!!.visibility = View.GONE


        resolvedTag.setOnCloseIconClickListener {
            chipGroup!!.removeView(resolvedTag as View)
            et_resolved_by.setText("")
            userNameList!!.remove(userName)
            userSelectedId!!.remove(userId)


        }


    }

    /* private fun setUserTypes() {
         try {

                 if (ConstantMethods.checkForInternetConnection(context)) {
                         ConstantMethods.showProgessDialog(this, "Please Wait...")
                     reportResolvedPresenter!!.OnRequestUserList(this)
                 }
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }
 */

    /** ui listeners */
    private fun initWithListener() {

        /** It opens date picker  */
        dateTime!!.setOnClickListener {
            ConstantMethods.hideKeyBoard(context, this)
            val datePicker = DatePickerDialog(
                context, date, myCalendar!!.get(Calendar.YEAR), myCalendar!!.get(Calendar.MONTH),
                myCalendar!!.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
            datePicker.datePicker.maxDate = System.currentTimeMillis()
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
                    resolvedDate = "$selectedDate $selectedTime"
                    println("date and time $resolvedDate")

                    dateTime!!.setText(ConstantMethods.convertDateStringToShow(resolvedDate))

                    println(
                        "server date and time ${ConstantMethods.convertDateStringToServerDateFull(
                            dateTime!!.text.toString()
                        )}"
                    )

                },
                myCalendar!!.get(Calendar.HOUR_OF_DAY),
                myCalendar!!.get(Calendar.MINUTE),
                false
            ).show()

        }

        /** It opens camera intent to capture image */
        useCamera!!.setOnClickListener {
            try {
                ConstantMethods.hideKeyBoard(context, this)
                optionLayout!!.visibility = View.VISIBLE
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

        /** It opens gallery to select image from gallery */
        useGallery!!.setOnClickListener {
            try {
                ConstantMethods.hideKeyBoard(context, this)
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
                if (!isVideoSelected) {

                    optionLayout!!.visibility = View.GONE

                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 59)
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
                pickMedia(10-mediaAdapter!!.itemCount, MimeType.ofImage(), GALLERY_IMAGE)
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
                if(mediaAdapter!=null && !mediaAdapter!!.isVideoAvailable())
                    pickMedia(1, MimeType.ofVideo(), GALLERY_VIDEO)
                else
                    Toast.makeText(this, "You cannot upload more than one video", Toast.LENGTH_SHORT).show()
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

        /** It is used to call functions to add or update depends on conditions */
        resolved!!.setOnClickListener {
            try {
                if (isEdit) {

                    if (ConstantMethods.checkForInternetConnection(this@ReportResolvedActivity)) {
                        getDataToPostUpdate()
                    }
                } else {

                    if (ConstantMethods.checkForInternetConnection(this@ReportResolvedActivity)) {
                        getDataToPost()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /* resolvedBy!!.setOnClickListener {
             userTypeRecycler!!.visibility = View.VISIBLE

         }*/

        /** It will search the user from user list
         * from OnRequestUserListOnSearch function of report resolved presenter  */
        resolvedBy!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //userTypeRecycler!!.visibility = View.VISIBLE
                //reportResolvedPresenter!!.OnRequestUserList(context)
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length >= 3) {
                    userTypeRecycler!!.visibility = View.VISIBLE
                    reportResolvedPresenter!!.OnRequestUserListOnSearch(
                        context,
                        resolvedBy!!.text.toString().trim()


                    )
                } else if (p0.length == 0) {
                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            // reportResolvedPresenter!!.OnRequestUserList(context)
                            userTypeList!!.clear()
                            userTypeAdapter!!.notifyDataSetChanged()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        })

        /** It goes back to previous activity */
        back!!.setOnClickListener {
            onBackPressed()
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
        }


    }

    /** It is used to call update function and dialog to upload media files
     * to the server depends on comment data and comment details data   */
    private fun getDataToPostUpdate() {

        try {
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val details = resolvedDetails!!.text.toString().trim()

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

            if (ConstantMethods.checkForInternetConnection(this@ReportResolvedActivity)) {


                if (commentData != null) {

                    val jsonObject = JsonObject()


                    jsonObject.addProperty("resolutionId", resoltionId)
                    if (!resolvedDetails!!.text.toString().equals(
                            commentData!!.text,
                            ignoreCase = true
                        )
                    ) {
                        jsonObject.addProperty("text", details)
                    }
                    if (!ConstantMethods.convertStringToDateStringFull(commentData!!.resolutionDate)
                            .equals(
                                dateTime!!.text.toString(),
                                ignoreCase = true
                            )
                    ) {
                        jsonObject.addProperty(
                            "resolutionDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                    }

                    println("issue id in reported resolution $issueId")

                    if (!userTypeList!!.size.equals(commentData!!.resolutionUsers.size)) {
                        val userTypeJsonArray = JsonArray()


                        for (i in 0 until userSelectedId!!.size) {

                            userTypeJsonArray.add(userSelectedId!![i])

                        }
                        jsonObject.add("users", userTypeJsonArray)
                    }

                    //image array
                    if (!imageArray.size.equals(commentData!!.imageUrls.size)) {
                        val imageJsonArray = JsonArray()

                        for (i in 0 until imageArray.size) {

                            imageJsonArray.add(imageArray[i])

                        }

                        println("images json $imageArray")
                        jsonObject.add("imageUrls", imageJsonArray)

                    }


                    //video array

                    if (!videoArray.size.equals(commentData!!.videoUrls.size)) {
                        val videoJsonArray = JsonArray()
                        for (i in 0 until videoArray.size) {

                            videoJsonArray.add(videoArray[i])

                        }
                        println("video json $videoArray")
                        jsonObject.add("videoUrls", videoJsonArray)

                    }

                    if (count == mediaPojo.size) {

                        postDataToServerUpdate(jsonObject)

                    } else {
                        count = 0
                        ConstantMethods.showWarning(
                            this,
                            "Please wait",
                            "Please wait media uploading."
                        )
                    }


                    println("post data $jsonObject")
                } else if (commentDetailsData != null) {

                    val jsonObject = JsonObject()


                    jsonObject.addProperty("resolutionId", resoltionId)
                    if (!resolvedDetails!!.text.toString().equals(
                            commentDetailsData!!.data.text,
                            ignoreCase = true
                        )
                    ) {
                        jsonObject.addProperty("text", details)
                    }
                    if (!ConstantMethods.convertStringToDateStringFull(commentDetailsData!!.data!!.resolutionDate)
                            .equals(
                                dateTime!!.text.toString(),
                                ignoreCase = true
                            )
                    ) {
                        jsonObject.addProperty(
                            "resolutionDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                    }

                    println("issue id in reported resolution $issueId")

                    if (!userTypeList!!.size.equals(commentDetailsData!!.data!!.resolutionUsers.size)) {
                        val userTypeJsonArray = JsonArray()


                        for (i in 0 until userSelectedId!!.size) {

                            userTypeJsonArray.add(userSelectedId!![i])

                        }
                        jsonObject.add("users", userTypeJsonArray)
                    }

                    //image array
                    if (!imageArray.size.equals(commentDetailsData!!.data!!.imageUrls.size)) {
                        val imageJsonArray = JsonArray()

                        for (i in 0 until imageArray.size) {

                            imageJsonArray.add(imageArray[i])

                        }

                        println("images json $imageArray")
                        jsonObject.add("imageUrls", imageJsonArray)

                    }


                    //video array

                    if (!videoArray.size.equals(commentDetailsData!!.data.videoUrls.size)) {
                        val videoJsonArray = JsonArray()
                        for (i in 0 until videoArray.size) {

                            videoJsonArray.add(videoArray[i])

                        }
                        println("video json $videoArray")
                        jsonObject.add("videoUrls", videoJsonArray)

                    }

                    if (count == mediaPojo.size) {

                        postDataToServerUpdate(jsonObject)

                    } else {
                        count = 0
                        ConstantMethods.showWarning(
                            this,
                            "Please wait",
                            "Please wait media uploading."
                        )
                    }


                    println("post data $jsonObject")
                } else {
                    val jsonObject = JsonObject()


                    jsonObject.addProperty("resolutionId", resoltionId)
                    if (!resolvedDetails!!.text.toString().equals(
                            resolutionDetailsData!!.text,
                            ignoreCase = true
                        )
                    ) {
                        jsonObject.addProperty("text", details)
                    }
                    if (!ConstantMethods.convertStringToDateStringFull(resolutionDetailsData!!.resolutionDate)
                            .equals(
                                dateTime!!.text.toString(),
                                ignoreCase = true
                            )
                    ) {
                        jsonObject.addProperty(
                            "resolutionDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )
                    }

                    println("issue id in reported resolution $issueId")

                    if (!userTypeList!!.size.equals(resolutionDetailsData!!.resolutionUsers.size)) {
                        val userTypeJsonArray = JsonArray()


                        for (i in 0 until userSelectedId!!.size) {

                            userTypeJsonArray.add(userSelectedId!![i])

                        }
                        jsonObject.add("users", userTypeJsonArray)
                    }

                    //image array
                    if (!imageArray.size.equals(resolutionDetailsData!!.imageUrls.size)) {
                        val imageJsonArray = JsonArray()

                        for (i in 0 until imageArray.size) {

                            imageJsonArray.add(imageArray[i])

                        }


                        jsonObject.add("imageUrls", imageJsonArray)

                    }


                    //video array

                    if (!videoArray.size.equals(resolutionDetailsData!!.videoUrls.size)) {
                        val videoJsonArray = JsonArray()
                        for (i in 0 until videoArray.size) {

                            videoJsonArray.add(videoArray[i])

                        }
                        println("video json $videoArray")
                        jsonObject.add("videoUrls", videoJsonArray)

                    }

                    if (count == mediaPojo.size) {

                        postDataToServerUpdate(jsonObject)

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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onRequestReportResolvedDataUpdate function of report resolved presenter */
    private fun postDataToServerUpdate(jsonObject: JsonObject) {

        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportResolvedPresenter!!.onRequestReportResolvedDataUpdate(this, jsonObject)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    /** It checks the user data,
     *  if it is not empty then set ResolvedIssueUserAdapter
     */
    override fun setUserListAdapter(userList: ArrayList<UserList>) {
        try {
            userTypeList!!.clear()


            userPojo!!.addAll(userList)


            /* if(commentData!=null ) {
                 for (j in 0 until userList.size) {
                     for (i in 0 until commentData!!.resolutionUsers.size) {

                         if (userList[j]._id==commentData!!.resolutionUsers[i] ) {
                             userSelectedId!!.add(userList[j]._id)

                             userNameList!!.add(userList[j].name)

                             val resolvedTag = Chip(this)
                             resolvedTag.text = userList[j].name
                             //resolvedTag.chipIcon = ContextCompat.getDrawable(this, )
                             resolvedTag.isCloseIconVisible = true
                             resolvedTag.setTextAppearanceResource(R.style.ChipTextStyle)
                             resolvedTag.isCheckable = false
                             resolvedTag.isClickable = true

                             chipGroup!!.addView(resolvedTag as View)
                             chipGroup!!.setChipSpacing(20)
                             //et_resolved_by.setText("")
                             userTypeRecycler!!.visibility = View.GONE


                             resolvedTag.setOnCloseIconClickListener {
                                 chipGroup!!.removeView(resolvedTag as View)
                                 //et_resolved_by.setText("")
                                 userNameList!!.remove(userList[j].name)
                                 userSelectedId!!.remove(userList[j]._id)
                                 commentData!!.resolutionUsers.remove(userList[j]._id)


                             }


                         }

                     }
                 }
             }*/


            /*  else  if(commentDetailsData!=null ) {
                  for (j in 0 until userList.size) {
                      for (i in 0 until commentDetailsData!!.data.resolutionUsers.size) {

                          if (userList[j]._id==commentDetailsData!!.data!!.resolutionUsers[i] ) {
                              userSelectedId!!.add(userList[j]._id)

                              userNameList!!.add(userList[j].name)

                              val resolvedTag = Chip(this)
                              resolvedTag.text = userList[j].name
                              //resolvedTag.chipIcon = ContextCompat.getDrawable(this, )
                              resolvedTag.isCloseIconVisible = true
                              resolvedTag.setTextAppearanceResource(R.style.ChipTextStyle)
                              resolvedTag.isCheckable = false
                              resolvedTag.isClickable = true

                              chipGroup!!.addView(resolvedTag as View)
                              chipGroup!!.setChipSpacing(20)
                              //et_resolved_by.setText("")
                              userTypeRecycler!!.visibility = View.GONE


                              resolvedTag.setOnCloseIconClickListener {
                                  chipGroup!!.removeView(resolvedTag as View)
                                  //et_resolved_by.setText("")
                                  userNameList!!.remove(userList[j].name)
                                  userSelectedId!!.remove(userList[j]._id)
                                  commentDetailsData!!.data!!.resolutionUsers.remove(userList[j]._id)


                              }


                          }

                      }
                  }
              }*/
            /* else   if(resolutionDetailsData!=null ) {
                 for (j in 0 until userList.size) {
                     for (i in 0 until resolutionDetailsData!!.resolutionUsers.size) {

                         if (userList[j]._id==resolutionDetailsData!!.resolutionUsers[i] ) {
                             userSelectedId!!.add(userList[j]._id)

                             userNameList!!.add(userList[j].name)

                             val resolvedTag = Chip(this)
                             resolvedTag.text = userList[j].name
                             //resolvedTag.chipIcon = ContextCompat.getDrawable(this, )
                             resolvedTag.isCloseIconVisible = true
                             resolvedTag.setTextAppearanceResource(R.style.ChipTextStyle)
                             resolvedTag.isCheckable = false
                             resolvedTag.isClickable = true

                             chipGroup!!.addView(resolvedTag as View)
                             chipGroup!!.setChipSpacing(20)
                             //et_resolved_by.setText("")
                             userTypeRecycler!!.visibility = View.GONE


                             resolvedTag.setOnCloseIconClickListener {
                                 chipGroup!!.removeView(resolvedTag as View)
                                 //et_resolved_by.setText("")
                                 userNameList!!.remove(userList[j].name)
                                 userSelectedId!!.remove(userList[j]._id)
                                 resolutionDetailsData!!.resolutionUsers.remove(userList[j]._id)


                             }


                         }

                     }
                 }
             }*/
            // else {

            if (userList.isNotEmpty()) {
                for (i in 0 until userList.size) {
                    userTypeList!!.add(userList[i].name)


                }
            }

            if (userTypeList!!.isNotEmpty()) {

                userTypeAdapter = ResolvedIssueUserAdapter(
                    this,
                    userList,
                    resolvedBy!!.text.toString().trim()
                )
                userTypeRecycler!!.adapter = userTypeAdapter

            }
            // }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It is used to call function and dialog to upload media files
     * to the server depends on comment data and comment details data   */
    private fun getDataToPost() {

        try {
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val details = resolvedDetails!!.text.toString().trim()

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



            if (resolvedDetails!!.text.isEmpty()) {
                ConstantMethods.showWarning(
                    context,
                    "Details",
                    "Please Enter Report Resolution Details"
                )
            } else if (dateTime!!.text.isEmpty()) {
                ConstantMethods.showWarning(context, "Date And Time", "Please Select Date And Time")
            } else {


                if (details.isNotEmpty() && issueId.isNotEmpty() && userSelectedId!!.isNotEmpty() && dateTime!!.text.isNotEmpty() && mediaPojo.size == 0) {
                    if (ConstantMethods.checkForInternetConnection(this@ReportResolvedActivity)) {

                        /*val userArray = ArrayList<String>()
                        userArray.add(userSelectedId!!)*/

                        val jsonObject = JsonObject()

                        jsonObject.addProperty("issueId", issueId)
                        jsonObject.addProperty("text", details)
                        jsonObject.addProperty(
                            "resolutionDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )


                        val userTypeJsonArray = JsonArray()

                        for (i in 0 until userSelectedId!!.size) {

                            userTypeJsonArray.add(userSelectedId!![i])

                        }
                        jsonObject.add("users", userTypeJsonArray)

                        postDataToServer(jsonObject)


                    }
                } else if (details.isNotEmpty() && issueId.isNotEmpty() && userSelectedId!!.isNotEmpty() && dateTime!!.text.isNotEmpty() && imageArray.isNotEmpty() && videoArray.isNotEmpty()) {

                    if (ConstantMethods.checkForInternetConnection(this@ReportResolvedActivity)) {


                        val jsonObject = JsonObject()

                        jsonObject.addProperty("issueId", issueId)
                        jsonObject.addProperty("text", details)
                        jsonObject.addProperty(
                            "resolutionDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )


                        val userTypeJsonArray = JsonArray()

                        for (i in 0 until userSelectedId!!.size) {

                            userTypeJsonArray.add(userSelectedId!![i])

                        }
                        jsonObject.add("users", userTypeJsonArray)


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



                        if (count == mediaPojo.size) {

                            postDataToServer(jsonObject)

                        } else {
                            count = 0
                            ConstantMethods.showWarning(
                                this,
                                "Please wait",
                                "Please wait media uploading."
                            )
                        }


                    }

                } else if (details.isNotEmpty() && issueId.isNotEmpty() && userSelectedId!!.isNotEmpty() && dateTime!!.text.isNotEmpty() && imageArray.isNotEmpty()) {

                    if (ConstantMethods.checkForInternetConnection(this@ReportResolvedActivity)) {


                        val jsonObject = JsonObject()

                        jsonObject.addProperty("issueId", issueId)
                        jsonObject.addProperty("text", details)
                        jsonObject.addProperty(
                            "resolutionDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )


                        val userTypeJsonArray = JsonArray()

                        for (i in 0 until userSelectedId!!.size) {

                            userTypeJsonArray.add(userSelectedId!![i])

                        }
                        jsonObject.add("users", userTypeJsonArray)


                        //image array
                        val imageJsonArray = JsonArray()

                        for (i in 0 until imageArray.size) {

                            imageJsonArray.add(imageArray[i])

                        }


                        jsonObject.add("imageUrls", imageJsonArray)



                        if (count == mediaPojo.size) {

                            postDataToServer(jsonObject)

                        } else {
                            count = 0
                            ConstantMethods.showWarning(
                                this,
                                "Please wait",
                                "Please wait media uploading."
                            )
                        }


                    }

                } else if (details.isNotEmpty() && issueId.isNotEmpty() && userSelectedId!!.isNotEmpty() && dateTime!!.text.isNotEmpty() && videoArray.isNotEmpty()) {

                    if (ConstantMethods.checkForInternetConnection(this@ReportResolvedActivity)) {


                        val jsonObject = JsonObject()

                        jsonObject.addProperty("issueId", issueId)
                        jsonObject.addProperty("text", details)
                        jsonObject.addProperty(
                            "resolutionDate",
                            ConstantMethods.convertDateStringToServerDateFull(dateTime!!.text.toString())
                        )


                        val userTypeJsonArray = JsonArray()

                        for (i in 0 until userSelectedId!!.size) {

                            userTypeJsonArray.add(userSelectedId!![i])

                        }
                        jsonObject.add("users", userTypeJsonArray)


                        //video array
                        val videoJsonArray = JsonArray()
                        for (i in 0 until videoArray.size) {

                            videoJsonArray.add(videoArray[i])

                        }
                        jsonObject.add("videoUrls", videoJsonArray)



                        if (count == mediaPojo.size) {

                            postDataToServer(jsonObject)

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

    /** It takes the json object as input and send to onRequestReportResolvedData function of report resolved presenter */
    private fun postDataToServer(jsonObject: JsonObject) {

        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportResolvedPresenter!!.onRequestReportResolvedData(this, jsonObject)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** It is used to go back to previous activity with data stores in intent */
    override fun goToNextScreen() {

        val intent = Intent()
        intent.putExtra("issueId", issueId)
        intent.putExtra("reportedResolution", true)
        intent.putExtra("reportedCount", 1)
        setResult(Activity.RESULT_OK, intent)

        onBackPressed()

        EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
        count = 0

    }

    /** ui initialization */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        close = findViewById(R.id.img_close)
        closeGallery = findViewById(R.id.img_close_option)

        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        mediaTitle = findViewById(R.id.txt_media_title)
        mediaTitle!!.typeface = ConstantFonts.raleway_semibold


        or = findViewById(R.id.txt_or)
        or!!.typeface = ConstantFonts.raleway_semibold

        or = findViewById(R.id.txt_or)
        or!!.typeface = ConstantFonts.raleway_regular

        mediaOr = findViewById(R.id.txt_media_or)
        mediaOr!!.typeface = ConstantFonts.raleway_regular

        mediaOrOption = findViewById(R.id.txt_media_or_option)
        mediaOrOption!!.typeface = ConstantFonts.raleway_regular

        /** ids of edit text */
        dateTime = findViewById(R.id.et_date_time)
        dateTime!!.typeface = ConstantFonts.raleway_semibold

        resolvedDetails = findViewById(R.id.et_resolve_details)
        resolvedDetails!!.typeface = ConstantFonts.raleway_semibold

        resolvedBy = findViewById(R.id.et_resolved_by)
        resolvedBy!!.typeface = ConstantFonts.raleway_semibold

        /** button */
        resolved = findViewById(R.id.btn_resolved)
        resolved!!.typeface = ConstantFonts.raleway_semibold

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
        mediaRecycler!!.isNestedScrollingEnabled=false
        mediaRecycler!!.layoutManager = GridLayoutManager(this, 3)

        userTypeRecycler = findViewById(R.id.recycler_resolved_by)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        userTypeRecycler!!.setHasFixedSize(true)
        userTypeRecycler!!.layoutManager = linearLayoutManager

        /** ids of relative layout*/
        optionLayout = findViewById(R.id.captureOptionLayout)
        galleryOptionLayout = findViewById(R.id.galleryOptionLayout)

        /** ids of tag layout */
        /*  resolvedTag = findViewById(R.id.tagLayout)
          resolvedTag!!.setTextColor(ContextCompat.getColor(this,R.color.hint))*/

        chipGroup = findViewById(R.id.chipGroup)

        /** ids of linear layout */
        progressLayout = findViewById(R.id.progressLayout)

    }

    /** It get image or video url which selected from camera or gallery and set it to media adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap


        if (requestCode == GALLERY_IMAGE) {
            if(mediaAdapter!!.itemCount<10)
            {
                if (data != null) {
                    val mSelected:List<Uri> = Matisse.obtainResult(data)
                    for (i in mSelected!!.indices) {
                        val uri = mSelected[i]
                        getImageFilePath(uri)
                    }

                }
            }
            else
            {
                Toast.makeText(this, "You can't upload more that 10 photos", Toast.LENGTH_SHORT).show()
            }


        } else if (requestCode == GALLERY_VIDEO) {
            try {

                val mSelected:List<Uri> = Matisse.obtainResult(data)
                val uri: Uri = mSelected[0]

                if (uri != null) {
                    //val path = RealPathUtils.getPath(context, uri)
                    val path = FileUtils().getRealPath(context, uri)

                    val mimeType = ""
                    if (path != null) {
                        file = File(path)
//                    mimeType = ConstantsMethods.getMimeType(path)
                    } else {
                        file = RealPathUtils.getFile(context, uri)!!
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

                            videoArrayList!!.add(fileUri!!)

                            mediaStatusArray!!.add(
                                MediaUploadingPojo(
                                    fileUri!!,
                                    "",
                                    "video",
                                    false
                                )
                            )

                            mediaAdapter = MediaAdapter(context, mediaStatusArray!!, true)
                            mediaRecycler!!.adapter = mediaAdapter
                        }
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == CAMERA) {
            if (mMediaUri != null) {
                try {
                    /* var file = File(mMediaUri!!.getPath())

                     bitmap = BitmapFactory.decodeFile(mMediaUri!!.getPath())

                     bitmap =
                         ConstantMethods.imageOrientationValidator(bitmap, mMediaUri!!.getPath())

                     file = ConstantMethods.compressImage(context, bitmap)*/


/*
                    val sourceDirectory = File(
                        Environment.getExternalStorageDirectory().toString() +"/"+ resources.getString(R.string.app_name) +"/"
                    )

                    if (!sourceDirectory.exists()) {
                        sourceDirectory.mkdirs()
                    }

                    val targetFilePath =
                        Environment.getExternalStorageDirectory().toString() +"/"+ resources.getString(R.string.app_name)+"/"


                    val finalPath = ConstantMethods.copyFile(file.path, file.name, targetFilePath)

                    println("file path intenal $finalPath")*/


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

                    //decodeFile(picturePath);
                    //5mb in kb
                    /*  if (length > 5120) {
                          ConstantMethods.showError(
                              context,
                              "Large Image Size",
                              "Maximum image allowed is 5 MB"
                          )


                      } else {*/

                    imageArrayList!!.add(mMediaUri!!.path!!)

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
                    //}

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        } else if (requestCode == VIDEO) {
            if (data != null) {
                try {
                    mMediaUri = data.data
                    val filePathColumn = arrayOf(MediaStore.Video.Media.DATA)
                    Log.e("path", filePathColumn[0])

                    val cursor = context.contentResolver.query(
                        mMediaUri!!,
                        filePathColumn, null, null, null
                    )
                    cursor!!.moveToFirst()

                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    picturePath = cursor.getString(columnIndex)
                    cursor.close()
                    // Log.e("picture path",picturePath);

/*
                val ffmpeg = FFmpeg.getInstance(context)
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


                })*/


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


                    /*         val file = File(picturePath)

                             progressLayout!!.visibility = View.VISIBLE
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

                             println("command r $r")


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
                             }*/


                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

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

    /** It is used to go back to the activity */
    override fun onBackPressed() {

        super.onBackPressed()

        finish()
    }

    private fun pickMedia(maxLimit:Int, mimeType : Set<MimeType>, requestCode: Int)
    {
        if(maxLimit>0) {
            Matisse.from(this)
                .choose(mimeType)
                .countable(true)
                .maxSelectable(maxLimit)
                .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .showPreview(false) // Default is `true`
                .forResult(requestCode);
        }
        else
        {
            Toast.makeText(this, "You can't upload more that 10 photos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getImageFilePath(uri: Uri) {
        val cursor: Cursor =
            contentResolver.query(uri, projection, null, null, null)!!
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val absolutePathOfImage =
                    cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                absolutePathOfImage?.let { setSelectedImage(it, uri) } ?: setSelectedImage(uri.toString(), uri)
            }
        }
    }

    private fun setSelectedImage(filePath :String, uri: Uri)
    {
        val path = FileUtils().getRealPath(context!!, uri!!)

        var file1: File? = null

        if (path != null) {


            val compressedPath =

                ConstantMethods.getCompressImage(context!!, path)

            if (compressedPath != null) {
                file1 = File(compressedPath)
            } else {
                file1 = RealPathUtils.getFile(context!!, mMediaUri!!)
            }

        } else {
            file1 = RealPathUtils.getFile(context!!, mMediaUri!!)

        }


        mediaStatusArray!!.add(
            MediaUploadingPojo(
                file1!!.absolutePath,
                "",
                "image",
                false
            )
        )

        val json1 = Gson().toJson(mediaStatusArray!!)
        EasySP.init(context).putString(
            ConstantEasySP.UPLOADED_MEDIA,json1)

        mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!, false)
        mediaRecycler!!.adapter = mediaAdapter
    }
}
