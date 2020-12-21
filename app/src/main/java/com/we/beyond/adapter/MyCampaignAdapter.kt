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
import com.we.beyond.Interface.OnCheckInListener
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnGoingListener
import com.we.beyond.R
import com.we.beyond.model.LatLongPojo
import com.we.beyond.model.MyCampaignData
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.campaign.campaignDetails.CampaignDetailsActivity
import com.we.beyond.ui.gathering.gathering.GatheringActivity
import com.we.beyond.ui.profile.MyCampaignActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.list_my_campaign.view.*

/** It binds user campaign data set to views */

class MyCampaignAdapter(
    var context: Context,
    campaignList: ArrayList<MyCampaignData>
) : RecyclerView.Adapter<MyCampaignAdapter.ViewHolder>() {

    /** init array list */
    var campaignList: ArrayList<MyCampaignData>? = null

    /** init double  */
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    /** init firebase database reference */
    lateinit var rootRef: DatabaseReference

    /** init listeners */
    var onDeletePresenter: OnDeleteListener? = null
    var onGoingPresenter: OnGoingListener? = null
    var onCheckInListener: OnCheckInListener? = null

    init {
        this.campaignList = campaignList

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.list_my_campaign, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return campaignList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize the listeners */
        try {

            val activity = context as Activity

            try {
                onDeletePresenter = activity as OnDeleteListener
            } catch (e: java.lang.Exception) {
                e.printStackTrace()

            }

            try {
                onGoingPresenter = activity as OnGoingListener
            } catch (e: Exception) {
                e.printStackTrace()

            }


            try {
                onCheckInListener = activity as OnCheckInListener
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /** It gets the current location using gps provider,
             */
            getLocation()

            /** It set data to views */
            holder.campaignTitle.text = campaignList!![position].title
            holder.campaignTitle.typeface = ConstantFonts.raleway_medium


            /** It opens LocationActivity with pass going id when click on it */
            holder.locationLayout.setOnClickListener {
                val intent = Intent(context, LocationActivity::class.java)
                (context).startActivity(intent)
                (context as MyCampaignActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )

                EasySP.init(context).put("lat", campaignList!![position].location.coordinates[0])
                EasySP.init(context).put("long", campaignList!![position].location.coordinates[1])
                EasySP.init(context).putBoolean("resolved", campaignList!![position].issue.resolved)
            }

            if (campaignList!![position].imageUrls != null && campaignList!![position].imageUrls.isNotEmpty()) {

                Glide.with(context!!)
                    .load(campaignList!![position].imageUrls[0])
//                    .placeholder(R.drawable.loading_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.imgCampaign)

                holder.play.visibility = View.GONE

            } else if (campaignList!![position].videoUrls != null && campaignList!![position].videoUrls.isNotEmpty()) {
                Glide
                    .with(context!!)
                    .load(campaignList!![position].videoUrlThumbnails[0])
                    .placeholder(R.drawable.processing_video)
                    .into(holder.imgCampaign)

                holder.play.visibility = View.VISIBLE

            } else {
                holder.play.visibility = View.GONE
                holder.imgCampaign.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.placeholder
                    )
                )
            }



            holder.campaignLocation.text =
                campaignList!![position].address + ", ${campaignList!![position].city}"
            holder.campaignLocation.typeface = ConstantFonts.raleway_semibold

            holder.campaignDate.text =
                ConstantMethods.convertStringToDateStringFull(campaignList!![position].campaignDate)
            holder.campaignDate.typeface = ConstantFonts.raleway_regular

            holder.campaignDescription.text = campaignList!![position].description
            holder.campaignDescription.typeface = ConstantFonts.raleway_medium

            println(
                "date ${ConstantMethods.convertStringToDateFull(
                    holder.campaignDate.text.toString().trim()
                )}"
            )
            println(
                "month ${ConstantMethods.convertStringToMonthFull(
                    holder.campaignDate.text.toString().trim()
                )}"
            )

            holder.monthTitle.text =
                ConstantMethods.convertStringToMonthFull(holder.campaignDate.text.toString().trim())
            holder.monthTitle.typeface = ConstantFonts.raleway_regular

            holder.dateTitle.text =
                ConstantMethods.convertStringToDateFull(holder.campaignDate.text.toString().trim())
            holder.dateTitle.typeface = ConstantFonts.raleway_regular


            /** It opens CampaignDetailsActivity with pass going id when click on it */
            holder.itemView.setOnClickListener {

                val intent = Intent(context, CampaignDetailsActivity::class.java)
                //intent.putExtra("gatheringId",gatheringList!![position]._id)
                intent.putExtra("campaignId", campaignList!![position]._id)
                intent.putExtra("myCampaign", true)
                (context as MyCampaignActivity).startActivityForResult(intent, 200)
                (context as MyCampaignActivity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )

            }


            /** It opens google map with given lat-long */
            holder.itemView.img_directions.setOnClickListener {

                if (campaignList!![position].location.coordinates != null) {
                    val uri =
                        "http://maps.google.com/maps?saddr=" + "$latitude" + "," + "$longitude" + "&daddr=" + "${campaignList!![position].location.coordinates[0]}" + "," + "${campaignList!![position].location.coordinates[1]}"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setClassName(
                        "com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity"
                    )
                    (context as MyCampaignActivity).startActivity(intent)
                }
            }

            /** It opens more layout with animation */
            holder.more.setOnClickListener {
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_in_up
                    )
                )

            }

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

            /** It opens normal dialog and call onDelete function of onDeletePresenter */
            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {

                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )
                (context as MyCampaignActivity).campaignAdapter!!.notifyDataSetChanged()


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == campaignList!![position].user) {
                    println("user id ${userId} campaign user ${campaignList!![position].user}")
                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "Do you want to delete campaign?"
                        sweetAlertDialog.confirmText = "Yes"
                        sweetAlertDialog.cancelText = "No"
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()


                            try {

                                val jsonObject = JsonObject()
                                jsonObject.addProperty("type", "disabled")
                                jsonObject.addProperty("campaignId", campaignList!![position]._id)


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
                        sweetAlertDialog.contentText = "You are not owner of this campaign."
//                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()
                        }
                        ConstantMethods.showToast(context,"You are not the owner of this campaign.")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }


            /** It closes more layout with animation,
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
                    if (userId == campaignList!![position].user) {

                        ConstantMethods.showToast(context, "You can not abuse your campaign.")
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }



            if (campaignList!![position].isGoing != null) {
                if (campaignList!![position].isGoing) {
                    holder.imgNotGoing.visibility = View.VISIBLE
                    holder.imgGoing.visibility = View.GONE

                    holder.goingTitle!!.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.button_background
                        )
                    )
                }
            } else {
                holder.imgNotGoing.visibility = View.GONE
                holder.imgGoing.visibility = View.VISIBLE

                holder.goingTitle!!.setTextColor(
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

                onGoingPresenter!!.onGoing(campaignList!![position]._id)
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

                onGoingPresenter!!.onNoGoing(campaignList!![position]._id)
            }


            if (campaignList!![position].isCheckedIn != null) {
                if (campaignList!![position].isCheckedIn) {
                    holder.alreadyCheckIn.visibility = View.VISIBLE
                    holder.checkIn.visibility = View.GONE

                    holder.checkInTitle.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.button_background
                        )
                    )
                }
            } else {
                holder.alreadyCheckIn.visibility = View.GONE
                holder.checkIn.visibility = View.VISIBLE

                holder.checkInTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )
            }

            holder.checkIn.setOnClickListener {

                holder.alreadyCheckIn.visibility = View.VISIBLE
                holder.checkIn.visibility = View.GONE

                holder.checkInTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.button_background
                    )
                )

                try {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "campaign")
                    jsonObject.addProperty("typeId", campaignList!![position]._id)


                    onCheckInListener!!.onCheckIn(jsonObject)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            holder.alreadyCheckIn.setOnClickListener {

                holder.checkIn.visibility = View.VISIBLE
                holder.alreadyCheckIn.visibility = View.GONE

                holder.checkInTitle.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorPrimary
                    )
                )

                try {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "campaign")
                    jsonObject.addProperty("typeId", campaignList!![position]._id)


                    onCheckInListener!!.onCheckIn(jsonObject)

                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }



            holder.moreTitle.typeface = ConstantFonts.raleway_regular
            holder.goingTitle.typeface = ConstantFonts.raleway_regular
            holder.directionTitle.typeface = ConstantFonts.raleway_regular
            holder.checkInTitle.typeface = ConstantFonts.raleway_regular


        } catch (e: Exception) {
            e.printStackTrace()
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
        var campaignTitle = itemView.txt_campaign_title!!
        var imgCampaign = itemView.img_campaign!!
        var imgDirection = itemView.img_directions!!
        var campaignLocation = itemView.txt_campaign_location!!
        var campaignDate = itemView.txt_campaign_date!!
        var campaignDescription = itemView.txt_campaign_description!!
        var more = itemView.img_more!!
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
        var reportAbuse = itemView.txt_report_abuse!!
        var moreLayout = itemView.moreLayout!!
        var moreTitle = itemView.txt_more_title!!
        var goingTitle = itemView.txt_resolved_title!!
        var directionTitle = itemView.txt_direction_title!!
        var checkInTitle = itemView.txt_checkIn_title!!
        var imgGoing = itemView.img_resolved!!
        var imgNotGoing = itemView.img_already_resolved!!
        var checkIn = itemView.img_checkIn!!
        var alreadyCheckIn = itemView.img_already_checkIn!!
        var play = itemView.img_play!!
        var locationLayout = itemView.campaignDetailsLayout!!
    }


}