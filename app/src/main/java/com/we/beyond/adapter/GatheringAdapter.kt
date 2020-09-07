package com.we.beyond.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DatabaseReference
import com.google.gson.JsonObject
import com.we.beyond.Interface.*
import com.we.beyond.R
import com.we.beyond.model.GatheringData
import com.we.beyond.model.LatLongPojo
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.campaign.createCampaign.CreateCampaignActivity
import com.we.beyond.ui.gathering.gathering.GatheringActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.list_gathering.view.*
import kotlin.collections.ArrayList

/** It binds all gathering data set to views */
class GatheringAdapter(
    var context: Context,
    gatheringList: ArrayList<GatheringData>
) : RecyclerView.Adapter<GatheringAdapter.ViewHolder>() {


    /** init array list */
    var gatheringList: ArrayList<GatheringData>? = null

    /** init double */
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    /** init firebase database reference */
    lateinit var rootRef: DatabaseReference

    /** init listeners */
    var onGoingPresenter: OnGoingListener? = null
    var onDeletePresenter: OnDeleteListener? = null
    var onSubscribeListener: OnSubscribeListener? = null
    var onReportAbuseListener: OnReportAbuseListener? = null

    init {
        this.gatheringList = gatheringList

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.list_gathering, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gatheringList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** listener initialization */
        try {

            val activity = context as Activity
            try {
                onGoingPresenter = activity as OnGoingListener
            } catch (e: Exception) {
                e.printStackTrace()

            }


            try {
                onDeletePresenter = activity as OnDeleteListener
            } catch (e: Exception) {
                e.printStackTrace()

            }

            try {
                onSubscribeListener = activity as OnSubscribeListener
            } catch (e: Exception) {
                e.printStackTrace()

            }

            try {
                onReportAbuseListener = activity as OnReportAbuseListener
            } catch (e: Exception) {
                e.printStackTrace()

            }




            getLocation()

            /** It set data to views */
            holder.gatheringTitle.text = gatheringList!![position].title
            holder.gatheringTitle.typeface = ConstantFonts.raleway_medium

            /** It opens LocationActivity and stores location coordinates */
            holder.locationLayout.setOnClickListener {
                val intent = Intent(context, LocationActivity::class.java)
                (context).startActivity(intent)
                (context as GatheringActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )

                EasySP.init(context).put("lat", gatheringList!![position].location.coordinates[0])
                EasySP.init(context).put("long", gatheringList!![position].location.coordinates[1])
                EasySP.init(context)
                    .putBoolean("resolved", gatheringList!![position].issue.resolved)
            }

            /** It check image and video urls, if these are not empty then set it image using glide library
             * else set placeholder */
            if (gatheringList!![position].imageUrls != null && gatheringList!![position].imageUrls.isNotEmpty()) {

                Glide.with(context!!)
                    .load(gatheringList!![position].imageUrls[0])
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading_image)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.imgGathering)
            } else if (gatheringList!![position].videoUrls != null && gatheringList!![position].videoUrls.isNotEmpty()) {
                Glide
                    .with(context!!)
                    .load(gatheringList!![position].videoUrlThumbnails[0])
                    .placeholder(R.drawable.processing_video)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgGathering)

            } else {

                holder.imgGathering.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.placeholder
                    )
                )
            }


            /** It set data to views */

            if (gatheringList!![position].user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {
                holder.hostedByName.text =
                    " ${gatheringList!![position].user.firstName} ${gatheringList!![position].user.lastName}"
                holder.hostedByName.typeface = ConstantFonts.raleway_regular
            } else {
                holder.hostedByName.text =
                    " ${gatheringList!![position].user.organizationName}"
                holder.hostedByName.typeface = ConstantFonts.raleway_regular

            }

            holder.gatheringLocation.text =
                gatheringList!![position].address + ", ${gatheringList!![position].city}"
            holder.gatheringLocation.typeface = ConstantFonts.raleway_semibold

            holder.gatheringDate.text =
                ConstantMethods.convertStringToDateStringFull(gatheringList!![position].gatheringDate)
            holder.gatheringDate.typeface = ConstantFonts.raleway_regular

            holder.gatheringDescription.text = gatheringList!![position].description
            holder.gatheringDescription.typeface = ConstantFonts.raleway_regular

            println(
                "date ${ConstantMethods.convertStringToDateFull(
                    holder.gatheringDate.text.toString().trim()
                )}"
            )
            println(
                "month ${ConstantMethods.convertStringToMonthFull(
                    holder.gatheringDate.text.toString().trim()
                )}"
            )

            holder.monthTitle.text =
                ConstantMethods.convertStringToMonthFull(
                    holder.gatheringDate.text.toString().trim()
                )
            holder.monthTitle.typeface = ConstantFonts.raleway_regular

            holder.dateTitle.text =
                ConstantMethods.convertStringToDateFull(holder.gatheringDate.text.toString().trim())
            holder.dateTitle.typeface = ConstantFonts.raleway_regular

            holder.hostedByTitle.typeface = ConstantFonts.raleway_regular

            if (gatheringList!![position].campaigns != null) {

            }

            if (gatheringList!![position].invites != null) {

            }

            /** It opens GatheringDetailsActivity by passing gathering id  */
            holder.itemView.setOnClickListener {

                val intent = Intent(context, GatheringDetailsActivity::class.java)
                //intent.putExtra("gatheringId",gatheringList!![position]._id)
                intent.putExtra("gatheringId", gatheringList!![position]._id)
                (context as GatheringActivity).startActivityForResult(intent, 200)
                // (context as GatheringActivity).startActivity(intent)
                (context as GatheringActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                // (context as GatheringActivity).finish()
            }

            /** It opens CreateCampaignActivity if issue is not resolved
             * else we cannot create campaign on resolved issue*/
            holder.itemView.img_create_campaign.setOnClickListener {

                if (gatheringList!![position].issue.resolved) {
                    ConstantMethods.showWarning(
                        context,
                        "",
                        "You cannot create campaign on resolved issues."
                    )

                } else {
                    EasySP.init(context).putString(ConstantEasySP.CAMPAIGN_DATE, "")
                    EasySP.init(context).putString(ConstantEasySP.SELECTED_CAMPAIGN_ADDRESS, "")
                    EasySP.init(context).putString("city", "")
                    EasySP.init(context).putString("lat", "")
                    EasySP.init(context).putString("long", "")
                    EasySP.init(context).putString("campaignTitle", "")
                    EasySP.init(context).putString("campaignDetails", "")
                    EasySP.init(context).putString("campaignDate", "")

                    val intent = Intent(context, CreateCampaignActivity::class.java)
                    intent.putExtra("campaign", true)
                    (context as GatheringActivity).startActivityForResult(intent, 200)
                    (context as GatheringActivity).overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    //(context as GatheringActivity).finish()


                    EasySP.init(context)
                        .putString(ConstantEasySP.GATHERING_ID, gatheringList!![position]._id)
                    EasySP.init(context)
                        .putString(ConstantEasySP.GATHERING_TITLE, gatheringList!![position].title)

                }
            }

            /** show and hide going and notGoing image,
             * set change text color */
            if (gatheringList!![position].isGoing != null) {
                if (gatheringList!![position].isGoing) {
                    holder.imgNotGoing.visibility = View.VISIBLE
                    holder.imgGoing.visibility = View.GONE

                    holder.goingTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.button_background
                        )
                    )
                }
            } else {
                holder.imgNotGoing.visibility = View.GONE
                holder.imgGoing.visibility = View.VISIBLE

                holder.goingTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }

            /** show and hide going and notGoing image,
             * set change text color,
             * call onGoing function of onGoingPresenter */
            holder.imgGoing.setOnClickListener {

                holder.imgNotGoing.visibility = View.VISIBLE
                holder.imgGoing.visibility = View.GONE

                holder.goingTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )

                onGoingPresenter!!.onGoing(gatheringList!![position]._id)
            }

            /** show and hide going and notGoing image,
             * set change text color,
             * call onNoGoing function of onGoingPresenter */
            holder.imgNotGoing.setOnClickListener {

                holder.imgGoing.visibility = View.VISIBLE
                holder.imgNotGoing.visibility = View.GONE

                holder.goingTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )

                onGoingPresenter!!.onNoGoing(gatheringList!![position]._id)
            }

