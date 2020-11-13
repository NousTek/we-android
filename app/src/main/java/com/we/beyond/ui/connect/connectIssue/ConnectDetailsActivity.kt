package com.we.beyond.ui.connect.connectIssue

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.adapter.ConnectCommentsAdapter
import com.we.beyond.adapter.ResolvedIssueUserAdapter
import com.we.beyond.Interface.OnEditListener
import com.we.beyond.Interface.OnItemClickListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.R
import com.we.beyond.model.CommentsData
import com.we.beyond.model.CommentsPojo
import com.we.beyond.model.ConnectDetailsPojo
import com.we.beyond.model.UserList
import com.we.beyond.presenter.comments.commentById.CommentsImpl
import com.we.beyond.presenter.comments.commentById.CommentsPresenter
import com.we.beyond.presenter.comments.createComment.CreateCommentImpl
import com.we.beyond.presenter.comments.createComment.CreateCommentPresenter
import com.we.beyond.presenter.connect.connectIssue.ConnectByIdImpl
import com.we.beyond.presenter.connect.connectIssue.ConnectByIdPresenter
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.presenter.reportResolved.ReportResolvedImpl
import com.we.beyond.presenter.reportResolved.ReportResolvedPresenter
import com.we.beyond.ui.MediaViewPagerActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.connect.publishConnect.PublishConnectActivity
import com.we.beyond.ui.dashboard.NotificationActivity
import com.we.beyond.ui.dashboard.SummaryDetailsActivity
import com.we.beyond.ui.profile.MyConnectActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList

/**
 * It shows the articles details perform further actions
 */
