package com.we.beyond.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.we.beyond.R
import com.we.beyond.ui.campaign.campaignDetails.CampaignDetailsActivity
import com.we.beyond.ui.connect.connectIssue.ConnectDetailsActivity
import com.we.beyond.ui.dashboard.CategoriesActivity
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.ui.gathering.gathering.GatheringDetailsActivity
import com.we.beyond.ui.issues.nearByIssue.NearByIssueDetailsActivity
import com.we.beyond.ui.login.LoginActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP

/**
 * this activity is the first screen of app
 */
class SplashActivity : AppCompatActivity()
{

    /** init text view */
    var weLogo : TextView?=null
    var detailTitle : TextView?=null
    var detailSubTitle : TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_splash)


        /**
         * ui initialization
         * setting fonts
         */
        weLogo = findViewById(R.id.img_we_logo)
        weLogo!!.typeface = ConstantFonts.abys_regular

        detailTitle = findViewById(R.id.txt_splash_details_title)
        detailTitle!!.typeface = ConstantFonts.raleway_semibold

        detailSubTitle = findViewById(R.id.txt_splash_details_sub_title)
        detailSubTitle!!.typeface = ConstantFonts.raleway_semibold


        try {
            /** handler for delay to hold the splash screen */
            Handler().postDelayed({ setScreen() }, 3000.toLong())
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }


    /**
     * this function start the  activity  with respective type and type id
     */
    private fun setScreen() {
        try {
            val isLogin: Boolean =
                EasySP.init(this@SplashActivity).getBoolean(ConstantEasySP.SP_IS_LOGIN,false)
              val isSelected : Boolean =  EasySP.init(this@SplashActivity).getBoolean(ConstantEasySP.SP_IS_CATEGORY_SELECTED,false)

            val type = intent.getStringExtra("type")
            val typeId = intent.getStringExtra("typeId")
            val issue = intent.getStringExtra("issue")
            val gathering = intent.getStringExtra("gathering")
            val connect = intent.getStringExtra("connect")
           /* val typeId = intent.getStringExtra(Constants.COMMENT_ID)
            val issue = intent.getStringExtra("issueId")
            val gathering = intent.getStringExtra("gatheringId")
            val connect = intent.getStringExtra("connectCategoryId")*/

            if(type !=null) {
                println("type $type type id $typeId")

                    when (type) {
                        "issue" -> {
                            val intent = Intent(this, NearByIssueDetailsActivity::class.java)
                            intent.putExtra("issueId", typeId)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()


                        }

                        "resolution" -> {
                            val intent = Intent(this, NearByIssueDetailsActivity::class.java)
                            intent.putExtra("issueId", issue)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                        }

                        "gathering" -> {

                            val intent = Intent(this, GatheringDetailsActivity::class.java)
                            intent.putExtra("gatheringId", typeId)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                        }


                        "issue-campaign" -> {
                            val intent = Intent(this, CampaignDetailsActivity::class.java)
                            intent.putExtra("campaignId", typeId)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                        }


                        "gathering-campaign" -> {
                            val intent = Intent(this, CampaignDetailsActivity::class.java)
                            intent.putExtra("campaignId", typeId)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                        }

                        "campaign" -> {
                            val intent = Intent(this, CampaignDetailsActivity::class.java)
                            intent.putExtra("campaignId", typeId)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                        }


                        "connect" -> {
                            val intent = Intent(this, ConnectDetailsActivity::class.java)
                            intent.putExtra("connectCategoryId", typeId)
                            intent.putExtra("notification", true)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                        }


                        "comment" -> {
                            if (issue != null) {
                                //val intent = Intent(context, CommentActivity::class.java)
                                val intent = Intent(this, NearByIssueDetailsActivity::class.java)
                                intent.putExtra(Constants.COMMENT_ID, typeId)
                                intent.putExtra("issueId", issue)
                                intent.putExtra("notification", true)
                                startActivity(intent)
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                                finish()


                            } else if (connect != null) {
                                //val intent = Intent(context, ConnectCommentActivity::class.java)
                                val intent = Intent(this, ConnectDetailsActivity::class.java)
                                intent.putExtra("connectCategoryId", connect)
                                intent.putExtra(Constants.COMMENT_ID, typeId)
                                intent.putExtra("notification", true)
                                startActivity(intent)
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                                finish()

                            } else if (gathering != null) {
                                //val intent = Intent(context, ConnectCommentActivity::class.java)
                                val intent = Intent(this, GatheringDetailsActivity::class.java)
                                intent.putExtra("gatheringId", gathering)
                                intent.putExtra(Constants.COMMENT_ID, typeId)
                                intent.putExtra("notification", true)
                                startActivity(intent)
                                overridePendingTransition(
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left
                                )
                                finish()

                            }

                        }


                        else -> {

                            val intent = Intent(this, DashboardActivity::class.java)
                            intent.putExtra("gatheringId", gathering)
                            intent.putExtra("connectCategoryId", connect)
                            intent.putExtra(Constants.COMMENT_ID, typeId)
                            intent.putExtra("notification", true)
                            intent.putExtra("issueId", issue)
                            intent.putExtra("type", type)
                            startActivity(intent)
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                            finish()
                        }
                    }

            }
            else {
                if (isLogin) {

                    if (isSelected) {
                        val intent = Intent(this@SplashActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                        println("is selected $isSelected")
                    } else {
                        val intent = Intent(this@SplashActivity, CategoriesActivity::class.java)
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                        println("is login $isLogin")
                    }

                } else {

                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    finish()

                    println("else")

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
