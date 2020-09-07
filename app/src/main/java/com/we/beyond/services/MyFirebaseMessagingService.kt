package com.we.beyond.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibrationEffect
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.we.beyond.R
import com.we.beyond.ui.CommentActivity
import com.we.beyond.ui.ConnectCommentActivity
import com.we.beyond.ui.campaign.campaignDetails.CampaignDetailsActivity
import com.we.beyond.ui.connect.connectIssue.ConnectDetailsActivity
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.ui.profile.ReportedResolutionDetailsActivity
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService()
{

    var tokenId:String?=null
    var title:String?=null
    var body:String?=null
    var type:String?=null
    var image:String?=null
    var typeId:String?=null
    var issue : String?=null
    var connect : String?=null
    var gathering : String ?=null
    var context:Context=this

    // Sets an ID for the notification, so it can be updated.
    internal var notifyID = 1
    internal var CHANNEL_ID = "my_channel_01"// The id of the channel.
    //CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.
    internal var importance = NotificationManager.IMPORTANCE_HIGH

    val GROUP_KEY="com.we.beyond"
    //use constant ID for notification used as group summary
    val SUMMARY_ID = 0
    val GROUP_KEY_WORK_EMAIL = "com.we.beyond"
    override fun onNewToken(token: String) {


        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    println("getInstanceId failed ${task.exception}")
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                EasySP.init(context).putString("token",token)
                // Log and toast
                //val msg = getString(R.string.app_name, token)
                println(token)
                //Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
            })
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }




    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        println("From: ${remoteMessage.notification}")


        // Check if message contains a data payload.
        remoteMessage.notification.let {
            println("Message data payload: " + remoteMessage.notification)


        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            println("Message Notification Body: ${it.body}")
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


        body = remoteMessage.data["body"]
        type = remoteMessage.data["type"]
        typeId = remoteMessage.data["typeId"]
        title = remoteMessage.data["title"]
        issue = remoteMessage.data["issue"]
        connect = remoteMessage.data["connect"]
        gathering = remoteMessage.data["gathering"]

        sendNotification(body,type,typeId,title,issue,connect,gathering)

    }

    private fun sendNotification(
        body: String?,
        type: String?,
        typeId: String?,
        title : String?,
        issue : String?,
        connect : String?,
        gathering : String?

    ) {

        println("send notification type $type typeId $typeId issue $issue resolution ")

        val ibstyle = NotificationCompat.InboxStyle()


        EasySP.init(context).put("notification", true)
        when (type) {
            "issue" -> {
                val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                intent.putExtra("issueId", typeId)
                intent.putExtra("notification", true)
                setIntent(intent,title!!)

            }

            "resolution" -> {
                val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                intent.putExtra("issueId", issue)
                intent.putExtra("notification", true)
                setIntent(intent,title!!)
            }

            "gathering" -> {
                val intent = Intent(context, GatheringDetailsActivity::class.java)
                intent.putExtra("gatheringId", typeId)
                intent.putExtra("notification", true)
                 setIntent(intent,title!!)
            }


            "issue-campaign" -> {
                val intent = Intent(context, CampaignDetailsActivity::class.java)
                intent.putExtra("campaignId", typeId)
                intent.putExtra("notification", true)
                setIntent(intent,title!!)
            }


            "gathering-campaign" -> {
                val intent = Intent(context, CampaignDetailsActivity::class.java)
                intent.putExtra("campaignId", typeId)
                intent.putExtra("notification", true)
                setIntent(intent,title!!)
            }

            "campaign" -> {
                val intent = Intent(context, CampaignDetailsActivity::class.java)
                intent.putExtra("campaignId", typeId)
                intent.putExtra("notification", true)
                setIntent(intent,title!!)
            }


            "connect" -> {
                val intent = Intent(context, ConnectDetailsActivity::class.java)
                intent.putExtra("connectCategoryId", typeId)
                intent.putExtra("notification", true)
                setIntent(intent,title!!)
            }


            "comment" -> {
                if (issue != null) {
                    //val intent = Intent(context, CommentActivity::class.java)
                    val intent = Intent(context, NearByIssueDetailsActivity::class.java)
                    intent.putExtra(Constants.COMMENT_ID, typeId)
                    intent.putExtra("issueId", issue)
                    intent.putExtra("notification", true)
                    setIntent(intent,title!!)
                }
                else if (connect != null) {
                    //val intent = Intent(context, ConnectCommentActivity::class.java)
                    val intent = Intent(context, ConnectDetailsActivity::class.java)
                    intent.putExtra("connectCategoryId", connect)
                    intent.putExtra(Constants.COMMENT_ID, typeId)
                    intent.putExtra("notification", true)
                    setIntent(intent,title!!)
                }
                else if (gathering != null) {
                    //val intent = Intent(context, ConnectCommentActivity::class.java)
                    val intent = Intent(context, GatheringDetailsActivity::class.java)
                    intent.putExtra("gatheringId", gathering)
                    intent.putExtra(Constants.COMMENT_ID, typeId)
                    intent.putExtra("notification", true)
                    setIntent(intent, title!!)
                }

            }
            else ->{
                val intent = Intent(context, DashboardActivity::class.java)
                intent.putExtra("gatheringId",gathering)
                intent.putExtra("connectCategoryId",connect)
                intent.putExtra(Constants.COMMENT_ID, typeId)
                intent.putExtra("notification", true)
                intent.putExtra("issueId",issue)
                intent.putExtra("campaignId",typeId)
                setIntent(intent,title!!)
            }

        }


    }




    private fun setIntent(intent: Intent,title : String)
    {
        val pIntent = PendingIntent.getActivity(
            this,notifyID, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val newMessageNotification1 = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.playstore)
            .setContentText(title)
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setContentIntent(pIntent)
            .setLights(ContextCompat.getColor(context,R.color.colorPrimary),50,10)
            .setAutoCancel(true)
            .setChannelId(CHANNEL_ID)
            .build()


        val summaryNotification = NotificationCompat.Builder(context, CHANNEL_ID)

            //set content text to support devices running API level < 24
            .setContentText(title)
            .setSmallIcon(R.drawable.playstore)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setVibrate(longArrayOf(1000, 1000,1000,1000,1000))
            .setLights(ContextCompat.getColor(context,R.color.colorPrimary),50,10)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

            //build summary info into InboxStyle template
            .setStyle(
                NotificationCompat.InboxStyle())
            //specify which group this notification belongs to
            .setGroup(GROUP_KEY_WORK_EMAIL)
            //set this notification as the summary for the group
            .setGroupSummary(true)
            .setChannelId(CHANNEL_ID)
            .build()


        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()

            val v=getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500,0))
            }
            else{
                v.vibrate(500)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }



        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val mChannel = NotificationChannel(CHANNEL_ID, " ", importance)
            mChannel.enableVibration(true)
            mChannel.vibrationPattern=longArrayOf(1000, 1000,1000,1000,1000)
            mChannel.lightColor=R.color.colorPrimary
            mChannel.setShowBadge(true)
            mNotificationManager.createNotificationChannel(mChannel)


            //mNotificationManager.createNotificationChannel(mChannel);
        }
        NotificationManagerCompat.from(this).apply {
            notify(100, newMessageNotification1)
            notify(SUMMARY_ID, summaryNotification)
        }

    }

    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return null

        }

    }





    private fun sendRegistrationToServer(token: String)
    {
        EasySP.init(context).putString("token", token)

        tokenId = token
        if (token != null) {
            EasySP.init(context).putString("token", token)
            tokenId = token
        } else {
            EasySP.init(context).putString( "token", token)
            tokenId = token
        }

    }



}