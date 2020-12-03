package com.we.beyond.ui.issues.submitIssue.fragments


import android.content.ClipData
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arthenica.mobileffmpeg.FFmpeg
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.RealPathUtils
import com.we.beyond.adapter.MediaAdapter
import com.we.beyond.model.MediaUploadingPojo
import com.we.beyond.model.NearByIssueByIdDetailsPojo
import com.we.beyond.presenter.issues.submitIssue.SubmitIssueImpl
import com.we.beyond.presenter.issues.submitIssue.SubmitIssuePresenter
import com.we.beyond.ui.issues.submitIssue.SubmitAnIssueActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.FileUtils
import com.white.easysp.EasySP
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import java.io.File

/** It is used to select media to submit an issue */
class IssueMediaFragment : Fragment(),
    SubmitIssuePresenter.ISubmitIssueView {

    /** initialize implementors */
    var issuePresenter: SubmitIssueImpl? = null

    private var projection =
        arrayOf(MediaStore.MediaColumns.DATA)
    /** init text view */
    var mediaTitle: TextView? = null
    var or: TextView? = null
    var mediaOr: TextView? = null
    var mediaOrOption: TextView? = null

    /** init button */
    var useCamera: Button? = null
    var useGallery: Button? = null
    var image: Button? = null
    var video: Button? = null
    var imageGallery: Button? = null
    var videoGalley: Button? = null

    /** init image view */
    var close: ImageView? = null
    var closeGallery: ImageView? = null


    /** init recycler view */
    var mediaRecycler: RecyclerView? = null

    /** init array list */

    var mimeTypeArray: ArrayList<String>? = null
    var mediaArray: ArrayList<String>? = null
    var mediaStatusArray: ArrayList<MediaUploadingPojo>? = null

    /** init relative layout */
    var optionLayout: RelativeLayout? = null
    var galleryOptionLayout: RelativeLayout? = null

    /** init linear layout */
    var progressLayout: LinearLayout? = null

    /** initialize adapter */
    var mediaAdapter: MediaAdapter? = null

    /** init int */
    var CAMERA = 1
    var VIDEO = 2
    var GALLERY_IMAGE = 3
    var GALLERY_VIDEO = 4

    /** init variables */
    var file: File? = null
    var fileUri: String? = null
    var picturePath: String? = null

    var isVideoSelected: Boolean = false

    var mMediaUri: Uri? = null
    var mclipData: ClipData?=null
    var compressedFile: File? = null
    var RequestPermissionCode = 1

    var issueData: NearByIssueByIdDetailsPojo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_media, container, false)

        /** initialize implementation  */
        issuePresenter = SubmitIssueImpl(this)

        /** array initialization */
        mimeTypeArray = ArrayList()
        mediaArray = ArrayList()
        mediaStatusArray = ArrayList()

        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        /** initialize ids of elements */
        initElementsWithIds(v)

        /** initialize onclick listener */
        initWithListener()


        /*  if (ConstantMethods.checkPermission(context!!)) {
              val mydir =
                  File(Environment.getExternalStorageDirectory().toString() + resources.getString(R.string.app_name))
              if (!mydir.exists()) {
                  mydir.mkdirs()
              }
          } else {
              ConstantMethods.requestPermission(context!!)
          }*/

        /** Get all stored data using intent and assign it respectively */
        val getIntentData = arguments!!.getString("issueData")
        issueData = Gson().fromJson(getIntentData, NearByIssueByIdDetailsPojo::class.java)
        mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!, false)
        if (issueData != null) {


            if (issueData!!.data.imageUrls != null && issueData!!.data.imageUrls.isNotEmpty()) {
                for (i in 0 until issueData!!.data.imageUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            issueData!!.data.imageUrls[i],
                            "image",
                            true
                        )
                    )

//                    mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }

            if (issueData!!.data.videoUrls != null && issueData!!.data.videoUrls.isNotEmpty()) {
                for (i in 0 until issueData!!.data.videoUrls.size) {
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            "",
                            issueData!!.data.videoUrls[i],
                            "video",
                            true
                        )
                    )

//                    mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter
                }
            }
        }


        return v
    }

    /** ui listeners */
    private fun initWithListener() {

        /** It opens option layout with animation  */
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

                mMediaUri = ConstantMethods.getMediaOutputUri(context!!)
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

                /*
                 if(mediaStatusArray!!.size==0)
                 {*/
                if (!isVideoSelected) {

                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 59)
                    startActivityForResult(intent, VIDEO)
                } else {
                    ConstantMethods.showToast(
                        context!!,
                        "You cannot upload more than 1 video."
                    )
                }
                /*  }
                  else {
                      for (i in 0 until mediaStatusArray!!.size) {
                          if (mediaStatusArray!![i].mimeType.contains("video")) {
                              if (mediaStatusArray!!.size > 1) {
                                  ConstantMethods.showWarning(
                                      context!!,
                                      "",
                                      "You can not upload more than 1 video."
                                  )
                              } else {


                                  val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                                  intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 59)
                                  startActivityForResult(intent, VIDEO)
                              }
                          }
                      }

                  }*/
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
                    Toast.makeText(activity, "You cannot upload more than one video", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(activity, "You can't upload more that 10 photos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun goToNextScreen() {

    }

    /** ui initialization */
    private fun initElementsWithIds(v: View) {
        /** ids of text view */
        mediaTitle = v.findViewById(R.id.txt_media_title)
        mediaTitle!!.typeface = ConstantFonts.raleway_semibold

        or = v.findViewById(R.id.txt_or)
        or!!.typeface = ConstantFonts.raleway_regular

        mediaOr = v.findViewById(R.id.txt_media_or)
        mediaOr!!.typeface = ConstantFonts.raleway_regular

        mediaOrOption = v.findViewById(R.id.txt_media_or_option)
        mediaOrOption!!.typeface = ConstantFonts.raleway_regular

        /** ids of button */
        useCamera = v.findViewById(R.id.btn_use_camera)
        useCamera!!.typeface = ConstantFonts.raleway_semibold

        useGallery = v.findViewById(R.id.btn_browse_file)
        useGallery!!.typeface = ConstantFonts.raleway_semibold

        image = v.findViewById(R.id.btn_image)
        image!!.typeface = ConstantFonts.raleway_semibold

        video = v.findViewById(R.id.btn_video)
        video!!.typeface = ConstantFonts.raleway_semibold

        imageGallery = v.findViewById(R.id.btn_image_option)
        imageGallery!!.typeface = ConstantFonts.raleway_semibold

        videoGalley = v.findViewById(R.id.btn_video_option)
        videoGalley!!.typeface = ConstantFonts.raleway_semibold

        /** ids of recycler view */
        mediaRecycler = v.findViewById(R.id.recycler_media)
        mediaRecycler!!.isNestedScrollingEnabled=false
        mediaRecycler!!.layoutManager = GridLayoutManager(context!!, 3)

        /** ids of relative layout*/
        optionLayout = v.findViewById(R.id.captureOptionLayout)
        galleryOptionLayout = v.findViewById(R.id.galleryOptionLayout)

        /** ids of linear layout */
        progressLayout = v.findViewById(R.id.progressLayout)

        /** ids of image view */
        close = v.findViewById(R.id.img_close)
        closeGallery = v.findViewById(R.id.img_close_option)

    }

    /** It get image or video url which selected from camera or gallery and set it to media adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap
        val submitActivity= activity as SubmitAnIssueActivity

        if (requestCode == GALLERY_IMAGE ) {
            if(mediaAdapter!!.itemCount<10)
            {
            if (data != null) {
                    submitActivity!!.shouldEnableNextBtn(true)
                submitActivity!!.shouldEnableCategoryIcon(true)
                val mSelected:List<Uri> = Matisse.obtainResult(data);
                for (i in mSelected!!.indices) {
                    val uri = mSelected[i]
                    getImageFilePath(uri)
                }

            }
            }
            else
            {
                Toast.makeText(activity, "You can't upload more that 10 photos", Toast.LENGTH_SHORT).show()
            }

        } else if (requestCode == GALLERY_VIDEO) {
            if (data != null) {
                try {
                    submitActivity!!.shouldEnableNextBtn(true)
                    submitActivity!!.shouldEnableCategoryIcon(true)
                    val mSelected:List<Uri> = Matisse.obtainResult(data)
                    val uri: Uri = mSelected[0]

                    if (uri != null) {
                        val path = FileUtils().getRealPath(context!!, uri)
                        val mimeType = ""
                        if (path != null) {
                            file = File(path)
                        } else {
                            file = RealPathUtils.getFile(context!!, uri)!!
                        }

                        isVideoSelected = true

                        var length: Long? = file!!.length()
                        length = length!! / 1024
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
                                EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA,json1)

                                mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!,true)
                                mediaRecycler!!.adapter = mediaAdapter
                            }
                        }
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == CAMERA) {
            // if(data!=null) {
            if (mMediaUri != null) {
                try {
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
                    mediaStatusArray!!.add(
                        MediaUploadingPojo(
                            file1!!.absolutePath,
                            "",
                            "image",
                            false
                        )
                    )

                    val json1 = Gson().toJson(mediaStatusArray!!)
                    EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA,json1)

                    mediaAdapter = MediaAdapter(context!!, mediaStatusArray!!, false)
                    mediaRecycler!!.adapter = mediaAdapter

                    EasySP.init(context!!).putString("image", fileUri)

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        } else if (requestCode == VIDEO) {
            if (data != null) {
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

                    println("file target $fileTarget")

                    val file = File(picturePath!!)


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

                    val json1 = Gson().toJson(mediaStatusArray!!)
                    EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA,json1)


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


    private fun getImageFilePath(uri: Uri) {
        val cursor: Cursor =
            activity!!.contentResolver.query(uri, projection, null, null, null)!!
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
                        context!!, "Permission Granted",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(context!!, "Permission Denied ", Toast.LENGTH_LONG).show()

                }
            }
        }

    }
}
