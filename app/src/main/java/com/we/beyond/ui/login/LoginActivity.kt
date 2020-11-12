package com.we.beyond.ui.login

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
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
import org.json.JSONException
import org.json.JSONObject
import java.util.*

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
    var issuePostOnWAText: TextView? = null
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
    var createAccount: LinearLayout? = null
    var googleBtn: LinearLayout? = null
    var fbBtn: RelativeLayout? = null
    var forgotPassword: Button? = null
    var txtSignupInfo: TextView? = null
    var txtSignup: TextView? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"
    lateinit var loginButton : LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ConstantMethods.hideKeyBoard(this, this)

        setContentView(R.layout.activity_login_temp)

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        loginPresenter = LoginImpl(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
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
        googleBtn!!.setOnClickListener {
            performGoogleSignIn()
        }
        fbBtn!!.setOnClickListener {
            performFBLogin()
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

        loginButton.setOnClickListener {
            loginButton.setReadPermissions(listOf(EMAIL))
            callbackManager = CallbackManager.Factory.create()

            /*loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.d("MainActivity", "Facebook token: " + loginResult!!.accessToken.token)
                }

                override fun onCancel() { // App code
                }

                override fun onError(exception: FacebookException) { // App code
                }
            })*/
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        if (loginResult != null) {
                            setFacebookData(loginResult)
                        }
                    }

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                    }

                    val accessToken = AccessToken.getCurrentAccessToken()
                }
            )
        }
    }
    private fun performGoogleSignIn()
    {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    private fun performFBLogin()
    {
        val permissions: MutableList<String> =
            ArrayList()
        permissions.add("email")
        permissions.add("public_profile")
        loginButton.setPermissions(permissions)
        loginButton.performClick()
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
        loginButton = findViewById(R.id.fb_connect)
        loginButton.loginBehavior = LoginBehavior.WEB_VIEW_ONLY
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
        issueText!!.typeface = ConstantFonts.raleway_regular
        issuePostOnWAText=findViewById(R.id.txt_post_on_wa)
        issuePostOnWAText!!.typeface = ConstantFonts.raleway_semibold

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
        googleBtn=findViewById(R.id.googleBtn)
        fbBtn=findViewById(R.id.fbBtn)
        txtSignupInfo=findViewById(R.id.txt_signup_info)
        txtSignup=findViewById(R.id.text_signup)
        txtSignup!!.typeface = ConstantFonts.raleway_semibold
        txtSignupInfo!!.typeface = ConstantFonts.raleway_regular
        forgotPassword = findViewById(R.id.btn_forgot_password)
        forgotPassword!!.typeface = ConstantFonts.raleway_semibold

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID",googleId)

            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)

            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)

            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)

            val googleProfilePicURL = account?.photoUrl.toString()
            Log.i("Google Profile Pic URL", googleProfilePicURL)

            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

            showLoggedInUser("$googleFirstName $googleLastName")

        } catch (e: ApiException) {
            // Sign in was unsuccessful
            Log.e(
                "failed code=", e.statusCode.toString()
            )
        }
    }

    private fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { jsonObject: JSONObject?, response: GraphResponse ->
            // Application code
            try {
                Log.i("Response", response.toString())
                var id = ""
                val token = AccessToken.getCurrentAccessToken()
                val email = response.jsonObject.getString("email")
                val firstName = response.jsonObject.getString("first_name")
                val lastName = response.jsonObject.getString("last_name")
                showLoggedInUser("$firstName $lastName")
                val profile = Profile.getCurrentProfile()
                if (profile != null) {
                    id = profile.id
                    val link = profile.linkUri.toString()
                    Log.i("Link", link)

                }
                if (Profile.getCurrentProfile() != null) {
                    Log.i(
                        "Login",
                        "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(
                            200,
                            200
                        )
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name,gender")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun showLoggedInUser(userName : String)
    {
        val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
        sweetAlertDialog.titleText = ""
        sweetAlertDialog.contentText = "Login as $userName"
//        sweetAlertDialog.show()
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.setConfirmClickListener {
            sweetAlertDialog.dismissWithAnimation()
        }
        goToCategoriesScreen()
    }
}
