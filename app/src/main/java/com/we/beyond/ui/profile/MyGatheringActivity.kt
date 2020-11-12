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
import com.we.beyond.adapter.MyGatheringAdapter
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnSubscribeListener
import com.we.beyond.R
import com.we.beyond.model.GatheringData
import com.we.beyond.model.GatheringListPojo
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.profile.myGathering.MyGatheringImpl
import com.we.beyond.presenter.profile.myGathering.MyGatheringPresenter
import com.we.beyond.presenter.subscribe.SubscribeImpl
import com.we.beyond.presenter.subscribe.SubscribePresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods

/** It will show all gathering which created by user */

class MyGatheringActivity : AppCompatActivity() , MyGatheringPresenter.IMyGatheringView,
    LikePresenter.ILikeView,
    OnLikeDislikeListener, OnDeleteListener,OnSubscribeListener ,SubscribePresenter.ISubscribeView{


    val context: Context = this

    /** initialize implementors */
    var gatheringPresenter: MyGatheringImpl? = null
    var likePresenter: LikeImpl? = null
    var subscribePresenter : SubscribeImpl?=null

    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var upcoming : TextView?=null
    var past : TextView?=null
    var noContent : TextView?=null

    /** init recycler view and adapter and array list  */
    var gatheringRecycler: RecyclerView? = null
    var gatheringAdapter: MyGatheringAdapter? = null
    var gatheringArray: ArrayList<GatheringData>? = null


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
        setContentView(R.layout.activity_my_gathering)

        /** It hides keyboard */
        ConstantMethods.hideKeyBoard(this, this)

        /** array initialization */
        gatheringArray = ArrayList()
        gatheringArray!!.clear()

        /** initialize implementation */
        gatheringPresenter = MyGatheringImpl(this)
        likePresenter = LikeImpl(this)
        subscribePresenter = SubscribeImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get data to post */
        getDataToPost()


    }

    /** It create json object
     * and call postDataToServerOnSubscribe function
     */
    override fun onSubscribe(_id: String)
    {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "gathering")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {

                postDataToServerOnSubscribe(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onSubscribe function of subscribe presenter */
    private fun postDataToServerOnSubscribe(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                subscribePresenter!!.onSubscribe(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes id as input and delete connect data from array and notify to adapter
     */
    override fun setOnDelete(_id: String)
    {
        val deleteGatheringList = ArrayList<GatheringData>()
        deleteGatheringList.addAll(gatheringArray!!)

        val gatheringId = _id
        for( i in 0 until deleteGatheringList.size)
        {

            if(deleteGatheringList[i]._id == gatheringId)
            {
                deleteGatheringList.remove(deleteGatheringList[i])
                gatheringArray!!.clear()
                gatheringArray!!.addAll(deleteGatheringList)
                gatheringAdapter!!.notifyDataSetChanged()

                break
            }
        }

    }

    /** It calls postDataToServerOnDelete function with json object as parameter
     */
    override fun onDelete(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {

                postDataToServerOnDelete(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onDelete function of gathering presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                gatheringPresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception)
        {
            e.printStackTrace()
        }


    }




    override fun summaryType(type: String) {

    }


    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "issue")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {

                postDataToServerOnLike(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onLike function of like presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                likePresenter!!.onLike(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the id as input and call postDataToServerOnDislike function */
    override fun onDislike(_id: String) {

        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "issue")
            jsonObject.addProperty("typeId", _id)


            try {
                postDataToServerOnDislike(jsonObject)

            } catch (e: Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onLike function of like presenter */
    private fun postDataToServerOnDislike(jsonObject: JsonObject) {

        try {
            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = ""
            sweetAlertDialog.contentText = "Do you want to DownVote?"
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()


                try {
                    if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {
                       // ConstantMethods.showProgessDialog(this, "Please Wait...")
                        likePresenter!!.onLike(this, jsonObject)
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


    }

    /** It calls function depends on condition */
    private fun getDataToPost() {

        try {

            if(sortByUpcoming) {
                if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {

                    postDataToServer(pageNo, PAGE_SIZE, "upcoming")
                    gatheringAdapter!!.notifyDataSetChanged()
                }
            }
            else{
                if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {

                    postDataToServer(pageNo, PAGE_SIZE, "past")
                    gatheringAdapter!!.notifyDataSetChanged()
                }
            }



        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It takes the below input and send to onMyGathering function of gathering presenter */
    private fun postDataToServer(pageNo: Int, size: Int, type: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                resetValue()
                gatheringPresenter!!.onMyGathering(this, pageNo, size, type)
            }

        } catch (e: Exception) {
                e.printStackTrace()
        }
    }

    /** It takes the below input and send to onMyGatheringOnLoadMore function of gathering presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int, size: Int, type: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {
                gatheringPresenter!!.onMyGatheringOnLoadMore(this, pageNo, size, type)
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

        /** Call load more api when scrolling the recycler view */
        gatheringRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                visibleItemCount = gatheringRecycler!!.getChildCount()
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
                    this@MyGatheringActivity.runOnUiThread(object : Runnable {
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
                if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {
                    // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    postDataToServerOnLoadMore(++page, PAGE_SIZE, "upcoming")
                }
            }
            else{
                if (ConstantMethods.checkForInternetConnection(this@MyGatheringActivity)) {
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
        title = findViewById(R.id.txt_my_gathering_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        upcoming = findViewById(R.id.txt_upcoming)
        upcoming!!.typeface = ConstantFonts.raleway_semibold

        past = findViewById(R.id.txt_past)
        past!!.typeface = ConstantFonts.raleway_semibold


        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of recycler view */
        gatheringRecycler = findViewById(R.id.myGatheringRecycler)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        gatheringRecycler!!.setHasFixedSize(true)
        gatheringRecycler!!.layoutManager = linearLayoutManager

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)


    }

    /** It checks the gatheringListDetails data,
     *  if it is not empty then set data to MyGatheringAdapter
     */
    override fun setMyGatheringAdapter(gatheringListDetails: GatheringListPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (gatheringListDetails.data.size == 0) {


            if(sortByPast) {
                /* ConstantMethods.showWarning(
                     this,
                     "",
                     "There are no past gatherings."
                 )*/

                noContent!!.visibility = View.VISIBLE
                noContent!!.text =  "There are no past gatherings."

                try {
                    val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "There are no past gatherings."
//                    sweetAlertDialog.show()
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.setConfirmClickListener {
                        sweetAlertDialog.dismissWithAnimation()

                       // onBackPressed()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else{
                /* ConstantMethods.showWarning(
                     this,
                     "",
                     "There are no upcoming gatherings."
                 )*/

                noContent!!.visibility = View.VISIBLE
                noContent!!.text =  "There are no upcoming gatherings."

                try {
                    val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "There are no upcoming gatherings."
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

            println("size of near by issue ${gatheringListDetails.data.size}")

            gatheringArray!!.addAll(gatheringListDetails.data)


            println("near by issue $gatheringArray")

            gatheringAdapter = MyGatheringAdapter(this, gatheringArray!!)
            gatheringRecycler!!.adapter = gatheringAdapter

        }


    }

    /** It checks the gatheringListDetails data,
     *  if it is not empty then notify to MyGatheringAdapter
     */
    override fun setMyGatheringOnLoadMore(gatheringListDetails: GatheringListPojo) {

        if (gatheringListDetails.data.size > 0) {

            gatheringArray!!.addAll(gatheringListDetails.data)


            gatheringAdapter!!.notifyDataSetChanged()
        }


    }

    /** It is used to delete gathering data and notify to adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == 201)

        {
            val deleteGatheringList = ArrayList<GatheringData>()
            deleteGatheringList.addAll(gatheringArray!!)

            val gatheringId = data!!.getStringExtra("gatheringId")
            for( i in 0 until deleteGatheringList.size)
            {

                if(deleteGatheringList[i]._id == gatheringId)
                {
                    deleteGatheringList.remove(deleteGatheringList[i])
                    gatheringArray!!.clear()
                    gatheringArray!!.addAll(deleteGatheringList)
                    gatheringAdapter!!.notifyDataSetChanged()

                    break
                }
            }

            if(gatheringArray!!.size == 0)
            {
                if(sortByPast) {
                   /* ConstantMethods.showWarning(
                        this,
                        "",
                        "There are no past gatherings."
                    )*/

                    noContent!!.visibility = View.VISIBLE
                    noContent!!.text =  "There are no past gatherings."

                    try {
                        val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "There are no past gatherings."
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
                        "There are no upcoming gatherings."
                    )*/

                    noContent!!.visibility = View.VISIBLE
                    noContent!!.text =   "There are no upcoming gatherings."

                    try {
                        val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
//                        sweetAlertDialog.contentText = "There are no upcoming gatherings."
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()

                           // onBackPressed()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

        }


        else if (requestCode == 200 && resultCode == 503)
        {
            val gatheringId = data!!.getStringExtra("gatheringId")
            val isGathering = data!!.getBooleanExtra("isSubscribe",false)

            val subscribeGatheringList = ArrayList<GatheringData>()
            subscribeGatheringList.addAll(gatheringArray!!)

            for( i in 0 until subscribeGatheringList.size) {
                if (subscribeGatheringList[i]._id == gatheringId) {
                    subscribeGatheringList[i].isSubscribe = isGathering
                    gatheringArray!!.clear()
                    gatheringArray!!.addAll(subscribeGatheringList)
                    gatheringAdapter!!.notifyDataSetChanged()

                    break
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
        gatheringArray!!.clear()
    }

    /** It go back to the previous activity */
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        // finish()
    }
}
