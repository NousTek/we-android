package com.we.beyond.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.adapter.MediaAdapter
import com.we.beyond.BuildConfig
import com.we.beyond.PhotoActivity
import com.we.beyond.R
import com.we.beyond.RealPathUtils
import com.we.beyond.api.FileUploadApi
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.presenter.profile.ProfileImpl
import com.we.beyond.presenter.profile.ProfilePresenter
import com.we.beyond.ui.badges.BadgesActivity
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.ui.login.LoginActivity
import com.we.beyond.util.*
import com.white.easysp.EasySP
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException

/** It shows profile and user details */
class UserProfileActivity : AppCompatActivity(), ProfilePresenter.IProfileView {

    /** initialize implementors */
    var profilePresenter : ProfileImpl?=null

    /** init image view */
    var back : ImageView?=null
    var edit : ImageView?=null
    var profilePic : CircleImageView?=null

    /** init text view */
    var profileName : TextView?=null
    var title : TextView?=null
    var type : TextView?=null
    var appVersion : TextView?=null
    var issuePostedTitle : TextView?=null
    var issuePostedCount : TextView?=null
    var issueResolvedTitle : TextView?=null
    var issueResolvedCount : TextView?=null
    var badgesTitle : TextView?=null
    var badgesCount : TextView?=null
    var myBadgesTitle : TextView?=null
    var editProfileTitle : TextView?=null
    var userDefaultCategoriesTitle : TextView?=null
    var receivedResolutionTitle : TextView?=null
    var myIssuesTitle : TextView?=null
    var myResolutionTitle : TextView?=null
    var myGatheringTitle : TextView?=null
    var myCampaignTitle : TextView?=null
    var myConnectTitle : TextView?=null
    var myActivitiesTitle : TextView?=null
    var changePasswordTitle : TextView?=null
    var logoutTitle : TextView?=null

    /** init progress bar */
    var progressBar : ProgressBar?=null

    /** init relative layout */
    var editProfileLayout : RelativeLayout?=null
    var userDefaultCategoriesLayout : RelativeLayout?=null
    var receivedResolutionLayout : RelativeLayout?=null
    var myIssuesLayout : RelativeLayout?=null
    var myResolutionLayout : RelativeLayout?=null
    var myGatheringLayout : RelativeLayout?=null
    var myCampaignLayout : RelativeLayout?=null
    var myConnectLayout : RelativeLayout?=null
    var myActivitiesLayout : RelativeLayout?=null
    var changePasswordLayout : RelativeLayout?=null
    var logout : RelativeLayout?=null
    var badgesLayout : RelativeLayout?=null


   /* *//** init linear layout *//*
    var badgesLayout : LinearLayout?=null*/

    var GALLERY_IMAGE = 1

    var mMediaUri: Uri? = null

    /** init adapter */
    var mediaAdapter: MediaAdapter? = null

    /** init array list */
    var mediaArray: ArrayList<String>? = null
    var mediaList: ArrayList<MediaUploadingPojo>? = null

    /** init view pager */
    //var pager  :  ViewPager?=null

    /** init tab layout */
    //var tabs : TabLayout?=null

   /* var badgesFragment : BadgesFragment?=null
    var lastContributionFragment : LastContributionFragment?=null
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

       /* badgesFragment = BadgesFragment()
        lastContributionFragment = LastContributionFragment()*/

        /** array initialization */
        mediaArray = ArrayList()
        mediaList = ArrayList()

        /** initialize implementation */
        profilePresenter = ProfileImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** getDataToPost */
        //getDataToPost()

        /** initialize onclick listener */
        initWithListener()

