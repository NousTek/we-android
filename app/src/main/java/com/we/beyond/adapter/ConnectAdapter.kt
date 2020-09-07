package com.we.beyond.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.R
import com.we.beyond.model.ConnectList
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.connect.connectIssue.ConnectDetailsActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import kotlinx.android.synthetic.main.list_connect.view.*
import kotlinx.android.synthetic.main.list_connect.view.img_close
import kotlinx.android.synthetic.main.list_connect.view.img_connect
import kotlinx.android.synthetic.main.list_connect.view.img_play
import kotlinx.android.synthetic.main.list_connect.view.img_profile_pic
import kotlinx.android.synthetic.main.list_connect.view.moreLayout
import kotlinx.android.synthetic.main.list_connect.view.txt_comment
import kotlinx.android.synthetic.main.list_connect.view.txt_delete
import kotlinx.android.synthetic.main.list_connect.view.txt_name
import kotlinx.android.synthetic.main.list_connect.view.txt_report_abuse
import kotlinx.android.synthetic.main.list_connect.view.txt_time
import kotlin.Exception
import kotlin.collections.ArrayList

/** It binds article data set to views */
class ConnectAdapter(
    val context: Context,
    connectDetails: ArrayList<ConnectList>
) : RecyclerView.Adapter<ConnectAdapter.ViewHolder>() {

    /** init array list */
    var connectDetails: ArrayList<ConnectList>? = null

    /** init listeners */
    var onLikeDislikeListener : OnLikeDislikeListener?=null
    var onDeletePresenter : OnDeleteListener?=null
    var onReportAbuseListener : OnReportAbuseListener?=null


    init {
        this.connectDetails = connectDetails
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(context).inflate(
            R.layout.list_connect,
            parent,
            false
        )

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {

        return connectDetails!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        /** initialize listeners and set data to views */
        val activity = context as Activity
        try{
            onLikeDislikeListener = activity as OnLikeDislikeListener
        }
        catch(e: Exception)
        {
            e.printStackTrace()

        }


        try {
            onDeletePresenter = activity as OnDeleteListener
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

        }

        try {
            onReportAbuseListener = activity as OnReportAbuseListener
        } catch (e: Exception) {
            e.printStackTrace()

        }




        try {

            if(connectDetails!![position].likeByUser != null && connectDetails!![position].likeByUser)
            {
                holder.upvote.visibility = View.GONE
                holder.downvote.visibility = View.VISIBLE
            }


            /** share articles details using intent */
            holder.share.typeface = ConstantFonts.raleway_semibold
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
                        "${Constants.BASE_URL}connects/${connectDetails!![position]._id}"
                    )
                    (context).startActivity(Intent.createChooser(share, "Share Connect!"))
                }
                catch (e : java.lang.Exception)
                {
                    e.printStackTrace()
                }
            }


            holder.connectTitle.text = connectDetails!![position].title
            holder.connectTitle.typeface = ConstantFonts.raleway_semibold

            if (connectDetails!![position].imageUrls != null && connectDetails!![position].imageUrls.isNotEmpty()) {
                Glide.with(context)
                    .load(connectDetails!![position].imageUrls[0])
                    .placeholder(R.drawable.loading_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.connectImage)

                holder.play.visibility = View.GONE

            } else if (connectDetails!![position].videoUrls != null && connectDetails!![position].videoUrls.isNotEmpty()) {
                Glide
                    .with(context)
                    .load(connectDetails!![position].videoUrlThumbnails[0])
                    .placeholder(R.drawable.processing_video)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.connectImage)

                holder.play.visibility = View.VISIBLE

            }
            else{
                holder.play.visibility = View.GONE
                holder.connectImage.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.placeholder))
            }



            if (connectDetails!![position].user.profileUrl != null && connectDetails!![position].user.profileUrl.length > 0) {
                Picasso.with(context)
                    .load(connectDetails!![position].user.profileUrl)
                    .placeholder(R.drawable.profile)
                    //.memoryPolicy(MemoryPolicy.NO_CACHE)
                    //.networkPolicy(NetworkPolicy.NO_CACHE)
                    //.resize(400, 400)  // optional
                    .into(holder.profilePic)
            } else {
                holder.profilePic.setBackgroundResource(R.drawable.profile)
            }


            /** show and hide image views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_upvote.setOnClickListener {
                holder.downvote.visibility = View.VISIBLE
                holder.upvote.visibility = View.GONE

                onLikeDislikeListener!!.onLike(connectDetails!![position]._id)

            }

            /** show and hide image views and call onLike function of onLikeDislikeListener */
            holder.itemView.txt_downvote.setOnClickListener {
                holder.upvote.visibility = View.VISIBLE
                holder.downvote.visibility = View.GONE

                onLikeDislikeListener!!.onLike(connectDetails!![position]._id)

            }

            holder.connectTime.text =
                ConstantMethods.convertStringToDateStringFull(connectDetails!![position].createdAt)
            holder.connectTime.typeface = ConstantFonts.raleway_regular

            holder.connectDescription.text = connectDetails!![position].description
            holder.connectDescription.typeface = ConstantFonts.raleway_medium

            holder.upvoteCount.text = "${connectDetails!![position].likes}  L i k e s,  "
            holder.upvoteCount.typeface = ConstantFonts.raleway_semibold

            holder.commentCount.text = "${connectDetails!![position].comments}  C o m m e n t s"
            holder.commentCount.typeface = ConstantFonts.raleway_semibold

            holder.connect.typeface = ConstantFonts.raleway_semibold
            holder.upvote.typeface = ConstantFonts.raleway_semibold
            holder.downvote.typeface = ConstantFonts.raleway_semibold

            if (connectDetails!![position].user.userLoginType.userType.equals(
                    "individual",
                    ignoreCase = true
                )
            ) {

                holder.name.text =
                    connectDetails!![position].user.firstName + " ${connectDetails!![position].user.lastName}"


            } else {
                holder.name.text = connectDetails!![position].user.organizationName
            }


            /** It opens ConnectDetailsActivity and passes below data */
            holder.itemView.txt_comment.setOnClickListener {

                println("image click")
                val intent = Intent(context, ConnectDetailsActivity::class.java)
                intent.putExtra("connectCategoryId", connectDetails!![position]._id)
                intent.putExtra("connectComment", true)
                (context ).startActivity(intent)
                (context ).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
                //(context as ConnectIssueActivity).finish()

            }


            /** It opens ConnectDetailsActivity and passes below data */
            holder.itemView.setOnClickListener {

                val intent = Intent(context, ConnectDetailsActivity::class.java)
                intent.putExtra("connectCategoryId", connectDetails!![position]._id)
                intent.putExtra("myConnect",true)
                //intent.putExtra("issueNumber",connectDetails!![position].issueNumber)
                (context ).startActivityForResult(intent,200)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                //(context).finish()
            }

            /** It opens BadgesActivity and passes below data */
            holder.profilePic.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,connectDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            /** It opens BadgesActivity and passes below data */
            holder.name.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,connectDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }


            /** It opens more layout with animation */
            holder.more.setOnClickListener {
                holder.moreLayout.visibility = View.VISIBLE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_up))

            }

            /** It closes more layout with animation */
            holder.close.setOnClickListener {
                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
            }

            /** It opens normal dialog and call onDelete function of onDeletePresenter  */
            holder.delete.typeface = ConstantFonts.raleway_semibold
            holder.delete.setOnClickListener {

                holder.moreLayout.visibility = View.GONE
                holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))



                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == connectDetails!![position].user._id) {
                    println("user id ${userId} connect user ${connectDetails!![position].user._id}")
                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "Do you want to delete connect?"
                        sweetAlertDialog.confirmText = "Yes"
                        sweetAlertDialog.cancelText = "No"
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()


                            try {

                                val jsonObject = JsonObject()
                                jsonObject.addProperty("type", "disabled")
                                jsonObject.addProperty("connectId", connectDetails!![position]._id)


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

                        e.printStackTrace()
                    }
                } else {

                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "You are not owner of this connect."
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

            /** it opens BadgesActivity and passes below data */
            holder.profilePic.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,connectDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }


            /** it opens BadgesActivity and passes below data */
            holder.name.setOnClickListener {
                val intent = Intent(context, BadgesActivity::class.java)
                intent.putExtra(Constants.USER_ID,connectDetails!![position].user._id)
                (context).startActivity(intent)
                (context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            /**It closes more layout with animation and check stored user and connect details user
             * if matches, show warning dialog
             * else call ReportAbuse function of onReportAbuseListener */
            holder.reportAbuse.typeface = ConstantFonts.raleway_semibold
            holder.reportAbuse.setOnClickListener {
                try {
                    holder.moreLayout.visibility = View.GONE
                    holder.moreLayout.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))
                    val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                    if (userId == connectDetails!![position].user._id) {

                        ConstantMethods.showWarning(context,"","You can not abuse your connect.")
                    }

                    else {

                        onReportAbuseListener!!.ReportAbuse(
                            connectDetails!![position]._id,
                            "connect"
                        )
                    }
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }


        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** View holder to initialize ui */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        
        var connectTitle = itemView.txt_connect_title!!
        var connectImage = itemView.img_connect!!
        var profilePic = itemView.img_profile_pic!!
        var connectTime = itemView.txt_time!!
        var connectDescription = itemView.txt_connect_description!!
        var upvoteCount = itemView.txt_connect_upvote!!
        var commentCount = itemView.txt_connect_comment!!
        var connect = itemView.txt_comment!!
        var upvote = itemView.txt_upvote!!
        var downvote = itemView.txt_downvote!!
        var name = itemView.txt_name!!
        var more = itemView.img_more!!
        var close = itemView.img_close!!
        var delete = itemView.txt_delete!!
        var moreLayout = itemView.moreLayout!!
        var reportAbuse = itemView.txt_report_abuse!!
        var play = itemView.img_play!!
        var share = itemView.txt_share!!


    }


}