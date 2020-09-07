package com.we.beyond.ui.login

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.presenter.login.LoginImpl
import com.we.beyond.presenter.login.LoginPresenter
import com.we.beyond.ui.dashboard.CategoriesActivity
import com.we.beyond.ui.dashboard.DashboardActivity
import com.we.beyond.ui.registration.RegistrationActivity
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import org.apache.commons.codec.digest.DigestUtils
import kotlin.Exception

/**
 * this activity for login the app
 */
class LoginActivity : AppCompatActivity(), LoginPresenter.ILoginView {

    var loginPresenter: LoginImpl? = null

    /** init image view */
    var close: ImageView? = null

    /** init text view */
    var weLogo: TextView? = null
    var detailTitle: TextView? = null
    var detailSubTitle: TextView? = null
    var welcomeTitle: TextView? = null
    var loginDetailsText: TextView? = null
    var forgotPasswordText: TextView? = null
    var orText: TextView? = null
    var issueText: TextView? = null
    var forgotTitle: TextView? = null

    /** init text input edit text */
    var emailEdit: TextInputEditText? = null
    var passwordEdit: TextInputEditText? = null
    var emailPasswordEdit: TextInputEditText? = null

    /** init text input layout */
    var usernameLayout: TextInputLayout? = null
    var passwordLayout: TextInputLayout? = null
    var emailPasswordLayout: TextInputLayout? = null

    /** init relative layout */
    var forgotPasswordLayout: RelativeLayout? = null

    /** init button */
    var login: Button? = null
    var createAccount: Button? = null
    var forgotPassword: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ConstantMethods.hideKeyBoard(this, this)

        setContentView(R.layout.activity_login)

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        loginPresenter = LoginImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** get Data */
        getData()

