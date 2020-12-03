package com.we.beyond.ui.badges

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.we.beyond.adapter.BadgesAdapter
import com.we.beyond.Interface.OnBadgesDetailsListener
import com.we.beyond.R
import com.we.beyond.model.Badges
import com.we.beyond.model.BadgesPojo
import com.we.beyond.presenter.badges.getBadges.BadgesImpl
import com.we.beyond.presenter.badges.getBadges.BadgesPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import de.hdodenhof.circleimageview.CircleImageView
/** It will show the user profile with their badges */
class BadgesActivity : AppCompatActivity(), BadgesPresenter.IBadgesView, OnBadgesDetailsListener {

    /** initialize implementors */
    var badgesPresenter: BadgesImpl? = null

    /** initialize model */
    var badgesData: BadgesPojo? = null

    /** init image view */
    var back: ImageView? = null
    var profilePic: CircleImageView? = null

    /** init text view */
    var profileName: TextView? = null
    var title: TextView? = null
    var type: TextView? = null
    var issuePostedTitle: TextView? = null
    var issuePostedCount: TextView? = null
    var issueResolvedTitle: TextView? = null
    var issueResolvedCount: TextView? = null
    var badgesTitle: TextView? = null
    var badgesCount: TextView? = null
    var badgeDate: TextView? = null
    var badgeTitle: TextView? = null
    var badgeDescription: TextView? = null

    /** init progress bar */
    var progressBar: ProgressBar? = null

    /** init recycler view */
    var badgesRecycler: RecyclerView? = null

    /** init adapter */
    var badgesAdapter: BadgesAdapter? = null

    var linearLayoutManager: LinearLayoutManager? = null

    /** init array list */
    var badgesArray: ArrayList<Badges>? = null

    /** init relative layout */
    var badgeLayout: RelativeLayout? = null

    /** init button */
    var ok: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        /** array initialization */
        badgesArray = ArrayList()
        badgesArray!!.clear()

        /** initialize implementation */
        badgesPresenter = BadgesImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()


