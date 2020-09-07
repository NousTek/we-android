package com.we.beyond.adapter

import android.content.Context
import android.net.Uri
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.viewpager.widget.PagerAdapter
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.we.beyond.R


/** It set data to view pager using this adapter  */
class CustomPagerAdapter(
    internal var mContext: Context,
    internal var mediaArrayList: ArrayList<String>,
    mediaTagArray: ArrayList<String>
) :
    PagerAdapter() {
    internal var mLayoutInflater: LayoutInflater
    var mediaTagArrayList : ArrayList<String>?=null
    var player: SimpleExoPlayer? = null
    var url: String = ""

    override fun getCount(): Int {
        return  mediaArrayList.size

    }

    init {

        mLayoutInflater =
            mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mediaTagArrayList = mediaTagArray


    }

    override  fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override  fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false)

        try {
            val imageView = itemView.findViewById(R.id.imageView) as ImageView
            var play = itemView.findViewById(R.id.img_play) as ImageView
            // val videoViewLayout = itemView.findViewById(R.id.videoView) as FrameLayout
            val progressBar = itemView.findViewById(R.id.progressBar) as ProgressBar
            var video_view=itemView.findViewById<PlayerView>(R.id.simpleExoPlayerView)



            if(mediaTagArrayList!![position].equals("image",ignoreCase = true)) {
                imageView.visibility = View.VISIBLE
                video_view.visibility = View.GONE
                play.visibility = View.GONE


                progressBar.visibility = View.VISIBLE
                Picasso.with(mContext)
                    .load("" + mediaArrayList[position])
                    //.resize(500,400)  // optional
                    .into(imageView, object : Callback {
                        override fun onSuccess() {
                            progressBar.visibility = View.GONE
                        }

                        override fun onError() {

                        }

                    })
            }
            else {
                video_view.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                play.visibility = View.VISIBLE


                url = mediaArrayList[position]
                if (url.length > 0) {
                    //initialisePlayer(Uri.parse(url))

                    player = ExoPlayerFactory.newSimpleInstance(
                        DefaultRenderersFactory(mContext),
                        DefaultTrackSelector(), DefaultLoadControl()
                    )
                    play.visibility = View.GONE
//                    //val mediaSource:MediaSource = buildMediaSources(Uri.parse(url))
                    video_view!!.player = player

                    //player!!.release()


                }
            }


            video_view.setPlaybackPreparer {
                buildMediaSource()
            }


            val imageMatrixTouchHandler = ImageMatrixTouchHandler(mContext)
            imageView.setOnTouchListener(imageMatrixTouchHandler)

            container.addView(itemView)



        } catch (e: Exception) {
            e.printStackTrace()
        }


        return itemView
    }


    fun buildMediaSource() {
        val mUri = Uri.parse(url)
        // Measures bandwidth during playback. Can be null if not required.
        val bandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(
            mContext,
            Util.getUserAgent(mContext, mContext.getString(R.string.app_name)), bandwidthMeter
        )
        // This is the MediaSource representing the media to be played.
        val videoSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mUri)
        // Prepare the player with the source.

        player!!.prepare(videoSource)
        player!!.setPlayWhenReady(true)
        player!!.addListener(object : Player.EventListener{
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

            }

            override fun onSeekProcessed() {

            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {

            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                buildMediaSource()

            }

            override fun onLoadingChanged(isLoading: Boolean) {

            }

            override fun onPositionDiscontinuity(reason: Int) {

            }

            override fun onRepeatModeChanged(repeatMode: Int) {

            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {

            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {

                    Player.STATE_BUFFERING -> {


                    }
                    Player.STATE_ENDED -> {

                    }
                    Player.STATE_IDLE -> {


                    }
                    Player.STATE_READY -> {

                    }
                    else -> {

                    }
                }// Activate the force enable
                // status = PlaybackStatus.IDLE;
            }

        })
    }

    override  fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)


    }

}

