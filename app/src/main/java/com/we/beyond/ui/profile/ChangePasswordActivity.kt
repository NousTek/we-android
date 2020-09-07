package com.we.beyond.ui.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.presenter.profile.changePassword.ChangePasswordImpl
import com.we.beyond.presenter.profile.changePassword.ChangePasswordPresenter
import com.we.beyond.ui.login.LoginActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantFonts
import com.we.beyond.util.ConstantMethods
import com.white.easysp.EasySP
import org.apache.commons.codec.digest.DigestUtils

/** It will change the password of user */
class ChangePasswordActivity : AppCompatActivity(),ChangePasswordPresenter.IChangePasswordView
{
    /** initialize implementors */
    var changePasswordPresenter : ChangePasswordImpl?=null

    /** init image view */
    var back : ImageView?=null

    /** init text view */
    var title : TextView?=null
    var tvPasswordHint : TextView?=null

    /** init text input edit text */
    var password: TextInputEditText? = null
    var newPassword: TextInputEditText? = null

    /** init text input layout */
    var passwordLayout: TextInputLayout? = null
    var retypePasswordLayout: TextInputLayout? = null


    /** init button */
    var update: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        /** initialize implementation */
        changePasswordPresenter = ChangePasswordImpl(this)

        /** initialize ids of elements */
        initElementsWithIds()

        /** initialize onclick listener */
        initWithListener()

    }

    /** It will remove the access token and open LoginActivity */
    override fun setChangePasswordData()
    {
        try {

            EasySP.init(this).remove(ConstantEasySP.SP_IS_LOGIN)

            EasySP.init(this).remove(ConstantEasySP.SP_ACCESS_TOKEN)

            //EasySP.init(this).remove("token")

            val intent = Intent(this, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()



        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    /** ui initialization */
    private fun initWithListener()
    {

        /** It goes back to previous fragment or activity */
        back!!.setOnClickListener {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        /** It will call postDataToServer() with old and new password
         * in the form of json object */
        update!!.setOnClickListener {

            try {

                if (password!!.text!!.isEmpty()) {
                    passwordLayout!!.error = "Please Fill Password"
                } else {
                    passwordLayout!!.isErrorEnabled = false
                }
                if (newPassword!!.text!!.isEmpty()) {
                    retypePasswordLayout!!.error = "Please Fill New Password"
                } else {
                    retypePasswordLayout!!.isErrorEnabled = false
                }

                if(password!!.text!!.isNotEmpty() && newPassword!!.text!!.isNotEmpty()){
                    val jsonObject = JsonObject()

                    val oldPasswordHash = DigestUtils.sha256(password!!.text.toString().trim())
                    val newPasswordHash = DigestUtils.sha256(newPassword!!.text.toString().trim())

                    val oldHashString = ConstantMethods.bytesToHex(oldPasswordHash)
                    val newHashString = ConstantMethods.bytesToHex(newPasswordHash)

                    if (oldHashString != null && oldHashString.length > 0 && newHashString != null && newHashString.length > 0){
                        jsonObject.addProperty("oldPassword", oldHashString)
                        jsonObject.addProperty("newPassword", newHashString)
                    }


                    if (ConstantMethods.checkForInternetConnection(this)) {
                        postDataToServer(jsonObject)
                    }
                }
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

        }

    }

    /** It takes the json object as input and send to onRequestChangePasswordData function of changePassword presenter */
    private fun postDataToServer(jsonObject: JsonObject)
    {
        try{
            if (ConstantMethods.checkForInternetConnection(this)) {
                ConstantMethods.showProgessDialog(this, "Please Wait...")
                changePasswordPresenter!!.onRequestChangePasswordData(this, jsonObject)
            }
        }

        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** ui initialization */
    private fun initElementsWithIds()
    {
        /** ids of image */
        back = findViewById(R.id.img_back)

        /** ids of button */
        update = findViewById(R.id.btn_update)

        /** ids of text view */
        title = findViewById(R.id.txt_title)
        title!!.typeface = ConstantFonts.raleway_semibold
        tvPasswordHint = findViewById(R.id.tvPasswordHint)
        tvPasswordHint!!.typeface = ConstantFonts.raleway_semibold


        /** ids of edit text */
        password = findViewById(R.id.et_password)
        password!!.typeface = ConstantFonts.raleway_semibold

        newPassword = findViewById(R.id.et_new_password)
        newPassword!!.typeface = ConstantFonts.raleway_semibold


        /** ids of input  text  layout*/
        passwordLayout = findViewById(R.id.PasswordLayout)
        retypePasswordLayout = findViewById(R.id.newPasswordLayout)


    }

    /** It goes back to previous fragment or activity */
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()

    }
}
