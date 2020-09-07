package com.we.beyond.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.database.DatabaseReference
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.adapter.MyReportedResolutionAdapter
import com.we.beyond.Interface.*
import com.we.beyond.R
import com.we.beyond.model.*
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.presenter.reportResolved.*
import com.we.beyond.ui.LocationActivity
import com.we.beyond.ui.MediaViewPagerActivity
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.dashboard.NotificationActivity
import com.we.beyond.ui.dashboard.SummaryDetailsActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import de.hdodenhof.circleimageview.CircleImageView

/**
 * It shows the reported resolution details perform further actions
 */
class ReportedResolutionDetailsActivity : AppCompatActivity(),
    MarAsResolvedDetailsPresenter.IMarAsResolvedDetailsView,
    LikePresenter.ILikeView, OnLikeDislikeListener,
    OnIssuesResolvedListener, MarkAsResolvedPresenter.IMarkAsResolvedView,
    ReportAbusePresenter.IReportAbuseView,
    OnReportAbuseListener {

    var context: Context = this

    /** initialize respected implementors */
    var isseIdPresenter: MarAsResolvedDetailsImpl? = null
    var likeDislikePresenter: LikeImpl? = null
    var markAsResolvedPresenter: MarkAsResolvedImpl? = null
    var reportAbusePresenter: ReportAbuseImpl? = null

    var issuesData: ReportedResolutionDetailsPojo? = null

    /** pull to refresh layout */
    var pullToRefresh: SwipeRefreshLayout? = null

    /** init image view */
    var back: ImageView? = null
    var profilePic: CircleImageView? = null
    var issueImage: ImageView? = null
    var closeAbuse: ImageView? = null
    var play: ImageView? = null

    // var commentImage: ImageView? = null
    //var sendComment: ImageView? = null
    //var gatheringImage: ImageView? = null
    //var close: ImageView? = null
    //var more: ImageView? = null
    //var like: ImageView? = null
    //var dislike: ImageView? = null
    //var resolved: ImageView? = null
    //var unResolved: ImageView? = null

    /** init edit text */
    var abuseEditText: EditText? = null

    /** init button */
    var abuse: Button? = null


    /** init text view */
    var title: TextView? = null
    var issueNumber: TextView? = null
    var issueTitle: TextView? = null
    var issueLocation: TextView? = null
    var issueDistance: TextView? = null
    var name: TextView? = null
    var time: TextView? = null
    var issueDescription: TextView? = null
    var reportResolvedCount: TextView? = null
    var categoryTitle: TextView? = null
    var resolvedFlag: TextView? = null
    var moreTitle: TextView? = null
    var reportAbuse: TextView? = null
    var edit: TextView? = null

    //var getDirections: TextView? = null


    /** init recycler view, adapter and array list */
    var resolutionRecycler: RecyclerView? = null
    var resolutionArray: ArrayList<Resolution>? = null
    var resolutionAdapter: MyReportedResolutionAdapter? = null


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

    /** init variables  */
    var issueId: String? = null
    var getIssueNumber: Int? = null
    var getDistance: String? = null
    var commentId: String = ""
    var userId: String = ""
    var commentPosition: Int? = null
    var commentIssue: Boolean = false
    var unresolvedIssue: Boolean = false
    var resolvedIssue: Boolean = false
    var isIssueResolved = false
    var notification: Boolean = false

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var rootRef: DatabaseReference

    /** init relative layout */
    // var operationLayout: RelativeLayout? = null
    //var commentLayout: RelativeLayout? = null
    var moreLayout: RelativeLayout? = null
    var mainLayout: RelativeLayout? = null
    var reportAbuseLayout: RelativeLayout? = null
    var locationLayout: RelativeLayout? = null

    //var backgroundLayout: RelativeLayout? = null


    /** init scroll view */
    var scrollView: ScrollView? = null


    /* */
    /** init text input layout *//*
    var commentTextLayout : TextInputLayout?=null*/



    companion object {
        val PAGE_SIZE = 5
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reported_resolution_details)

        /** array initialization */
        resolutionArray = ArrayList()
        resolutionArray!!.clear()

        /** initialize implementation */
        isseIdPresenter = MarAsResolvedDetailsImpl(this)
        likeDislikePresenter = LikeImpl(this)
        markAsResolvedPresenter = MarkAsResolvedImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)


        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** get data */
        getData()

        /** get current location */
        getLocation()

        /** Get all stored data using intent and assign it respectively */
        issueId = intent.getStringExtra("issueId")
        getIssueNumber = intent.getIntExtra("issueNumber", 0)
        commentIssue = intent.getBooleanExtra("comment", false)
        unresolvedIssue = intent.getBooleanExtra("unresolvedIssue", false)
        resolvedIssue = intent.getBooleanExtra("resolvedIssue", false)
        notification = intent.getBooleanExtra("notification", false)
        getDistance = intent.getStringExtra("distance")

        /* if (commentIssue) {
             operationLayout!!.visibility = View.GONE
             commentLayout!!.visibility = View.VISIBLE
             comment!!.isCursorVisible = true
             backgroundLayout!!.visibility = View.VISIBLE
             ConstantMethods.showKeyBoard(this)

         }
 */

        if (issueId != null && issueId!!.isNotEmpty()) {
            resetValue()

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    isseIdPresenter!!.onMarAsResolvedDetails(this, pageNo, 10000, issueId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

        if (getIssueNumber != null && getIssueNumber != 0) {
            title = findViewById(R.id.txt_title)
            title!!.text = "Issue #$getIssueNumber"
            title!!.typeface = ConstantFonts.raleway_semibold
        }


    }


    fun getData() {
        println("comment id  $commentId")
/*
        if (commentId != null && commentId.isNotEmpty()) {
            operationLayout!!.visibility = View.GONE
            commentLayout!!.visibility = View.VISIBLE
            comment!!.isCursorVisible = true
            backgroundLayout!!.visibility = View.VISIBLE
        }*/

    }

    /** It issue id is not empty then
     * call onMyResolutionDetails function of issue id presenter  */
    override fun onResolved() {
        if (issueId != null && issueId!!.isNotEmpty()) {
            resetValue()

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                    isseIdPresenter!!.onMarAsResolvedDetails(this, pageNo, 10000, issueId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }


    override fun summaryType(type: String) {

    }

    /** It takes the id as input and call postDataToServerOnResolved function */
    override fun onApproved(_id: String) {
        try {
            val jsonObject = JsonObject()
            jsonObject.addProperty("resolutionId", _id)

            if (ConstantMethods.checkForInternetConnection(this@ReportedResolutionDetailsActivity)) {
                postDataToServerOnResolved(jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It takes the json object as input and send to onMarkAsResolvedData function of markAsResolved  presenter */
    private fun postDataToServerOnResolved(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                markAsResolvedPresenter!!.onMarkAsResolvedData(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "comment")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@ReportedResolutionDetailsActivity)) {

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

                if (ConstantMethods.checkForInternetConnection(context)) {
                    postDataToServerOnDislike(jsonObject)
                }
            } catch (e: Exception) {

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It opens abuse layout with animation */
    override fun ReportAbuse(_id: String, s: String) {
        issueId = _id
        reportAbuseLayout!!.visibility = View.VISIBLE
        reportAbuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))

    }

    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous activity depends on conditions  when click on it */
        back!!.setOnClickListener {

            ConstantMethods.hideKeyBoard(this, this)

            if (unresolvedIssue) {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UNRESOLVED_ISSUES)
                // startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else if (resolvedIssue) {
                val intent = Intent(this, SummaryDetailsActivity::class.java)
                intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.RESOLVED_ISSUES)
                //startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else if (notification) {
                val intent = Intent(this, NotificationActivity::class.java)
                // startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else {
                if (issuesData!!.data.issue.resolutionsCount != 0) {
                    println("0 resolution")
                    val intent = Intent(this, ReportedResolutionActivity::class.java)
                    intent.putExtra("issueId", issueId)
                    setResult(201, intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                } else {
                    val intent = Intent(this, ReportedResolutionActivity::class.java)
                    setResult(202, intent)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }
            }
        }

        /* commentRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                     this@ReportedResolutionDetailsActivity.runOnUiThread(object : Runnable {
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
         })*/

        /* commentImage!!.setOnClickListener {
             operationLayout!!.visibility = View.GONE
             commentLayout!!.visibility = View.VISIBLE

             backgroundLayout!!.visibility = View.VISIBLE
             ConstantMethods.showKeyBoard(this)

         }*/

        /* sendComment!!.setOnClickListener {
             try {

                 if (ConstantMethods.checkForInternetConnection(this@ReportedResolutionDetailsActivity)) {
                     resetValue()
                     getDataToPost()
                     println("comment position $commentPosition")
                     if (commentPosition != null) {

                     } else {
                         scrollView!!.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS)
                         //scrollView!!.fullScroll(ScrollView.FOCUS_UP)
                         ConstantMethods.hideKeyBoard(this, this)
                         backgroundLayout!!.visibility = View.GONE
                     }
                 }

             } catch (e: Exception) {

             }
         }
 */

        /* gatheringImage!!.setOnClickListener {
             val intent = Intent(this, CreateGatheringActivity::class.java)
             startActivity(intent)
             overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
             finish()
         }*/

        /*  more!!.setOnClickListener {
              moreLayout!!.visibility = View.VISIBLE
              overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down)

          }

          close!!.setOnClickListener {
              moreLayout!!.visibility = View.GONE
              overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up)
          }*/

        /* getDirections!!.setOnClickListener {
             if (issuesData!!.data.issue.coordinates != null) {
                 val uri =
                     "http://maps.google.com/maps?saddr=" + "$latitude" + "," + "$longitude" + "&daddr=" + "${issuesData!!.data.issue.coordinates[0]}" + "," + "${issuesData!!.data.issue.coordinates[1]}"
                 val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                 intent.setClassName(
                     "com.google.android.apps.maps",
                     "com.google.android.maps.MapsActivity"
                 )
                 startActivity(intent)
             }
         }*/

        /* like!!.setOnClickListener {
             dislike!!.visibility = View.VISIBLE
             like!!.visibility = View.GONE

             try {

                 val jsonObject = JsonObject()
                 jsonObject.addProperty("type", "issue")
                 jsonObject.addProperty("typeId", issueId)


                 if (ConstantMethods.checkForInternetConnection(this@ReportedResolutionDetailsActivity)) {
                     ConstantMethods.showProgessDialog(this, "Please Wait...")
                     postDataToServerOnLike(jsonObject)
                 }


             } catch (e: Exception) {
                 e.printStackTrace()
             }


         }

         dislike!!.setOnClickListener {


             // if (downVote) {
             dislike!!.visibility = View.GONE
             like!!.visibility = View.VISIBLE
             // }

             try {

                 val jsonObject = JsonObject()
                 jsonObject.addProperty("type", "issue")
                 jsonObject.addProperty("typeId", issueId)


                 try {

                     if (ConstantMethods.checkForInternetConnection(context)) {
                         postDataToServerOnDislike(jsonObject)
                     }
                 } catch (e: Exception) {

                 }


             } catch (e: Exception) {
                 e.printStackTrace()
             }

         }


         unResolved!!.setOnClickListener {
             ConstantMethods.showWarning(this, "Issue Resolved", "Issue already resolved")
         }


         resolved!!.setOnClickListener {
             val intent = Intent(context, ReportResolvedActivity::class.java)
             intent.putExtra("issueId", issueId)
             startActivity(intent)
             overridePendingTransition(
                 R.anim.slide_in_right,
                 R.anim.slide_out_left
             )

         }*/

        /** If image or video url is not empty then opens MediaViewPagerActivity to see the image and video
         * else show warning dialog  */
        issueImage!!.setOnClickListener {

            if (issuesData!!.data.issue.imageUrls != null && issuesData!!.data.issue.imageUrls.isNotEmpty() || issuesData!!.data.issue.videoUrls != null && issuesData!!.data.issue.videoUrls.isNotEmpty()) {
                val intent = Intent(context, MediaViewPagerActivity::class.java)
                intent.putStringArrayListExtra("mediaImage", issuesData!!.data.issue.imageUrls)
                intent.putStringArrayListExtra("mediaVideo", issuesData!!.data.issue.videoUrls)
                startActivity(intent)
                overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            } else {
                ConstantMethods.showWarning(this, "No Data", "Images not available for this issue.")
            }
        }

        /*  comment!!.setOnTouchListener(object : View.OnTouchListener {
              override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                  val DRAWABLE_LEFT = 0
                  val DRAWABLE_TOP = 1
                  val DRAWABLE_RIGHT = 2
                  val DRAWABLE_BOTTOM = 3

                  ConstantMethods.hideKeyBoard(context,this@ReportedResolutionDetailsActivity)
                  if (event!!.getAction() === MotionEvent.ACTION_UP) {

                      if (event!!.getRawX() >= (comment!!.getRight() - comment!!.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                          // your action here
                          commentLayout!!.visibility = View.GONE
                          operationLayout!!.visibility = View.VISIBLE
                          backgroundLayout!!.visibility = View.GONE
                          comment!!.setText("")

                          return true

                      }
                  }
                  return false


              }
          })*/


        /** It closes the more layout with animation
         * get stored user id and check with issue user id, if matches then we cannot abuse the issue
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
                if (userId == issuesData!!.data.issue.user._id) {
                    ConstantMethods.showWarning(context, "", "You can not abuse your issue.")
                } else {

                    reportAbuseLayout!!.visibility = View.VISIBLE
                    reportAbuseLayout!!.startAnimation(
                        AnimationUtils.loadAnimation(
                            this,
                            R.anim.slide_in_up
                        )
                    )
                }

            } catch (e: Exception) {

            }
        }


        /** It closes the abuse layout with animation and call getDataToPostOnAbuse() */
        abuse!!.setOnClickListener {
            try {
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
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        /** It closes the report abuse layout with animation */
        closeAbuse!!.setOnClickListener {
            reportAbuseLayout!!.visibility = View.GONE
            reportAbuseLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )
        }


        /** It calls again onMarAsResolvedDetails function of issue id presenter
         * when pull it from top */
        pullToRefresh!!.setOnRefreshListener {

            if (issueId != null && issueId!!.isNotEmpty()) {
                resetValue()

                try {
                    if (ConstantMethods.checkForInternetConnection(context)) {
                        ConstantMethods.showProgessDialog(this, "Please Wait...")
                        isseIdPresenter!!.onMarAsResolvedDetails(this, pageNo, 10000, issueId!!)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }

            pullToRefresh!!.isRefreshing = false
        }

        /**It opens LocationActivity */
        locationLayout!!.setOnClickListener {
            val intent = Intent(context, LocationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            EasySP.init(context).put("lat", issuesData!!.data.issue.coordinates[0])
            EasySP.init(context).put("long", issuesData!!.data.issue.coordinates[1])
            EasySP.init(context).putBoolean("resolved", issuesData!!.data.issue.resolved)
        }

    }

    /** It calls postDataToServerOnAbuse function with json object */
    private fun getDataToPostOnAbuse() {
        try {

            if (abuseEditText!!.text.trim() != null && abuseEditText!!.text.trim().isNotEmpty()) {
                var abuse = abuseEditText!!.text.trim()

                if (ConstantMethods.checkForInternetConnection(this)) {
                    ConstantMethods.showProgessDialog(
                        this,
                        "Please Wait..."
                    )
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "resolution")
                    jsonObject.addProperty("typeId", issueId)
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

                    println("post data $jsonObject")


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
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** It takes the json object as input and send to onLike function of like dislike  presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                likeDislikePresenter!!.onLike(this, jsonObject)
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
                        ConstantMethods.showProgessDialog(this, "Please Wait...")
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


    /* fun getDataToPost() {
         try {
             if (commentId != null && commentId.isNotEmpty()) {
                 if (comment!!.text != null && comment!!.text.isNotEmpty()) {
                     val jsonObject = JsonObject()
                     jsonObject.addProperty("issueId", issueId!!)
                     jsonObject.addProperty("text", comment!!.text.toString().trim())
                     jsonObject.addProperty("parentId", commentId)
                     commentId = ""

                     try {
                         if (ConstantMethods.checkForInternetConnection(context)) {
                             postDataToServer(jsonObject)
                         }
                     }
                     catch (e : Exception)
                     {
                         e.printStackTrace()
                     }

                 } else {
                     ConstantMethods.showWarning(this, "Empty Data", "Please give us your comment.")
                 }
             } else {
                 val jsonObject = JsonObject()
                 jsonObject.addProperty("issueId", issueId)
                 jsonObject.addProperty("text", comment!!.text.toString().trim())

                 try {
                     if (ConstantMethods.checkForInternetConnection(context)) {
                         postDataToServer(jsonObject)
                     }
                 }
                 catch (e : Exception)
                 {
                     e.printStackTrace()
                 }
             }


         } catch (e: Exception) {
             e.printStackTrace()
         }
     }

     private fun postDataToServer(jsonObject: JsonObject) {
         try {

             if (ConstantMethods.checkForInternetConnection(context)) {
                 createCommentPresenter!!.onCommentCreated(this, jsonObject)
             }
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }

     override fun setCommentAdapter() {
         try {
             operationLayout!!.visibility = View.VISIBLE
             commentLayout!!.visibility = View.GONE
             backgroundLayout!!.visibility = View.GONE
             comment!!.setText("")

             try {
                 if (ConstantMethods.checkForInternetConnection(context)) {
                     //commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)
                 }
             }
             catch (e : Exception)
             {
                 e.printStackTrace()
             }

             ConstantMethods.hideKeyBoard(this,this)

         } catch (e: Exception) {

         }
     }*/


    /* private fun loadMore() {
         try {


             if (ConstantMethods.checkForInternetConnection(this@ReportedResolutionDetailsActivity)) {

                 postDataToServerOnLoadMore(issueId!!, "issue", ++page, PAGE_SIZE)
             }

         } catch (e: Exception) {

             e.printStackTrace()
         }

     }

     private fun postDataToServerOnLoadMore(issueId: String?, s: String, i: Int, pageSize: Int) {
         try {

             if (ConstantMethods.checkForInternetConnection(context)) {
                // commentPresenter!!.onCommentsByIdOnLoadMore(this, issueId!!, s, i, pageSize)
             }
         } catch (e: Exception) {

         }
     }*/

    /** ui listeners */
    private fun initElementsWithIds() {

        /** ids of image view */
        back = findViewById(R.id.img_back)
        profilePic = findViewById(R.id.img_profile_pic)
        issueImage = findViewById(R.id.img_issue)
        closeAbuse = findViewById(R.id.img_close_window)
        play = findViewById(R.id.img_play)

        /*commentImage = findViewById(R.id.img_comment)
        sendComment = findViewById(R.id.img_send_comment)
        gatheringImage = findViewById(R.id.img_gathering)
        more = findViewById(R.id.img_more)
        close = findViewById(R.id.img_close)
        like = findViewById(R.id.img_support)
        dislike = findViewById(R.id.img_dislike)
        resolved = findViewById(R.id.img_resolved)
        unResolved = findViewById(R.id.img_already_resolved)*/

        /** ids of pull to refresh layout */
        pullToRefresh = findViewById(R.id.pullToRefresh)

        /** ids of text view */

        issueNumber = findViewById(R.id.txt_issue_number)
        //issueNumber!!.typeface = ConstantFonts.raleway_semibold

        issueTitle = findViewById(R.id.txt_issue_title)
        issueTitle!!.typeface = ConstantFonts.raleway_semibold

        issueLocation = findViewById(R.id.txt_issue_location)
        issueLocation!!.typeface = ConstantFonts.raleway_semibold

        issueDistance = findViewById(R.id.txt_issue_distance)
        issueDistance!!.typeface = ConstantFonts.raleway_semibold

        name = findViewById(R.id.txt_name)
        name!!.typeface = ConstantFonts.raleway_regular

        time = findViewById(R.id.txt_time)
        time!!.typeface = ConstantFonts.raleway_regular

        issueDescription = findViewById(R.id.txt_issue_description)
        issueDescription!!.typeface = ConstantFonts.raleway_medium

        reportResolvedCount = findViewById(R.id.txt_reported_resolution_Count)
        reportResolvedCount!!.typeface = ConstantFonts.raleway_semibold


        categoryTitle = findViewById(R.id.txt_category_title)
        categoryTitle!!.typeface = ConstantFonts.raleway_regular

        resolvedFlag = findViewById(R.id.txt_flag)
        resolvedFlag!!.typeface = ConstantFonts.raleway_regular

        reportAbuse = findViewById(R.id.txt_report_abuse)
        reportAbuse!!.typeface = ConstantFonts.raleway_semibold

        /* moreTitle = findViewById(R.id.txt_more)
         moreTitle!!.typeface = ConstantFonts.raleway_regular*/

        /* getDirections = findViewById(R.id.txt_get_direction)
         getDirections!!.typeface = ConstantFonts.raleway_semibold*/

        /** ids of recycler view */
        resolutionRecycler = findViewById(R.id.resolutionRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resolutionRecycler!!.setHasFixedSize(true)
        resolutionRecycler!!.layoutManager = linearLayoutManager


        /** ids of relative layout */
        //operationLayout = findViewById(R.id.operationLayout)
        // commentLayout = findViewById(R.id.commentLayout)
        moreLayout = findViewById(R.id.moreLayout)
        mainLayout = findViewById(R.id.mainLayout)
        reportAbuseLayout = findViewById(R.id.reportAbuseLayout)
        locationLayout = findViewById(R.id.issueDetailsLayout)

        // backgroundLayout = findViewById(R.id.backgroundLayout)

        /* */
        /** ids of text input layout *//*
        commentTextLayout = findViewById(R.id.commentTextLayout)*/

        /** ids of edit text */

        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of scroll view */
        scrollView = findViewById(R.id.nearByIssueDetailsScrollView)

        /** ids of button */
        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold


        /** ids of scroll view */
        scrollView = findViewById(R.id.nearByIssueDetailsScrollView)


    }

    /* override fun setCommentsByIdAdapter(commentsDetails: CommentsPojo) {

         if (commentsDetails.data.size == 0) {
             *//*commentLayout!!.visibility = View.VISIBLE
            operationLayout!!.visibility = View.GONE*//*

        } else {

            commentArray!!.clear()

            println("size of near by issue ${commentsDetails.data.size}")

            commentArray!!.addAll(commentsDetails.data)


            println("near by issue $commentArray")

            isseIdPresenter!!.onMarAsResolvedDetails(this,pageNo,10000, issueId!!)

            if (commentPosition != null) {

                commentsAdapter = CommentAdapter(this, commentArray!!,userId,isIssueResolved)
                commentRecycler!!.adapter = commentsAdapter
                commentRecycler!!.smoothScrollToPosition(commentPosition!!)
                commentRecycler!!.scrollToPosition(commentPosition!! - 2)
                linearLayoutManager!!.scrollToPositionWithOffset(
                    commentPosition!!,
                    commentPosition!!
                )
            } else {
                commentsAdapter = CommentAdapter(this, commentArray!!,userId,isIssueResolved)
                commentRecycler!!.adapter = commentsAdapter
                commentRecycler!!.smoothScrollToPosition(0)
                linearLayoutManager!!.scrollToPositionWithOffset(0, 0)
            }


        }

    }*/


    /*  override fun setCommentsByIdAdapterOnLoadMore(commentsDetails: CommentsPojo) {
          if (commentsDetails.data.size > 0) {
              println("size of near by issue ${commentsDetails.data.size}")

              commentArray!!.addAll(commentsDetails.data)

              println("near by issue $commentArray")

              commentsAdapter!!.notifyDataSetChanged()
          }

      }
  */
    /** It checks the issueDetails data,
     *  if it is not empty then set it to respective views
     */
    override fun setMarAsResolvedDetailsAdapter(issueDetails: ReportedResolutionDetailsPojo) {

        try {
            if (issueDetails.data != null) {

                issuesData = issueDetails

                userId = issueDetails.data.issue.user._id

                /* if(issueDetails.data.issue.likeByUser != null && issueDetails.data.issue.likeByUser)
                 {
                     like!!.visibility = View.GONE
                     dislike!!.visibility = View.VISIBLE
                 }*/


                if (issueDetails.data.issue.issueNumber != null) {
                    issueNumber!!.text = "# ${issueDetails.data.issue.issueNumber}"

                    title = findViewById(R.id.txt_title)
                    title!!.text = "Issue #${issueDetails.data.issue.issueNumber}"
                    title!!.typeface = ConstantFonts.raleway_semibold


                }

                if (issueDetails.data.issue.title != null) {
                    issueTitle!!.text = issueDetails.data.issue.title
                }
                if (issueDetails.data.issue.address != null) {
                    issueLocation!!.text = issueDetails.data.issue.address
                }
                if (issueDetails.data.issue.city != null) {
                    issueLocation!!.text = issueDetails.data.issue.city
                }
                if (issueDetails.data.issue.address != null && issueDetails.data.issue.city != null) {
                    issueLocation!!.text =
                        issueDetails.data.issue.address + ", ${issueDetails.data.issue.city}"
                }


                if (getDistance != null && getDistance!!.isNotEmpty()) {
                    issueDistance!!.text = getDistance + "Km Away"
                }

                if (issueDetails.data.issue.user.userLoginType.userType.equals(
                        "individual",
                        ignoreCase = true
                    )
                ) {
                    name!!.text =
                        issueDetails.data.issue.user.firstName + " ${issueDetails.data.issue.user.lastName}"
                    name!!.typeface = ConstantFonts.raleway_regular
                } else {
                    name!!.text = issueDetails.data.issue.user.organizationName
                    name!!.typeface = ConstantFonts.raleway_regular

                }


                if (issueDetails.data.issue.createdAt != null && issueDetails.data.issue.createdAt.isNotEmpty()) {
                    time!!.text =
                        ConstantMethods.convertStringToDateStringFull(issueDetails.data.issue.createdAt)
                }

                if (issueDetails.data.issue.description != null && issueDetails.data.issue.description.isNotEmpty()) {
                    issueDescription!!.text = issueDetails.data.issue.description
                }

                if (issueDetails.data.issue.imageUrls != null && issueDetails.data.issue.imageUrls.isNotEmpty()) {
                    Glide.with(this)
                        .load(issueDetails.data.issue.imageUrls[0])
                        .placeholder(R.drawable.loading_image)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(issueImage!!)

                    play!!.visibility = View.GONE

                } else if (issueDetails.data.issue.videoUrls != null && issueDetails.data.issue.videoUrls.isNotEmpty()) {
                    Glide
                        .with(context)
                        .load(issueDetails.data.issue.videoUrlThumbnails[0])
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.processing_video)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(issueImage!!)

                    play!!.visibility = View.VISIBLE

                } else {
                    play!!.visibility = View.GONE
                    issueImage!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.placeholder
                        )
                    )
                }

                if (issueDetails.data.issue.user.profileUrl != null && issueDetails.data.issue.user.profileUrl.isNotEmpty()) {
                    Picasso.with(this)
                        .load(issueDetails.data.issue.user.profileUrl)
                        .placeholder(R.drawable.background)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(profilePic)
                }

                reportResolvedCount!!.text =
                    "   ${issueDetails.data.issue.resolutionsCount}  R e p o r t e d   r e s o l u t i o n"



                if (issueDetails.data.issue.category.name != null && issueDetails.data.issue.category.name.isNotEmpty()) {
                    categoryTitle!!.text = issueDetails.data.issue.category.name
                    categoryTitle!!.typeface = ConstantFonts.raleway_regular
                }

                if (issueDetails.data.issue.resolved) {
                    resolvedFlag!!.visibility = View.VISIBLE
                    resolvedFlag!!.text = "Resolved"

                    //resolved!!.visibility = View.GONE
                    //unResolved!!.visibility = View.VISIBLE

                    isIssueResolved = true
                } else {
                    resolvedFlag!!.visibility = View.GONE

                    //resolved!!.visibility = View.VISIBLE
                    //unResolved!!.visibility = View.GONE

                    isIssueResolved = false
                }


                // isseIdPresenter!!.onMarAsResolvedDetails(this,pageNo,10000, issueId!!)

                println("resolution ${issueDetails.data.resolutions}")

                if (issueDetails.data.resolutions != null) {

                    EasySP.init(this)
                        .putInt("resolutionCount", issueDetails.data.issue.resolutionsCount)

                    resolutionArray!!.addAll(issueDetails.data.resolutions)

                    if (commentPosition != null) {

                        resolutionAdapter = MyReportedResolutionAdapter(
                            this,
                            resolutionArray!!,
                            userId,
                            isIssueResolved
                        )
                        resolutionRecycler!!.adapter = resolutionAdapter
                        resolutionRecycler!!.smoothScrollToPosition(commentPosition!!)
                        resolutionRecycler!!.scrollToPosition(commentPosition!! - 2)
                        linearLayoutManager!!.scrollToPositionWithOffset(
                            commentPosition!!,
                            commentPosition!!
                        )
                    } else {
                        resolutionAdapter = MyReportedResolutionAdapter(
                            this,
                            resolutionArray!!,
                            userId,
                            isIssueResolved
                        )
                        resolutionRecycler!!.adapter = resolutionAdapter
                        resolutionRecycler!!.smoothScrollToPosition(0)
                        linearLayoutManager!!.scrollToPositionWithOffset(0, 0)
                    }
                }

                /** It opens BadgesActivity */
                profilePic!!.setOnClickListener {
                    val intent = Intent(context, BadgesActivity::class.java)
                    intent.putExtra(Constants.USER_ID, issueDetails.data.issue.user._id)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }


                /** It opens BadgesActivity */
                name!!.setOnClickListener {
                    val intent = Intent(context, BadgesActivity::class.java)
                    intent.putExtra(Constants.USER_ID, issueDetails.data.issue.user._id)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        resolutionArray!!.clear()
    }

    /** It gets the current location using gps provider,
     */
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        try {

            val locationManager =
                this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                2000,
                10f,
                locationListener
            )
            var currentLocation = getLastKnownLocation()
//            latitude = currentLocation.latitude
//            longitude = currentLocation.longitude
            if (currentLocation != null) {
                latitude = currentLocation.latitude
                longitude = currentLocation.longitude


            }


        } catch (e: Exception) {

        }
    }

    /** It returns the last known location  */
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        var mLocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = mLocationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.getAccuracy() < bestLocation.accuracy) {
                // Found best last known location: %s", l);
                bestLocation = l
            }
        }
        return bestLocation
    }

    /** It is listener which store the location data
     * to firebase database reference whenever location changes
     */
    private val locationListener = object : LocationListener {
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String?) {

        }

        override fun onProviderDisabled(provider: String?) {

        }

        override fun onLocationChanged(location: Location) {
            try {
                val deviceFcmToken: String =
                    EasySP.init(context).getString(ConstantEasySP.SP_FCM_DEVICE_TOKEN)
                if (deviceFcmToken != null && deviceFcmToken.length > 0) {
                    val latLongPojo =
                        LatLongPojo(location.latitude, location.longitude, deviceFcmToken)
                    rootRef.setValue(latLongPojo)
                }

            } catch (e: Exception) {

            }

        }
    }

/*
    override fun onResume() {
        super.onResume()
        issueId = intent.getStringExtra("issueId")

        if (issueId != null && issueId!!.isNotEmpty()) {

            nearByIssieIdPresenter!!.onNearByIssueById(this, issueId!!)
            commentPresenter!!.onCommentsById(this, issueId!!, "issue", pageNo, PAGE_SIZE)

        }

    }*/

    /** It call onMarAsResolvedDetails function using issue id presenter */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            resetValue()

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    isseIdPresenter!!.onMarAsResolvedDetails(this, pageNo, 10000, issueId!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }

    /** It goes back to previous activity depends on condition   */
    override fun onBackPressed() {
        //  super.onBackPressed()

        ConstantMethods.hideKeyBoard(this, this)

        if (unresolvedIssue) {
            val intent = Intent(this, SummaryDetailsActivity::class.java)
            intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.UNRESOLVED_ISSUES)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (resolvedIssue) {
            val intent = Intent(this, SummaryDetailsActivity::class.java)
            intent.putExtra(Constants.DASHBOARD_SUMMARY, Constants.RESOLVED_ISSUES)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else if (notification) {
            val intent = Intent(this, NotificationActivity::class.java)
            // startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        } else {
            /*if(issuesData!!.data.issue.resolutionsCount!=0) {*/
            val resolutionCount = EasySP.init(this).getInt("resolutionCount")
            println("resolution count $resolutionCount resolution array ${resolutionArray!!.size}")
            if (resolutionCount != resolutionArray!!.size) {
                println("0 resolution")
                val intent = Intent(this, ReportedResolutionActivity::class.java)
                intent.putExtra("issueId", issueId)
                setResult(201, intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            } else {
                val intent = Intent(this, ReportedResolutionActivity::class.java)
                //setResult(202,intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
            }
        }
    }

}
