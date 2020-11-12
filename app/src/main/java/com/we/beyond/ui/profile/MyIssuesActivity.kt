package com.we.beyond.ui.profile

import android.app.Activity
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
import com.we.beyond.adapter.MyIssuesAdapter
import com.we.beyond.Interface.OnDeleteListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.R
import com.we.beyond.model.NearByIssues
import com.we.beyond.model.NearByIssuesPojo
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.profile.myIssues.MyIssuesImpl
import com.we.beyond.presenter.profile.myIssues.MyIssuesPresenter
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import kotlin.collections.ArrayList

/** It will show all issues which created by user */
class MyIssuesActivity : AppCompatActivity(), MyIssuesPresenter.IMyIssuesView,
    LikePresenter.ILikeView,
    OnLikeDislikeListener, OnDeleteListener {


    val context: Context = this

    /** initialize implementors */
    var issuePresenter: MyIssuesImpl? = null
    var likePresenter: LikeImpl? = null

    /** init image view */
    var back: ImageView? = null

    /** init text view */
    var title: TextView? = null
    var unresolved : TextView?=null
    var resolved : TextView?=null
    var noContent : TextView?=null

    /** init recycler view, adapter and array list  */
    var issuesRecycler: RecyclerView? = null
    var issuesAdapter: MyIssuesAdapter? = null
    var issueArray: ArrayList<NearByIssues>? = null


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

    var sortByUnresolved = true
    var sortByResolved = false
    var isResolved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_issues)


        /** It hides keyboard */
        ConstantMethods.hideKeyBoard(this, this)

        /** array initialization */
        issueArray = ArrayList()
        issueArray!!.clear()

        /** initialize implementation */
        issuePresenter = MyIssuesImpl(this)
        likePresenter = LikeImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()


        /** initialize onclick listener */
        initWithListener()


        /** get data to post */
        getDataToPost()




    }
    override fun summaryType(type: String)
    {

    }

    /** It takes id as input and delete issue data from array and notify to adapter
     */
    override fun setOnDelete(_id: String)
    {
        /*try{
            connectArray!!.clear()
            getDataToPost()
            connectAdapter!!.notifyDataSetChanged()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }*/
        val deleteIssueList = ArrayList<NearByIssues>()
        deleteIssueList.addAll(issueArray!!)

        val issueId = _id
        for( i in 0 until deleteIssueList.size)
        {

            if(deleteIssueList[i]._id == issueId)
            {
                deleteIssueList.remove(deleteIssueList[i])
                issueArray!!.clear()
                issueArray!!.addAll(deleteIssueList)
                issuesAdapter!!.notifyDataSetChanged()

                break
            }
        }

    }


    /** It will call postDataToServerOnDelete() */
    override fun onDelete(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                postDataToServerOnDelete(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onDelete function of issue presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                issuePresenter!!.onDelete(this, jsonObject)
            }

        } catch (e:Exception)
        {
            e.printStackTrace()
        }


    }

    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "issue")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                postDataToServerOnLike(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onLike function of like presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
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

    /** It opens normal dialog to ask down vote and
     * It takes the json object as input and send to onLike function of like  presenter */

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
                    if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                        //ConstantMethods.showProgessDialog(this, "Please Wait...")
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

    /** It will get stored data and type, and call function depends on condition
     */
    private fun getDataToPost() {

        try {

            if(sortByUnresolved) {
                if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                    postDataToServer(pageNo, PAGE_SIZE, "unresolved")

                }
            }
            else{
                if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                    postDataToServer(pageNo, PAGE_SIZE, "resolved")

                }
            }



        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** It takes the below input and send to onMyIssues function of issue presenter */
    private fun postDataToServer(pageNo: Int, size: Int, type: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                resetValue()
                issuePresenter!!.onMyIssues(this, pageNo, size, type)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the below input and send to onMyIssuesOnLoadMore function of issue presenter */
    private fun postDataToServerOnLoadMore(pageNo: Int, size: Int, type: String) {
        try {
            if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {

                issuePresenter!!.onMyIssuesOnLoadMore(this, pageNo, size, type)
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
        issuesRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                visibleItemCount = issuesRecycler!!.getChildCount()
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
                    this@MyIssuesActivity.runOnUiThread(object : Runnable {
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
        unresolved!!.setOnClickListener {
            sortByUnresolved = true
            sortByResolved = false

            resolved!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            resolved!!.setBackgroundResource(R.drawable.connect_border)


            unresolved!!.setTextColor(ContextCompat.getColor(this, R.color.white))
            unresolved!!.setBackgroundResource(R.drawable.button_fill_border)

             getDataToPost()
        }

        /** It will changes background and text color of views and call getDataToPost() when click on it */
        resolved!!.setOnClickListener {
            sortByUnresolved = false
            sortByResolved = true

            unresolved!!.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            unresolved!!.setBackgroundResource(R.drawable.connect_border)


            resolved!!.setTextColor(ContextCompat.getColor(this, R.color.white))
            resolved!!.setBackgroundResource(R.drawable.button_fill_border)

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

            if(sortByUnresolved) {
                if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                    // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    postDataToServerOnLoadMore(++page, PAGE_SIZE, "unresolved")
                }
            }
            else{
                if (ConstantMethods.checkForInternetConnection(this@MyIssuesActivity)) {
                    // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    postDataToServerOnLoadMore(++page, PAGE_SIZE, "resolved")
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
        title = findViewById(R.id.txt_my_issue_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        unresolved = findViewById(R.id.txt_unResolved)
        unresolved!!.typeface = ConstantFonts.raleway_semibold

        resolved = findViewById(R.id.txt_resolved)
        resolved!!.typeface = ConstantFonts.raleway_semibold

        noContent = findViewById(R.id.txt_no_content)
        noContent!!.typeface = ConstantFonts.raleway_medium


        /** ids of recycler view */
        issuesRecycler = findViewById(R.id.myIssuesRecycler)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        issuesRecycler!!.setHasFixedSize(true)
        issuesRecycler!!.layoutManager = linearLayoutManager


        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)


    }


    /** It checks the nearByIssueDetails data,
     *  if it is not empty then set data to MyIssuesAdapter
     */
    override fun setMyIssuesAdapter(nearByIssueDetails: NearByIssuesPojo) {

        //Toast.makeText(this,"first call",Toast.LENGTH_SHORT).show()
        if (nearByIssueDetails.data.size == 0) {


            if(sortByResolved) {

               /* ConstantMethods.showWarning(
                    this,
                    "",
                    "There are no issues resolved."
                )*/

                noContent!!.visibility = View.VISIBLE
                issuesRecycler!!.visibility=View.GONE
                noContent!!.text = "There are no issues resolved."

                try {
                    val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "There are no issues resolved."
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
                    "There are no issues posted."
                )*/
                noContent!!.visibility = View.VISIBLE
                noContent!!.text = "There are no issues posted."
                issuesRecycler!!.visibility=View.GONE

                try {
                    val sweetAlertDialog = SweetAlertDialog(context , SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText =  "There are no issues posted."
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
            issuesRecycler!!.visibility=View.VISIBLE
            issueArray!!.addAll(nearByIssueDetails.data)

            issuesAdapter = MyIssuesAdapter(this, issueArray!!)
            issuesRecycler!!.adapter = issuesAdapter

        }


    }

    /** It checks the nearByIssueDetails data,
     *  if it is not empty then notify to MyIssuesAdapter
     */

    override fun setMyIssuesOnLoadMore(nearByIssueDetails: NearByIssuesPojo) {


        if (nearByIssueDetails.data.size > 0) {

            issueArray!!.addAll(nearByIssueDetails.data)


            issuesAdapter!!.notifyDataSetChanged()
        }


    }


    fun resetValue() {
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
        loading = true
        previousTotal = 0
        visibleThreshold = 5
        page = 1
        pageNo = 1
        issueArray!!.clear()
    }

    /** It is used to delete issue data and notify to adapter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode== Activity.RESULT_OK)
        {
           /* if(data != null) {
                val isApproved = data.getBooleanExtra("approved", false)

                if (isApproved) {
                    getDataToPost()

                    issuesAdapter!!.notifyDataSetChanged()
                } else {

                }
            }*/

            val reportedResolution = data!!.getBooleanExtra("reportedResolution", false)
            val reportedResolutionCount = data!!.getIntExtra("reportedCount", 0)
            val issueId = data!!.getStringExtra("issueId")

            val likeIssueList = ArrayList<NearByIssues>()
            likeIssueList.addAll(issueArray!!)


            if(reportedResolution) {
                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].resolved = false
                        likeIssueList[i].resolutionsCount = reportedResolutionCount
                        issueArray!!.clear()
                        issueArray!!.addAll(likeIssueList)
                        issuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }

            }
            else {
                val likeIssueList = ArrayList<NearByIssues>()
                likeIssueList.addAll(issueArray!!)

                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].resolved = true
                        issueArray!!.clear()
                        issueArray!!.addAll(likeIssueList)
                        issuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }
            }

        }

        else if(requestCode == 200 && resultCode == 201) {
            val deleteIssueList = ArrayList<NearByIssues>()
            deleteIssueList.addAll(issueArray!!)

            val issueId = data!!.getStringExtra("issueId")

            for (i in 0 until deleteIssueList.size) {

                if (deleteIssueList[i]._id == issueId) {
                    deleteIssueList.remove(deleteIssueList[i])
                    issueArray!!.clear()
                    issueArray!!.addAll(deleteIssueList)
                    issuesAdapter!!.notifyDataSetChanged()

                    break
                }
            }

            if(issueArray!!.size == 0)
            {
                ConstantMethods.showWarning(
                    this,
                    "",
                    "There are no issues posted."
                )

                noContent!!.visibility = View.VISIBLE
                noContent!!.text = "There are no issues posted."
            }
        }

        else if (requestCode == 200 && resultCode == 501) {

            var isLike = data!!.getBooleanExtra("isLikeByUser", false)
            var isDisLike = data!!.getBooleanExtra("isDislikeByUser", false)
            var issueId = data!!.getStringExtra("issueId")


            if (isLike) {
                val likeIssueList = ArrayList<NearByIssues>()
                likeIssueList.addAll(issueArray!!)



                for (i in 0 until likeIssueList.size) {

                    if (likeIssueList[i]._id == issueId) {
                        likeIssueList[i].likeByUser = isLike
                        issueArray!!.clear()
                        issueArray!!.addAll(likeIssueList)
                        issuesAdapter!!.notifyDataSetChanged()

                        break
                    }
                }

            }

        else{
            val likeIssueList = ArrayList<NearByIssues>()
            likeIssueList.addAll(issueArray!!)



            for (i in 0 until likeIssueList.size) {

                if (likeIssueList[i]._id == issueId) {
                    likeIssueList[i].likeByUser = !isDisLike
                    issueArray!!.clear()
                    issueArray!!.addAll(likeIssueList)
                    issuesAdapter!!.notifyDataSetChanged()

                    break
                }
            }

        }
        }

        else if (requestCode == 200 && resultCode == 205)
        {


        }


    }

    /** It go back to the previous activity */
    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        //finish()
    }

}