package com.we.beyond.presenter.profile

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.ProfilePojo
import com.we.beyond.model.RegistrationPojo
import com.we.beyond.model.UpdateProfilePic

interface ProfilePresenter
{
    /** this interface and functions are showing result on view */
    interface IProfileView
    {
        fun setUserProfileDataAdapter( userData : ProfilePojo )
        //fun setCitiesAdapter( cities : ArrayList<RegistrationPojo> )
        fun onUpdateSuccessfully()
        fun setUserProfilePic(userProfilePic : UpdateProfilePic)

    }

    /** this interface is working for calling respected apis */
    interface  IProfilePresenter
    {
        fun onRequestUserProfileData( context : Context)
        //fun onRequestCities( context: Context )
        fun onUpdateProfile(context: Context,jsonObject: JsonObject)
        fun onUpdateProfilePic(context: Context,jsonObject: JsonObject)
    }
}