        /** Get stored data using intent and assign it and call functions */
        var userId = intent.getStringExtra(Constants.USER_ID)
        if (userId != null && userId.isNotEmpty()) {
            getDataToPostOtherUser(userId)
        } else {
            getDataToPost()
        }
    }


    /** It will open animate the badge layout and show badge details*/
    override fun onBadgesDetails(position: Int) {
        badgeLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))
        badgeLayout!!.visibility = View.GONE

        if (badgesData != null) {

            if (badgesData!!.data.badges[position].isSelected) {
                badgeDate!!.visibility = View.VISIBLE
                badgeDate!!.text =
                    "Received Date : ${ConstantMethods.convertStringToDateStringFull(
                        badgesData!!.data.badges[position].date
                    )}"
                ConstantMethods.showToast(this@BadgesActivity, "Received Date : ${ConstantMethods.convertStringToDateStringFull(
                    badgesData!!.data.badges[position].date
                )}")
            } else {
                badgeDate!!.visibility = View.GONE
                ConstantMethods.showToast(this@BadgesActivity, badgesData!!.data.badges[position].description)
            }

            badgeTitle!!.text = badgesData!!.data.badges[position].name
            badgeDescription!!.text =
                "Description : ${badgesData!!.data.badges[position].description}"

        }


    }

    /** It will call onBadges function of badge presenter */
    private fun getDataToPost() {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                badgesPresenter!!.onBadges(this)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It will call onBadgesUser function of badge presenter */
    private fun getDataToPostOtherUser(userId: String) {
        try {

            if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                badgesPresenter!!.onBadgesUser(this, userId)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It will set badge details to respective views */
    override fun setBadgesAdapter(badgesDetails: BadgesPojo) {
        try {
            if (badgesDetails != null) {

                if (badgesDetails.data.profileUrl != null && badgesDetails.data.profileUrl.isNotEmpty()) {
                    progressBar!!.visibility = View.VISIBLE
                    Glide
                        .with(this)
                        .load(badgesDetails.data.profileUrl)
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
                                progressBar!!.visibility = View.GONE

                                return false
                            }


                        })
                        .into(profilePic!!)
                }
                if (badgesDetails.data.userLoginType.userType.equals(
                        "individual",
                        ignoreCase = true
                    )
                ) {
                    profileName!!.text =
                        badgesDetails.data.firstName + " ${badgesDetails.data.lastName}"

                } else {
                    profileName!!.text = badgesDetails.data.organizationName

                }

                if (badgesDetails.data.userLoginType.name != null && badgesDetails.data.userLoginType.name.isNotEmpty()) {
                    type!!.text = badgesDetails.data.userLoginType.name
                }

                if (badgesDetails.data.summary != null) {
                    issuePostedCount!!.text = badgesDetails.data.summary.issues.toString()
                    issueResolvedCount!!.text = badgesDetails.data.summary.resolutions.toString()
                    badgesCount!!.text = badgesDetails.data.summary.badges.toString()


                }

                if (badgesDetails.data != null) {

                    for (i in 0 until badgesDetails.data.badges.size) {
                        for (j in 0 until badgesDetails.data.userBadges.size) {

                            if (badgesDetails.data.userBadges[j].badge == badgesDetails.data.badges[i]._id) {
                                badgesDetails.data.badges[i].isSelected = true
                                badgesDetails.data.badges[i].date =
                                    badgesDetails.data.userBadges[j].createdAt

                                badgesData = badgesDetails

                            }

                        }

                    }

                    badgesArray!!.addAll(badgesDetails.data.badges)
                    badgesAdapter = BadgesAdapter(this, badgesDetails.data, badgesArray!!)
                    badgesRecycler!!.adapter = badgesAdapter
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity when click on it */
        back!!.setOnClickListener {

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        /** It opens badge layout with animation */
        ok!!.setOnClickListener {
            badgeLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_down))
            badgeLayout!!.visibility = View.GONE

        }


    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of text view */
        profileName = findViewById(R.id.txt_Name)
        profileName!!.typeface = ConstantFonts.raleway_medium

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        type = findViewById(R.id.txt_type)
        type!!.typeface = ConstantFonts.raleway_medium

        issuePostedTitle = findViewById(R.id.txt_issue_posted_title)
        issuePostedTitle!!.typeface = ConstantFonts.raleway_medium

        issuePostedCount = findViewById(R.id.txt_issue_posted_count)
        issuePostedCount!!.typeface = ConstantFonts.raleway_medium

        issueResolvedTitle = findViewById(R.id.txt_issue_resolved_title)
        issueResolvedTitle!!.typeface = ConstantFonts.raleway_medium

        issueResolvedCount = findViewById(R.id.txt_issue_resolved_count)
        issueResolvedCount!!.typeface = ConstantFonts.raleway_medium

        badgesTitle = findViewById(R.id.txt_badges_title)
        badgesTitle!!.typeface = ConstantFonts.raleway_medium

        badgesCount = findViewById(R.id.txt_badges_count)
        badgesCount!!.typeface = ConstantFonts.raleway_medium

        badgeTitle = findViewById(R.id.txt_badge_title)
        badgeTitle!!.typeface = ConstantFonts.raleway_semibold

        badgeDescription = findViewById(R.id.txt_badge_description)
        badgeDescription!!.typeface = ConstantFonts.raleway_medium

        badgeDate = findViewById(R.id.txt_received_date)
        badgeDate!!.typeface = ConstantFonts.raleway_medium


        /** ids of image view */
        back = findViewById(R.id.img_back)
        profilePic = findViewById(R.id.img_profile_pic)

        /** ids of progress bar */
        progressBar = findViewById(R.id.progressBar)

        /** ids of recycler view */
        badgesRecycler = findViewById(R.id.badgesRecycler)
        badgesRecycler!!.layoutManager = GridLayoutManager(this, 3)

        /** ids of relative layout */
        badgeLayout = findViewById(R.id.badgeDetailsLayout)

        /** ids of button */
        ok = findViewById(R.id.btn_ok)
        ok!!.typeface = ConstantFonts.raleway_semibold

    }

    /** It goes back to previous fragment or activity when click on it */
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

}
