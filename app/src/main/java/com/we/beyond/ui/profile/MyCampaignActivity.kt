package com.we.beyond.ui.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.JsonObject
import com.we.beyond.adapter.MyCampaignAdapter
import com.we.beyond.Interface.OnCheckInListener
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnGoingListener
import com.we.beyond.R
import com.we.beyond.model.*
import com.we.beyond.presenter.chechin.CheckInImpl
import com.we.beyond.presenter.chechin.CheckInPresenter
import com.we.beyond.presenter.going.going.GoingImpl
import com.we.beyond.presenter.going.going.GoingPresenter
import com.we.beyond.presenter.profile.myCampaign.MyCampaignImpl
import com.we.beyond.presenter.profile.myCampaign.MyCampaignPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods

/** It will show all campaign done by user */

class MyCampaignActivity : AppCompatActivity() , MyCampaignPresenter.IMyCampaignView,
    OnDeleteListener ,OnCheckInListener,CheckInPresenter.ICheckInView, GoingPresenter.IGoingView,
    OnGoingListener {

    val context: Context = this

    /** initialize implementors */
    var campaignPresenter: MyCampaignImpl? = null
    var checkInPresenter : CheckInImpl?=null
    var goingPresenter : GoingImpl?=null


    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var upcoming : TextView?=null
    var past : TextView?=null
    var noContent : TextView?=null

    /** init recycler view and its adapter  */
    var campaignRecycler: RecyclerView? = null
    var campaignAdapter: MyCampaignAdapter? = null
    var campaignArray: ArrayList<MyCampaignData>? = null


    /**  for Lazy loading */
    var firstVisibleItem: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading = true
    var previousTotal = 0
    var visibleThreshold = 5
    var page = 1
    var pageNo: Int = 1
    var linearLayoutManager: LinearLayoutManager? = null

    /** pull to refresh layout */
    var pullToRefresh : SwipeRefreshLayout?=null

    companion object {
        val PAGE_SIZE = 5
    }

    var sortByUpcoming = true
    var sortByPast = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_campaign)

        /** It hides keyboard */
        ConstantMethods.hideKeyBoard(this, this)

        /** array initialization */
        campaignArray = ArrayList()
        campaignArray!!.clear()

        /** initialize implementation */
        campaignPresenter = MyCampaignImpl(this)
        checkInPresenter = CheckInImpl(this)
        goingPresenter = GoingImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()


        /** initialize onclick listener */
        initWithListener()


        /** get data to post */
        getDataToPost()


    }


    override fun onSuccess()
    {

    }



    /** It create json object
     * and call postDataToServerOnGoing function
     */
    override fun onGoing(_id: String)
    {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "campaign")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {

                postDataToServerOnGoing(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It create json object
     * and call postDataToServerOnNotGoing function
     */
    override fun onNoGoing(_id: String)
    {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "campaign")
            jsonObject.addProperty("typeId", _id)


            try {
                postDataToServerOnNotGoing(jsonObject)

            } catch (e: Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** It takes the json object as input and send to onGoing function of going presenter */
    private fun postDataToServerOnGoing(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                goingPresenter!!.onGoing(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onGoing function of going presenter */
    private fun postDataToServerOnNotGoing(jsonObject: JsonObject) {

        try {

            try {
                if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                    //ConstantMethods.showProgessDialog(this, "Please Wait...")
                    goingPresenter!!.onGoing(this, jsonObject)
                }

            } catch (e: Exception)
            {
                e.printStackTrace()
            }


        } catch (e: Exception) {
        e.printStackTrace()

        }


    }


    /** It takes the json object as input and send to onCheckIn function of checkIn presenter */
    override fun onCheckIn(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                checkInPresenter!!.onCheckIn(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** It takes id as input and delete campaign data from array and notify to adapter
     */
    override fun setOnDelete(_id: String)
    {
        val deleteGatheringList = ArrayList<MyCampaignData>()
        deleteGatheringList.addAll(campaignArray!!)

        val gatheringId = _id
        for( i in 0 until deleteGatheringList.size)
        {

            if(deleteGatheringList[i]._id == gatheringId)
            {
                deleteGatheringList.remove(deleteGatheringList[i])
                campaignArray!!.clear()
                campaignArray!!.addAll(deleteGatheringList)
                campaignAdapter!!.notifyDataSetChanged()

                break
            }
        }

    }

    /** It will call postDataToServerOnDelete() */
    override fun onDelete(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {

                postDataToServerOnDelete(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onDelete function of campaign presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                campaignPresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception)
        {
            e.printStackTrace()
        }


    }



    /** It checks the sortByUpcoming boolean, if it is true then call postDataToServer() with upcoming type
     * else call postDataToServer() with past type and notify the adapter
     */
    private fun getDataToPost() {

        try {

            if(sortByUpcoming) {
                if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                    //ConstantMethods.showProgessDialog(this, "Please Wait...")
                    postDataToServer(pageNo, PAGE_SIZE, "upcoming")
                    campaignAdapter!!.notifyDataSetChanged()
                }
            }
            else{
                if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                   // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    postDataToServer(pageNo, PAGE_SIZE, "past")
                    campaignAdapter!!.notifyDataSetChanged()
                }
            }



        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It takes the json object as input and send to onMyCampaign function of compaign presenter */
    private fun postDataToServer(pageNo: Int, size: Int, type: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                resetValue()
                campaignPresenter!!.onMyCampaign(this, pageNo, size, type)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onMyCampaignOnLoadMore function of compaign presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int, size: Int, type: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                    //ConstantMethods.showProgessDialog(this, "Please Wait...")
                campaignPresenter!!.onMyCampaignOnLoadMore(this, pageNo, size, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            //finish()
        }

        /** It calls loadMore() when scrolling  */
        campaignRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                visibleItemCount = campaignRecycler!!.getChildCount()
                totalItemCount = linearLayoutManager!!.getItemCount()
                firstVisibleItem = linearLayoutManager!!.findFirstVisibleItemPosition()
                if (loading) {
                    // if(dy>0) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                    //}
                }
                if ((!loading && ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)))) {
                    // End has been reached
                    this@MyCampaignActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                loadMore()
                            }
                        }
                    })
                    // Do something
                    loading = true
                }
                println("loading")
            }
        })

        /** It will changes background and text color of views and call getDataToPost() when click on it */
        upcoming!!.setOnClickListener {
            sortByUpcoming = true
            sortByPast = false

            past!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            past!!.setBackgroundResource(R.drawable.connect_border)


            upcoming!!.setTextColor(ContextCompat.getColor(this, R.color.white))
            upcoming!!.setBackgroundResource(R.drawable.button_fill_border)

            getDataToPost()
        }


        /** It will changes background and text color of views and call getDataToPost() when click on it */
        past!!.setOnClickListener {
            sortByUpcoming = false
            sortByPast = true

            upcoming!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            upcoming!!.setBackgroundResource(R.drawable.connect_border)


            past!!.setTextColor(ContextCompat.getColor(this, R.color.white))
            past!!.setBackgroundResource(R.drawable.button_fill_border)

            getDataToPost()
        }

        /** It calls again getDataToPost function when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            resetValue()
            getDataToPost()
            pullToRefresh!!.isRefreshing = false
        }


    }

    /** It calls postDataToServerOnLoadMore() with below inputs  */
    private fun loadMore() {
        try {

            if(sortByUpcoming) {
                if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                    // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    postDataToServerOnLoadMore(++page, PAGE_SIZE, "upcoming")
                }
            }
            else{
                if (ConstantMethods.checkForInternetConnection(this@MyCampaignActivity)) {
                    // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    postDataToServerOnLoadMore(++page, PAGE_SIZE, "past")
                }
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image view */
        back = findViewById(R.id.img_back)

        /** ids of text view */
        title = findViewById(R.id.txt_my_campaign_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        upcoming = findViewById(R.id.txt_upcoming)
        upcoming!!.typeface = ConstantFonts.raleway_semibold

        past = findViewById(R.id.txt_past)
        past!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium


        /** ids of recycler view */
        campaignRecycler = findViewById(R.id.myCampaignRecycler)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        campaignRecycler!!.setHasFixedSize(true)
        campaignRecycler!!.layoutManager = linearLayoutManager


        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

    }

    /** It checks the campaignListDetails data,
     *  if it is not empty then set data to MyCampaignAdapter
     */
    override fun setMyCampaignAdapter(campaignListDetails: MyCampaignPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (campaignListDetails.data.size == 0) {

          /* if(sortByPast) {
               ConstantMethods.showWarning(
                   this,
                   "",
                   "There are no past campaigns."
               )
           }
            else{
               ConstantMethods.showWarning(
                   this,
                   "",
                   "There are no upcoming campaigns."
               )
           }
*/

            if(sortByPast) {
                /*  ConstantMethods.showWarning(
                      this,
                      "",
                      "There are no past campaigns."
                  )*/

                noContent!!.visibility = View.VISIBLE
                noContent!!.text = "There are no past campaigns."


                try {
                    val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "There are no past campaigns."
//                    sweetAlertDialog.show()
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.setConfirmClickListener {
                        sweetAlertDialog.dismissWithAnimation()

                        //onBackPressed()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else{
                /* ConstantMethods.showWarning(
                     this,
                     "",
                     "There are no upcoming campaigns."
                 )*/


                noContent!!.visibility = View.VISIBLE
                noContent!!.text = "There are no upcoming campaigns."

                try {
                    val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "There are no upcoming campaigns."
//                    sweetAlertDialog.show()
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.setConfirmClickListener {
                        sweetAlertDialog.dismissWithAnimation()

                        //onBackPressed()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        } else {

            noContent!!.visibility = View.GONE

            println("size of near by issue ${campaignListDetails.data.size}")

            campaignArray!!.addAll(campaignListDetails.data)


            println("near by issue $campaignArray")

            campaignAdapter = MyCampaignAdapter(this, campaignArray!!)
            campaignRecycler!!.adapter = campaignAdapter

        }


    }

    /** It checks the campaignListDetails data,
     *  if it is not empty then notify to CampaignAdapter
     */
    override fun setMyCampaignOnLoadMore(campaignListDetails: MyCampaignPojo) {

        if (campaignListDetails.data.size > 0) {
            println("size of near by issue ${campaignListDetails.data.size}")

            campaignArray!!.addAll(campaignListDetails.data)

            println("near by issue $campaignArray")

            campaignAdapter!!.notifyDataSetChanged()
        }


    }

    /** It is used to delete campaign data and notify to adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == 201)

        {

            val isEdit = data!!.getBooleanExtra("isEdit",false)
            if(isEdit)
            {
                getDataToPost()
            }
            val deleteGatheringList = ArrayList<MyCampaignData>()
            deleteGatheringList.addAll(campaignArray!!)

            val gatheringId = data.getStringExtra("campaignId")
            for( i in 0 until deleteGatheringList.size)
            {

                if(deleteGatheringList[i]._id == gatheringId)
                {
                    deleteGatheringList.remove(deleteGatheringList[i])
                    campaignArray!!.clear()
                    campaignArray!!.addAll(deleteGatheringList)
                    campaignAdapter!!.notifyDataSetChanged()

                    break
                }
            }

            if(campaignArray!!.size == 0)
            {
                if(sortByPast) {
                  /*  ConstantMethods.showWarning(
                        this,
                        "",
                        "There are no past campaigns."
                    )*/

                    noContent!!.visibility = View.VISIBLE
                    noContent!!.text = "There are no past campaigns."

                    try {
                        val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "There are no past campaigns."
//                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()

                            //onBackPressed()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else{
                   /* ConstantMethods.showWarning(
                        this,
                        "",
                        "There are no upcoming campaigns."
                    )*/

                    noContent!!.visibility = View.VISIBLE
                    noContent!!.text = "There are no upcoming campaigns."

                    try {
                        val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "There are no upcoming campaigns."
//                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()

                            //onBackPressed()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }

    }


    /** reset all values  */
    fun resetValue() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        loading = true
        previousTotal = 0
        visibleThreshold = 5
        page = 1
        pageNo = 1
        campaignArray!!.clear()
    }
    /** It go back to the previous activity */
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        // finish()
    }
}
