package com.we.beyond.presenter.profile

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.we.beyond.R
import com.we.beyond.api.ProfileApi
import com.we.beyond.interceptor.ApplicationController
import com.we.beyond.model.*
import com.we.beyond.util.ConstantMethods
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.io.IOException
/** It is implementation class of EditProfile and UserProfile Activity
 * which implement all api regarding get and update profile  */
class ProfileImpl (profile : ProfilePresenter.IProfileView) : ProfilePresenter.IProfilePresenter
{


    var profile = profile

    /** It calls getUserProfileData method  */
    override fun onRequestUserProfileData(context: Context)
    {
        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getUserProfileData(context)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

    }

    /** It calls the getProfile api
     * and set UserProfileDataAdapter
     */
    private fun getUserProfileData(context: Context)
    {
        try {
            val profileApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<ProfilePojo> = profileApi.getProfile()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ProfilePojo>() {
                    override fun onSuccess(profilePojo: ProfilePojo) {

                        ConstantMethods.cancleProgessDialog()

                        if (profilePojo != null) {

                            profile.setUserProfileDataAdapter(profilePojo)

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


   /* override fun onRequestCities(context: Context)
    {

        try{
            if(ConstantMethods.checkForInternetConnection(context))
            {
                getCities(context)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }


    }

    fun getCities(context: Context)
    {

        try {
            val registerApi = ApplicationController.retrofit.create(RegistrationApi::class.java)
            val response: Single<ArrayList<RegistrationPojo>> = registerApi.getAllCities()
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<ArrayList<RegistrationPojo>>() {
                    override fun onSuccess(citiesPojo: ArrayList<RegistrationPojo>) {
                        ConstantMethods.cancleProgessDialog()
                        if (citiesPojo != null) {

                            if (citiesPojo != null && citiesPojo.isNotEmpty()) {
                                profile.setCitiesAdapter(citiesPojo)
                            } else {

                            }

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
            ConstantMethods.showError(context, context.resources.getString(R.string.error_title), context.resources.getString(R.string.error_message))
        }

    }


*/

    /** It calls getDataToUpdateProfile method  */
    override fun onUpdateProfile(context: Context,jsonObject: JsonObject)
    {


        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToUpdateProfile(context,jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the updateProfile api with json object
     * and show success dialog
     */
    private fun getDataToUpdateProfile(
        context: Context,
        jsonObject: JsonObject
    )

    {
        try {
            val issueApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<UpdateProfilePojo> = issueApi.updateProfile(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateProfilePojo>() {
                    override fun onSuccess(updateProfilePojo: UpdateProfilePojo) {
                        ConstantMethods.cancleProgessDialog()
                        if (updateProfilePojo != null) {

                            onSuccess(context)

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

    }


    /** It calls getDataToUpadteProfilePic method  */
    override fun onUpdateProfilePic(context: Context,jsonObject: JsonObject)
    {


        try {
            if (ConstantMethods.checkForInternetConnection(context)) {
                getDataToUpdateProfilePic(context,jsonObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /** It calls the updateProfilePic api with json object
     * and set UserProfilePic
     */
    private fun getDataToUpdateProfilePic(
        context: Context,
        jsonObject: JsonObject
    )

    {
        try {
            val issueApi = ApplicationController.retrofit.create(ProfileApi::class.java)
            val response: Single<UpdateProfilePic> = issueApi.updateProfilePic(jsonObject)
            response.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : DisposableSingleObserver<UpdateProfilePic>() {
                    override fun onSuccess(updateProfilePic: UpdateProfilePic) {
                        ConstantMethods.cancleProgessDialog()
                        if (updateProfilePic != null) {

                            profile.setUserProfilePic(updateProfilePic)


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

    }

    /** show success dialog */
   fun onSuccess(context: Context)
   {
       try {
           val sweetAlertDialog =
               SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
           sweetAlertDialog.titleText = "Success!"
           sweetAlertDialog.contentText = "Successfully updated profile."
           sweetAlertDialog.show()
           sweetAlertDialog.setCancelable(false)
           sweetAlertDialog.setConfirmClickListener {
               sweetAlertDialog.dismissWithAnimation()

               profile.onUpdateSuccessfully()
           }

       } catch (e: Exception) {
           e.printStackTrace()
       }


   }

}