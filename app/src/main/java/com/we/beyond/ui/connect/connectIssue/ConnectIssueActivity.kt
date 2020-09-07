package com.we.beyond.ui.connect.connectIssue

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.JsonObject
import com.we.beyond.adapter.ConnectAdapter
import com.we.beyond.adapter.ConnectCategoriesAdapter
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.R
import com.we.beyond.model.ConnectCategories
import com.we.beyond.model.ConnectList
import com.we.beyond.model.ConnectPojo
import com.we.beyond.presenter.connect.connectIssue.ConnectIssueImpl
import com.we.beyond.presenter.connect.connectIssue.ConnectIssuePresenter
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.ui.connect.publishConnect.PublishConnectActivity
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import kotlin.Exception

/** It shows an all articles  */
class ConnectIssueActivity : AppCompatActivity(), ConnectIssuePresenter.IConnectIssueView,
    LikePresenter.ILikeView, OnLikeDislikeListener, OnDeleteListener, OnReportAbuseListener,
    ReportAbusePresenter.IReportAbuseView {

    /** initialize respected implementors */
    var context: Context = this
    var connectPresenter: ConnectIssueImpl? = null
    var likePresenter: LikeImpl? = null
    var reportAbusePresenter: ReportAbuseImpl? = null


    /** init image view */
    var back: ImageView? = null
    var close: ImageView? = null
    var searchView: SearchView? = null


    /** init text view */
    var title: TextView? = null
    var newest: TextView? = null
    var popular: TextView? = null
    var noContent: TextView? = null

    /** init button */
    var publish: Button? = null
    var abuse: Button? = null

    /** init edit text */
    var abuseEditText: EditText? = null

    /** init relative layout */
    var abuseLayout: RelativeLayout? = null

    /** init recycler view and its adapter */
    var connectCategoryRecycler: RecyclerView? = null
    var connectRecycler: RecyclerView? = null

    var linearLayoutManager: LinearLayoutManager? = null
    var connectCategoryAdapter: ConnectCategoriesAdapter? = null
    var connectAdapter: ConnectAdapter? = null

    /** init boolean */
    var sortByNewest = true
    var sortByPopular = false
    var isSearch: Boolean = false

    /** init strings */
    var connectCategoryId: String = ""
    var connectId: String = ""

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
    var pullToRefresh: SwipeRefreshLayout? = null

    companion object {
        val PAGE_SIZE = 5
    }

    /** init array list  */
    var connectArray: ArrayList<ConnectList>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_issue)

        /** It hides the keyboard */
        ConstantMethods.hideKeyBoard(this, this)

        /** array initialization */
        connectArray = ArrayList()
        connectArray!!.clear()

        /** initialize implementation */
        connectPresenter = ConnectIssueImpl(this)
        likePresenter = LikeImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()

        /** set connect category adapter */
        setConnectCategory()

        /** initialize onclick listener */
        initWithListener()

        /** get data to post */
        getDataToPost()


    }


    /* override fun onResume() {
         super.onResume()
         println("on resume")

         val delete = intent.getBooleanExtra("delete",false)
         println("delete $delete")
         if(delete)
         {
             try{
                 connectArray!!.clear()
                 getDataToPost()
                 connectAdapter!!.notifyDataSetChanged()
             }
             catch (e : Exception)
             {
                 e.printStackTrace()
             }
         }
     }

     override fun onPause() {
         super.onPause()
         println("on pause")
     }
 */
    /** It opens abuse layout with animation */
    override fun ReportAbuse(_id: String, s: String) {
        connectId = _id
        abuseLayout!!.visibility = View.VISIBLE
        abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))

    }


    /** It takes id as input and delete connect data from array and notify to adapter
     */
    override fun setOnDelete(_id: String) {

        val deleteConnectList = ArrayList<ConnectList>()
        deleteConnectList.addAll(connectArray!!)

        val connectId = _id
        for (i in 0 until deleteConnectList.size) {

            if (deleteConnectList[i]._id == connectId) {
                deleteConnectList.remove(deleteConnectList[i])
                connectArray!!.clear()
                connectArray!!.addAll(deleteConnectList)
                connectAdapter!!.notifyDataSetChanged()

                break
            }
        }

    }

    /** It calls postDataToServerOnDelete function with json object as parameter
     */
    override fun onDelete(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {
                postDataToServerOnDelete(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onDelete function of connect presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject) {

        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    /** It calls function depends on condition */
    fun getDataToPost() {
        try {
            //val categoryId = EasySP.init(this).getString("connectCategoryId")
            if (sortByNewest && connectCategoryId != null && connectCategoryId.isNotEmpty()) {
                searchView!!.visibility = View.GONE
                if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {

                    postDataToServerWithCategoryId(pageNo, "Newest", connectCategoryId)
                }

            } else if (sortByPopular && connectCategoryId != null && connectCategoryId.isNotEmpty()) {
                searchView!!.visibility = View.GONE
                if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {

                    postDataToServerWithCategoryId(pageNo, "Popular", connectCategoryId)
                }

            } else if (sortByNewest) {
                searchView!!.visibility = View.VISIBLE
                if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {

                    postDataToServer(pageNo, "Newest")
                    //sortByNewest=false
                }
            } else if (sortByPopular) {
                searchView!!.visibility = View.VISIBLE
                if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {

                    postDataToServer(pageNo, "Popular")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes below input and send to onSearch function of connect presenter */
    private fun postDataToServerOnSearch(text: String, pageNo: Int, size: Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                resetValue()
                connectPresenter!!.onSearch(this, text, pageNo, size)
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


            if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {
                postDataToServerOnLike(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes below input and send to onLike function of like presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

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

    /** It takes below input and send to onLike function of like presenter */
    private fun postDataToServerOnDislike(jsonObject: JsonObject) {

        try {
            /*  val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
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


            //  }


            /*  sweetAlertDialog.setCancelClickListener {
                  sweetAlertDialog.dismissWithAnimation()
              }
  */

        } catch (e: Exception) {

            e.printStackTrace()
        }


    }

    /** It takes below input and send to getConnectList function of connect presenter */
    private fun postDataToServer(pageNo: Int, sortBy: String) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")

                connectPresenter!!.getConnectList(this, pageNo, PAGE_SIZE, sortBy)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes below input and send to getConnectWithCategoryList function of connect presenter */
    private fun postDataToServerWithCategoryId(pageNo: Int, sortBy: String, categoryId: String?) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.getConnectWithCategoryList(
                    this,
                    pageNo,
                    PAGE_SIZE,
                    sortBy,
                    categoryId!!
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes below input and send to getConnectListOnLoadMore function of connect presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int, sortBy: String) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.getConnectListOnLoadMore(this, pageNo, PAGE_SIZE, sortBy)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes below input and send to getConnectWithCategoryListOnLoadMore function of connect presenter */
    private fun postDataToServerWithCategoryIdOnLoadMore(
        pageNo: Int,
        sortBy: String,
        categoryId: String?
    ) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {

                // ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectPresenter!!.getConnectWithCategoryListOnLoadMore(
                    this,
                    pageNo,
                    PAGE_SIZE,
                    sortBy,
                    categoryId!!
                )
            }
        } catch (e: Exception) {
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
            ConstantMethods.showWarning(this, "", "There are no published connect.")

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no published connect."


        } else {


            noContent!!.visibility = View.GONE


            connectArray!!.addAll(connectPojo.data)

            connectAdapter = ConnectAdapter(this, connectArray!!)
            connectRecycler!!.adapter = connectAdapter
            connectAdapter!!.notifyDataSetChanged()


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

    /** It checks the searched connect data,
     *  if it is not empty then set ConnectAdapter
     *  else show warning dialog
     */
    override fun setConnectOnSearchAdapter(connectPojo: ConnectPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (connectPojo.data.size == 0) {
            ConstantMethods.showWarning(this, "", "There are no published connect.")

            noContent!!.visibility = View.VISIBLE
            noContent!!.text = "There are no published connect."


        } else {


            noContent!!.visibility = View.GONE


            connectArray!!.addAll(connectPojo.data)

            connectAdapter = ConnectAdapter(this, connectArray!!)
            connectRecycler!!.adapter = connectAdapter
            connectAdapter!!.notifyDataSetChanged()


        }

    }

    /** It checks the searched connect data,
     *  if it is not empty then notify to ConnectAdapter
     */
    override fun setConnectOnSearchAdapterOnLoadMore(connectPojo: ConnectPojo) {

        if (connectPojo.data.size > 0) {


            connectArray!!.addAll(connectPojo.data)

            connectAdapter!!.notifyDataSetChanged()
        }

    }


    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity when click on it */
        back!!.setOnClickListener {
            onBackPressed()
            /* val intent = Intent(this, DashboardActivity::class.java)
             startActivity(intent)*/
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            //finish()
        }

        /** It changes the background and text color of text view
         * reset the values
         * call getDataToPost() when click on it
         */
        newest!!.setOnClickListener {
            sortByPopular = false
            sortByNewest = true

            popular!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            popular!!.setBackgroundResource(R.drawable.connect_border)


            newest!!.setTextColor(ContextCompat.getColor(this, R.color.white))
            newest!!.setBackgroundResource(R.drawable.button_fill_border)

            resetValue()
            getDataToPost()

        }

        /** It changes the background and text color of text view
         * reset the values
         * call getDataToPost() when click on it
         */
        popular!!.setOnClickListener {
            sortByNewest = false
            sortByPopular = true

            newest!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            newest!!.setBackgroundResource(R.drawable.connect_border)


            popular!!.setTextColor(ContextCompat.getColor(this, R.color.white))
            popular!!.setBackgroundResource(R.drawable.button_fill_border)

            resetValue()
            getDataToPost()

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
                    this@ConnectIssueActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                if (isSearch) {
                                    loadMoreSearch()
                                } else {
                                    loadMore()
                                }
                            }
                        }
                    })
                    // Do something
                    loading = true
                }
                println("loading")
            }
        })

        /** It opens PublishConnectActivity */
        publish!!.setOnClickListener {
            val intent = Intent(this, PublishConnectActivity::class.java)
            startActivityForResult(intent, 200)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            //finish()
        }

        /** It closes the abuse layout with animation */
        close!!.setOnClickListener {
            abuseLayout!!.visibility = View.GONE
            abuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_down))
        }

        /** It closes the abuse layout with animation and
         * calls the getDataToPostOnAbuse function
         */
        abuse!!.setOnClickListener {
            try {
                abuseLayout!!.visibility = View.GONE
                abuseLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_down
                    )
                )

                if (ConstantMethods.checkForInternetConnection(this)) {
                    getDataToPostOnAbuse()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It calls again getDataToPost api when pull it from top */
        pullToRefresh!!.setOnRefreshListener {
            resetValue()
            getDataToPost()
            pullToRefresh!!.isRefreshing = false
        }

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchView!!.clearFocus()
                if (query!!.toString().length > 2) {
                    try {

                        isSearch = true
                        postDataToServerOnSearch(query.toString().trim(), pageNo, PAGE_SIZE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "short length", Toast.LENGTH_LONG).show()
                }

                searchView!!.clearFocus()
                ConstantMethods.hideKeyBoard(context, this@ConnectIssueActivity)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.toString().length == 0) {
                    ConstantMethods.hideKeyBoard(context, this@ConnectIssueActivity)

                    searchView!!.clearFocus()
                    isSearch = false
                    resetValue()
                    getDataToPost()
                }
                return false
            }

        })

    }

    /** It calls postDataToServerOnAbuse function with json object */
    private fun getDataToPostOnAbuse() {
        try {

            if (abuseEditText!!.text.trim() != null && abuseEditText!!.text.trim().isNotEmpty()) {
                var abuse = abuseEditText!!.text.trim()

                if (ConstantMethods.checkForInternetConnection(this)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "connect")
                    jsonObject.addProperty("typeId", connectId)
                    jsonObject.addProperty(
                        "data", "" + abuse
                    )

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerOnAbuse(jsonObject)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onReportAbuse function of report abuse presenter */
    private fun postDataToServerOnAbuse(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls postDataToServerOnSearchLoadMore function for loading data */
    private fun loadMoreSearch() {
        try {

            postDataToServerOnSearchLoadMore(
                searchView!!.query.toString().trim(),
                ++page,
                PAGE_SIZE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onSearchLoadMore function of connect presenter */
    private fun postDataToServerOnSearchLoadMore(text: String, pageNo: Int, size: Int) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {
                connectPresenter!!.onSearchLoadMore(this, text, pageNo, size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It checks the conditions and call postDataToServerWithCategoryIdOnLoadMore function for loading data */
    private fun loadMore() {
        try {
            // val categoryId = EasySP.init(this).getString("connectCategoryId")

            if (sortByNewest && connectCategoryId != null && connectCategoryId.isNotEmpty()) {
                if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {
                    postDataToServerWithCategoryIdOnLoadMore(++page, "Newest", connectCategoryId)
                }

            } else if (sortByPopular && connectCategoryId != null && connectCategoryId.isNotEmpty()) {
                if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {
                    postDataToServerWithCategoryIdOnLoadMore(++page, "Popular", connectCategoryId)
                }

            } else if (sortByNewest) {
                if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {
                    postDataToServerOnLoadMore(++page, "Newest")
                }
            } else if (sortByPopular) {
                if (ConstantMethods.checkForInternetConnection(this@ConnectIssueActivity)) {
                    postDataToServerOnLoadMore(++page, "Popular")
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the context as input and send to onRequestConnectCategory function of connect presenter */
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

    /**It set category data to connect category adapter */
    override fun setConnectCategoryAdapter(connectCategories: ArrayList<ConnectCategories>) {
        connectCategoryAdapter = ConnectCategoriesAdapter(this, connectCategories)
        connectCategoryRecycler!!.adapter = connectCategoryAdapter
    }

    /**It goes back to DashboardActivity */
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
        close = findViewById(R.id.img_close)
        searchView = findViewById(R.id.img_search)
        searchView!!.queryHint =
            (Html.fromHtml("<font color = #000000>" + "Search Connect" + "</font>"))


        /** ids of text view */
        title = findViewById(R.id.txt_connect_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        newest = findViewById(R.id.txt_newest)
        newest!!.typeface = ConstantFonts.raleway_semibold

        popular = findViewById(R.id.txt_popular)
        popular!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium


        /** ids of button */
        publish = findViewById(R.id.btn_publish_connect)
        publish!!.typeface = ConstantFonts.raleway_semibold

        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold


        /** ids of recycler view */
        connectCategoryRecycler = findViewById(R.id.connectCategoryRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        connectCategoryRecycler!!.setHasFixedSize(true)
        connectCategoryRecycler!!.layoutManager = linearLayoutManager


        connectRecycler = findViewById(R.id.connectRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        connectRecycler!!.setHasFixedSize(true)
        connectRecycler!!.layoutManager = linearLayoutManager

        /** ids of relative layout */
        abuseLayout = findViewById(R.id.reportAbuseLayout)

        /** ids of edit text */
        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

    }


    /** It checks the requestCode
     * if requestCode is 201, it will delete specific connect object from connect array
     * and store to connect array to notify adapter
     * and if requestCode is 202, it will reset all values and call getDataToPost()
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == 201) {
            val deleteConnectList = ArrayList<ConnectList>()
            deleteConnectList.addAll(connectArray!!)

            val connectId = data!!.getStringExtra("connectId")
            for (i in 0 until deleteConnectList.size) {

                if (deleteConnectList[i]._id == connectId) {
                    deleteConnectList.remove(deleteConnectList[i])
                    connectArray!!.clear()
                    connectArray!!.addAll(deleteConnectList)
                    connectAdapter!!.notifyDataSetChanged()

                    break
                }
            }

        } else if (requestCode == 200 && resultCode == 202) {
            resetValue()
            getDataToPost()
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

        ConstantMethods.hideKeyBoard(this, this)
        //finish()
    }
}