/*
            holder.more.setOnClickListener {
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_in_up
                    )
                )

            }*/

            /** It closes more layout with animation */
            holder.close.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )
            }

            /** It closes more layout with animation,
             * call onDelete function of onDeletePresenter */
            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {

                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )
                (context as GatheringActivity).gatheringAdapter!!.notifyDataSetChanged()


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == gatheringList!![position].user._id) {
                    println("user id ${userId} gathering user ${gatheringList!![position].user._id}")
                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "Do you want to delete gathering?"
                        sweetAlertDialog.confirmText = "Yes"
                        sweetAlertDialog.cancelText = "No"
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()


                            try {

                                val jsonObject = JsonObject()
                                jsonObject.addProperty("type", "disabled")
                                jsonObject.addProperty("gatheringId", gatheringList!![position]._id)


                                try {
                                    onDeletePresenter!!.onDelete(jsonObject)

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }


                            } catch (e: Exception) {
                                e.printStackTrace()
                            }


                        }


                        sweetAlertDialog.setCancelClickListener {
                            sweetAlertDialog.dismissWithAnimation()
                        }


                    } catch (e: Exception) {


                    }
                } else {

                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "You are not owner of this gathering."
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }

            /** It open google map with location */
            holder.itemView.img_directions.setOnClickListener {

                if (gatheringList!![position].location.coordinates != null) {
                    val uri =
                        "http://maps.google.com/maps?saddr=" + "$latitude" + "," + "$longitude" + "&daddr=" + "${gatheringList!![position].location.coordinates[0]}" + "," + "${gatheringList!![position].location.coordinates[1]}"
                    val intent = Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setClassName(
                        "com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity"
                    )
                    (context as GatheringActivity).startActivity(intent)
                }
            }


            /** show and hide subscribe and notSubscribe image,
             * set change text color,
             * call onSubscribe function of onSubscribeListener */
            holder.imgSubscribe.setOnClickListener {
                holder.imgNotSubscribe.visibility = View.VISIBLE
                holder.imgSubscribe.visibility = View.GONE

                holder.subscribeTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )

                onSubscribeListener!!.onSubscribe(gatheringList!![position]._id)
            }

            /** show and hide subscribe and notSubscribe image,
             * set change text color,
             * call onSubscribe function of onSubscribeListener */
            holder.imgNotSubscribe.setOnClickListener {
                holder.imgNotSubscribe.visibility = View.GONE
                holder.imgSubscribe.visibility = View.VISIBLE

                holder.subscribeTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
                onSubscribeListener!!.onSubscribe(gatheringList!![position]._id)
            }

            /** It check gathering is subscribe or not, depends on this change image and text color */
            if (gatheringList!![position].isSubscribe) {
                holder.imgNotSubscribe.visibility = View.VISIBLE
                holder.imgSubscribe.visibility = View.GONE

                holder.subscribeTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )

            } else {
                holder.imgSubscribe.visibility = View.VISIBLE
                holder.imgNotSubscribe.visibility = View.GONE

                holder.subscribeTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }


            /** It closes more layout with animation,
             * call ReportAbuse function of onReportAbuseListener if current gathering user and store user does not match
             * else show warning dialog */
            holder.reportAbuse.typeface = ConstantFonts.raleway_semibold
            holder.reportAbuse.setOnClickListener {
                try {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )
                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == gatheringList!![position].user._id) {

                        ConstantMethods.showWarning(
                            context,
                            "",
                            "You can not abuse your gathering."
                        )
                    } else {

                        onReportAbuseListener!!.ReportAbuse(
                            gatheringList!![position]._id,
                            "gathering"
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            holder.optionLayout!!.setOnClickListener {

            }

            /** It opens BadgesActivity */
            holder.hostedByName.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID, gatheringList!![position].user._id)
                (context as GatheringActivity).startActivity(intent)
                (context as GatheringActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }


            /** set fonts */
            holder.goingTitle.typeface = ConstantFonts.raleway_regular
            holder.subscribeTitle.typeface = ConstantFonts.raleway_regular
            holder.directionTitle.typeface = ConstantFonts.raleway_regular
            holder.campaignTitle.typeface = ConstantFonts.raleway_regular
            holder.shareTitle.typeface = ConstantFonts.raleway_regular

            /** It closes more layout with animation */
            holder.moreLayout.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )
            }


            /** share gathering details using intent */
            holder.share.setOnClickListener {
                try {

                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                            context,
                            R.anim.slide_out_down
                        )
                    )

                    val share = Intent(Intent.ACTION_SEND)
                    share.setType("text/plain")
                    share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                    share.putExtra(Intent.EXTRA_SUBJECT, "WE")
                    share.putExtra(
                        Intent.EXTRA_TEXT,
                        "${Constants.BASE_URL}gatherings/${gatheringList!![position]._id}"
                    )
                    (context).startActivity(Intent.createChooser(share, "Share Gathering!"))
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It gets the current location using gps provider */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {

            val locationManager =
                (context as GatheringActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f,
                locationListener
            )
            var currentLocation = getLastKnownLocation()
//            latitude = currentLocation.latitude
//            longitude = currentLocation.longitude
            if (currentLocation != null) {
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude


            }


        } catch (e: Exception) {

        }
    }

    /** It returns the last known location  */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        var mLocationManager =
            (context as GatheringActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationManager =
            (context as GatheringActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.getAccuracy() < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }

    /** It is listener which store the location data
     * to firebase database reference whenever location changes
     */
    private val locationListener = object : LocationListener {
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }

        override fun onLocationChanged(location: Location) {
            try {
                val deviceFcmToken: String =
                    EasySP.init(context).getString(ConstantEasySP.SP_FCM_DEVICE_TOKEN)
                if (deviceFcmToken != null && deviceFcmToken.length > 0) {
                    val latLongPojo =
                        LatLongPojo(location.latitude, location.longitude, deviceFcmToken)
                    rootRef.setValue(latLongPojo)
                }

            } catch (e: Exception) {

            }

        }
    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var monthTitle = itemView.txt_month_title!!
        var dateTitle = itemView.txt_date_title!!
        var gatheringTitle = itemView.txt_gathering_title!!
        var hostedByTitle = itemView.txt_hosted_by_title!!
        var hostedByName = itemView.txt_hosted_by_name!!
        var imgGathering = itemView.img_gathering!!
        var imgDirection = itemView.img_directions!!
        var share = itemView.img_share!!
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
        var moreLayout = itemView.moreLayout!!
        var imgGoing = itemView.img_resolved!!
        var imgSubscribe = itemView.img_subscribe!!
        var imgNotSubscribe = itemView.img_already_subscribe!!
        var imgNotGoing = itemView.img_already_resolved!!
        var gatheringLocation = itemView.txt_gathering_location!!
        var gatheringDate = itemView.txt_gathering_date!!
        var gatheringDescription = itemView.txt_gathering_description!!
        var subscribe = itemView.txt_subscribe_title!!
        var reportAbuse = itemView.txt_report_abuse!!
        var goingTitle = itemView.txt_resolve_title!!
        var subscribeTitle = itemView.txt_subscribe_title!!
        var directionTitle = itemView.txt_direction_title!!
        var campaignTitle = itemView.txt_campaign_title!!
        var shareTitle = itemView.txt_share_title!!
        var optionLayout = itemView.optionLayout!!
        var locationLayout = itemView.gatheringDetailsLayout!!


    }


}