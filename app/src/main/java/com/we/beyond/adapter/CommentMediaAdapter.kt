package com.we.beyond.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
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
import com.we.beyond.R
import com.we.beyond.model.MediaUploadingPojo
import com.we.beyond.ui.PreviewActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantMethods
import kotlinx.android.synthetic.main.media_item.view.*

/** It binds media of comment data set to views */
class CommentMediaAdapter(var context: Context, mediaList: ArrayList<MediaUploadingPojo>) :
    RecyclerView.Adapter<CommentMediaAdapter.ViewHolder>() {

    /** init array list */
    var mediaList: ArrayList<MediaUploadingPojo>? = null

    init {

        this.mediaList = mediaList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.comment_media_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mediaList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        println("uploaded media $mediaList")

        /** It set data to views */
        if (mediaList!![position].serverUrl.isNotEmpty() && mediaList!![position].serverUrl != null) {


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

        } else {

                holder.progressBar.visibility = View.VISIBLE
                Glide
                    .with(context)
                    .load(mediaList!![position].serverUrl)
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


        /** It opens PreviewActivity with pass data when click on it */
        holder.cardview.setOnClickListener {

            if (mediaList!![position].serverUrl != null && mediaList!![position].serverUrl.isNotEmpty()) {
                val intent = Intent(context, PreviewActivity::class.java)
                intent.putExtra(ConstantEasySP.PREVIEW_MEDIA, mediaList!![position].serverUrl)
                intent.putExtra(ConstantEasySP.MIME_TYPE, mediaList!![position].mimeType)
                (context).startActivity(intent)

            } else {
                ConstantMethods.showToast(context,  "Please wait media uploading")
            }
        }


    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image = itemView.img_media!!
        var cardview = itemView.cardView!!
        var progressBar = itemView.progressBar!!

    }
}