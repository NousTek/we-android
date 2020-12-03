package com.we.beyond.ui.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.JsonObject
import com.we.beyond.adapter.ConnectAdapter
import com.we.beyond.adapter.MyConnectCategoryAdapter
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.R
import com.we.beyond.model.ConnectCategories
import com.we.beyond.model.ConnectList
import com.we.beyond.model.ConnectPojo
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.profile.myConnect.MyConnectImpl
import com.we.beyond.presenter.profile.myConnect.MyConnectPresenter
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP

/** It will show all articles created by user */

class MyConnectActivity : AppCompatActivity() , MyConnectPresenter.IMyConnectIssueView ,
    LikePresenter.ILikeView, OnLikeDislikeListener, OnDeleteListener {

    /** initialize implementors */
    var context: Context = this
    var connectPresenter: MyConnectImpl? = null
    var likePresenter: LikeImpl? = null

    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var noContent : TextView?=null

    /** init recycler view and its adapter  */
    var connectCategoryRecycler: RecyclerView? = null
    var connectRecycler: RecyclerView? = null

    var linearLayoutManager: LinearLayoutManager? = null
    var connectCategoryAdapter: MyConnectCategoryAdapter? = null
    var connectAdapter: ConnectAdapter? = null


    var connectCategoryId: String = ""

    /**  for Lazy loading */
    var firstVisibleItem: Int = 0
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var loading = true
    var previousTotal = 0
    var visibleThreshold = 5
    var page = 1
    var pageNo: Int = 1

    /** pull to refresh layout */
    var pullToRefresh : SwipeRefreshLayout?=null

    companion object {
        val PAGE_SIZE = 5
    }

    /** init array list */
    var connectArray: ArrayList<ConnectList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_connect)

        /** It hides the keyboard  */
        ConstantMethods.hideKeyBoard(this,this)

        /** array initialization */
        connectArray = ArrayList()
        connectArray!!.clear()

        /** initialize implementation */
        connectPresenter = MyConnectImpl(this)
        likePresenter = LikeImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** set connect category adapter */
        setConnectCategory()

        /** initialize onclick listener */
        initWithListener()

        /** get data to post */
        getDataToPost()


    }

    /** It takes id as input and delete connect data from array and notify to adapter
     */
    override fun setOnDelete(_id: String)
    {
        val deleteGatheringList = ArrayList<ConnectList>()
        deleteGatheringList.addAll(connectArray!!)

        val gatheringId = _id
        for( i in 0 until deleteGatheringList.size)
        {

            if(deleteGatheringList[i]._id == gatheringId)
            {
                deleteGatheringList.remove(deleteGatheringList[i])
                connectArray!!.clear()
                connectArray!!.addAll(deleteGatheringList)
                connectAdapter!!.notifyDataSetChanged()

                break
            }
        }

    }

    /** It calls postDataToServerOnDelete function with json object as parameter
     */
    override fun onDelete(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyConnectActivity)) {
                postDataToServerOnDelete(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onDelete function of connect presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this@MyConnectActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception)
        {
            e.printStackTrace()
        }


    }



    /** It calls function depends on condition */
    fun getDataToPost() {
        try {

            if(connectCategoryId != null && connectCategoryId.isNotEmpty()) {
                if (ConstantMethods.checkForInternetConnection(this@MyConnectActivity)) {
                    postDataToServerWithCategoryId(pageNo, connectCategoryId)
                    connectAdapter!!.notifyDataSetChanged()
                }
            }
                else {
                if (ConstantMethods.checkForInternetConnection(this@MyConnectActivity)) {
                    postDataToServer(pageNo)
                    connectAdapter!!.notifyDataSetChanged()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun summaryType(type: String) {

    }


    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "connect")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@MyConnectActivity)) {
                postDataToServerOnLike(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onLike function of like presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
               // ConstantMethods.showProgessDialog(this, "Please Wait...")
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
            jsonObject.addProperty("type", "connect")
            jsonObject.addProperty("typeId", _id)

            try {

                if (ConstantMethods.checkForInternetConnection(context)) {
                    postDataToServerOnDislike(jsonObject)
                }

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
            /*val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = ""
            sweetAlertDialog.contentText = "Do you want to DownVote?"
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()

*/
                try {

                    if (ConstantMethods.checkForInternetConnection(context)) {
                            //ConstantMethods.showProgessDialog(this, "Please Wait...")
                        likePresenter!!.onLike(this, jsonObject)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }


           /* }


            sweetAlertDialog.setCancelClickListener {
                sweetAlertDialog.dismissWithAnimation()
            }
*/

        } catch (e: Exception) {
            e.printStackTrace()

        }


    }

    /** It takes the below input and send to getConnectList function of connect presenter */
    private fun postDataToServer(pageNo: Int) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                   // ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.getConnectList(this, pageNo, PAGE_SIZE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the below input and send to getConnectWithCategoryList function of connect presenter */
    private fun postDataToServerWithCategoryId(pageNo: Int, categoryId: String?) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                   // ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.getConnectWithCategoryList(
                    this,
                    pageNo,
                    PAGE_SIZE,
                    categoryId!!
                )
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the below input and send to getConnectListOnLoadMore function of connect presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                   // ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.getConnectListOnLoadMore(this, pageNo, PAGE_SIZE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the below input and send to getConnectWithCategoryListOnLoadMore function of connect presenter */
    private fun postDataToServerWithCategoryIdOnLoadMore(
        pageNo: Int,
        categoryId: String?
    ) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                    //ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.getConnectWithCategoryListOnLoadMore(
                    this,
                    pageNo,
                    PAGE_SIZE,
                    categoryId!!
                )
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It checks the connect data,
     *  if it is not empty then set ConnectAdapter
     *  else show warning dialog
     */
    override fun setConnectAdapter(connectPojo: ConnectPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (connectPojo.data.size == 0) {
            //ConstantMethods.showWarning(this,"", "There are no published connect.")

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no published articles."


            try {
                val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = ""
                sweetAlertDialog.contentText = "There are no published articles."
//                sweetAlertDialog.show()
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.setConfirmClickListener {
                    sweetAlertDialog.dismissWithAnimation()

                    //onBackPressed()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {

            noContent!!.visibility = View.GONE

            connectArray!!.addAll(connectPojo.data)



            connectAdapter = ConnectAdapter(this, connectArray!!)
            connectRecycler!!.adapter = connectAdapter


        }

    }

    /** It checks the connect data,
     *  if it is not empty then notify to ConnectAdapter
     */
    override fun setConnectLoadMoreAdapter(connectPojo: ConnectPojo) {

        if (connectPojo.data.size > 0) {


            connectArray!!.addAll(connectPojo.data)


            connectAdapter!!.notifyDataSetChanged()
        }


    }

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            /* val intent = Intent(this, DashboardActivity::class.java)
             startActivity(intent)*/
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            //finish()
        }


        /** Call load more api when scrolling the recycler view */
        connectRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = connectRecycler!!.getChildCount()
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
                    this@MyConnectActivity.runOnUiThread(object : Runnable {
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

        /** It calls again getDataToPost function when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            resetValue()
            getDataToPost()
            pullToRefresh!!.isRefreshing = false
        }


    }

    /** It checks the conditions and call functions depends on it for loading data */
    private fun loadMore() {
        try {
            // val categoryId = EasySP.init(this).getString("connectCategoryId")
              if (connectCategoryId != null && connectCategoryId.isNotEmpty()) {
                  if (ConstantMethods.checkForInternetConnection(this@MyConnectActivity)) {

                      postDataToServerWithCategoryIdOnLoadMore(++page, connectCategoryId)
                  }

              }
                 else  {
                if (ConstantMethods.checkForInternetConnection(this@MyConnectActivity)) {

                    postDataToServerOnLoadMore(++page)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls to onRequestConnectCategory function of connect presenter */
    private fun setConnectCategory() {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.onRequestConnectCategory(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It set MyConnectCategoryAdapter */
    override fun setConnectCategoryAdapter(connectCategories: ArrayList<ConnectCategories>) {
        connectCategoryAdapter = MyConnectCategoryAdapter(this, connectCategories)
        connectCategoryRecycler!!.adapter = connectCategoryAdapter
    }

    /** It go to previous activity */
    override fun goToPreviousScreen() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }

    /** ui initialization */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)

        /** ids of text view */
        title = findViewById(R.id.txt_connect_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium

        /** ids of recycler view */
        connectCategoryRecycler = findViewById(R.id.connectCategoryRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        connectCategoryRecycler!!.setHasFixedSize(true)
        connectCategoryRecycler!!.layoutManager = linearLayoutManager


        connectRecycler = findViewById(R.id.connectRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        connectRecycler!!.setHasFixedSize(true)
        connectRecycler!!.layoutManager = linearLayoutManager

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)
    }

    /** It is used to delete connect data and notify to adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == 201)

        {
            val deleteGatheringList = ArrayList<ConnectList>()
            deleteGatheringList.addAll(connectArray!!)

            val gatheringId = data!!.getStringExtra("connectId")
            for( i in 0 until deleteGatheringList.size)
            {

                if(deleteGatheringList[i]._id == gatheringId)
                {
                    deleteGatheringList.remove(deleteGatheringList[i])
                    connectArray!!.clear()
                    connectArray!!.addAll(deleteGatheringList)
                    connectAdapter!!.notifyDataSetChanged()

                    break
                }
            }

            if (connectArray!!.size == 0) {
               // ConstantMethods.showWarning(this,"", "There are no published connect.")

                noContent!!.visibility = View.VISIBLE
                noContent!!.text = "There are no published articles."

                try {
                    val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "There are no published articles."
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
        EasySP.init(this).remove("connectCategoryId")
        connectArray!!.clear()
    }

    /** It goes back to previous activity of fragment and hides the keyboard   */
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        ConstantMethods.hideKeyBoard(this,this)
        //finish()
    }
}