        /** initialize onclick listeners */
        initWithListener()


    }

    /**
     * get the data and check the access token is active or not
     */
    private fun getData() {
        try {
            val isInterceptor: Boolean =
                intent.getBooleanExtra(Constants.IS_INTERCEPTOR, false)


            if (isInterceptor) {
                val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                sweetAlertDialog.titleText = "Session Expired!"
                sweetAlertDialog.contentText = "Please Login to continue"
                sweetAlertDialog.show()
                sweetAlertDialog.setCancelable(false)
                sweetAlertDialog.setConfirmClickListener {
                    sweetAlertDialog.dismissWithAnimation()
                }
            }


        } catch (e: Exception) {

        }
    }

    /**
     * listener initialization
     */
    private fun initWithListener() {

        /** go to registration activity for create new account  */
        createAccount!!.setOnClickListener {
            intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            finish()
        }

        /** call function for post the data */
        login!!.setOnClickListener {
            try {
                if (ConstantMethods.checkForInternetConnection(this)) {
                    getDataToPost()
                }

            } catch (e: Exception) {

            }
        }


        /** post issue on whats app  */
        issueText!!.setOnClickListener {

            //This intent will help you to launch if the package is already installed

            val uri = Uri.parse("https://api.whatsapp.com/send?phone=917263065065")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

        }

        /** this open up the forgot password layout with animation  */
        forgotPasswordText!!.setOnClickListener {
            forgotPasswordLayout!!.visibility = View.VISIBLE
            forgotPasswordLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_in_up
                )
            )
        }


        /** close the forgot password layout with animation */
        close!!.setOnClickListener {
            forgotPasswordLayout!!.visibility = View.GONE
            forgotPasswordLayout!!.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.slide_out_down
                )
            )
        }

        /**
         * call the function onForgotPassword of login presenter
         * and passes json object as parameter
         */
        forgotPassword!!.setOnClickListener {
            val emailPassword = emailPasswordEdit!!.text!!.toString().toLowerCase()


            if (emailPassword.isEmpty()) {
                emailPasswordLayout!!.error =
                    "Please fill Email Address or Mobile Number to forgot password"
            } else {
                forgotPasswordLayout!!.visibility = View.GONE
                forgotPasswordLayout!!.startAnimation(
                    AnimationUtils.loadAnimation(
                        this,
                        R.anim.slide_out_down
                    )
                )
                try {

                    val jsonObject = JsonObject()
                    jsonObject.addProperty("emailMobile", emailPasswordEdit!!.text.toString())
                    if (ConstantMethods.checkForInternetConnection(this)) {
                        ConstantMethods.showProgessDialog(
                            this,
                            "Please Wait..."
                        )
                        loginPresenter!!.onForgotPassword(this, jsonObject)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }


    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }

        return false
    }

    /**
     * get the data from ui and converts to json object
     */
    private fun getDataToPost() {
        try {
            val username = emailEdit!!.text!!.toString().toLowerCase()
            val password = passwordEdit!!.text!!.toString()

            if (username.isEmpty()) {
                usernameLayout!!.error = "Please fill Email Address or Mobile Number"
            } else {
                usernameLayout!!.isErrorEnabled = false
            }
            if (password.isEmpty()) {
                passwordLayout!!.error = "Please fill Password"
            } else {
                passwordLayout!!.isErrorEnabled = false
            }

            if (emailEdit!!.text!!.isNotEmpty()) {
                if (passwordEdit!!.text!!.isNotEmpty()) {
                    var refreshedToken = EasySP.init(this).getString("token")

                    if (refreshedToken!!.length == 1) {
                        refreshedToken = FirebaseInstanceId.getInstance().token
                    }

                    //Toast.makeText(this,"refreshed Token $refreshedToken",Toast.LENGTH_SHORT).show()

                    if (ConstantMethods.checkForInternetConnection(this@LoginActivity)) {
                        val jsonObject = JsonObject()
                        jsonObject.addProperty("userName", username)
                        //encrypt password here

                        val hex = DigestUtils.sha256(password)
                        val hash:String = ConstantMethods.bytesToHex(hex)

                        if (hash != null && hash.length > 0){
                            jsonObject.addProperty("password", hash)
                        }
                        jsonObject.addProperty("deviceToken", refreshedToken)
                        jsonObject.addProperty("deviceType", "android")
                        postDataToServer(jsonObject)

                    }

                } else {
                    passwordLayout!!.error = "Please Fill Password"
                }

            } else {
                usernameLayout!!.error = "Please Fill Email Address Or Mobile Number"
            }


        } catch (e: Exception) {

        }
    }

    /**
     * this function calls the onLogin method of login presenter
     * and pass json object as parameter
     */
    private fun postDataToServer(jsonObject: JsonObject) {
        try {
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                loginPresenter!!.onLogin(this@LoginActivity, jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /**
     * after success of login api,
     * goToNextScreen and goToCategoriesScreen functions
     * are worked for going next screen
     */
    override fun goToNextScreen() {
        intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    override fun goToCategoriesScreen() {
        intent = Intent(this, CategoriesActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }


    /** ui initialization */
    private fun initElementsWithIds() {
        /** ids of image view */
        close = findViewById(R.id.img_close)

        /** ids of text view */

        weLogo = findViewById(R.id.img_we_logo)
        weLogo!!.typeface = ConstantFonts.abys_regular

        detailTitle = findViewById(R.id.txt_splash_details_title)
        detailTitle!!.typeface = ConstantFonts.raleway_semibold

        detailSubTitle = findViewById(R.id.txt_splash_details_sub_title)
        detailSubTitle!!.typeface = ConstantFonts.raleway_semibold

        welcomeTitle = findViewById(R.id.txt_welcome)
        welcomeTitle!!.typeface = ConstantFonts.raleway_regular

        loginDetailsText = findViewById(R.id.txt_details)
        loginDetailsText!!.typeface = ConstantFonts.raleway_regular

        forgotPasswordText = findViewById(R.id.txt_forgot_password)
        forgotPasswordText!!.typeface = ConstantFonts.raleway_semibold

        orText = findViewById(R.id.txt_or)
        orText!!.typeface = ConstantFonts.raleway_regular

        issueText = findViewById(R.id.txt_issue)
        issueText!!.typeface = ConstantFonts.raleway_semibold

        forgotTitle = findViewById(R.id.txt_forgot_title)
        forgotTitle!!.typeface = ConstantFonts.raleway_semibold

        /** ids of edit text */
        emailEdit = findViewById(R.id.et_email)
        emailEdit!!.typeface = ConstantFonts.raleway_semibold

        passwordEdit = findViewById(R.id.et_password)
        passwordEdit!!.typeface = ConstantFonts.raleway_semibold

        emailPasswordEdit = findViewById(R.id.et_email_password)
        emailPasswordEdit!!.typeface = ConstantFonts.raleway_semibold


        /** ids of text input layout */
        usernameLayout = findViewById(R.id.usernameLayout)
        passwordLayout = findViewById(R.id.passwordLayout)
        emailPasswordLayout = findViewById(R.id.forgotLayout)

        /** ids of relative layout */
        forgotPasswordLayout = findViewById(R.id.forgotPasswordLayout)

        /** ids of button */
        login = findViewById(R.id.btn_login)
        login!!.typeface = ConstantFonts.raleway_semibold

        createAccount = findViewById(R.id.btn_create_account)
        createAccount!!.typeface = ConstantFonts.raleway_semibold

        forgotPassword = findViewById(R.id.btn_forgot_password)
        forgotPassword!!.typeface = ConstantFonts.raleway_semibold

    }
}
