package com.we.beyond.ui.leaderBoard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.we.beyond.adapter.LeaderBoardCriteriaAdapter
import com.we.beyond.adapter.LeaderBoardProfileAdapter
import com.we.beyond.Interface.OnLeaderBoardSelectedListener
import com.we.beyond.R
import com.we.beyond.model.LeaderBoardPojo
import com.we.beyond.presenter.leaderBoard.LeaderBoardImpl
import com.we.beyond.presenter.leaderBoard.LeaderBoardPresenter
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_leader_board.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/** This Activity show the leader board from today, last 7 days, 30 days and all time
 * on all categories such as submitted Issues, Issue Resolved, Created Gatherings, Published Activities  */
class LeaderBoardActivity : AppCompatActivity(), LeaderBoardPresenter.ILeaderBoardView {

    /** initialize respected implementors */
    var leaderBoardPresenter: LeaderBoardImpl? = null
    var context: Context = this
    var leaderBoardData: LeaderBoardPojo? = null

    /** init image view */
    var back: ImageView? = null
    var profilePic: CircleImageView? = null

    /** init text view */
    var profileName: TextView? = null
    var leaderBoardCategories: TextView? = null
    var noContent : TextView?=null

    /** init recycler view */
    var daysRecycler: RecyclerView? = null

    /** init view pager */
    var pager: ViewPager? = null
    var profilePager: ViewPager? = null

    /** init tab layout */
    var tabs: TabLayout? = null
    var profileTab: TabLayout? = null

    /** init relative layout */
    var noContentLayout : RelativeLayout?=null
    var noContentImageLayout : RelativeLayout?=null

    /** init adapter */
    var leaderBoardAdapter: LeaderBoardCriteriaAdapter? = null
    var leaderBoardProfileAdapter  : LeaderBoardProfileAdapter?=null

    /** initialize array list */
    var leaderBoardCriteria: ArrayList<String>? = null

    var linearLayoutManager: LinearLayoutManager? = null

    /** initialize fragments */
    var leaderBoardSubmittedIssueFragment: LeaderBoardSubmittedIssueFragment? = null
    var leaderBoardResolvedIssueFragment: LeaderBoardResolvedIssueFragment? = null
    var leaderBardCreatedGatheringFragment: LeaderBoardCreatedGatheringFragment? = null
    var leaderBoardPostArticlesFragment: LeaderBoardPostArticlesFragment? = null

    /** initialize strings */
    var currentDate: String = ""
    var startDate: String = ""
    var endDate: String = ""
    var leaderBoardId: String = ""
    var currentPosition = 0

