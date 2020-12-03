package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.Interface.OnVideoDeleteSuccess
import com.we.beyond.R
import com.we.beyond.RealPathUtils
import com.we.beyond.api.FileUploadApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.ui.PreviewActivity
import com.we.beyond.ui.campaign.createCampaign.CreateCampaignActivity
import com.we.beyond.ui.connect.publishConnect.PublishConnectActivity
import com.we.beyond.ui.issues.submitIssue.SubmitAnIssueActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.categories_item.view.cardView
import kotlinx.android.synthetic.main.media_item.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

/** It binds media data to view */
class MediaAdapter(
    var context: Context,
    mediaList: ArrayList<MediaUploadingPojo>,
    val isGalleryVideo: Boolean
) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>(){


    /** init array list */
    var mediaList: ArrayList<MediaUploadingPojo>? = null

    /** init listener */
    var onVideoDeleteSuccess : OnVideoDeleteSuccess?=null

    init {

        this.mediaList = mediaList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.media_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mediaList!!.size
        /*if (mediaList != null) {
            return mediaList!!.size.coerceAtMost(10)
        } else {
            return 10
        }*/
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize the listeners */
        val activity = context as Activity
        try {
            onVideoDeleteSuccess = activity as OnVideoDeleteSuccess
        } catch (e:Exception) {
            e.printStackTrace()

        }

        println("uploaded media $mediaList")

        /** It is used to upload image or video using uploadImage or uploadVideo api depends on condition */
        if(mediaList!![position].mimeType.contains("video") && mediaList!![position].isUpload== false)
        {

            try {

                val video = EasySP.init(context).getString("video")
                var file = File(mediaList!![position].localUrl)
                if (file != null) {
                    var mimeType = RealPathUtils.getMimeType(file)

                    if (mimeType != null && mimeType.length > 0) {
                        var requestFile = RequestBody.create(MediaType.parse(mimeType), file)
                        var multipartBody =
                            MultipartBody.Part.createFormData("file", file.name, requestFile)

                        if (multipartBody != null) {
                            try {
                                val uploadMediaApi = ApplicationController.retrofit.create(
                                    FileUploadApi::class.java)
                                val response: Single<FileUploadPojo> =
                                    uploadMediaApi.uploadVideo(multipartBody)
                                response.subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(object : DisposableSingleObserver<FileUploadPojo>() {
                                        override fun onSuccess(uploadPojo: FileUploadPojo) {
                                            if (uploadPojo != null) {
                                                if (uploadPojo.fileUrl != null && uploadPojo.fileUrl.length > 0) {
                                                    if (ConstantMethods.checkForInternetConnection(context))
                                                    {

                                                        if (!isGalleryVideo){
                                                            file.delete()
                                                        }


                                                       mediaList!![position].serverUrl = uploadPojo.fileUrl
                                                       mediaList!![position].mimeType = uploadPojo.fileMimeType
                                                       mediaList!![position].isUpload = true

                                                        println("uploaded video ${mediaList!!}")

                                                        EasySP.init(context).putString("videoThumb",uploadPojo.fileUrl)

                                                        val json1 = Gson().toJson(mediaList!!)
                                                        EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA,json1)

                                                    }
                                                }
                                            }
                                        }

                                        override fun onError(e: Throwable) {
                                            ConstantMethods.cancleProgessDialog()
                                            try {
                                                if(e is IOException){
                                                    ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(R.string.no_internet_sub_title))
                                                }
                                                else {
                                                    val exception: HttpException = e as HttpException
                                                    val er: String = exception.response()!!.errorBody()!!.string()
                                                    val errorPojo: ErrorPojo =
                                                        Gson().fromJson(er, ErrorPojo::class.java)

                                                    if (errorPojo != null) {
                                                        if (errorPojo.error.isNotEmpty()) {
                                                            if (errorPojo.message.isNotEmpty()) {
                                                                ConstantMethods.showError(
                                                                    context,
                                                                    errorPojo.error,
                                                                    errorPojo.message
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            catch (e : Exception)
                                            {
                                                ConstantMethods.cancleProgessDialog()
                                                ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                                                    R.string.error_message))

                                            }
                                        }

                                    })

                            } catch (e: Exception) {
                                ConstantMethods.cancleProgessDialog()
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.error_title),
                                    "Please try after sometime"
                                )
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                ConstantMethods.cancleProgessDialog()
                ConstantMethods.showError(
                    context,
                    context.resources.getString(R.string.error_title),
                    context.resources.getString(
                        R.string.error_message
                    )
                )
            }
        }
        else if(mediaList!![position].mimeType.contains("image") && mediaList!![position].isUpload== false){
            try {

                val image = EasySP.init(context).getString("image")
                val file = File(mediaList!![position].localUrl)
                if (file != null) {
                    var mimeType = RealPathUtils.getMimeType(file)

                    if (mimeType != null && mimeType.length > 0) {
                        var requestFile = RequestBody.create(MediaType.parse(mimeType), file)
                        var multipartBody =
                            MultipartBody.Part.createFormData("file", file.name, requestFile)

                        if (multipartBody != null) {
                            try {
                                val uploadMediaApi = ApplicationController.retrofit.create(
                                    FileUploadApi::class.java)
                                val response: Single<FileUploadPojo> =
                                    uploadMediaApi.uploadImage(multipartBody)
                                response.subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(object : DisposableSingleObserver<FileUploadPojo>() {
                                        override fun onSuccess(uploadPojo: FileUploadPojo) {
                                            if (uploadPojo != null) {
                                                if (uploadPojo.fileUrl != null && uploadPojo.fileUrl.length > 0) {
                                                    if (ConstantMethods.checkForInternetConnection(context)
                                                    ) {

                                                        file.delete()

                                                        mediaList!![position].serverUrl = uploadPojo.fileUrl
                                                        mediaList!![position].mimeType = uploadPojo.fileMimeType
                                                        mediaList!![position].isUpload = true
                                                        println("image set ${mediaList!!}")
                                                        val json1 = Gson().toJson(
                                                            mediaList!!
                                                        )
                                                        EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA,json1)


                                                    }
                                                }
                                            }
                                        }

                                        override fun onError(e: Throwable) {
                                            ConstantMethods.cancleProgessDialog()
                                            try {
                                                if(e is IOException){
                                                    ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(R.string.no_internet_sub_title))
                                                }
                                                else {
                                                    val exception: HttpException = e as HttpException
                                                    val er: String = exception.response()!!.errorBody()!!.string()
                                                    val errorPojo: ErrorPojo =
                                                        Gson().fromJson(er, ErrorPojo::class.java)

                                                    if (errorPojo != null) {
                                                        if (errorPojo.error.isNotEmpty()) {
                                                            if (errorPojo.message.isNotEmpty()) {
                                                                ConstantMethods.showError(
                                                                    context,
                                                                    errorPojo.error,
                                                                    errorPojo.message
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            catch (e : Exception)
                                            {
                                                ConstantMethods.cancleProgessDialog()
                                                ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                                                    R.string.error_message))

                                            }
                                        }

                                    })

                            } catch (e: Exception) {
                                ConstantMethods.cancleProgessDialog()
                                ConstantMethods.showError(
                                    context,
                                    context.resources.getString(R.string.error_title),
                                    "Please try after sometime"
                                )
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                ConstantMethods.cancleProgessDialog()
                ConstantMethods.showError(
                    context,
                    context.resources.getString(R.string.error_title),
                    context.resources.getString(
                        R.string.error_message
                    )
                )
            }
        }

        /** It set image or video to image view */
        if(mediaList!![position].serverUrl.isNotEmpty() && mediaList!![position].serverUrl!=null)
        {
                if(mediaList!![position].isUpload) {

                    if (mediaList!![position].serverUrl.contains("video")) {
                        var thumbnailPath =
                            mediaList!![position].serverUrl.replace("video.m3u8", "thumb00001.jpg")
                        holder.progressBar.visibility = View.VISIBLE
                        Glide
                            .with(context!!)
                            .load(thumbnailPath)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .thumbnail(Glide.with(context!!).load(thumbnailPath))
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    holder.progressBar.visibility = View.GONE

                                    return false
                                }


                            })

                            .into(holder.image)

                        val json1 = Gson().toJson(
                            mediaList!!
                        )
                        EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA, json1)
                    } else {
                        mediaList!![position].serverUrl = mediaList!![position].serverUrl
                        mediaList!![position].mimeType = mediaList!![position].mimeType
                        mediaList!![position].isUpload = true
                        println("image set ${mediaList!!}")
                        val json1 = Gson().toJson(
                            mediaList!!
                        )
                        EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA, json1)



                        holder.progressBar.visibility = View.VISIBLE
                        Glide
                            .with(context!!)
                            .load(mediaList!![position].serverUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .thumbnail(Glide.with(context!!).load(mediaList!![position].serverUrl))
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    holder.progressBar.visibility = View.GONE

                                    return false
                                }


                            })
                            .into(holder.image)


                    }
                }


        }
        else{
        holder.progressBar.visibility = View.VISIBLE
                Glide
                    .with(context!!)
                    .load(mediaList!![position].localUrl).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .thumbnail(Glide.with(context!!).load(mediaList!![position].localUrl))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            holder.progressBar.visibility = View.GONE

                            return false
                        }


                    })
                    .into(holder.image)


        }

        if(mediaList!![position].mimeType.contains("video"))
        {
            holder.play.visibility = View.VISIBLE
        }
        else{
            holder.play.visibility = View.GONE
        }


        /** It opens PreviewActivity */
        holder.cardview.setOnClickListener {

            /*if(mediaList!![position].localUrl !=null && mediaList!![position].localUrl.isNotEmpty()) {
                val intent = Intent(context, PreviewActivity::class.java)
                intent.putExtra(ConstantEasySP.PREVIEW_MEDIA, mediaList!![position].localUrl)
                intent.putExtra(ConstantEasySP.MIME_TYPE, mediaList!![position].mimeType)
                (context).startActivity(intent)

            }
            else*/
            if(mediaList!![position].serverUrl !=null && mediaList!![position].serverUrl.isNotEmpty())
            {
                val intent = Intent(context, PreviewActivity::class.java)
                intent.putExtra(ConstantEasySP.PREVIEW_MEDIA, mediaList!![position].serverUrl)
                intent.putExtra(ConstantEasySP.MIME_TYPE, mediaList!![position].mimeType)
                (context).startActivity(intent)

            }
            else{
                ConstantMethods.showToast(context,"Please wait media uploading")
            }
        }

        /** It delete media using deleteMedia api */
        holder.delete.setOnClickListener {

            try {
                if (mediaList!![position].serverUrl.isNotEmpty()) {

                val jsonObject = JsonObject()
                jsonObject.addProperty("fileUrl", mediaList!![position].serverUrl)


                    val deleteApi = ApplicationController.retrofit.create(FileUploadApi::class.java)
                    val response: Single<FileUploadPojo> = deleteApi.deleteMedia(jsonObject)
                    response.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : DisposableSingleObserver<FileUploadPojo>() {
                            override fun onSuccess(deletePojo: FileUploadPojo) {
                                if (deletePojo != null) {

                                    ConstantMethods.cancleProgessDialog()

                                    if(mediaList!![position].mimeType.contains("video"))
                                    {
                                        onVideoDeleteSuccess!!.onVideoDelete()
                                    }

                                    mediaList!!.remove(mediaList!![position])
                                    notifyDataSetChanged()

                                    if(mediaList!!.size == 0)
                                    {
                                        EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA, "")
                                        if(context is SubmitAnIssueActivity)
                                        {
                                            val activity =context as SubmitAnIssueActivity
                                            activity.shouldEnableCategoryIcon(false)
                                            activity.shouldEnableNextBtn(false)
                                        }
                                        else
                                            if(context is PublishConnectActivity)
                                            {
                                                val activity =context as PublishConnectActivity
                                                if(!activity.isValidForm())
                                                {
                                                    activity.shouldEnabledCreateButton(false)
                                                }
                                            }
                                            else
                                                if(context is CreateCampaignActivity)
                                                {
                                                    val activity =context as CreateCampaignActivity
                                                    if(!activity.isValidForm())
                                                    {
                                                        activity.shouldEnabledCreateButton(false)
                                                    }
                                                }
                                    }
                                    else {

                                        val json1 = Gson().toJson(mediaList!!)
                                        EasySP.init(context)
                                            .putString(ConstantEasySP.UPLOADED_MEDIA, json1)
                                    }

                                    println("success ${mediaList!!.size}")

                                }
                            }

                            override fun onError(e: Throwable) {
                                ConstantMethods.cancleProgessDialog()
                                try {
                                    if(e is IOException){
                                        ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(R.string.no_internet_sub_title))
                                    }
                                    else {
                                        val exception: HttpException = e as HttpException
                                        val er: String = exception.response()!!.errorBody()!!.string()
                                        val errorPojo: ErrorPojo =
                                            Gson().fromJson(er, ErrorPojo::class.java)

                                        if (errorPojo != null) {
                                            if (errorPojo.error.isNotEmpty()) {
                                                if (errorPojo.message.isNotEmpty()) {
                                                    ConstantMethods.showError(
                                                        context,
                                                        errorPojo.error,
                                                        errorPojo.message
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                                catch (e : Exception)
                                {
                                    ConstantMethods.cancleProgessDialog()
                                    ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                                        R.string.error_message))

                                }
                            }

                        })

                }
                else{
                    ConstantMethods.showToast(context,"Please wait media uploading")
                }
            }
                catch (e: Exception) {
                ConstantMethods.cancleProgessDialog()
                ConstantMethods.showError(
                    context,
                    context.resources.getString(R.string.error_title),
                    context.resources.getString(
                        R.string.error_message
                    )
                )
            }
        }

    }

    fun isVideoAvailable():Boolean
    {
        var isVideoAvailable:Boolean=false
        if(mediaList!=null) {
            for (item in mediaList!!) {
                if (item.mimeType.contains("video"))
                    isVideoAvailable = true
            }
        }
        return isVideoAvailable
    }


    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath:String):Bitmap {
        var bitmap: Bitmap ?= null
        var mediaMetadataRetriever:MediaMetadataRetriever ?= null
        try
        {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
            else
                mediaMetadataRetriever.setDataSource(videoPath)
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        }
        catch (e:Exception) {
            e.printStackTrace()
            throw Throwable(
                ("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message))
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release()
            }
        }
        return bitmap!!
    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image = itemView.img_media!!
        var play = itemView.img_play!!
        var cardview = itemView.cardView!!
        var progressBar = itemView.progressBar!!
        var delete = itemView.img_delete!!

    }
}