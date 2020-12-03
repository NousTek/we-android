package com.we.beyond.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import com.we.beyond.adapter.CommentMediaAdapter
import com.we.beyond.adapter.ConnectCommentDetailsAdapter
import com.we.beyond.adapter.ResolvedIssueUserAdapter
import com.we.beyond.Interface.OnEditListener
import com.we.beyond.Interface.OnItemClickListener
import com.we.beyond.Interface.OnLikeDislikeListener
import com.we.beyond.Interface.OnReportAbuseListener
import com.we.beyond.R
import com.we.beyond.api.CommentsApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.presenter.comments.commentDetails.CommentDetailsImpl
import com.we.beyond.presenter.comments.commentDetails.CommentDetailsPresenter
import com.we.beyond.presenter.comments.createComment.CreateCommentImpl
import com.we.beyond.presenter.comments.createComment.CreateCommentPresenter
import com.we.beyond.presenter.like.LikeImpl
import com.we.beyond.presenter.like.LikePresenter
import com.we.beyond.presenter.reportAbuse.ReportAbuseImpl
import com.we.beyond.presenter.reportAbuse.ReportAbusePresenter
import com.we.beyond.presenter.reportResolved.ReportResolvedImpl
import com.we.beyond.presenter.reportResolved.ReportResolvedPresenter
import com.we.beyond.ui.reportResolved.ReportResolvedActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/**
 * It shows the article comments details perform further actions
 */
