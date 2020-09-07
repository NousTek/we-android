package com.we.beyond.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.VideoView
import com.bumptech.glide.Glide
import com.we.beyond.R
import com.we.beyond.util.ConstantEasySP
import android.media.MediaPlayer
import android.widget.FrameLayout
import android.widget.Toast
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

/** It is used to show preview of media  */
class PreviewActivity : AppCompatActivity(), Player.EventListener {

    /** init image view */
    var preview: ImageView? = null

    /* var back : ImageView?=null*/
    var play: ImageView? = null

    /** frame layout */
    var videoViewLayout: FrameLayout? = null


    /** init video view */
    //  var videoView :VideoView?=null
    var player: SimpleExoPlayer? = null
    var video_view: PlayerView? = null

    /** init strings */
    var mediaUrl: String = ""
    var url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        /** initialize ids of elements */
        initElementsWithIds()

        /** load data */
        //loadData()

        /** initialize onclick listeners */
        initWithListener()
    }

    /** call loadData() when this activity resumes */
    override fun onResume() {
        super.onResume()
        loadData()
    }

    /** ui listensers */
    private fun initWithListener() {
        val imageMatrixTouchHandler = ImageMatrixTouchHandler(this)
        preview!!.setOnTouchListener(imageMatrixTouchHandler)

    }

    /** Get data using intent and set images to respective image view
     * and video to exo player to play video*/
    private fun loadData() {
        mediaUrl = intent.getStringExtra(ConstantEasySP.PREVIEW_MEDIA)
        var mimeType = intent.getStringExtra(ConstantEasySP.MIME_TYPE)

        println("media url $mediaUrl")
        println("mime type url $mimeType")

        if (mimeType.contains("image")) {
            if (mediaUrl != null && mediaUrl.isNotEmpty()) {

                video_view!!.visibility = View.GONE
                play!!.visibility = View.GONE
                preview!!.visibility = View.VISIBLE


                Glide
                    .with(this)
                    .load(mediaUrl)
                    .into(preview!!)


            }
        } else {
            if (mediaUrl != null && mediaUrl.isNotEmpty()) {
                preview!!.visibility = View.GONE
                video_view!!.visibility = View.VISIBLE
                play!!.visibility = View.VISIBLE
                // videoView!!.setVideoURI(Uri.parse(mediaUrl))


                url = mediaUrl
                if (url.length > 0) {
                    //initialisePlayer(Uri.parse(url))
                    play!!.visibility = View.GONE
                    player = ExoPlayerFactory.newSimpleInstance(
                        DefaultRenderersFactory(this),
                        DefaultTrackSelector(), DefaultLoadControl()
                    )
//                    //val mediaSource:MediaSource = buildMediaSources(Uri.parse(url))
                    video_view!!.player = player
                    buildMediaSource(Uri.parse(url))
                    //videoView!!.start()
                }
            }
        }
    }


    /** It is used to prepare the exo player */
    private fun buildMediaSource(mUri: Uri) {
        // Measures bandwidth during playback. Can be null if not required.
        val bandwidthMeter = DefaultBandwidthMeter()
        // Produces DataSource instances through which media data is loaded.
        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter
        )
        // This is the MediaSource representing the media to be played.
        val videoSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mUri)
        // Prepare the player with the source.
        player!!.prepare(videoSource)
        player!!.setPlayWhenReady(true)
        player!!.addListener(this)

    }


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
        buildMediaSource(Uri.parse(url))

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


    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image view */
        preview = findViewById(R.id.img_preview)

        play = findViewById(R.id.img_play)

        /** ids of video view */
        //videoView = findViewById(R.id.video_preview)
        video_view = findViewById(R.id.simpleExoPlayerView)
        //videoViewLayout = findViewById(R.id.videoView)
    }

    /** pause the player on activity pause */
    override fun onPause() {
        super.onPause()
        if (player != null) {
            player!!.release()
        }
    }

    /** destroy the player on activity destroy */
    override fun onDestroy() {
        super.onDestroy()
        if (player != null) {
            player!!.release()
        }
    }

    /** stop the player on activity stop */
    override fun onStop() {
        super.onStop()
        if (player != null) {
            player!!.release()
        }

    }

    /** It goes back to previous activity */
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }


}
