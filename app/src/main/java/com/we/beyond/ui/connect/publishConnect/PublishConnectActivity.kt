package com.we.beyond.ui.connect.publishConnect

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
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.we.beyond.adapter.MediaAdapter
import com.we.beyond.Interface.OnVideoDeleteSuccess
import com.we.beyond.R
import com.we.beyond.RealPathUtils
import com.we.beyond.model.*
import com.we.beyond.presenter.connect.publishConnect.PublishConnectImpl
import com.we.beyond.presenter.connect.publishConnect.PublishConnectPresenter
import com.we.beyond.presenter.mediaUpload.MediaImpl
import com.we.beyond.ui.connect.connectIssue.ConnectDetailsActivity
import com.we.beyond.ui.connect.connectIssue.ConnectIssueActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.FileUtils
import com.white.easysp.EasySP
import java.io.File

/**
 * This activity used to create an articles to publish
 */
class PublishConnectActivity : AppCompatActivity(), PublishConnectPresenter.IPublishConnectView,
    OnVideoDeleteSuccess {

    /** initialize implementors */
    var publishConnectPresenter: PublishConnectImpl? = null
    var mediaPresenter: MediaImpl? = null
    var context: Context = this

    /** init image view */
    var back: ImageView? = null
    var closeGallery: ImageView? = null
    var close: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var mediaTitle: TextView? = null
    var or: TextView? = null
    var mediaOr: TextView? = null
    var mediaOrOption: TextView? = null

    /** init edit text */
    var connectTitle: EditText? = null
    var connectDetails: EditText? = null

    /** init button */
    var useCamera: Button? = null
    var useGallery: Button? = null
    var submit: Button? = null
    var image: Button? = null
    var video: Button? = null
    var imageGallery: Button? = null
    var videoGalley: Button? = null


    /** init autocomplete text view for spinner */
    var connectCategory: AutoCompleteTextView? = null

    /** Array list */
    var connectCategoryList: ArrayList<String>? = null
    var mimeTypeArray: ArrayList<String>? = null
    var mediaArray: ArrayList<String>? = null
    var mediaStatusArray: ArrayList<MediaUploadingPojo>? = null
    var categories: ArrayList<ConnectCategories>? = null

    /** init recycler view and its adapter */
    var mediaRecycler: RecyclerView? = null
    var mediaAdapter: MediaAdapter? = null


    /** init text input layout */
    var connectionCategoryLayout: TextInputLayout? = null

    /** init relative layout */
    var optionLayout: RelativeLayout? = null
    var galleryOptionLayout: RelativeLayout? = null

    /** These variables are used for media  */
    var CAMERA = 1
    var VIDEO = 2
    var GALLERY_IMAGE = 3
    var GALLERY_VIDEO = 4

    var file: File? = null
    var fileUri: String? = null
    var connectCategoryId: String = ""
    var picturePath: String? = null
    var mMediaUri: Uri? = null
    var count = 0
    var RequestPermissionCode = 1

    /** init linear layout */
    var progressLayout: LinearLayout? = null

    /** init booleans */
    var isEdit: Boolean = false
    var isMyConnect: Boolean = false
    var isVideoSelected: Boolean = false

    /** init model */
    var connectDetailsData: ConnectDetailsPojo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish_connect)

        /** It hides the keyboard */
        ConstantMethods.hideKeyBoard(this, this)

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        /** array initialization */
        connectCategoryList = ArrayList()
        categories = ArrayList()
        mimeTypeArray = ArrayList()
        mediaArray = ArrayList()
        mediaStatusArray = ArrayList()

        count = 0

        /** initialize implementation */
        publishConnectPresenter = PublishConnectImpl(this)

        EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA, "")

        /** initialize ids of elements */
        initElementsWithIds()

        /** set user type data to spinner */
        setConnectCategories()

        /** initialize onclick listener */
        initWithListener()
