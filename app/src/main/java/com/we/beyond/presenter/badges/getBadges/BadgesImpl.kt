package com.we.beyond.presenter.badges.getBadges

import android.content.Context
import com.google.gson.Gson
import com.we.beyond.R
import com.we.beyond.api.BadgesApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.Badges
import com.we.beyond.model.BadgesPojo
import com.we.beyond.model.ErrorPojo
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException

/** It is implementation class of Create Gathering Activity
 * which implement all api regarding get badges */

class BadgesImpl(badges : BadgesPresenter.IBadgesView) : BadgesPresenter.IBadgesPresenter
{


    var badges = badges

    /** It calls getDataToPost method which takes context input  */
    override fun onBadges(context: Context)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPost(context)
            }
        } catch (e: Exception) {
                e.printStackTrace()
        }
    }

    /** It calls getDataToPostWithUserId method which takes context and json object as input  */
    override fun onBadgesUser(context: Context, userId: String)
    {
        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToPostWithUserId(context,userId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /** It calls the getBadges api
     * and set BadgesAdapter
     */
    private fun getDataToPost(context: Context)
    {
        try {
            val badgesApi = ApplicationController.retrofit.create(BadgesApi::class.java)
            val response: Single<BadgesPojo> = badgesApi.getBadges()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<BadgesPojo>() {
                    override fun onSuccess(badgesPojo: BadgesPojo) {
                        ConstantMethods.cancleProgessDialog()
                        if (badgesPojo != null) {

                            badges.setBadgesAdapter(badgesPojo)

                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if(e is IOException){
                                ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(R.string.no_internet_sub_title))
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
                                                context,
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
                            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                                R.string.error_message))

                        }
                    }

                })



        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }
    }

    /** It calls the getBadgesById api which below parameter
     * and set BadgesAdapter
     */
    private fun getDataToPostWithUserId(context: Context,userId: String)
    {
        try {
            val badgesApi = ApplicationController.retrofit.create(BadgesApi::class.java)
            val response: Single<BadgesPojo> = badgesApi.getBadgesById(userId)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<BadgesPojo>() {
                    override fun onSuccess(badgesPojo: BadgesPojo) {
                        ConstantMethods.cancleProgessDialog()
                        if (badgesPojo != null) {

                            badges.setBadgesAdapter(badgesPojo)

                        }
                    }

                    override fun onError(e: Throwable) {
                        ConstantMethods.cancleProgessDialog()
                        try {
                            if(e is IOException){
                                ConstantMethods.showError(context, context.resources.getString(R.string.no_internet_title),context.resources.getString(R.string.no_internet_sub_title))
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
                                                context,
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
                            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                                R.string.error_message))

                        }
                    }

                })



        }
        catch (e : Exception)
        {
            ConstantMethods.cancleProgessDialog()
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(
                R.string.error_message))
        }
    }


}