class ConnectDetailsActivity : AppCompatActivity() , ConnectByIdPresenter.IConnectByIdView
    , CommentsPresenter.ICommentsByIdView , CreateCommentPresenter.ICreateCommentView, LikePresenter.ILikeView,
     OnLikeDislikeListener, ReportAbusePresenter.IReportAbuseView, OnEditListener, ReportResolvedPresenter.IReportResolvedView,
    OnItemClickListener
{


    var context : Context = this

    /** initialize respected implementors */
    var connectIdPresenter: ConnectByIdImpl? = null
    var commentPresenter: CommentsImpl? = null
    var createCommentPresenter : CreateCommentImpl?=null
    var likeDislikePresenter : LikeImpl?=null
    var reportAbusePresenter : ReportAbuseImpl?=null
    var reportResolvedPresenter: ReportResolvedImpl? = null

    /** init image view */
    var back: ImageView? = null
    var profilePic: CircleImageView? = null
    var connectImage: ImageView? = null
    var sendComment : ImageView?=null
    var more : ImageView?=null
    var close: ImageView? = null
    var closeAbuse : ImageView?=null
    var closeComment : ImageView?=null
    var play : ImageView?=null


    /** init text view */
    var title: TextView? = null
    var connectTitle: TextView? = null
    var name: TextView? = null
    var time: TextView? = null
    var connectDescription: TextView? = null
    var connectCount : TextView?=null
    var upvoteCount : TextView?=null
    var comment : TextView?=null
    var upvote : TextView?=null
    var downvote : TextView?=null
    var delete : TextView?=null
    var reportAbuse : TextView?=null
    var edit : TextView?=null
    var share : TextView?=null

    /** init recycler view */
    var commentRecycler: RecyclerView? = null
    var userTypeRecycler: RecyclerView? = null


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
    var linearLayoutManager2: LinearLayoutManager? = null

    var connectData : ConnectDetailsPojo?=null

    companion object {
        val PAGE_SIZE = 5
    }

    /** init adapter */
    var commentsAdapter : ConnectCommentsAdapter?=null
    var userTypeAdapter: ResolvedIssueUserAdapter? = null

    /** init array list */
    var commentArray : ArrayList<CommentsData>?=null
    var userTypeList: ArrayList<String>? = null
    var userPojo: ArrayList<UserList>? = null
    var tagsList : ArrayList<String>?=null
    var tagUserName : ArrayList<String>?=null

    /** init scroll view */
    var scrollView : ScrollView?=null

    /** init edit text */
    var createComment : EditText?=null
    var abuseEditText : EditText?=null

    /** init button */
    var abuse : Button?=null

    /** pull to refresh layout */
    var pullToRefresh : SwipeRefreshLayout?=null

    /** init variables */
    var commentPosition : Int ?=null
    var commentIssue : Boolean = false
    var newInfoPublished : Boolean = false
    var myConnect : Boolean = false
    var notification : Boolean = false
    var editComment = false
    var connectId: String? = null
    var searchText : String =""
    var allText : String = ""
    var commentId : String=""


    /** init relative layout */
    var operationLayout : RelativeLayout?=null
    var commentLayout : RelativeLayout?=null
    var moreLayout : RelativeLayout?=null
    var backgroundLayout: RelativeLayout? = null
    var reportAbuseLayout : RelativeLayout?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** It hides the keyboard */
        ConstantMethods.hideKeyBoard(this,this@ConnectDetailsActivity)

        setContentView(R.layout.activity_connect_details)

        /** array initialization */
        commentArray = ArrayList()
        commentArray!!.clear()

        userTypeList = ArrayList()
        userTypeList!!.clear()
        userPojo = ArrayList()
        tagsList = ArrayList()
        tagUserName = ArrayList()


        /** initialize implementation */
        connectIdPresenter = ConnectByIdImpl(this)

        commentPresenter = CommentsImpl(this)

        createCommentPresenter = CreateCommentImpl(this)

        likeDislikePresenter = LikeImpl(this)

        reportAbusePresenter = ReportAbuseImpl(this)

        reportResolvedPresenter = ReportResolvedImpl(this)




        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** Get all stored data using intent and assign it respectively */
        if (getIntent() != null && getIntent().getData() != null)
        {
            val data = getIntent().getData()
            val path =data!!.path
            connectId = path!!.replace("/connects/","",ignoreCase = true)

        }
        else
        {
            connectId = intent.getStringExtra("connectCategoryId")
        }

        commentIssue = intent.getBooleanExtra("connectComment",false)
        newInfoPublished = intent.getBooleanExtra("newPublished",false)
        myConnect = intent.getBooleanExtra("myConnect",false)
        notification = intent.getBooleanExtra("notification",false)

        if(commentIssue)
        {
            operationLayout!!.visibility = View.GONE
            commentLayout!!.visibility = View.VISIBLE
            createComment!!.isCursorVisible = true
            createComment!!.requestFocus()
            backgroundLayout!!.visibility = View.VISIBLE

            ConstantMethods.showKeyBoard(this)

        }

        if (connectId != null && connectId!!.isNotEmpty()) {

            println("connect id $connectId")

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                        ConstantMethods.showProgessDialog(this, "Please Wait...")
                    connectIdPresenter!!.onConnectById(this, connectId!!)
                }
            } catch (e: Exception)
            {
                e .printStackTrace()
            }

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    commentPresenter!!.onCommentsById(
                        this,
                        connectId!!,
                        "connect",
                        pageNo,
                        PAGE_SIZE
                    )
                }
                }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

        }



    }

    /** It calls  onConnectById function of connect id presenter
     * and onCommentsById function of comment presenter when click on it */
    override fun goToNextScreen() {

        if (connectId != null && commentId.isNotEmpty()) {
            resetValue()

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    connectIdPresenter!!.onConnectById(this, connectId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    commentArray!!.clear()
                    commentPresenter!!.onCommentsById(this, connectId!!, "connect", pageNo, PAGE_SIZE
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }

    /** It show operation layout
     * hides keyboard
     * and call onCommentsById function of comment presenter
     */
    override fun onUpdateSuccessfully()
    {
        try {
            operationLayout!!.visibility = View.VISIBLE
            commentLayout!!.visibility = View.GONE
            createComment!!.isCursorVisible = true
            createComment!!.requestFocus()
            backgroundLayout!!.visibility = View.GONE

            ConstantMethods.hideKeyBoard(this,this@ConnectDetailsActivity)

            if (ConstantMethods.checkForInternetConnection(context)) {
                commentPresenter!!.onCommentsById(this, connectId!!, "connect", pageNo, PAGE_SIZE)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }


    }

    /** It sets the comment text and add user name to tag user name array */
    override fun OnClick(userName: String, userId: String) {

        val editText = createComment!!.text.toString()
        val newText = editText.replace(searchText,"")

        createComment!!.setText("${newText}${userName.trim()}")
        createComment!!.setSelection(createComment!!.text.length)

        tagUserName!!.add(userName)
        tagsList!!.add(userId)

        userTypeRecycler!!.visibility = View.GONE

    }

    /** It is used to edit the comment */
    override fun OnEdit(_id: String, commentText: String)
    {
        operationLayout!!.visibility = View.GONE
        commentLayout!!.visibility = View.VISIBLE
        createComment!!.isCursorVisible = true
        createComment!!.requestFocus()
        backgroundLayout!!.visibility = View.VISIBLE
        ConstantMethods.showKeyBoard(this)
        createComment!!.setText(commentText)

        editComment = true
        commentId = _id

    }

    /** It show and hide the layout */
    fun getData() {
        if(commentId!=null && commentId.isNotEmpty())
        {
            operationLayout!!.visibility = View.GONE
            commentLayout!!.visibility = View.VISIBLE
            createComment!!.requestFocus()
            createComment!!.isCursorVisible = true
            backgroundLayout!!.visibility = View.VISIBLE
        }

    }

    override fun summaryType(type: String)
    {

    }

    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "comment")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@ConnectDetailsActivity)) {

                postDataToServerOnLike(jsonObject)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the id as input and call postDataToServerOnDislike function */
    override fun onDislike(_id: String) {

        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "comment")
            jsonObject.addProperty("typeId", _id)


            try {
                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {

                        postDataToServerOnDislike(jsonObject)
                    }
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous fragment or activity depends on conditions  when click on it */
        back!!.setOnClickListener {
            ConstantMethods.hideKeyBoard(this,this@ConnectDetailsActivity)

            if(newInfoPublished)
            {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.PUBLISH_CONNECT)
                //startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
            else if(notification)
            {
                val intent = Intent(this, NotificationActivity::class.java)
                // startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
           else {

                val intent = Intent(this, ConnectIssueActivity::class.java)
                intent.putExtra("connectComment", false)
                //startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }

        /** It calls loadMore() when scrolling  */
        commentRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = commentRecycler!!.getChildCount()
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
                    this@ConnectDetailsActivity.runOnUiThread(object : Runnable {
                        override fun run() {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                loadMore()
                            }
                        }
                    })
                    // Do something
                    loading = true
                }

            }
        })

        /** It opens comment layout and show keyboard */
        comment!!.setOnClickListener {
            operationLayout!!.visibility = View.GONE
            commentLayout!!.visibility = View.VISIBLE
            createComment!!.requestFocus()
            createComment!!.setText("")
            tagsList!!.clear()

            backgroundLayout!!.visibility = View.VISIBLE

            ConstantMethods.showKeyBoard(this)

        }

        /**
         * It reset the values
         * call getDataToPost function
         * and hides the keyboard
         */
        sendComment!!.setOnClickListener {
            try {

                if (ConstantMethods.checkForInternetConnection(this@ConnectDetailsActivity)) {
                    resetValue()
                    getDataToPost()

                    if(commentPosition!=null)
                    {
                        /*scrollView!!.fullScroll(commentPosition!!)
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(sendComment!!.getWindowToken(), 0)*/
                    }
                    else {
                        scrollView!!.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS)

                        createComment!!.clearFocus()
                        //scrollView!!.fullScroll(ScrollView.FOCUS_UP)
                        ConstantMethods.hideKeyBoard(this, this@ConnectDetailsActivity)
                        backgroundLayout!!.visibility = View.GONE
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It show and hide upvote, downvote image view, change text color
         * and call postDataToServerOnLike() with json object */
        upvote!!.setOnClickListener {
            downvote!!.visibility = View.VISIBLE
            upvote!!.visibility = View.GONE

            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "connect")
                jsonObject.addProperty("typeId", connectId)


                if (ConstantMethods.checkForInternetConnection(this@ConnectDetailsActivity)) {

                    postDataToServerOnLike(jsonObject)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        /** It show and hide upvote, downvote image view, change text color
         * and call postDataToServerOnLike() with json object */
        downvote!!.setOnClickListener {

            downvote!!.visibility = View.GONE
            upvote!!.visibility = View.VISIBLE


            try {

                val jsonObject = JsonObject()
                jsonObject.addProperty("type", "connect")
                jsonObject.addProperty("typeId", connectId)


                try {
                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {

                            postDataToServerOnDislike(jsonObject)
                        }
                    }
                    catch (e : Exception)
                    {
                        e.printStackTrace()
                    }
                }
                catch(e : Exception)
                {
                    e.printStackTrace()
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        /** If image or video url is not empty then opens MediaViewPagerActivity to see the image and video
         * else show warning dialog  */
        connectImage!!.setOnClickListener {
            if(connectData!!.data.imageUrls !=null && connectData!!.data.imageUrls.isNotEmpty() || connectData!!.data.videoUrls !=null && connectData!!.data.videoUrls.isNotEmpty()) {
                val intent = Intent(context, MediaViewPagerActivity::class.java)
                intent.putStringArrayListExtra("mediaImage", connectData!!.data.imageUrls)
                intent.putStringArrayListExtra("mediaVideo", connectData!!.data.videoUrls)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            else{
                ConstantMethods.showWarning(this,"No Data","Images not available for this connect.")
            }
        }

        /** It hides keyboard, comment layout and show operation layout */
        closeComment!!.setOnClickListener {

            commentLayout!!.visibility = View.GONE
            operationLayout!!.visibility = View.VISIBLE
            backgroundLayout!!.visibility = View.GONE

            createComment!!.clearFocus()

            ConstantMethods.hideKeyBoard(this,this@ConnectDetailsActivity)

        }


        /** It hides keyboard, comment layout and show operation layout */
        commentLayout!!.setOnClickListener {
            commentLayout!!.visibility = View.GONE
            operationLayout!!.visibility = View.VISIBLE
            backgroundLayout!!.visibility = View.GONE

            createComment!!.clearFocus()
            ConstantMethods.hideKeyBoard(this,this@ConnectDetailsActivity)
        }


        /** It closes more option layout with animation */
        more!!.setOnClickListener {
            moreLayout!!.visibility = View.VISIBLE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_in_up))


        }

        /** It closes more option layout with animation */
        close!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(context,R.anim.slide_out_down))

        }

        /**  It will show dialog to delete an connect and call postDataToServerOnDelete() with json object
         */
        delete!!.setOnClickListener {
            val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
            if(userId == connectData!!.data.user._id) {
                println("user id ${userId} gathering user ${connectData!!.data.user._id}")
                try {
                    val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                    sweetAlertDialog.titleText = ""
                    sweetAlertDialog.contentText = "Do you want to delete connect?"
                    sweetAlertDialog.show()
                    sweetAlertDialog.setCancelable(false)
                    sweetAlertDialog.setConfirmClickListener {
                        sweetAlertDialog.dismissWithAnimation()

                        try {

                            val jsonObject = JsonObject()
                            jsonObject.addProperty("type", "disabled")
                            jsonObject.addProperty("connectId", connectData!!.data._id)


                            try {
                                postDataToServerOnDelete(jsonObject)

                            } catch (e:Exception) {
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
            }
            else{
                ConstantMethods.showWarning(this,"","You are not owner of this connect.")
            }

        }

        /** It closes the more layout with animation
         * get stored user id and check with connect user id, if matches then we cannot abuse the connect
         * else It will open report abuse layout  */
        reportAbuse!!.setOnClickListener {
            try {
                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )

                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == connectData!!.data.user._id) {
                    ConstantMethods.showWarning(context,"","You can not abuse your connect.")
                }

                else {

                    reportAbuseLayout!!.visibility = View.VISIBLE
                    reportAbuseLayout!!.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.slide_in_up
                        )
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It closes the abuse layout with animation and call getDataToPostOnAbuse() */
        abuse!!.setOnClickListener {
            try{
                reportAbuseLayout!!.visibility = View.GONE
                reportAbuseLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_down
                    )
                )


                /* val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                 if (userId == nearByIssuesData!!.data.user._id) {
                     ConstantMethods.showWarning(context,"","You can not abuse your issue.")
                 }
                 else {
 */

                if (ConstantMethods.checkForInternetConnection(this)) {

                    getDataToPostOnAbuse()
                }
                // }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }


        /** It closes the report abuse layout with animation */
        closeAbuse!!.setOnClickListener {
            reportAbuseLayout!!.visibility = View.GONE
            reportAbuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this,R.anim.slide_out_down))
        }


        /** It closes the more layout with animation
         * get stored user id and check with connect user id, if matches then  opens PublishConnectActivity to edit a article
         * else show warning dialog */
        edit!!.setOnClickListener {

            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )



            val userId = EasySP.init(this).getString(ConstantEasySP.USER_ID)
            if (userId == connectData!!.data.user._id) {


                val jsonString = Gson().toJson(connectData)
                val intent = Intent(this, PublishConnectActivity::class.java)
                intent.putExtra("connectData", jsonString)
                intent.putExtra("edit", true)
                startActivityForResult(intent,200)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            } else {

                ConstantMethods.showWarning(this,"","You are not owner of this connect.")
            }

        }

        /** It closes the more layout with animation */
        moreLayout!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )
        }


        /** It calls again onConnectById function of connect id presenter
         * and onCommentsById function of comment presenter when pull it from top */
        pullToRefresh!!.setOnRefreshListener {


            if (connectId != null && connectId!!.isNotEmpty()) {

                commentArray!!.clear()

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {
                        ConstantMethods.showProgessDialog(this, "Please Wait...")
                        connectIdPresenter!!.onConnectById(this, connectId!!)
                    }
                } catch (e: Exception)
                {
                    e .printStackTrace()
                }

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {

                        // ConstantMethods.showProgessDialog(this, "Please Wait...")
                        commentPresenter!!.onCommentsById(
                            this,
                            connectId!!,
                            "connect",
                            pageNo,
                            PAGE_SIZE
                        )
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }

            }

            pullToRefresh!!.isRefreshing = false
        }

        /** It closes the more layout with animation
         * It will share issue through share intent*/
        share!!.setOnClickListener {

            try {

                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_down
                    )
                )

                val share = Intent(Intent.ACTION_SEND)
                share.setType("text/plain")
                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                share.putExtra(Intent.EXTRA_SUBJECT, "WE")
                share.putExtra(
                    Intent.EXTRA_TEXT,
                    "${Constants.BASE_URL}connects/$connectId"
                )
                startActivity(Intent.createChooser(share, "Share Connect!"))
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
        }

        /** It will search the user from user list using '@' character
         * from OnRequestUserListOnSearch function of report resolved presenter  */
        createComment!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                allText = p0.toString()

                allText = p0.toString().replace("(","")

                if(allText.endsWith(")",ignoreCase = true)) {
                    if (allText != null && allText.isNotEmpty()) {
                        val parts = allText.split(")")
                        val first = parts[0]
                        var second = parts[1]

                        allText = first.replace(first, "")
                    }
                }

                allText = allText.replace(")","")


                println("all text $allText")


                if(p0!!.startsWith("@",ignoreCase = true) && p1>=3) {
                    searchText = allText.substring(allText.lastIndexOf("@") + 1)

                    if (searchText != null && searchText.isNotEmpty()) {
                        if (searchText.length >= 3) {
                            try {

                                if (ConstantMethods.checkForInternetConnection(context)) {
                                    reportResolvedPresenter!!.OnRequestUserListOnSearch(
                                        context,
                                        searchText.trim()
                                    )
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                        }
                    }
                } else {

                    allText = allText.replace("(","")

                    if(allText.endsWith(")",ignoreCase = true)) {
                        if (allText != null && allText.isNotEmpty()) {
                            val parts = allText.split(")")
                            val first = parts[0]
                            var second = parts[1]

                            allText = first.replace(first, "")
                        }
                    }

                    allText = allText.replace(")","")

                    println("all text $allText")



                    searchText = allText.substring(allText.lastIndexOf("@") + 1)


                    if (searchText != null && searchText.isNotEmpty()) {
                        if (searchText.length >= 3) {
                            try {

                                if (ConstantMethods.checkForInternetConnection(context)) {
                                    reportResolvedPresenter!!.OnRequestUserListOnSearch(
                                        context,
                                        searchText.trim()
                                    )
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                if(p0.length==0)
                {
                    tagsList!!.clear()
                    tagUserName!!.clear()
                    userTypeRecycler!!.visibility = View.GONE
                }

                else{
                    for(i in 0 until tagUserName!!.size) {
                        if(!p0.contains(tagUserName!![i],ignoreCase = true))
                        {
                            tagUserName!!.remove(tagUserName!![i])
                            tagsList!!.remove(tagsList!![i])

                            break

                        }
                    }

                }
            }

        })




    }

    /** It checks the user data,
     *  if it is not empty then set ResolvedIssueUserAdapter
     */
    override fun setUserListAdapter(userList: ArrayList<UserList>) {
        try {
            userTypeList!!.clear()


            if (userList.isNotEmpty()) {
                for (i in 0 until userList.size) {
                    userTypeList!!.add(userList[i].name)
                    userPojo!!.addAll(userList)

                }
            }

            if (userTypeList!!.isNotEmpty()) {
                userTypeRecycler!!.visibility = View.VISIBLE
                userTypeAdapter = ResolvedIssueUserAdapter(this, userList, "")
                userTypeRecycler!!.adapter = userTypeAdapter

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It calls postDataToServerOnAbuse function with json object */
    private fun getDataToPostOnAbuse()
    {
        try{

            if(abuseEditText!!.text.trim() !=null && abuseEditText!!.text.trim().isNotEmpty())
            {
                var abuse = abuseEditText!!.text.trim()

                if (ConstantMethods.checkForInternetConnection(this)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "connect")
                    jsonObject.addProperty("typeId",connectId )
                    jsonObject.addProperty(
                        "data", ""+abuse
                    )

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerOnAbuse(jsonObject)
                        }
                    }
                    catch (e : Exception)
                    {
                        e.printStackTrace()
                    }

                    println("post data $jsonObject")


                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onReportAbuse function of report abuse presenter */
    private fun postDataToServerOnAbuse(jsonObject: JsonObject)
    {
        try{
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }



    /** It takes the json object as input and send to onDelete function of connect id presenter */
    private fun postDataToServerOnDelete(jsonObject: JsonObject)
    {

        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                connectIdPresenter!!.onDelete(this, jsonObject)
            }

        } catch (e: Exception)
        {
            e.printStackTrace()
        }


    }

    /** It takes the json object as input and send to onLike function of like dislike presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {
            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                       // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    likeDislikePresenter!!.onLike(this, jsonObject)
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }
    }

    /** It opens normal dialog to ask down vote and
     * It takes the json object as input and send to onLike function of like dislike  presenter */
    private fun postDataToServerOnDislike(jsonObject: JsonObject) {

        try {
            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
            sweetAlertDialog.titleText = "DownVote"
            sweetAlertDialog.contentText = "Do you want to DownVote?"
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()


                try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                               // ConstantMethods.showProgessDialog(this, "Please Wait...")
                            likeDislikePresenter!!.onLike(this, jsonObject)
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

    /** It checks the edit comment boolean, if it is true then call postDataToServerUpdateComment()
     * else call postDataToServer() with required json object
     */
    fun getDataToPost() {
        try{

            if(editComment)
            {

                if (createComment!!.text != null && createComment!!.text.isNotEmpty()) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("text", (createComment!!.text.toString()))
                    jsonObject.addProperty("commentId", commentId)

                    val jsonArray = JsonArray()

                    if(tagsList != null  &&  tagsList!!.isNotEmpty())
                    {
                        for(i in 0 until tagsList!!.size) {
                            jsonArray.add(tagsList!![i])
                        }
                        jsonObject.add("tags",jsonArray)
                    }

                    val jsonArrayName = JsonArray()

                    if(tagUserName != null  &&  tagUserName!!.isNotEmpty())
                    {
                        for(i in 0 until tagUserName!!.size) {
                            jsonArrayName.add(tagUserName!![i])
                        }
                        jsonObject.add("tagNames",jsonArrayName)
                    }

                    commentId = ""

                    println("json $jsonObject")
                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerUpdateComment(jsonObject)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    ConstantMethods.showWarning(
                        this,
                        "Empty Data",
                        "Please give us your comment."
                    )
                }

            }


            else {


                if (commentId != null && commentId.isNotEmpty()) {
                    if (createComment!!.text != null && createComment!!.text.isNotEmpty()) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("connectId", connectId!!)
                        jsonObject.addProperty("text", createComment!!.text.toString().trim())
                        jsonObject.addProperty("parentId", commentId)

                        val jsonArray = JsonArray()

                        if(tagsList != null  &&  tagsList!!.isNotEmpty())
                        {
                            for(i in 0 until tagsList!!.size) {
                                jsonArray.add(tagsList!![i])
                            }
                            jsonObject.add("tags",jsonArray)
                        }


                        val jsonArrayName = JsonArray()

                        if(tagUserName != null  &&  tagUserName!!.isNotEmpty())
                        {
                            for(i in 0 until tagUserName!!.size) {
                                jsonArrayName.add(tagUserName!![i])
                            }
                            jsonObject.add("tagNames",jsonArrayName)
                        }
                        commentId = ""

                        try {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                postDataToServer(jsonObject)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }


                    } else {
                        ConstantMethods.showWarning(
                            this,
                            "Empty Data",
                            "Please give us your comment."
                        )
                    }
                } else {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("connectId", connectId)
                    jsonObject.addProperty("text", createComment!!.text.toString().trim())
                    val jsonArray = JsonArray()

                    if(tagsList != null  &&  tagsList!!.isNotEmpty())
                    {
                        for(i in 0 until tagsList!!.size) {
                            jsonArray.add(tagsList!![i])
                        }
                        jsonObject.add("tags",jsonArray)
                    }

                    val jsonArrayName = JsonArray()

                    if(tagUserName != null  &&  tagUserName!!.isNotEmpty())
                    {
                        for(i in 0 until tagUserName!!.size) {
                            jsonArrayName.add(tagUserName!![i])
                        }
                        jsonObject.add("tagNames",jsonArrayName)
                    }

                    println("json $jsonObject")
                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServer(jsonObject)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }
        catch(e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onUpdateComment function of comment presenter */
    private fun postDataToServerUpdateComment(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                commentPresenter!!.onUpdateComment(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onCommentCreated function of create comment presenter */
    private fun postDataToServer(jsonObject: JsonObject)
    {
        try {

                if (ConstantMethods.checkForInternetConnection(context)) {
                    createCommentPresenter!!.onCommentCreated(this, jsonObject)
                }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onConnectById function of connect id presenter
     * and onCommentsById function of comment presenter
     * hide keyboard */
    override fun setCommentAdapter()
    {
        try {
            createComment!!.clearFocus()
            ConstantMethods.hideKeyBoard(this,this@ConnectDetailsActivity)

            operationLayout!!.visibility = View.VISIBLE
            commentLayout!!.visibility = View.GONE
            backgroundLayout!!.visibility = View.GONE
            createComment!!.setText("")


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    connectIdPresenter!!.onConnectById(this, connectId!!)
                }
            } catch (e: Exception)
            {
                e .printStackTrace()
            }


            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    commentPresenter!!.onCommentsById(
                        this,
                        connectId!!,
                        "connect",
                        pageNo,
                        PAGE_SIZE
                    )
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls postDataToServerOnLoadMore() with below inputs  */
    private fun loadMore() {
        try {


            if (ConstantMethods.checkForInternetConnection(this@ConnectDetailsActivity)) {

                postDataToServerOnLoadMore(connectId!!,"connect",++page,
                    PAGE_SIZE
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onCommentsByIdOnLoadMore function of comment presenter */
    private fun postDataToServerOnLoadMore(issueId: String?, s: String, i: Int, pageSize: Int) {
        try {

                if (ConstantMethods.checkForInternetConnection(context)) {
                    commentPresenter!!.onCommentsByIdOnLoadMore(this, issueId!!, s, i, pageSize)
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** ui initialization */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        profilePic = findViewById(R.id.img_profile_pic)
        connectImage = findViewById(R.id.img_connect)
        sendComment = findViewById(R.id.img_send_comment)
        more = findViewById(R.id.img_more)
        close = findViewById(R.id.img_close)
        closeAbuse = findViewById(R.id.img_close_window)
        closeComment = findViewById(R.id.img_close_comment)
        play = findViewById(R.id.img_play)

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)


        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        connectTitle = findViewById(R.id.txt_connect_title)
        connectTitle!!.typeface = ConstantFonts.raleway_semibold

        name = findViewById(R.id.txt_name)
        name!!.typeface = ConstantFonts.raleway_regular

        time = findViewById(R.id.txt_time)
        time!!.typeface = ConstantFonts.raleway_regular

        connectDescription = findViewById(R.id.txt_connect_description)
        connectDescription!!.typeface = ConstantFonts.raleway_medium

        connectCount = findViewById(R.id.txt_connect_comment)
        connectCount!!.typeface = ConstantFonts.raleway_semibold

        upvoteCount = findViewById(R.id.txt_connect_upvote)
        upvoteCount!!.typeface = ConstantFonts.raleway_semibold

        comment = findViewById(R.id.txt_comment)
        comment!!.typeface = ConstantFonts.raleway_semibold


        upvote = findViewById(R.id.txt_upvote)
        upvote!!.typeface = ConstantFonts.raleway_semibold

        downvote = findViewById(R.id.txt_downvote)
        downvote!!.typeface = ConstantFonts.raleway_semibold

        delete = findViewById(R.id.txt_delete)
        delete!!.typeface = ConstantFonts.raleway_semibold

        reportAbuse = findViewById(R.id.txt_report_abuse)
        reportAbuse!!.typeface = ConstantFonts.raleway_semibold

        share = findViewById(R.id.txt_share)
        share!!.typeface = ConstantFonts.raleway_semibold

        edit = findViewById(R.id.txt_edit)
        edit!!.typeface = ConstantFonts.raleway_semibold



        /** ids of recycler view */
        commentRecycler = findViewById(R.id.commentRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        commentRecycler!!.setHasFixedSize(true)
        commentRecycler!!.layoutManager = linearLayoutManager

        userTypeRecycler = findViewById(R.id.recycler_resolved_by)
        linearLayoutManager2 = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        userTypeRecycler!!.setHasFixedSize(true)
        userTypeRecycler!!.layoutManager = linearLayoutManager2


        /** ids of relative layout */
        operationLayout = findViewById(R.id.operationLayout)
        commentLayout = findViewById(R.id.commentLayout)
        backgroundLayout = findViewById(R.id.backgroundLayout)
        moreLayout = findViewById(R.id.moreLayout)
        reportAbuseLayout = findViewById(R.id.reportAbuseLayout)


        /** ids of scroll view */
        scrollView = findViewById(R.id.connectDetailsScrollView)

        /** ids of edit text */
        createComment = findViewById(R.id.et_comment)
        createComment!!.typeface = ConstantFonts.raleway_regular

        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of button */
        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold
    }


    /** It checks the commentDetails data,
     *  if it is not empty then
     *  call onConnectById function of connect id presenter
     *  and set ConnectCommentAdapter
     */
    override fun setCommentsByIdAdapter(commentsDetails: CommentsPojo) {

        if(commentsDetails.data.size == 0)
        {

        }
        else {

            commentArray!!.addAll(commentsDetails.data)

            println("comment array $commentArray")

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    connectIdPresenter!!.onConnectById(this, connectId!!)
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

            if(commentPosition!=null) {

                commentsAdapter = ConnectCommentsAdapter(this, commentArray!!,connectData)
                commentRecycler!!.adapter = commentsAdapter
                commentRecycler!!.smoothScrollToPosition(commentPosition!!)
                commentRecycler!!.scrollToPosition(commentPosition!!-2)
                linearLayoutManager!!.scrollToPositionWithOffset(commentPosition!!, commentPosition!!)
            }
            else{
                commentsAdapter = ConnectCommentsAdapter(this, commentArray!!,connectData)
                commentRecycler!!.adapter = commentsAdapter
                commentRecycler!!.smoothScrollToPosition(0)
                linearLayoutManager!!.scrollToPositionWithOffset(0, 0)
            }


        }

    }

    /** It checks the commentsDetails data,
     *  if it is not empty then notify to CommentAdapter
     */
    override fun setCommentsByIdAdapterOnLoadMore(commentsDetails: CommentsPojo)
    {
        if(commentsDetails.data.size>0) {
            println("size of near by issue ${commentsDetails.data.size}")

            commentArray!!.addAll(commentsDetails.data)

            println("near by issue $commentArray")

            commentsAdapter!!.notifyDataSetChanged()
        }

    }

    /** It checks the connectDetails data,
     *  if it is not empty then set data to respective views
     */
    override fun setConnectByIdAdapter(connectDetails: ConnectDetailsPojo) {
        try {
            if (connectDetails.data != null) {

                connectData = connectDetails


                if(connectDetails.data.likeByUser != null && connectDetails.data.likeByUser)
                {
                    upvote!!.visibility = View.GONE
                    downvote!!.visibility = View.VISIBLE
                }


                if(connectDetails.data.connectCategory!=null)
                {
                    if(connectDetails.data.connectCategory.name!=null) {
                        title!!.text = connectDetails.data.connectCategory.name
                    }
                }

                if (connectDetails.data.title != null) {
                    connectTitle!!.text = connectDetails.data.title
                }
                if(connectDetails.data.user.userLoginType.userType.equals("individual",ignoreCase = true)) {
                    if (connectDetails.data.user.firstName != null && connectDetails.data.user.firstName.isNotEmpty()) {
                        name!!.text = connectDetails.data.user.firstName
                    }


                    if (connectDetails.data.user.lastName != null && connectDetails.data.user.lastName.isNotEmpty()) {
                        name!!.text =  connectDetails.data.user.lastName
                    }

                    if (connectDetails.data.user.firstName != null && connectDetails.data.user.lastName != null) {
                        name!!.text =
                            connectDetails.data.user.firstName + " ${connectDetails.data.user.lastName}"
                    }

                }
                else{
                    name!!.text = connectDetails.data.user.organizationName
                }
                if (connectDetails.data.createdAt != null && connectDetails.data.createdAt.isNotEmpty()) {
                    time!!.text = convertStringToDateStringFull(connectDetails.data.createdAt)
                }

                if (connectDetails.data.description != null && connectDetails.data.description.isNotEmpty()) {
                    connectDescription!!.text = connectDetails.data.description
                }

                if (connectDetails.data.imageUrls != null && connectDetails.data.imageUrls.isNotEmpty()) {
                    Glide.with(this)
                        .load(connectDetails.data.imageUrls[0])
//                        .placeholder(R.drawable.loading_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(connectImage!!)

                    play!!.visibility = View.GONE
                }
                else if(connectDetails.data.videoUrls != null && connectDetails.data.videoUrls.isNotEmpty())
                {
                    Glide
                        .with(context)
                        .load(connectDetails.data.videoUrlThumbnails[0])
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .placeholder(R.drawable.loading_image)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(connectImage!!)

                    play!!.visibility =View.VISIBLE

                }
                else{
                    play!!.visibility = View.GONE
                    connectImage!!.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.placeholder))
                }

                if (connectDetails.data.user.profileUrl != null && connectDetails.data.user.profileUrl.isNotEmpty()) {
                    Picasso.with(this)
                        .load(connectDetails.data.user.profileUrl)
                        .placeholder(R.drawable.background)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(profilePic)
                }

                if(connectDetails.data.comments != null)
                {
                    connectCount!!.text = "${connectDetails.data.comments}  c o m m e n t s"
                }
                if(connectDetails.data.likes!=null)
                {
                    upvoteCount!!.text = "${connectDetails.data.likes}  L i k e s,  "
                }

                profilePic!!.setOnClickListener {
                    val intent = Intent(context, BadgesActivity::class.java)
                    intent.putExtra(Constants.USER_ID,connectDetails.data.user._id)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }


                name!!.setOnClickListener {
                    val intent = Intent(context, BadgesActivity::class.java)
                    intent.putExtra(Constants.USER_ID,connectDetails.data.user._id)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }


            }
        } catch (e: Exception) {
           e.printStackTrace()
        }
    }

    /** It takes date string input and convert it to data and time string */
    fun convertStringToDateStringFull(date1: String): String {
        var convertString = ""
        val tz = TimeZone.getTimeZone("UTC")
        var date: Date? = null
        //String FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        val FORMAT_DATETIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        // String FORMAT_DATETIME = "yyyy-MM-dd'T'hh:mm:ss";
        val sdf = SimpleDateFormat(FORMAT_DATETIME)
        val sdf2 = SimpleDateFormat("dd MMM yyyy, hh:mm a")
        sdf.setTimeZone(tz)
        // sdf2.setTimeZone(tz);
        try {
            date = sdf.parse(date1)
            convertString = sdf2.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return convertString
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
        commentArray!!.clear()
    }


    /** It opens activity depends on condition
     */
    override fun setOnDelete()
    {
        if(newInfoPublished)
        {
            val intent = Intent(this, SummaryDetailsActivity::class.java)
            intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.PUBLISH_CONNECT)
            //startActivity(intent)
            intent.putExtra("delete",true)
            intent.putExtra("connectId",connectId)
            setResult(201,intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if (myConnect)
        {
            val intent = Intent(this, MyConnectActivity::class.java)
            intent.putExtra("delete",true)
            intent.putExtra("connectId",connectId)
            setResult(201,intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else {

            val intent = Intent(this, ConnectIssueActivity::class.java)
            intent.putExtra("connectComment", false)
            intent.putExtra("delete",true)
            intent.putExtra("connectId",connectId)
            setResult(201,intent)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()


        }

    }


    /** It call below functions using corresponding presenters */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == 201) {

            val isEdit = data!!.getBooleanExtra("isEdit", false)
            val connectId = data.getStringExtra("connectCategoryId")

            if (isEdit) {
                if (connectId != null && connectId!!.isNotEmpty()) {

                    println("connect id $connectId")

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                            connectIdPresenter!!.onConnectById(this, connectId!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {

                                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                            commentPresenter!!.onCommentsById(
                                this,
                                connectId!!,
                                "connect",
                                pageNo,
                                PAGE_SIZE
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }
        }
        else  if(requestCode == 1 && resultCode== Activity.RESULT_OK)
        {
            resetValue()

            val connectId = data!!.getStringExtra("connectId")

            if (connectId != null && connectId!!.isNotEmpty()) {

                println("connect id $connectId")

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {

                           // ConstantMethods.showProgessDialog(this, "Please Wait...")
                        connectIdPresenter!!.onConnectById(this, connectId!!)
                    }
                } catch (e: Exception)
                {
                    e .printStackTrace()
                }

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {
                           //ConstantMethods.showProgessDialog(this, "Please Wait...")
                        commentPresenter!!.onCommentsById(
                            this,
                            connectId!!,
                            "connect",
                            pageNo,
                            PAGE_SIZE
                        )
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }

            }


        }

    }

    /** It goes back to previous activity depends on condition   */
    override fun onBackPressed() {
        super.onBackPressed()

        ConstantMethods.hideKeyBoard(this,this@ConnectDetailsActivity)

        if(newInfoPublished)
        {
            val intent = Intent(this, SummaryDetailsActivity::class.java)
            intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.PUBLISH_CONNECT)
            //startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else if(notification)
        {
            val intent = Intent(this, NotificationActivity::class.java)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
        else {

            val intent = Intent(this, ConnectIssueActivity::class.java)
            intent.putExtra("connectComment", false)
           // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }
    }

}