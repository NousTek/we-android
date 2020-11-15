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
class IssueMediaAdapter(
    var context: Context,
    mediaList: List<String>,
    val isGalleryVideo: Boolean
) :
    RecyclerView.Adapter<IssueMediaAdapter.ViewHolder>(){


    /** init array list */
    var mediaList: List<String>? = null

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
        holder.delete!!.visibility=View.INVISIBLE
        /** It is used to upload image or video using uploadImage or uploadVideo api depends on condition */
        /** It set image or video to image view */
        if(mediaList!![position]!=null && mediaList!![position].isNotEmpty())
        {
                if (mediaList!![position].contains("video")) {
                    var thumbnailPath =
                        mediaList!![position].replace("video.m3u8", "thumb00001.jpg")
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
                    println("image set ${mediaList!!}")
                    val json1 = Gson().toJson(
                        mediaList!!
                    )
                    EasySP.init(context).putString(ConstantEasySP.UPLOADED_MEDIA, json1)



                    holder.progressBar.visibility = View.VISIBLE
                    Glide
                        .with(context!!)
                        .load(mediaList!![position])
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .thumbnail(Glide.with(context!!).load(mediaList!![position]))
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

        /** It opens PreviewActivity */
        holder.cardview.setOnClickListener {
            if(mediaList!![position]!=null && mediaList!![position].isNotEmpty())
            {
                val intent = Intent(context, PreviewActivity::class.java)
                intent.putExtra(ConstantEasySP.PREVIEW_MEDIA, mediaList!![position])
                if(mediaList!![position].contains("video.m3u8"))
                intent.putExtra(ConstantEasySP.MIME_TYPE, "video")
                else
                intent.putExtra(ConstantEasySP.MIME_TYPE, "image")
                (context).startActivity(intent)

            }
        }
        if(mediaList!![position].contains("video.m3u8"))
        {
            holder.play.visibility = View.VISIBLE
        }
        else{
            holder.play.visibility = View.GONE
        }
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