        /*setupViewPager(pager)

        tabs!!.setupWithViewPager(pager)
        tabs!!.getTabAt(0)!!.text = "My Badges"
        tabs!!.getTabAt(1)!!.text = "Last Contributions"


*/


    }

   /* private fun setupViewPager(pager: ViewPager?)
    {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(badgesFragment!!, "My Badges")
        adapter.addFragment(lastContributionFragment!!, "Last Contributions")
        pager!!.adapter = adapter

    }*/


    /** It will check profile url and set it to image view */
    override fun setUserProfilePic(userProfilePic: UpdateProfilePic)
    {
        println("in update profile pic response")
        if(userProfilePic.data.profileUrl!=null && userProfilePic.data.profileUrl.isNotEmpty())
        {
            progressBar!!.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(userProfilePic.data.profileUrl)
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

    }

    override fun onUpdateSuccessfully() {

    }

/*
    override fun setCitiesAdapter(cities: ArrayList<RegistrationPojo>) {

    }*/


    /** It calls onRequestUserProfileData() of profile presenter
     */
    private fun getDataToPost() {
        try {
            
            appVersion!!.text = "Version ${BuildConfig.VERSION_NAME}"

            if (ConstantMethods.checkForInternetConnection(this@UserProfileActivity)) {
                    ConstantMethods.showProgessDialog(this, "Please Wait...")
                profilePresenter!!.onRequestUserProfileData(this)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It checks the user data, if it is not empty then set it to respective views */
    override fun setUserProfileDataAdapter(userData: ProfilePojo)
    {
        if(userData.data.profileUrl!=null && userData.data.profileUrl.isNotEmpty())
        {
            progressBar!!.visibility = View.VISIBLE
            Glide
                .with(this)
                .load(userData.data.profileUrl)
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
        if (userData.data.userLoginType.userType.equals(
                "individual",
                ignoreCase = true
            )
        ) {
            profileName!!.text =
                userData.data.firstName + " ${userData.data.lastName}"

        } else {
            profileName!!.text = userData.data.organizationName

        }

       if(userData.data.userLoginType.name !=null && userData.data.userLoginType.name.isNotEmpty())
       {
           type!!.text = userData.data.userLoginType.name
       }

        if(userData.data.summary !=null)
        {
            issuePostedCount!!.text = userData.data.summary.issues.toString()
            issueResolvedCount!!.text = userData.data.summary.resolutions.toString()
            badgesCount!!.text = userData.data.summary.badges.toString()

        }


    }

    /** ui listeners */
    private fun initWithListener()
    {
        /** It goes back to previous activity when click on it */
        back!!.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        /** It opens EditProfileActivity when click on it */
        editProfileLayout!!.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

        /** It opens MyDefaultCategoriesActivity when click on it */
        userDefaultCategoriesLayout!!.setOnClickListener {
            val intent = Intent(this, MyDefaultCategoriesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()

        }

        /** It opens ReportedResolutionActivity when click on it */
        receivedResolutionLayout!!.setOnClickListener {
            val intent = Intent(this, ReportedResolutionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        /** It opens MyIssuesActivity when click on it */
        myIssuesLayout!!.setOnClickListener {
            val intent = Intent(this, MyIssuesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /** It opens MyResolutionActivity when click on it */
        myResolutionLayout!!.setOnClickListener {
            val intent = Intent(this, MyResolutionActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        /** It opens MyGatheringActivity when click on it */
        myGatheringLayout!!.setOnClickListener {
            val intent = Intent(this, MyGatheringActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /** It opens MyCampaignActivity when click on it */
        myCampaignLayout!!.setOnClickListener {
            val intent = Intent(this, MyCampaignActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /** It opens MyConnectActivity when click on it */
        myConnectLayout!!.setOnClickListener {
            val intent = Intent(this, MyConnectActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        /** It opens MyActivitiesActivity when click on it */
        myActivitiesLayout!!.setOnClickListener {
            val intent = Intent(this, MyActivitiesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        /** It opens ChangePasswordActivity when click on it */
        changePasswordLayout!!.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }


        /** It remove the stored value and redirect to LOginActivity */
        logout!!.setOnClickListener {
            try {
                val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = "Logout"
                sweetAlertDialog.contentText = "Are you sure you want to Logout?"
                sweetAlertDialog.show()
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.cancelText= "Cancel"
                sweetAlertDialog.confirmText= "Logout"
                sweetAlertDialog.setConfirmClickListener {
                    sweetAlertDialog.dismissWithAnimation()

                    try {
                        val logoutApi = ApplicationController.retrofit.create(ProfileApi::class.java)
                        val response: Single<Logout> = logoutApi.logout()
                        response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : DisposableSingleObserver<Logout>() {
                                override fun onSuccess(logout : Logout) {

                                    ConstantMethods.cancleProgessDialog()
                                    try {

                                        EasySP.init(this@UserProfileActivity).remove(ConstantEasySP.SP_IS_LOGIN)

                                        EasySP.init(this@UserProfileActivity).remove(ConstantEasySP.SP_ACCESS_TOKEN)

                                        //EasySP.init(this).remove("token")

                                        val intent = Intent(this@UserProfileActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                                        finish()




                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }


                                }

                                override fun onError(e: Throwable) {
                                    ConstantMethods.cancleProgessDialog()
                                    try {
                                        if(e is IOException){
                                            ConstantMethods.showError(this@UserProfileActivity, this@UserProfileActivity.resources.getString(R.string.no_internet_title),this@UserProfileActivity.resources.getString(
                                                R.string.no_internet_sub_title))
                                        }
                                        else {
                                            val exception: HttpException = e as HttpException
                                            val er: String = exception.response()!!.errorBody()!!.string()
                                            val errorPojo: ErrorPojo =
                                                Gson().fromJson(er, ErrorPojo::class.java)

                                            if (errorPojo != null) {
                                                if (errorPojo.error.isNotEmpty()) {
                                                    if (errorPojo.message.isNotEmpty()) {
                                                        ConstantMethods.showError(
                                                            this@UserProfileActivity,
                                                            errorPojo.error,
                                                            errorPojo.message
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    catch (e : Exception)
                                    {
                                        ConstantMethods.cancleProgessDialog()
                                        ConstantMethods.showError(this@UserProfileActivity, this@UserProfileActivity.resources.getString(R.string.error_title), this@UserProfileActivity.resources.getString(
                                            R.string.error_message))

                                    }
                                }

                            })

                    } catch (e: Exception) {
                        ConstantMethods.cancleProgessDialog()
                        ConstantMethods.showError(
                            this@UserProfileActivity,
                            this@UserProfileActivity.resources.getString(R.string.error_title),
                            this@UserProfileActivity.resources.getString(
                                R.string.error_message
                            )
                        )
                    }


                }


                sweetAlertDialog.setCancelClickListener {
                    sweetAlertDialog.dismissWithAnimation()
                }


            } catch (e: Exception) {
                e.printStackTrace()

            }

        }

        /** It opens BadgesActivity when click on it */
        badgesLayout!!.setOnClickListener {
            /*if(badgesCount!!.text.equals("0"))
            {
                ConstantMethods.showWarning(this,"","You don't have badges.")
            }
            else{*/
                val intent = Intent(this, BadgesActivity::class.java)
                intent.putExtra("myProfile",true)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            //}
        }


        /** It opens gallery to select image */
        edit!!.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, GALLERY_IMAGE)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** ui initialization */
    private fun initElementsWithIds()
    {
        /** ids of text view */
        profileName = findViewById(R.id.txt_Name)
        profileName!!.typeface = ConstantFonts.raleway_medium

        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold

        type = findViewById(R.id.txt_type)
        type!!.typeface = ConstantFonts.raleway_medium

        appVersion = findViewById(R.id.txt_app_version)
        appVersion!!.typeface = ConstantFonts.raleway_medium

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

        editProfileTitle = findViewById(R.id.txt_edit_profile_title)
        editProfileTitle!!.typeface = ConstantFonts.raleway_medium

        userDefaultCategoriesTitle = findViewById(R.id.txt_my_default_categories_title)
        userDefaultCategoriesTitle!!.typeface = ConstantFonts.raleway_medium

        receivedResolutionTitle = findViewById(R.id.txt_received_resolution_title)
        receivedResolutionTitle!!.typeface = ConstantFonts.raleway_medium

        myIssuesTitle = findViewById(R.id.txt_my_issues_title)
        myIssuesTitle!!.typeface = ConstantFonts.raleway_medium

        myResolutionTitle = findViewById(R.id.txt_my_resolution_title)
        myResolutionTitle!!.typeface = ConstantFonts.raleway_medium

        myGatheringTitle = findViewById(R.id.txt_my_gathering_title)
        myGatheringTitle!!.typeface = ConstantFonts.raleway_medium

        myCampaignTitle = findViewById(R.id.txt_my_campaign_title)
        myCampaignTitle!!.typeface = ConstantFonts.raleway_medium

        myConnectTitle = findViewById(R.id.txt_my_connect_title)
        myConnectTitle!!.typeface = ConstantFonts.raleway_medium

        myActivitiesTitle = findViewById(R.id.txt_my_activities_title)
        myActivitiesTitle!!.typeface = ConstantFonts.raleway_medium

        changePasswordTitle = findViewById(R.id.txt_change_password_title)
        changePasswordTitle!!.typeface = ConstantFonts.raleway_medium

        logoutTitle = findViewById(R.id.txt_logout_title)
        logoutTitle!!.typeface = ConstantFonts.raleway_medium

        myBadgesTitle = findViewById(R.id.txt_edit_badges_title)
        myBadgesTitle!!.typeface = ConstantFonts.raleway_medium


        /** ids of image view */
        back = findViewById(R.id.img_back)
        profilePic = findViewById(R.id.img_profile_pic)
        edit = findViewById(R.id.img_edit_profile_pic)

        /** ids of progress bar */
        progressBar = findViewById(R.id.progressBar)


        /** ids of relative layout */
        editProfileLayout = findViewById(R.id.editProfileLayout)
        userDefaultCategoriesLayout = findViewById(R.id.myDefaultCategoriesLayout)
        receivedResolutionLayout = findViewById(R.id.receivedResolvedLayout)
        myIssuesLayout = findViewById(R.id.myIssuesLayout)
        myResolutionLayout = findViewById(R.id.myResolutionLayout)
        myGatheringLayout = findViewById(R.id.myGatheringLayout)
        myCampaignLayout = findViewById(R.id.myCampaignLayout)
        myConnectLayout = findViewById(R.id.myConnectLayout)
        myActivitiesLayout = findViewById(R.id.myActivitiesLayout)
        changePasswordLayout = findViewById(R.id.changePasswordLayout)
        logout = findViewById(R.id.logoutLayout)
        badgesLayout = findViewById(R.id.editBadgesLayout)

    }


 /*   class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

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
*/

    //}


    /** It will open PhotoActivity to crop the im\mage and call api to upload image to server  */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap


        if (requestCode == GALLERY_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null)
            {
               // mMediaUri = data.getData()

                try {
                    val intent = Intent(this, PhotoActivity::class.java)
                    intent.putExtra(Constants.CONSTANT_URI, data.data.toString())
                    startActivityForResult(intent, 200)
                }
                catch ( e : Exception)
                {
                    e.printStackTrace()
                }


        }


        }

        else if(requestCode == 200 && resultCode==Activity.RESULT_OK)
        {
            if(data!=null)
            {
                try{

            val uri: Uri = Uri.parse(data!!.getStringExtra(Constants.CROPPED_URI))


            if (ConstantMethods.checkForInternetConnection(this)) {

                if (uri != null) {
                    // ConstantMethods.showProgessDialog(this, "Uploading")
                    //upload image
                    println("ur cropped $uri")


                    try {

                      //  val path = RealPathUtils.getPath(this, uri)
                        val path = FileUtils().getRealPath(this, uri)

                        var file1: File? = null
                        if (path != null) {
                            file1 = File(path)
                        } else {
                            file1 = RealPathUtils.getFile(this, uri)

                        }



                        try {

                            // val file = File(file1.absolutePath)
                            if (file1 != null) {
                                var mimeType = RealPathUtils.getMimeType(file1)

                                if (mimeType != null && mimeType.length > 0) {
                                    var requestFile =
                                        RequestBody.create(MediaType.parse(mimeType), file1)
                                    var multipartBody =
                                        MultipartBody.Part.createFormData(
                                            "file",
                                            file1.name,
                                            requestFile
                                        )

                                    if (multipartBody != null) {
                                        try {
                                            val uploadMediaApi =
                                                ApplicationController.retrofit.create(
                                                    FileUploadApi::class.java
                                                )
                                            val response: Single<FileUploadPojo> =
                                                uploadMediaApi.uploadImage(multipartBody)
                                            response.subscribeOn(Schedulers.newThread())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(object :
                                                    DisposableSingleObserver<FileUploadPojo>() {
                                                    override fun onSuccess(uploadPojo: FileUploadPojo) {
                                                        if (uploadPojo != null) {
                                                            if (uploadPojo.fileUrl != null && uploadPojo.fileUrl.length > 0) {
                                                                if (ConstantMethods.checkForInternetConnection(
                                                                        this@UserProfileActivity
                                                                    )
                                                                ) {

                                                                    file1.delete()


                                                                    mediaList!!.add(
                                                                        MediaUploadingPojo(
                                                                            "",
                                                                            file1.absolutePath,
                                                                            uploadPojo.fileMimeType,
                                                                            true
                                                                        )
                                                                    )

                                                                    if (ConstantMethods.checkForInternetConnection(
                                                                            this@UserProfileActivity
                                                                        )
                                                                    ) {
                                                                        ConstantMethods.showProgessDialog(
                                                                            this@UserProfileActivity,
                                                                            "Please Wait Updating Profile..."


                                                                        )

                                                                        val jsonObject =
                                                                            JsonObject()
                                                                        jsonObject.addProperty(
                                                                            "profileUrl",
                                                                            uploadPojo.fileUrl
                                                                        )

                                                                        profilePresenter!!.onUpdateProfilePic(
                                                                            this@UserProfileActivity,
                                                                            jsonObject
                                                                        )
                                                                    }

                                                                    println("image set ${mediaList!!}")
                                                                    val json1 = Gson().toJson(
                                                                        mediaList!!
                                                                    )
                                                                    EasySP.init(this@UserProfileActivity)
                                                                        .putString(
                                                                            ConstantEasySP.UPLOADED_MEDIA,
                                                                            json1
                                                                        )


                                                                }
                                                            }
                                                        }
                                                    }

                                                    override fun onError(e: Throwable) {
                                                        ConstantMethods.cancleProgessDialog()
                                                        try {
                                                            if (e is IOException) {
                                                                ConstantMethods.showError(
                                                                    this@UserProfileActivity,
                                                                    this@UserProfileActivity.resources.getString(
                                                                        R.string.no_internet_title
                                                                    ),
                                                                    this@UserProfileActivity.resources.getString(
                                                                        R.string.no_internet_sub_title
                                                                    )
                                                                )
                                                            } else {
                                                                val exception: HttpException =
                                                                    e as HttpException
                                                                val er: String =
                                                                    exception.response()!!.errorBody()!!.string()
                                                                val errorPojo: ErrorPojo =
                                                                    Gson().fromJson(
                                                                        er,
                                                                        ErrorPojo::class.java
                                                                    )

                                                                if (errorPojo != null) {
                                                                    if (errorPojo.error.isNotEmpty()) {
                                                                        if (errorPojo.message.isNotEmpty()) {
                                                                            ConstantMethods.showError(
                                                                                this@UserProfileActivity,
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
                                                                this@UserProfileActivity,
                                                                this@UserProfileActivity.resources.getString(
                                                                    R.string.error_title
                                                                ),
                                                                this@UserProfileActivity.resources.getString(
                                                                    R.string.error_message
                                                                )
                                                            )

                                                        }
                                                    }

                                                })

                                        } catch (e: Exception) {
                                            ConstantMethods.cancleProgessDialog()
                                            ConstantMethods.showError(
                                                this,
                                                this.resources.getString(R.string.error_title),
                                                "Please try after sometime"
                                            )
                                        }
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            ConstantMethods.cancleProgessDialog()
                            ConstantMethods.showError(
                                this,
                                this.resources.getString(R.string.error_title),
                                this.resources.getString(
                                    R.string.error_message
                                )
                            )
                        }

                        /* mediaAdapter = MediaAdapter(this, mediaStatusArray!!)
                            mediaRecycler!!.adapter = mediaAdapter*/
                        //}


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            }
                catch (e : Exception)
                {
                e.printStackTrace()
                }
            }


        }
    }

    /** When activity resumes, call getDataToPost() */
    override fun onResume() {
        super.onResume()
        getDataToPost()
    }

    /** It goes back to previous activity */
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
    }
}
