package com.we.beyond.interceptor

import android.content.Context

import androidx.multidex.MultiDexApplication
import com.facebook.FacebookSdk
import com.we.beyond.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class ApplicationController : MultiDexApplication() {


    companion object {
        lateinit var context:Context
        lateinit var retrofit: Retrofit
        lateinit var executor: ExecutorService
        var profile: String = Dev.NAME

        object Dev {
            //val BASE_URL = "http://WeTest-env.m8vkwfpfid.ap-south-1.elasticbeanstalk.com/"
           val BASE_URL = "https://www.weapp.mobi/"
           // val BASE_URL = "http://weapplive-env-prod.xfaj3epmdm.ap-south-1.elasticbeanstalk.com/"
            val NAME = "dev"
        }

        object Prod {
            val BASE_URL = ""
            val NAME = "prod"
        }

        fun getBaseUrl(): String {

            if (Dev.NAME.equals(profile, ignoreCase = true)) {
                return Dev.BASE_URL
            } else if (Prod.NAME.equals(profile, ignoreCase = true)) {
                return Prod.BASE_URL
            } else {
                return Dev.BASE_URL
            }

        }
        fun getAppContext(): Context {
            return context
        }



    }
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        FacebookSdk.sdkInitialize(applicationContext)
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1)
        val okkHttpBuilder: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(MyInterceptor(context))
            .connectTimeout(600, TimeUnit.SECONDS)
            .readTimeout(600, TimeUnit.SECONDS)
            .build()
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL).client(okkHttpBuilder)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

        retrofit = builder.build()
    }



}