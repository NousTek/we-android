package com.we.beyond.presenter.registration

import android.content.Context
import com.google.gson.JsonObject
import com.we.beyond.model.RegistrationPojo
import com.we.beyond.model.UserTypes

interface RegistrationPresenter
{
    /** this interface and functions are showing result on view */
    interface IRegistrationView
    {
        fun setUserTypesAdapter( userTypes : ArrayList<UserTypes> )
        //fun setCitiesAdapter( cities : ArrayList<RegistrationPojo> )
        fun goToNextScreen()
        fun goToPreviousScreen()
    }

    /** this interface is working for calling request user type and register api respectively */
    interface  IRegistrationPresenter
    {
        fun onRequestUserTypes( context : Context )
        //fun onRequestCities( context: Context )
        fun onRegister( context: Context ,jsonObject: JsonObject)
        fun onBackClick()
    }
}