/*
        if (ConstantMethods.checkPermission(context)) {
            val mydir =
                File(Environment.getExternalStorageDirectory().toString() + resources.getString(R.string.app_name))
            if (!mydir.exists()) {
                mydir.mkdirs()
            }
        } else {
            ConstantMethods.requestPermission(context)
        }*/

        /** It get stored data using intent and checks the connect details
         * if not null then set data to edit text
         */
        val getIntentData = intent.getStringExtra("connectData")
        isEdit = intent.getBooleanExtra("edit", false)
        connectDetailsData = Gson().fromJson(getIntentData, ConnectDetailsPojo::class.java)


        if (isEdit) {
            submit!!.text = "update"

        } else {
            submit!!.text = "submit"
        }

        println("campaign data $isEdit")

        if (connectDetailsData != null) {

            connectCategoryId = connectDetailsData!!.data.connectCategory._id

            connectTitle = findViewById(R.id.et_connect_title)
            connectTitle!!.typeface = ConstantFonts.raleway_semibold
            connectTitle!!.setText(connectDetailsData!!.data.title)

            connectDetails = findViewById(R.id.et_connect_details)
            connectDetails!!.typeface = ConstantFonts.raleway_semibold
            connectDetails!!.setText(connectDetailsData!!.data.description)

            connectCategory!!.setText(connectDetailsData!!.data.connectCategory.name)


            if (connectDetailsData!!.data.imageUrls != null && connectDetailsData!!.data.imageUrls.isNotEmpty()) {
                for (i in 0 until connectDetailsData!!.data.imageUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            connectDetailsData!!.data.imageUrls[i],
                            "image",
                            true
                        )
                    )

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }


            if (connectDetailsData!!.data.videoUrls != null && connectDetailsData!!.data.videoUrls.isNotEmpty()) {
                for (i in 0 until connectDetailsData!!.data.videoUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            connectDetailsData!!.data.videoUrls[i],
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

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            //finish()
        }

        /** It opens option layout with animation */
        useCamera!!.setOnClickListener {
            try {
                ConstantMethods.hideKeyBoard(context, this)
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
                ConstantMethods.hideKeyBoard(context, this)
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
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    startActivityForResult(intent, VIDEO)


                } else {
                    ConstantMethods.showWarning(
                        context,
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
                if (!isVideoSelected) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.setType("video/*")
                    startActivityForResult(intent, GALLERY_VIDEO)


                    galleryOptionLayout!!.visibility = View.GONE

                } else {
                    ConstantMethods.showWarning(
                        context,
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


        /** It submits or update article depends on condition to server */
        submit!!.setOnClickListener {
            try {

                if (isEdit) {
                    if (ConstantMethods.checkForInternetConnection(this@PublishConnectActivity)) {
                        getDataToPostUpdate()
                    }
                } else {
                    if (ConstantMethods.checkForInternetConnection(this@PublishConnectActivity)) {
                        getDataToPost()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It selects the category to create an article */
        connectCategoryId = ""
        connectCategory!!.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                connectCategoryId = categories!![position]._id

            }
        })
    }

    /** It get all required fields from the class to update an article, converts to json object
     * pass to postDataToServerUpdate()
     * If fields are empty then shows warning dialog
     */
    private fun getDataToPostUpdate() {
        try {
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val category = connectCategory!!.text.toString().trim()

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

            if (ConstantMethods.checkForInternetConnection(this@PublishConnectActivity)) {
                val jsonObject = JsonObject()
                if (!connectTitle!!.text.toString()
                        .equals(connectDetailsData!!.data.title, ignoreCase = true)
                ) {
                    jsonObject.addProperty("title", connectTitle!!.text.toString().trim())
                }
                if (!connectDetails!!.text.toString()
                        .equals(connectDetailsData!!.data.description, ignoreCase = true)
                ) {
                    jsonObject.addProperty(
                        "description",
                        connectDetails!!.text.toString().trim()
                    )
                }

                if (!connectCategoryId.equals(
                        connectDetailsData!!.data.connectCategory._id,
                        ignoreCase = true
                    )
                ) {
                    jsonObject.addProperty("connectCategoryId", connectCategoryId)
                }

                jsonObject.addProperty("connectId", connectDetailsData!!.data._id)

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


            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onConnectUpdated function of publish connect presenter */
    private fun postDataToServerUpdate(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                publishConnectPresenter!!.onConnectUpdated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It get all required fields from the class to create an article, converts to json object
     * pass to postDataToServer()
     * If fields are empty then shows warning dialog
     */
    private fun getDataToPost() {
        try {
            val media = EasySP.init(this).getString(ConstantEasySP.UPLOADED_MEDIA)
            val category = connectCategory!!.text.toString().trim()

            var mediaPojo = ArrayList<MediaUploadingPojo>()
            val type = object : TypeToken<ArrayList<MediaUploadingPojo>>() {


            }.type

            try {
                mediaPojo = Gson().fromJson<ArrayList<MediaUploadingPojo>>(media, type)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            val imageArray = ArrayList<String>()
            val videoArray = ArrayList<String>()




            if (category.isEmpty()) {
                connectionCategoryLayout!!.error = "Please Select Connection Category"
            } else {
                connectionCategoryLayout!!.isErrorEnabled = false
            }

            if (connectTitle!!.text.isEmpty()) {
                ConstantMethods.showWarning(this, "Title", "Please Enter Title")
            }
            if (connectDetails!!.text.isEmpty()) {
                ConstantMethods.showWarning(this, "Details", "Please Enter Connection Details")
            }
            if (connectCategoryId.isEmpty()) {
                connectionCategoryLayout!!.error = "Please Select Connection Category"
            } else {
                connectionCategoryLayout!!.isErrorEnabled = false
            }
            if (media.isEmpty()) {
                ConstantMethods.showWarning(
                    this,
                    "Please Upload Photo / Video",
                    "You need to attach at least 1 photo or video to this connect to publish"
                )
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
                    ConstantMethods.showError(this, "Please wait", "Please wait media uploading.")
                }
            }


            /*  if (category.isNotEmpty() && connectTitle!!.text.isNotEmpty() && connectDetails!!.text.isNotEmpty() && connectCategoryId.isNotEmpty() && imageArray.isEmpty() && mediaPojo.size == 0) {
                  if (ConstantMethods.checkForInternetConnection(this@PublishConnectActivity)) {
                      val jsonObject = JsonObject()
                      jsonObject.addProperty("title", connectTitle!!.text.toString().trim())
                      jsonObject.addProperty("description", connectDetails!!.text.toString().trim())
                      jsonObject.addProperty("connectCategoryId", connectCategoryId)

                      try {
                          if (ConstantMethods.checkForInternetConnection(context)) {
                              postDataToServer(jsonObject)
                          }
                      }
                      catch ( e : Exception)
                      {
                          e.printStackTrace()
                      }

                      println("post data $jsonObject")


                  }
              } else*/ if (category.isNotEmpty() && connectTitle!!.text.isNotEmpty() && connectDetails!!.text.isNotEmpty() && connectCategoryId.isNotEmpty() && imageArray.isNotEmpty() && videoArray.isNotEmpty()) {

                if (ConstantMethods.checkForInternetConnection(this@PublishConnectActivity)) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("title", connectTitle!!.text.toString().trim())
                    jsonObject.addProperty("description", connectDetails!!.text.toString().trim())
                    jsonObject.addProperty("connectCategoryId", connectCategoryId)

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

                        postDataToServer(jsonObject)
                    } else {
                        count = 0
                        ConstantMethods.showWarning(

                            this,
                            "Please wait",
                            "Please wait media uploading."
                        )
                    }


                    println("post data $jsonObject")


                }

            } else if (category.isNotEmpty() && connectTitle!!.text.isNotEmpty() && connectDetails!!.text.isNotEmpty() && connectCategoryId.isNotEmpty() && imageArray.isNotEmpty()) {

                if (ConstantMethods.checkForInternetConnection(this@PublishConnectActivity)) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("title", connectTitle!!.text.toString().trim())
                    jsonObject.addProperty("description", connectDetails!!.text.toString().trim())
                    jsonObject.addProperty("connectCategoryId", connectCategoryId)

                    //image array
                    val imageJsonArray = JsonArray()

                    for (i in 0 until imageArray.size) {

                        imageJsonArray.add(imageArray[i])

                    }

                    println("images json $imageArray")
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


                    println("post data $jsonObject")


                }

            } else if (category.isNotEmpty() && connectTitle!!.text.isNotEmpty() && connectDetails!!.text.isNotEmpty() && connectCategoryId.isNotEmpty() && videoArray.isNotEmpty()) {

                if (ConstantMethods.checkForInternetConnection(this@PublishConnectActivity)) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("title", connectTitle!!.text.toString().trim())
                    jsonObject.addProperty("description", connectDetails!!.text.toString().trim())
                    jsonObject.addProperty("connectCategoryId", connectCategoryId)


                    //video array
                    val videoJsonArray = JsonArray()
                    for (i in 0 until videoArray.size) {

                        videoJsonArray.add(videoArray[i])

                    }
                    println("video json $videoArray")
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


                    println("post data $jsonObject")


                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onConnectCreated function of publish connect presenter */
    private fun postDataToServer(jsonObject: JsonObject) {

        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                ConstantMethods.showProgessDialog(this, "Please Wait...")
                publishConnectPresenter!!.onConnectCreated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It get image or video url which selected from camera or gallery and set it to media adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap


        if (requestCode == GALLERY_IMAGE) {
            if (data != null) {
                try {


                    mMediaUri = data.getData()

                    //val path = RealPathUtils.getPath(context!!, mMediaUri!!)
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



                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            file1.absolutePath,
                            "",
                            "image",
                            false
                        )
                    )

                    val json1 = Gson().toJson(mediaStatusArray!!)
                    EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, json1)

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                    // }


                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

        } else if (requestCode == GALLERY_VIDEO) {
            if (data != null) {
                try {

                    val uri: Uri = Uri.parse(data!!.data.toString())

                    if (uri != null) {
                        //val path = RealPathUtils.getPath(context!!, uri)
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

                                mediaStatusArray!!.add(
                                    MediaUploadingPojo(
                                        fileUri!!,
                                        "",
                                        "video",
                                        false
                                    )
                                )

                                val json1 = Gson().toJson(mediaStatusArray!!)
                                EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, json1)

                                mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!, false)
                                mediaRecycler!!.adapter = mediaAdapter
                            }
                        }
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } else if (requestCode == CAMERA) {
            if (mMediaUri != null) {
                try {

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


                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            file1.absolutePath,
                            "",
                            "image",
                            false
                        )
                    )

                    val json1 = Gson().toJson(mediaStatusArray!!)
                    EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, json1)

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


            } catch (e: Exception) {
                e.printStackTrace()
            }

            //}

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

                progressLayout.visibility = View.GONE

                var length: Long? = fileTarget.length()
                length = length!! / 1024

                //decodeFile(picturePath);
                //100mb in kb
                if (length > 102400) {
                    ConstantMethods.showError(
                        context,
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

                    val json1 = Gson().toJson(mediaStatusArray!!)
                    EasySP.init(context).put(ConstantEasySP.UPLOADED_MEDIA, json1)

                    val videosSet = java.util.HashSet<String>()
                    for (i in 0 until mediaArray!!.size) {
                        videosSet.add(mediaArray!![i])
                    }

                    // mimeTypeArray!!.add("video")

                    mediaAdapter = MediaAdapter(context, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter

                    EasySP.init(context).putString("video", fileUri)

                }
            } else {
                progressLayout!!.visibility = View.GONE

                Toast.makeText(context, "Error File not uploaded $rc.", Toast.LENGTH_LONG).show()
            }
        }


    }

    /** It takes the json object as input and send to onRequestConnectCategory function of create publish connect presenter */
    private fun setConnectCategories() {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                publishConnectPresenter!!.onRequestConnectCategory(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image */
        back = findViewById(R.id.img_back)
        close = findViewById(R.id.img_close)
        closeGallery = findViewById(R.id.img_close_option)

        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        mediaTitle = findViewById(R.id.txt_media_title)
        mediaTitle!!.typeface = ConstantFonts.raleway_semibold

        or = findViewById(R.id.txt_or)
        or!!.typeface = ConstantFonts.raleway_regular

        mediaOr = findViewById(R.id.txt_media_or)
        mediaOr!!.typeface = ConstantFonts.raleway_regular

        mediaOrOption = findViewById(R.id.txt_media_or_option)
        mediaOrOption!!.typeface = ConstantFonts.raleway_regular


        /** ids of edit text */
        connectTitle = findViewById(R.id.et_connect_title)
        connectTitle!!.typeface = ConstantFonts.raleway_semibold

        connectDetails = findViewById(R.id.et_connect_details)
        connectDetails!!.typeface = ConstantFonts.raleway_semibold

        /** ids of button */
        useCamera = findViewById(R.id.btn_use_camera)
        useCamera!!.typeface = ConstantFonts.raleway_semibold

        useGallery = findViewById(R.id.btn_browse_file)
        useGallery!!.typeface = ConstantFonts.raleway_semibold

        submit = findViewById(R.id.btn_submit)
        submit!!.typeface = ConstantFonts.raleway_semibold

        image = findViewById(R.id.btn_image)
        image!!.typeface = ConstantFonts.raleway_semibold

        video = findViewById(R.id.btn_video)
        video!!.typeface = ConstantFonts.raleway_semibold

        imageGallery = findViewById(R.id.btn_image_option)
        imageGallery!!.typeface = ConstantFonts.raleway_semibold

        videoGalley = findViewById(R.id.btn_video_option)
        videoGalley!!.typeface = ConstantFonts.raleway_semibold

        /** ids of AutoCompleteText View (spinner) */
        connectCategory = findViewById(R.id.dropdown_connection_category)
        connectCategory!!.typeface = ConstantFonts.raleway_semibold

        /** ids of recycler view */
        mediaRecycler = findViewById(R.id.recycler_media)
        mediaRecycler!!.layoutManager = GridLayoutManager(this, 3)

        /** ids of text input layout */
        connectionCategoryLayout = findViewById(R.id.connectionCategoriesLayout)

        /** ids of relative layout*/
        optionLayout = findViewById(R.id.captureOptionLayout)
        galleryOptionLayout = findViewById(R.id.galleryOptionLayout)

        /** ids of linear layout */
        progressLayout = findViewById(R.id.progressLayout)


    }

    /** It checks the connect category data,
     *  if it is not empty then set ConnectCategoryAdapter for spinner
     */
    override fun setConnectCategoryAdapter(connectCategories: ArrayList<ConnectCategories>) {
        try {
            connectCategoryList!!.clear()


            if (connectCategories.isNotEmpty()) {
                for (i in 0 until connectCategories.size) {
                    connectCategoryList!!.add(connectCategories[i].name)
                    categories!!.addAll(connectCategories)

                }
            }

            println("connection category $connectCategoryList")
            if (connectCategoryList!!.isNotEmpty()) {
                for (i in 0 until connectCategoryList!!.size) {
                    val connectCategoryAdapter =
                        ArrayAdapter<String>(
                            this,
                            R.layout.spinner_popup_item,
                            connectCategoryList!!
                        )
                    connectCategoryAdapter.setDropDownViewResource(R.layout.spinner_popup_item)
                    connectCategory!!.setAdapter(connectCategoryAdapter)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**It goes back to respected activity depends on condition */
    override fun goToPreviousScreen() {

        if (isEdit) {
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
            count = 0

            val intent = Intent(this, ConnectDetailsActivity::class.java)
            intent.putExtra("connectCategoryId", connectDetailsData!!.data._id)
            intent.putExtra("isEdit", true)
            setResult(201, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else {

            val intent = Intent(this, ConnectIssueActivity::class.java)
            setResult(202, intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()

            count = 0
            EasySP.init(this).put(ConstantEasySP.UPLOADED_MEDIA, "")
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

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }
}
