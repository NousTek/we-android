package com.we.beyond.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.we.beyond.adapter.CustomPagerAdapter
import com.we.beyond.R
import com.we.beyond.util.ConstantFonts

/** It is used to media on view pager to swipe one by one */
class MediaViewPagerActivity : AppCompatActivity() {

    var context : Context = this

    /** init image view */
    var back : ImageView?=null
    var next : ImageView?=null
    var previous : ImageView?=null

    /** init text view */
    var title : TextView?=null
    var count : TextView?=null

    /** init view pager */
    var viewPager : ViewPager?=null

    /** init adapter */
    var customPagerAdapter : CustomPagerAdapter?=null

    /** init array list */
    var imageArray : ArrayList<String>?=null
    var videoArray : ArrayList<String>?=null
    var mediaArray : ArrayList<String>?=null
    var mediaTagArray : ArrayList<String>?=null

    var isHideLayout : Boolean = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_view_pager)

        /** array initialization */
        imageArray = ArrayList()
        imageArray!!.clear()
        videoArray = ArrayList()
        videoArray!!.clear()
        mediaArray = ArrayList()
        mediaArray!!.clear()
        mediaTagArray = ArrayList()
        mediaTagArray!!.clear()

        /** initialize ids of elements */
        initElementsWithIds()

        /** set media adapter */
        setDataToAdapter()

        /** initialize onclick listener */
        initWithListener()


    }

    /** It will get stored data using intent and add to respective array
     * and set CustomPagerAdapter */
    private fun setDataToAdapter() {

        imageArray = intent.getStringArrayListExtra("mediaImage")
        videoArray = intent.getStringArrayListExtra("mediaVideo")

        if(videoArray!=null && imageArray!=null) {

            for(i in 0 until imageArray!!.size)
            {
                mediaTagArray!!.add("image")
            }

            for(i in 0 until videoArray!!.size)
            {
                mediaTagArray!!.add("video")
            }
            mediaArray!!.addAll(imageArray!!)
            mediaArray!!.addAll(videoArray!!)


        }
        else if(imageArray!=null)
        {
            mediaTagArray!!.add("image")
            mediaArray!!.addAll(imageArray!!)
        }
        else if(videoArray!=null)
        {
            mediaTagArray!!.add("video")
            mediaArray!!.addAll(videoArray!!)
        }

        customPagerAdapter = CustomPagerAdapter(context, mediaArray!!,mediaTagArray!!)
        viewPager!!.adapter =customPagerAdapter


    }

    /** ui listeners */
    private fun initWithListener()
    {
        /** It goes back to previous activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        }

        /** It is used to scroll or swipe the page on view pager
         * and if player is not null then stop the player of customPagerAdapter
         * else show next and previous images */
        viewPager!!.addOnPageChangeListener(object  : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                count!!.text = "${position+1} / ${mediaArray!!.size}"

                if(customPagerAdapter!!.player !=null) {
                    next!!.visibility = View.GONE
                    previous!!.visibility = View.GONE
                    customPagerAdapter!!.player!!.stop()
                }
                else{
                    next!!.visibility = View.VISIBLE
                    previous!!.visibility = View.VISIBLE
                }
            }

            override fun onPageSelected(position: Int) {

            }

        })


        /** Show previous image or video
         * and if player is not null then stop the player of customPagerAdapter
         * else show next and previous images */
        previous!!.setOnClickListener {
            var tab = viewPager!!.currentItem
            if(tab > 0)
            {
                tab--
                viewPager!!.currentItem=tab
            }
            else if(tab == 0){
                viewPager!!.currentItem = tab
            }
            if(customPagerAdapter!!.player !=null) {
                customPagerAdapter!!.player!!.stop()
                next!!.visibility = View.GONE
                previous!!.visibility = View.GONE
            }
            else{
                next!!.visibility = View.VISIBLE
                previous!!.visibility = View.VISIBLE
            }
        }

        /** Show next image or video
         * and if player is not null then stop the player of customPagerAdapter
         * else show next and previous images */
        next!!.setOnClickListener {
            var tab = viewPager!!.currentItem
            tab++
            viewPager!!.currentItem = tab

            if(customPagerAdapter!!.player !=null) {
                customPagerAdapter!!.player!!.stop()
                next!!.visibility = View.GONE
                previous!!.visibility = View.GONE
            }
            else{
                next!!.visibility = View.VISIBLE
                previous!!.visibility = View.VISIBLE
            }
        }


        /** Show and hide respective images on touch of view pager */
        viewPager!!.setOnTouchListener(object : View.OnTouchListener{
            private var pointX:Float = 0.toFloat()
            private var pointY:Float = 0.toFloat()
            private var tolerance = 50

            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {




                when (event!!.action) {
                    MotionEvent.ACTION_MOVE ->{
                        return false
                    }

                    MotionEvent.ACTION_DOWN->{
                        pointX = event.x
                        pointY = event.y


                    }

                    MotionEvent.ACTION_UP -> {
                        val sameX =
                            pointX + tolerance > event.getX() && pointX - tolerance < event.getX()
                        val sameY =
                            pointY + tolerance > event.getY() && pointY - tolerance < event.getY()

                        if (sameX && sameY) {

                            if (isHideLayout) {
                                next!!.visibility = View.VISIBLE
                                previous!!.visibility = View.VISIBLE
                                back!!.visibility = View.VISIBLE
                                title!!.visibility = View.VISIBLE
                                count!!.visibility = View.VISIBLE

                                isHideLayout = false


                            } else {
                                next!!.visibility = View.GONE
                                previous!!.visibility = View.GONE
                                back!!.visibility = View.GONE
                                title!!.visibility = View.GONE
                                count!!.visibility = View.GONE

                                isHideLayout = true
                            }
                        }
                    }
                }
                return false
            }

        })




    }

    /** ui initialization */
    private fun initElementsWithIds()
    {
        /** ids of image view */
        back = findViewById(R.id.img_back)
        next = findViewById(R.id.img_right_nav)
        previous = findViewById(R.id.img_left_nav)

        /** ids of text view */
        title = findViewById(R.id.txt_media_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        count = findViewById(R.id.txt_count)
        count!!.typeface = ConstantFonts.raleway_medium

        /** ids of view pager */
        viewPager = findViewById(R.id.pager)



    }


    /** If player is not null then stop the player of customPagerAdapter
     * else show next and previous images */
    override fun onStop() {
        super.onStop()
        if(customPagerAdapter!!.player !=null)
        {
            customPagerAdapter!!.player!!.stop()
            next!!.visibility = View.GONE
            previous!!.visibility = View.GONE
        }
        else{
            next!!.visibility = View.VISIBLE
            previous!!.visibility = View.VISIBLE
        }

    }

    /** It go back to previous activity */
    override fun onBackPressed()
    {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }
}
