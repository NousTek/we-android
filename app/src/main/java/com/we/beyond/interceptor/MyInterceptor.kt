package com.we.beyond.interceptor

import android.content.Context
import android.content.Intent
import android.util.Log
import com.we.beyond.ui.login.LoginActivity
import com.we.beyond.util.ConstantEasySP
import com.we.beyond.util.ConstantMethods
import com.we.beyond.util.Constants
import com.white.easysp.EasySP
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MyInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val token = EasySP.init(context).getString(ConstantEasySP.SP_ACCESS_TOKEN, "")

        var request: Request = chain.request()
        var builder: Request.Builder =
            request.newBuilder().header("Content-Type", "application/json")
        if (token.length > 0) {
            setAuthHeader(builder, token)
        }
        request = builder.build()
        var response : Response = chain.proceed(request)

        if (response.code() == 410) {
            redirectToLoginPage()
        }else if (response.code() == 401){
            redirectToLoginPage()
        }
        else if(response.code() == 408)
        {
            redirectToLoginPage()
        }

        return response
    }

    private fun redirectToLoginPage() {
        try {
            EasySP.init(context).putString(ConstantEasySP.SP_ACCESS_TOKEN,"")
            EasySP.init(context).putBoolean(ConstantEasySP.SP_IS_LOGIN,false)


            var intent = Intent(context, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra(Constants.IS_INTERCEPTOR,true)
            context.startActivity(intent)
//            var sweetAlertDialog: SweetAlertDialog =
//                SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
//            sweetAlertDialog.setTitleText("Session Expired!")
//            sweetAlertDialog.setContentText("Please Login to continue")
//            sweetAlertDialog.show()
//            sweetAlertDialog.setCancelable(false)
//            sweetAlertDialog.setConfirmClickListener(object :
//                SweetAlertDialog.OnSweetClickListener {
//                override fun onClick(sweetAlertDialog: SweetAlertDialog) {
//
//                    sweetAlertDialog.dismissWithAnimation()
//                }
//            })




        } catch (e: Exception) {
            Log.i("error",e.message)
        }
    }

    private fun setAuthHeader(builder: Request.Builder, token: String) {
        if (token != null && token.length>0)
        //Add Auth token to each request if authorized
            builder.header("x-auth-token", token)
    }

}