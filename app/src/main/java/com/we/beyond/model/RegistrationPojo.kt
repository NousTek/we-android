package com.we.beyond.model

data class RegistrationPojo
    ( var statusCode : Int, var message : String ,  var data : ArrayList<UserTypes> , var order: Int, var _id: String, var name : String ,var description : String ,var areas : ArrayList<String>)


data class UserTypes
    ( var order : Int , var _id : String , var name : String , var description : String , var userType : String )