    /** initialize interface */
    var onLeaderBoardSelectedListener: OnLeaderBoardSelectedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)

        /** assign listener to this activity */
        ((LeaderBoardActivity())).onLeaderBoardSelectedListener


        /** fragment creation */
        leaderBoardSubmittedIssueFragment = LeaderBoardSubmittedIssueFragment()
        leaderBoardResolvedIssueFragment = LeaderBoardResolvedIssueFragment()
        leaderBardCreatedGatheringFragment = LeaderBoardCreatedGatheringFragment()
        leaderBoardPostArticlesFragment = LeaderBoardPostArticlesFragment()


        /** array initialization */
        leaderBoardCriteria = ArrayList()
        leaderBoardCriteria!!.add("Today's")
        leaderBoardCriteria!!.add("Last 7 Days")
        leaderBoardCriteria!!.add("Last 30 Days")
        leaderBoardCriteria!!.add("All Time")

        /** initialize implementation */
        leaderBoardPresenter = LeaderBoardImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()

        /** set leader board category adapter */
        setLeaderBoardCriteria()

        /** initialize onclick listener */
        initWithListener()

        /** It adds fragments and set it to view pager */
        setupViewPager(pager)

        /** Set tittle to tabs */
        tabs!!.setupWithViewPager(pager)
        tabs!!.getTabAt(0)!!.text = "Submitted Issues"
        tabs!!.getTabAt(1)!!.text = "Issues Resolved"
        tabs!!.getTabAt(2)!!.text = "Created Gatherings"
        tabs!!.getTabAt(3)!!.text = "Published Articles"


    }


    /** It adds fragments and set it to view pager */
    private fun setupViewPager(pager: ViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(leaderBoardSubmittedIssueFragment!!, "Submitted Issues")
        adapter.addFragment(leaderBoardResolvedIssueFragment!!, "Issues Resolved")
        adapter.addFragment(leaderBardCreatedGatheringFragment!!, "Created Gatherings")
        adapter.addFragment(leaderBoardPostArticlesFragment!!, "Published Articles")
        pager!!.adapter = adapter

    }

    /** It check the leader board data,
     * if it is empty, it will show no content layout and hide all profile tabs and view pager
     * else it will hide no content layout and show all profile tabs and view pager
     * and set leader board profile adapter depends on pager item condition
     */
    override fun setLeaderBoardAdapter(leaderBoardDetails: LeaderBoardPojo) {
        println("leader board data $leaderBoardDetails")

        if (leaderBoardDetails != null && leaderBoardDetails.data.dataList.isNotEmpty()) {
            leaderBoardData = leaderBoardDetails
        }

        if(leaderBoardDetails.data.topList.isEmpty() && leaderBoardDetails.data.dataList.isEmpty())
        {
            noContentLayout!!.visibility = View.VISIBLE
            noContentImageLayout!!.visibility = View.VISIBLE
            profileTab!!.visibility = View.GONE
            profilePager!!.visibility = View.GONE


        }
        else{
            noContentLayout!!.visibility = View.GONE
            noContentImageLayout!!.visibility = View.GONE
            profileTab!!.visibility = View.VISIBLE
            profilePager!!.visibility = View.VISIBLE

        }
        try {

            if (pager!!.currentItem == 0) {

                try {
                    onLeaderBoardSelectedListener =
                        leaderBoardSubmittedIssueFragment as OnLeaderBoardSelectedListener


                        leaderBoardProfileAdapter = LeaderBoardProfileAdapter(leaderBoardDetails.data.topList,"Most Issues Submitted", context)
                        profilePager!!.setPageTransformer(true, ViewPagerStack())
                        profilePager!!.adapter = leaderBoardProfileAdapter
                        profileTab!!.setupWithViewPager(profilePager!!, true)



                } catch (e: Exception) {
                    e.printStackTrace()

                }

                onLeaderBoardSelectedListener!!.onSelected(leaderBoardDetails)

            } else if (pager!!.currentItem == 1) {

                println("resolved issue")

                try {
                    onLeaderBoardSelectedListener =
                        leaderBoardResolvedIssueFragment as OnLeaderBoardSelectedListener


                        leaderBoardProfileAdapter = LeaderBoardProfileAdapter(leaderBoardDetails.data.topList,"Most Issues Resolved", context)
                        profilePager!!.setPageTransformer(true, ViewPagerStack())
                        profilePager!!.adapter = leaderBoardProfileAdapter
                        profileTab!!.setupWithViewPager(profilePager!!, true)



                } catch (e: Exception) {
                    e.printStackTrace()

                }
                onLeaderBoardSelectedListener!!.onSelected(leaderBoardDetails)
            } else if (pager!!.currentItem == 2) {

                try {
                    onLeaderBoardSelectedListener =
                        leaderBardCreatedGatheringFragment as OnLeaderBoardSelectedListener

                        leaderBoardProfileAdapter = LeaderBoardProfileAdapter(leaderBoardDetails.data.topList,"Most Gathering Created", context)
                        profilePager!!.setPageTransformer(true, ViewPagerStack())
                        profilePager!!.adapter = leaderBoardProfileAdapter
                        profileTab!!.setupWithViewPager(profilePager!!, true)



                } catch (e:Exception) {
                    e.printStackTrace()

                }
                onLeaderBoardSelectedListener!!.onSelected(leaderBoardDetails)
            } else if (pager!!.currentItem == 3) {

                try {
                    onLeaderBoardSelectedListener =
                        leaderBoardPostArticlesFragment as OnLeaderBoardSelectedListener


                        leaderBoardProfileAdapter = LeaderBoardProfileAdapter(leaderBoardDetails.data.topList,"Most Articles Posted", context)
                        profilePager!!.setPageTransformer(true, ViewPagerStack())
                        profilePager!!.adapter = leaderBoardProfileAdapter
                        profileTab!!.setupWithViewPager(profilePager!!, true)



                } catch (e: Exception) {
                    e.printStackTrace()

                }
                onLeaderBoardSelectedListener!!.onSelected(leaderBoardDetails)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** get date and time from selection and convert to server formatted date and time
     * and calls postDataToServerWithCriteria()
     */
    fun getDateAndTime() {
        try {
            println("gathering id $leaderBoardId")
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            when (leaderBoardId) {

                "Today's" -> {
                    val d = Date()
                    var dayOfTheWeek = sdf.format(d)

                    println("date of week $dayOfTheWeek")

                    //today
                    startDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeek + " 00:00:00")
                    endDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeek + " 23:59:59")

                    println("start date today  $startDate $endDate")

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerWithCriteria(startDate, endDate)
                        }
                    }
                    catch (e : Exception)
                    {
                        e.printStackTrace()
                    }

                }
                "Last 7 Days" -> {

                    //last 7 days
                    val calendar = Calendar.getInstance()
                    val today = calendar.time

                    calendar.add(Calendar.DAY_OF_YEAR, -8)
                    val week = calendar.time

                    var dayOfTheWeek = sdf.format(week)
                    var todaysDate = sdf.format(today)

                    println("date of week $dayOfTheWeek")

                    startDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeek + " 23:59:59")
                    endDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(todaysDate + " 23:59:59")
                    println("start date last 7 days $startDate $endDate")

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerWithCriteria(startDate, endDate)
                        }
                    }
                    catch (e : Exception)
                    {
                        e.printStackTrace()
                    }


                }

                "Last 30 Days" -> {
                    //week end
                    val calendarNextWeekStart = Calendar.getInstance()
                    calendarNextWeekStart.time = Date()
                    calendarNextWeekStart.add(Calendar.DAY_OF_YEAR, -31)
                    val weekStart = calendarNextWeekStart.time

                    var dayOfTheWeekStart = sdf.format(weekStart)

                    println("date of week $dayOfTheWeekStart")

                    startDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeekStart + " 23:59:59")

                    println("week end $startDate")


                    val calendarNextWeekEnd = Calendar.getInstance()
                    val weekEnd = calendarNextWeekEnd.time


                    var dayOfTheWeekEnd = sdf.format(weekEnd)

                    println("date of week $dayOfTheWeekEnd")

                    endDate =
                        ConstantMethods.convertDateStringToServerDateTodayFull(dayOfTheWeekEnd + " 23:59:59")

                    println("week end $endDate")

                    println("ween end $startDate $endDate")

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerWithCriteria(startDate, endDate)
                        }
                    }
                    catch (e : Exception)
                    {
                        e.printStackTrace()
                    }

                }
                "All Time" -> {
                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerWithoutCriteria()
                        }
                    }
                    catch (e : Exception)
                    {
                        e.printStackTrace()
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    /** It calls onLeaderBoardWithoutCriteria function of leader board presenter */
    private fun postDataToServerWithoutCriteria() {


        try {

                if (ConstantMethods.checkForInternetConnection(context)) {
                    if (pager!!.currentItem == 0) {
                        println("submitted issue")

                        leaderBoardPresenter!!.onLeaderBoardWithoutCriteria(
                            this,
                            "submittedIssue"

                        )
                    } else if (pager!!.currentItem == 1) {
                        println("resolved issue")
                        leaderBoardPresenter!!.onLeaderBoardWithoutCriteria(
                            this,
                            "resolvedIssue"

                        )
                    } else if (pager!!.currentItem == 2) {
                        leaderBoardPresenter!!.onLeaderBoardWithoutCriteria(
                            this,
                            "createdGathering"
                        )
                    } else if (pager!!.currentItem == 3) {
                        leaderBoardPresenter!!.onLeaderBoardWithoutCriteria(
                            this,
                            "postArticle"

                        )
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes below input and send to onLeaderBoard function of leader board presenter */
    private fun postDataToServerWithCriteria(startDate: String, endDate: String) {
        try {

                if (ConstantMethods.checkForInternetConnection(context)) {

                    if (pager!!.currentItem == 0) {
                        println("submitted issue type")

                        leaderBoardPresenter!!.onLeaderBoard(
                            this,
                            "submittedIssue",
                            startDate,
                            endDate
                        )

                    } else if (pager!!.currentItem == 1) {

                        println("resolved issue type")


                        leaderBoardPresenter!!.onLeaderBoard(
                            this,
                            "resolvedIssue",
                            startDate,
                            endDate

                        )
                    } else if (pager!!.currentItem == 2) {
                        println("Created gathering type")

                        leaderBoardPresenter!!.onLeaderBoard(
                            this,
                            "createdGathering",
                            startDate,
                            endDate

                        )
                    } else if (pager!!.currentItem == 3) {

                        println("articles type")


                        leaderBoardPresenter!!.onLeaderBoard(
                            this,
                            "postArticle",
                            startDate,
                            endDate

                        )
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It sets the leader board adapter */
    private fun setLeaderBoardCriteria() {
        leaderBoardAdapter = LeaderBoardCriteriaAdapter(this, leaderBoardCriteria!!)
        leaderBoardTimeRecycler!!.adapter = leaderBoardAdapter
    }

    /** ui initialization */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        profilePic = findViewById(R.id.img_profile_pic)


        /** ids of text view */
       /* profileName = findViewById(R.id.txt_profile_name)
        profileName!!.typeface = ConstantFonts.raleway_medium*/

        /* leaderBoardCategories = findViewById(R.id.txt_leaderBoard_categories)
         leaderBoardCategories!!.typeface = ConstantFonts.raleway_medium*/

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of recycler view */
        daysRecycler = findViewById(R.id.leaderBoardTimeRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        daysRecycler!!.setHasFixedSize(true)
        daysRecycler!!.layoutManager = linearLayoutManager

        /** ids of tab layout */
        tabs = findViewById(R.id.tabs)
        profileTab = findViewById(R.id.tab_layout)

        /** ids of view pager */
        pager = findViewById(R.id.viewPager)
        profilePager = findViewById(R.id.profileViewPager)

        /** ids of relative layout */
        noContentLayout = findViewById(R.id.noContentLayout)
        noContentImageLayout = findViewById(R.id.noContentImageLayout)




    }

    /**This class is used to apply a custom transformation
     * to the page views using animation properties.*/
    class ViewPagerStack : ViewPager.PageTransformer {
        override fun transformPage(page: View, position: Float) {

            if (position >= 0) {
                page.scaleX = (1f - 0.03f * position)
                page.scaleY = 1f

                page.translationX = (-page.width * position)

                page.translationY = (30 * position)
            }

        }
    }

    /** ViewPager objects can animate screen slides automatically */
    class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        var mFragmentList = ArrayList<Fragment>()
        var mFragmentTitleList = ArrayList<String>()

        // this is for fragment tabs
        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }


        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }


        // this counts total number of tabs
        override fun getCount(): Int {
            return mFragmentList.size
        }


    }


    private fun initWithListener() {

        /** It goes back to DashboardActivity when click on it */
        back!!.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            finish()
        }

        /** It call the getDateAndTime() whenever pager changes the page */
        pager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(p0: Int) {

                when (p0) {
                    0 -> {

                        try {
                            getDateAndTime()

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()

                        }

                    }

                    1 -> {

                        try {
                            getDateAndTime()

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()

                        }


                    }

                    2 -> {

                        try {
                            getDateAndTime()

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()

                        }


                    }

                    3 -> {

                        try {
                            getDateAndTime()

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()

                        }

                    }
                }

            }

            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {


            }


        })


        /** It assigns the position to currentPosition variable whenever pager changes the page */
        profilePager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                currentPosition = position
            }

        })


    }

    /** It goes back to DashboardActivity */
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }


}