class ConnectCommentActivity : AppCompatActivity(), CommentDetailsPresenter.ICommentsByIdView,
    CreateCommentPresenter.ICreateCommentView
    , OnEditListener, ReportResolvedPresenter.IReportResolvedView, LikePresenter.ILikeView,
    OnLikeDislikeListener, ReportAbusePresenter.IReportAbuseView,
    OnReportAbuseListener, OnItemClickListener {


    /** init context, model and array list */
    var context: Context = this
    var replyData: CommentDetailsPojo? = null
    var replyDataArrayList: ArrayList<CommentDetailsPojo>? = null

    /** initialize respected implementors */
    var commentPresenter: CommentDetailsImpl? = null
    var reportResolvedPresenter: ReportResolvedImpl? = null
    var createCommentPresenter: CreateCommentImpl? = null
    var likeDislikePresenter: LikeImpl? = null
    var reportAbusePresenter: ReportAbuseImpl? = null
    var onEditListener: OnEditListener? = null

    /** init edit text */
    var comment: EditText? = null
    var abuseEditText: EditText? = null

    /** init image view */
    var back: ImageView? = null
    var profilePic: CircleImageView? = null
    var more: ImageView? = null
    var sendComment: ImageView? = null
    var closeComment: ImageView? = null
    var closeAbuse: ImageView? = null
    var close: ImageView? = null


    /** init text view */
    var title: TextView? = null
    var time: TextView? = null
    var reply: TextView? = null
    var like: TextView? = null
    var name: TextView? = null
    var flag: TextView? = null
    var description: TextView? = null
    var alreadyLiked: TextView? = null
    var reportAbuse: TextView? = null
    var edit: TextView? = null
    var delete: TextView? = null

    /** init recycler view */
    var replyRecycler: RecyclerView? = null
    var mediaRecycler: RecyclerView? = null
    var userTypeRecycler: RecyclerView? = null

    /** init variables */
    var commentId: String = ""
    var commentPosition: Int? = null
    var searchText: String = ""
    var allText: String = ""
    var connectId: String = ""

    /** init layout manager and adapter */
    var linearLayoutManager: LinearLayoutManager? = null
    var gridLayoutManager: GridLayoutManager? = null
    var commentsAdapter: ConnectCommentDetailsAdapter? = null
    var mediaAdapter: CommentMediaAdapter? = null
    var userTypeAdapter: ResolvedIssueUserAdapter? = null


    /** init array list */
    var commentArray: ArrayList<CommentsReplyDetails>? = null
    var mediaStatusArray = ArrayList<MediaUploadingPojo>()
    var tagsList: ArrayList<String>? = null
    var tagUserName: ArrayList<String>? = null
    var userTypeList: ArrayList<String>? = null
    var userPojo: ArrayList<UserList>? = null

    /** init button */
    var abuse: Button? = null


    /** init relative layout */
    var commentLayout: RelativeLayout? = null
    var backgroundLayout: RelativeLayout? = null
    var reportAbuseLayout: RelativeLayout? = null
    var moreLayout: RelativeLayout? = null

    var editComment = false

    /** init listener */
    var onLikeDislikeListener: OnLikeDislikeListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connect_comment)

        onEditListener = this

        /** initialize implementation */
        commentPresenter = CommentDetailsImpl(this)
        createCommentPresenter = CreateCommentImpl(this)
        likeDislikePresenter = LikeImpl(this)
        reportAbusePresenter = ReportAbuseImpl(this)
        reportResolvedPresenter = ReportResolvedImpl(this)


        /** array initialization */
        commentArray = ArrayList()
        commentArray!!.clear()
        replyDataArrayList = ArrayList()
        replyDataArrayList!!.clear()
        userTypeList = ArrayList()
        userTypeList!!.clear()
        userPojo = ArrayList()
        tagsList = ArrayList()
        tagUserName = ArrayList()


        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

        /** Get all stored data using intent and assign it respectively
         * and call onCommentsById function of comment presenter  */
        connectId = intent.getStringExtra("connectId")
        commentId = intent.getStringExtra(Constants.COMMENT_ID)

        if (commentId != null && commentId.isNotEmpty()) {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                commentPresenter!!.onCommentsById(this, commentId)
            }
        }

    }

    /** It sets the comment text and add user name to tag user name array */
    override fun OnClick(userName: String, userId: String) {

        val editText = comment!!.text.toString()
        val newText = editText.replace(searchText, "")

        comment!!.setText("${newText}${userName.trim()}")
        comment!!.setSelection(comment!!.text.length)

        tagUserName!!.add(userName)
        tagsList!!.add(userId)

        userTypeRecycler!!.visibility = View.GONE


    }

    /** It calls postDataToServerOnAbuse function with json object */
    private fun getDataToPostOnAbuse() {
        try {

            if (abuseEditText!!.text.trim() != null && abuseEditText!!.text.trim().isNotEmpty()) {
                var abuse = abuseEditText!!.text.trim()

                if (ConstantMethods.checkForInternetConnection(this@ConnectCommentActivity)) {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("type", "connectId")
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
                // ConstantMethods.showProgessDialog(this, "Please Wait...")
                reportAbusePresenter!!.onReportAbuse(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /** It opens abuse layout with animation */
    override fun ReportAbuse(_id: String, s: String) {
        connectId = _id
        reportAbuseLayout!!.visibility = View.VISIBLE
        reportAbuseLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))

    }


    override fun summaryType(type: String) {

    }

    /** It takes the id as input and call postDataToServerOnLike function */
    override fun onLike(_id: String) {
        try {

            val jsonObject = JsonObject()
            jsonObject.addProperty("type", "comment")
            jsonObject.addProperty("typeId", _id)


            if (ConstantMethods.checkForInternetConnection(this@ConnectCommentActivity)) {
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
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /** It call onCommentsById function of comment presenter */
    override fun goToNextScreen() {
        try {
            if (commentId != null && commentId.isNotEmpty()) {
                if (ConstantMethods.checkForInternetConnection(this)) {
                    // ConstantMethods.showProgessDialog(this, "Please Wait...")
                    commentPresenter!!.onCommentsById(this, commentId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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


    /** ui listeners */
    private fun initWithListener() {

        /** It goes back to previous activity  when click on it */
        back!!.setOnClickListener {
            onBackPressed()

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


        /** It opens the more layout with animation */
        more!!.setOnClickListener {
            moreLayout!!.visibility = View.VISIBLE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up))

        }

        /** It closes the more layout with animation */
        close!!.setOnClickListener {
            moreLayout!!.visibility = View.GONE
            moreLayout!!.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_out_down))
        }

        /** It closes the more layout with animation
         * get stored user id and check with near by reply user id, if matches then we cannot abuse the connect
         * and if issue is resolved then we cannot abuse the issue
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
                if (userId == replyData!!.data.user._id) {
                    ConstantMethods.showToast(context,  "You cannot abuse your connect.")
                }
                /* else  if (replyData!!.data.resolved) {
                     ConstantMethods.showWarning(context,"","You can not abuse resolved issues.")
                 }*/
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

                if (ConstantMethods.checkForInternetConnection(this@ConnectCommentActivity)) {
                    getDataToPostOnAbuse()
                }
                // }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** It closes more option layout with animation and
         * If issue resolved and markAsFinal, we can not delete approved resolution
         * else it will show dialog to delete comment and call deleteComment() api
         */
        delete!!.typeface = ConstantFonts.raleway_semibold
        delete!!.setOnClickListener {
            if (replyData!!.data.commentType.equals(
                    "resolution",
                    ignoreCase = true
                ) && replyData!!.data.markAsFinal
            ) {
                ConstantMethods.showToast(
                    context,
                    "You cannot delete approved resolution"
                )
            } else if (replyData!!.data.commentType.equals(
                    "resolution",
                    ignoreCase = true
                ) && !replyData!!.data.markAsFinal
            ) {

                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == replyData!!.data.user._id) {
                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "Do you want to delete resolution?"
                        sweetAlertDialog.confirmText = "Yes"
                        sweetAlertDialog.cancelText = "No"
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()


                            try {

                                val jsonObject = JsonObject()
                                jsonObject.addProperty(
                                    "commentId",
                                    replyData!!.data._id
                                )



                                try {
                                    val commentsApi =
                                        ApplicationController.retrofit.create(CommentsApi::class.java)
                                    val response: Single<DeleteCommentPojo> =
                                        commentsApi.deleteComment(jsonObject)
                                    response.subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(object :
                                            DisposableSingleObserver<DeleteCommentPojo>() {
                                            override fun onSuccess(deleteCommentPojo: DeleteCommentPojo) {
                                                if (deleteCommentPojo != null) {

                                                    println("response $deleteCommentPojo")
                                                    ConstantMethods.cancleProgessDialog()




                                                    onBackPressed()


                                                }
                                            }

                                            override fun onError(e: Throwable) {
                                                ConstantMethods.cancleProgessDialog()
                                                try {
                                                    if (e is IOException) {
                                                        ConstantMethods.showError(
                                                            context,
                                                            context.resources.getString(R.string.no_internet_title),
                                                            context.resources.getString(R.string.no_internet_sub_title)
                                                        )
                                                    } else {
                                                        val exception: HttpException =
                                                            e as HttpException
                                                        val er: String =
                                                            exception.response()!!.errorBody()!!
                                                                .string()
                                                        val errorPojo: ErrorPojo =
                                                            Gson().fromJson(
                                                                er,
                                                                ErrorPojo::class.java
                                                            )

                                                        if (errorPojo != null) {
                                                            if (errorPojo.error.isNotEmpty()) {
                                                                if (errorPojo.message.isNotEmpty()) {
                                                                    ConstantMethods.showError(
                                                                        context,
                                                                        errorPojo.error,
                                                                        errorPojo.message
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    ConstantMethods.cancleProgessDialog()
                                                    ConstantMethods.showError(
                                                        context,
                                                        context.resources.getString(R.string.error_title),
                                                        context.resources.getString(
                                                            R.string.error_message
                                                        )
                                                    )

                                                }
                                            }

                                        })


                                } catch (e: Exception) {
                                    ConstantMethods.cancleProgessDialog()
                                    ConstantMethods.showError(
                                        context,
                                        context.resources.getString(R.string.error_title),
                                        context.resources.getString(
                                            R.string.error_message
                                        )
                                    )
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

                } else {

                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "You are not owner of this resolution."
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            } else {
                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == replyData!!.data.user._id) {

                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "Do you want to delete comment?"
                        sweetAlertDialog.confirmText = "Yes"
                        sweetAlertDialog.cancelText = "No"
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()


                            try {

                                val jsonObject = JsonObject()
                                jsonObject.addProperty(
                                    "commentId",
                                    commentId
                                )

                                try {
                                    val commentsApi =
                                        ApplicationController.retrofit.create(CommentsApi::class.java)
                                    val response: Single<DeleteCommentPojo> =
                                        commentsApi.deleteComment(jsonObject)
                                    response.subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(object :
                                            DisposableSingleObserver<DeleteCommentPojo>() {
                                            override fun onSuccess(deleteCommentPojo: DeleteCommentPojo) {
                                                if (deleteCommentPojo != null) {

                                                    onBackPressed()


                                                }
                                            }

                                            override fun onError(e: Throwable) {
                                                ConstantMethods.cancleProgessDialog()
                                                try {
                                                    if (e is IOException) {
                                                        ConstantMethods.showError(
                                                            context,
                                                            context.resources.getString(R.string.no_internet_title),
                                                            context.resources.getString(R.string.no_internet_sub_title)
                                                        )
                                                    } else {
                                                        val exception: HttpException =
                                                            e as HttpException
                                                        val er: String =
                                                            exception.response()!!.errorBody()!!
                                                                .string()
                                                        val errorPojo: ErrorPojo =
                                                            Gson().fromJson(
                                                                er,
                                                                ErrorPojo::class.java
                                                            )

                                                        if (errorPojo != null) {
                                                            if (errorPojo.error.isNotEmpty()) {
                                                                if (errorPojo.message.isNotEmpty()) {
                                                                    ConstantMethods.showError(
                                                                        context,
                                                                        errorPojo.error,
                                                                        errorPojo.message
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    ConstantMethods.cancleProgessDialog()
                                                    ConstantMethods.showError(
                                                        context,
                                                        context.resources.getString(R.string.error_title),
                                                        context.resources.getString(
                                                            R.string.error_message
                                                        )
                                                    )

                                                }
                                            }

                                        })


                                } catch (e: Exception) {
                                    ConstantMethods.cancleProgessDialog()
                                    ConstantMethods.showError(
                                        context,
                                        context.resources.getString(R.string.error_title),
                                        context.resources.getString(
                                            R.string.error_message
                                        )
                                    )
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
                } else {

                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "You are not owner of this comment."
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }

        }

        /** It closes the more layout with animation
         * get stored user id and check with reply user id, if matches then  opens ReportResolvedActivity to edit a article
         * else show warning dialog */
        edit!!.typeface = ConstantFonts.raleway_semibold
        edit!!.setOnClickListener {

            if (replyData!!.data.tags != null && replyData!!.data.tags.isNotEmpty()) {
                tagsList!!.clear()
                tagUserName!!.clear()

                for (i in 0 until replyData!!.data.tags.size) {
                    tagsList!!.add(replyData!!.data.tags[i])
                    tagUserName!!.add(replyData!!.data.tagNames[i])

                    println("tags from server ${tagsList}")
                    println("tags names from server ${tagUserName}")
                }

            }

            if (replyData!!.data.commentType.equals(
                    "resolution",
                    ignoreCase = true
                ) && replyData!!.data.markAsFinal
            ) {
                ConstantMethods.showToast(
                    context,
                    "You cannot edit approved resolution"
                )
            } else if (replyData!!.data.commentType.equals(
                    "resolution",
                    ignoreCase = true
                ) && !replyData!!.data.markAsFinal
            ) {

                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == replyData!!.data.user._id) {
                    try {
                        /*onEditListener!!.OnEdit(
                            commentDetails!![position]._id,
                            commentDetails!![position].text
                        )*/
                        val jsonString = Gson().toJson(replyData)
                        val intent = Intent(context, ReportResolvedActivity::class.java)
                        intent.putExtra("connectId", connectId)
                        intent.putExtra("resolutionId", replyData!!.data.issue)
                        intent.putExtra("resolutionData", jsonString)
                        intent.putExtra("edit", true)
                        startActivityForResult(intent, 200)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )

                    } catch (e: Exception) {

                        e.printStackTrace()
                    }
                } else {

                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "You are not owner of this resolution."
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            } else {
                moreLayout!!.visibility = View.GONE
                moreLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.slide_out_down
                    )
                )


                val userId = EasySP.init(context).getString(ConstantEasySP.USER_ID)
                if (userId == replyData!!.data.user._id) {

                    try {
                        onEditListener!!.OnEdit(
                            commentId, replyData!!.data.text
                        )

                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                } else {

                    try {
                        val sweetAlertDialog =
                            SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                        sweetAlertDialog.titleText = ""
                        sweetAlertDialog.contentText = "You are not owner of this comment."
                        sweetAlertDialog.show()
                        sweetAlertDialog.setCancelable(false)
                        sweetAlertDialog.setConfirmClickListener {
                            sweetAlertDialog.dismissWithAnimation()
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }

        }


        /** It hides keyboard, comment layout */
        closeComment!!.setOnClickListener {

            comment!!.clearFocus()

            ConstantMethods.hideKeyBoard(this, this@ConnectCommentActivity)

            commentLayout!!.visibility = View.GONE
            backgroundLayout!!.visibility = View.GONE


        }

        /** It hides keyboard, comment layout */
        commentLayout!!.setOnClickListener {
            comment!!.clearFocus()
            ConstantMethods.hideKeyBoard(this, this@ConnectCommentActivity)

            commentLayout!!.visibility = View.GONE
            backgroundLayout!!.visibility = View.GONE


        }

        /** It will search the user from user list using '@' character
         * from OnRequestUserListOnSearch function of report resolved presenter  */
        comment!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                allText = p0.toString()

                allText = allText.replace("(", "")

                if (allText.endsWith(")", ignoreCase = true)) {
                    if (allText != null && allText.isNotEmpty()) {
                        val parts = allText.split(")")
                        val first = parts[0]
                        var second = parts[1]

                        allText = first.replace(first, "")
                    }
                }

                allText = allText.replace(")", "")

                println("all text $allText")


                if (p0!!.startsWith("@", ignoreCase = true) && p1 >= 3) {
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

                    allText = allText.replace("(", "")

                    if (allText.endsWith(")", ignoreCase = true)) {
                        if (allText != null && allText.isNotEmpty()) {
                            val parts = allText.split(")")
                            val first = parts[0]
                            var second = parts[1]

                            allText = first.replace(first, "")
                        }
                    }

                    allText = allText.replace(")", "")

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



                if (p0.length == 0) {
                    tagsList!!.clear()
                    tagUserName!!.clear()
                    userTypeRecycler!!.visibility = View.GONE
                } else {
                    for (i in 0 until tagUserName!!.size) {
                        if (!p0.contains(tagUserName!![i], ignoreCase = true)) {
                            tagUserName!!.remove(tagUserName!![i])
                            tagsList!!.remove(tagsList!![i])

                            break

                        }
                    }

                }
            }

        })


        /**
         * It reset the values
         * call getDataToPost function
         * and hides the keyboard
         */
        sendComment!!.setOnClickListener {
            try {

                if (ConstantMethods.checkForInternetConnection(this@ConnectCommentActivity)) {
                    resetValue()
                    getDataToPost()
                    //println("comment position $commentPosition")
                    if (commentPosition != null) {

                    } else {
                        comment!!.clearFocus()
                        ConstantMethods.hideKeyBoard(this, this@ConnectCommentActivity)
                        backgroundLayout!!.visibility = View.GONE
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /** it opens comment layout and show keyboard to add or reply comment */
        reply!!.setOnClickListener {
            getData()
        }


    }

    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of edit text */
        comment = findViewById(R.id.et_comment)
        abuseEditText = findViewById(R.id.et_abuse)
        abuseEditText!!.typeface = ConstantFonts.raleway_regular

        /** ids of image view */
        back = findViewById(R.id.img_back)
        sendComment = findViewById(R.id.img_send_comment)
        closeComment = findViewById(R.id.img_close_comment)
        closeAbuse = findViewById(R.id.img_close_window)
        close = findViewById(R.id.img_close)
        more = findViewById(R.id.img_more)


        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        reportAbuse = findViewById(R.id.txt_report_abuse)
        reportAbuse!!.typeface = ConstantFonts.raleway_semibold

        edit = findViewById(R.id.txt_edit)
        edit!!.typeface = ConstantFonts.raleway_semibold

        delete = findViewById(R.id.txt_delete)
        delete!!.typeface = ConstantFonts.raleway_semibold


        /** ids of image view */
        profilePic = findViewById(R.id.img_comment_profile_pic)
        name = findViewById(R.id.txt_name)
        description = findViewById(R.id.txt_comment_description)
        time = findViewById(R.id.txt_time)
        reply = findViewById(R.id.txt_comment_reply)
        like = findViewById(R.id.txt_comment_like)
        alreadyLiked = findViewById(R.id.txt_comment_already_liked)
        flag = findViewById(R.id.txt_flag)
        mediaRecycler = findViewById(R.id.recycler_media)


        /** ids of relative layout */
        commentLayout = findViewById(R.id.commentLayout)
        backgroundLayout = findViewById(R.id.backgroundLayout)
        reportAbuseLayout = findViewById(R.id.reportAbuseLayout)
        moreLayout = findViewById(R.id.moreLayout)


        /** ids of recycler view */
        replyRecycler = findViewById(R.id.replyRecycler)
        linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        replyRecycler!!.setHasFixedSize(true)
        replyRecycler!!.layoutManager = linearLayoutManager

        userTypeRecycler = findViewById(R.id.recycler_resolved_by)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        userTypeRecycler!!.setHasFixedSize(true)
        userTypeRecycler!!.layoutManager = linearLayoutManager

        /** ids of button */
        abuse = findViewById(R.id.btn_report_abuse)
        abuse!!.typeface = ConstantFonts.raleway_semibold

    }

    /** it opens comment layout and show keyboard to add or reply comment */
    fun getData() {
        println("comment id reply $commentId")

        if (commentId != null && commentId.isNotEmpty()) {

            commentLayout!!.visibility = View.VISIBLE
            comment!!.isCursorVisible = true
            comment!!.requestFocus()
            backgroundLayout!!.visibility = View.VISIBLE
            comment!!.setText("")
            tagsList!!.clear()

            ConstantMethods.showKeyBoard(this)
        }

    }

    /** It hides comment layout and keyboard
     * and call onCommentsById function of comment presenter */
    override fun setCommentAdapter() {
        try {

            commentLayout!!.visibility = View.GONE
            backgroundLayout!!.visibility = View.GONE
            comment!!.setText("")
            tagsList!!.clear()

            ConstantMethods.hideKeyBoard(this, this@ConnectCommentActivity)

            try {
                if (ConstantMethods.checkForInternetConnection(context)) {
                    commentPresenter!!.onCommentsById(this, commentId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            comment!!.clearFocus()
            ConstantMethods.hideKeyBoard(this, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onLike function of like dislike  presenter */
    private fun postDataToServerOnLike(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
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
            sweetAlertDialog.titleText = ""
            sweetAlertDialog.contentText = "Do you want to DownVote?"
            sweetAlertDialog.confirmText = "Yes"
            sweetAlertDialog.cancelText = "No"
            sweetAlertDialog.show()
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog.setConfirmClickListener {
                sweetAlertDialog.dismissWithAnimation()


                try {

                    if (ConstantMethods.checkForInternetConnection(context)) {
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

    /** It takes the commentsDetails as input and set respective data to views */
    override fun setCommentsByIdAdapter(commentsDetails: CommentDetailsPojo) {

        if (commentsDetails != null) {

            try {

                commentId = commentsDetails.data._id

                replyData = commentsDetails


                if (commentsDetails.data.tags != null && commentsDetails.data.tags.isNotEmpty()) {
                    tagsList!!.clear()
                    tagUserName!!.clear()

                    for (i in 0 until commentsDetails.data.tags.size) {
                        tagsList!!.add(commentsDetails.data.tags[i])
                        tagUserName!!.add(commentsDetails.data.tagNames[i])

                        println("tags from server ${tagsList}")
                        println("tags names from server ${tagUserName}")
                    }

                }

                //media recycler
                if (commentsDetails.data.imageUrls != null && commentsDetails.data.videoUrls != null) {
                    /* mediaArrayList.addAll(commentDetails!![position].imageUrls)
                 mediaArrayList.addAll(commentDetails!![position].videoUrls)*/
                    for (i in 0 until commentsDetails.data.imageUrls.size) {
                        mediaStatusArray.add(
                            MediaUploadingPojo(
                                "",
                                commentsDetails.data.imageUrls[i],
                                "image",
                                false
                            )
                        )
                    }
                    for (i in 0 until commentsDetails.data.videoUrls.size) {

                        mediaStatusArray.add(
                            MediaUploadingPojo(
                                "",
                                commentsDetails.data.videoUrls[i],
                                "video",
                                false
                            )
                        )
                    }
                } else if (commentsDetails.data.imageUrls != null) {
                    // mediaArrayList.addAll(commentDetails!![position].imageUrls)
                    for (i in 0 until commentsDetails.data.imageUrls.size) {
                        mediaStatusArray.add(
                            MediaUploadingPojo(
                                "",
                                commentsDetails.data.imageUrls[i],
                                "image",
                                false
                            )
                        )
                    }
                } else if (commentsDetails.data.videoUrls != null) {
                    for (i in 0 until commentsDetails.data.videoUrls.size) {
                        mediaStatusArray.add(
                            MediaUploadingPojo(
                                "",
                                commentsDetails.data.videoUrls[i],
                                "video",
                                false
                            )
                        )
                    }
                }


                val activity = context as Activity
                try {
                    onLikeDislikeListener = activity as OnLikeDislikeListener
                } catch (e: Exception) {
                    e.printStackTrace()

                }

                /** show and hide image views and call onLike function of onLikeDislikeListener */
                like!!.setOnClickListener {
                    alreadyLiked!!.visibility = View.VISIBLE
                    like!!.visibility = View.GONE

                    onLikeDislikeListener!!.onLike(commentId)

                }

                /** show and hide image views and call onLike function of onLikeDislikeListener */
                alreadyLiked!!.setOnClickListener {

                    alreadyLiked!!.visibility = View.GONE
                    like!!.visibility = View.VISIBLE


                    onLikeDislikeListener!!.onDislike(commentId)
                }


                if (commentsDetails.data.likeByUser != null && commentsDetails.data.likeByUser) {
                    like!!.visibility = View.GONE
                    alreadyLiked!!.visibility = View.VISIBLE
                }


                //set profile pic
                if (commentsDetails.data.user.profileUrl != null && commentsDetails.data.user.profileUrl.length > 0) {
                    Picasso.with(context)
                        .load(commentsDetails.data.user.profileUrl)
                        .placeholder(R.drawable.profile)
                        //.memoryPolicy(MemoryPolicy.NO_CACHE)
                        //.networkPolicy(NetworkPolicy.NO_CACHE)
                        //.resize(400, 400)  // optional
                        .into(profilePic)
                } else {
                    profilePic!!.setBackgroundResource(R.drawable.profile)
                }

                //set user name
                if (commentsDetails.data.user.userLoginType.userType.equals(
                        "individual",
                        ignoreCase = true
                    )
                ) {
                    name!!.text =
                        commentsDetails.data.user.firstName + " ${commentsDetails.data.user.lastName}"
                    name!!.typeface = ConstantFonts.raleway_semibold
                } else {
                    name!!.text =
                        commentsDetails.data.user.organizationName
                    name!!.typeface = ConstantFonts.raleway_semibold
                }

                //set comment description
                description!!.text = commentsDetails.data.text
                description!!.typeface = ConstantFonts.raleway_regular

                //set comment time
                time!!.text =
                    ConstantMethods.convertStringToDateStringFull(commentsDetails.data.createdAt)
                time!!.typeface = ConstantFonts.raleway_regular

                //set comment reply
                reply!!.typeface = ConstantFonts.raleway_semibold

                //set comment like
                like!!.typeface = ConstantFonts.raleway_semibold
                alreadyLiked!!.typeface = ConstantFonts.raleway_semibold


                //set resolved flag
                if (commentsDetails.data.commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && commentsDetails.data.markAsFinal
                ) {
                    flag!!.visibility = View.VISIBLE
                    flag!!.text = "Approved Resolution"
                    flag!!.typeface = ConstantFonts.raleway_regular

                    if (mediaStatusArray != null && mediaStatusArray.isNotEmpty()) {
                        gridLayoutManager = GridLayoutManager(context, 3)
                        mediaRecycler!!.layoutManager = gridLayoutManager

                        mediaRecycler!!.visibility = View.VISIBLE
                        mediaAdapter = CommentMediaAdapter(context, mediaStatusArray)
                        mediaRecycler!!.adapter = mediaAdapter
                    } else {
                        mediaRecycler!!.visibility = View.GONE

                    }

                } else if (commentsDetails.data.commentType.equals(
                        "resolution",
                        ignoreCase = true
                    ) && !commentsDetails.data.markAsFinal
                ) {

                    flag!!.visibility = View.VISIBLE
                    flag!!.text = "Reported  Resolution"
                    flag!!.typeface = ConstantFonts.raleway_regular

                    if (mediaStatusArray != null && mediaStatusArray.isNotEmpty()) {
                        gridLayoutManager = GridLayoutManager(context, 3)
                        mediaRecycler!!.layoutManager = gridLayoutManager

                        mediaRecycler!!.visibility = View.VISIBLE
                        mediaAdapter = CommentMediaAdapter(context, mediaStatusArray)
                        mediaRecycler!!.adapter = mediaAdapter

                        println("media status array $mediaStatusArray")
                    } else {
                        mediaRecycler!!.visibility = View.GONE
                    }
                } else {
                    flag!!.visibility = View.GONE
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (commentsDetails.data.reply != null) {
            commentArray!!.addAll(commentsDetails.data.reply)
            commentsAdapter = ConnectCommentDetailsAdapter(context, commentArray!!, commentsDetails)
            replyRecycler!!.adapter = commentsAdapter

            replyRecycler!!.scrollToPosition(replyRecycler!!.adapter!!.itemCount - 1)

        }

    }

    /** It is used to edit the comment */
    override fun OnEdit(_id: String, commentText: String) {

        commentLayout!!.visibility = View.VISIBLE
        comment!!.isCursorVisible = true
        comment!!.requestFocus()
        backgroundLayout!!.visibility = View.VISIBLE
        ConstantMethods.showKeyBoard(this)
        comment!!.setText(commentText)

        editComment = true
        commentId = _id

    }

    /** It checks the edit comment boolean, if it is true then call postDataToServerUpdateComment()
     * else call postDataToServer() with required json object
     */
    fun getDataToPost() {
        try {
            if (editComment) {
                if (comment!!.text != null && comment!!.text.isNotEmpty()) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty("text", (comment!!.text.toString()))
                    jsonObject.addProperty("commentId", commentId)

                    val jsonArray = JsonArray()
                    if (tagsList != null && tagsList!!.isNotEmpty()) {
                        for (i in 0 until tagsList!!.size) {
                            jsonArray.add(tagsList!![i])
                        }
                        jsonObject.add("tags", jsonArray)
                    }

                    val jsonArrayName = JsonArray()

                    if (tagUserName != null && tagUserName!!.isNotEmpty()) {
                        for (i in 0 until tagUserName!!.size) {
                            jsonArrayName.add(tagUserName!![i])
                        }
                        jsonObject.add("tagNames", jsonArrayName)
                    }


                    println("json object $jsonObject")


                    try {
                        if (ConstantMethods.checkForInternetConnection(context)) {
                            postDataToServerUpdateComment(jsonObject)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } else {
                    ConstantMethods.showToast(
                        this,
                        "Please give us your comment."
                    )
                }

            } else {
                if (commentId != null && commentId.isNotEmpty()) {
                    if (comment!!.text != null && comment!!.text.isNotEmpty()) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("connectId", connectId)
                        jsonObject.addProperty("text", (comment!!.text.toString()))
                        jsonObject.addProperty("parentId", commentId)
                        val jsonArray = JsonArray()
                        if (tagsList != null && tagsList!!.isNotEmpty()) {
                            for (i in 0 until tagsList!!.size) {
                                jsonArray.add(tagsList!![i])
                            }
                            jsonObject.add("tags", jsonArray)
                        }

                        val jsonArrayName = JsonArray()

                        if (tagUserName != null && tagUserName!!.isNotEmpty()) {
                            for (i in 0 until tagUserName!!.size) {
                                jsonArrayName.add(tagUserName!![i])
                            }
                            jsonObject.add("tagNames", jsonArrayName)
                        }

                        println("json object $jsonObject")


                        try {
                            if (ConstantMethods.checkForInternetConnection(context)) {
                                postDataToServer(jsonObject)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        ConstantMethods.showToast(
                            this,
                            "Please give us your comment."
                        )
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onCommentCreated function of create comment presenter */
    private fun postDataToServer(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                createCommentPresenter!!.onCommentCreated(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It takes the json object as input and send to onUpdateComment function of comment presenter */
    private fun postDataToServerUpdateComment(jsonObject: JsonObject) {
        try {

            if (ConstantMethods.checkForInternetConnection(context)) {
                commentPresenter!!.onUpdateComment(this, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It hide comment layout
     * hides keyboard
     * and call onCommentsById function of comment presenter
     */
    override fun onUpdateSuccessfully() {
        try {

            commentLayout!!.visibility = View.GONE
            comment!!.isCursorVisible = true
            backgroundLayout!!.visibility = View.GONE

            comment!!.clearFocus()
            ConstantMethods.hideKeyBoard(this, this@ConnectCommentActivity)

            if (ConstantMethods.checkForInternetConnection(context)) {
                //ConstantMethods.showProgessDialog(this, "Please Wait...")
                commentPresenter!!.onCommentsById(this, commentId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** reset all values  */
    fun resetValue() {
        commentArray!!.clear()
    }


    /** It goes back to previous activity */
    override fun onBackPressed() {

        val intent = Intent()
        intent.putExtra("connectId", connectId)
        setResult(Activity.RESULT_OK, intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()

        super.onBackPressed()
    }
}
