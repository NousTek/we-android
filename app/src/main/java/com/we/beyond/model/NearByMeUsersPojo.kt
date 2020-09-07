package com.we.beyond.model

data class NearByMeUsersPojo (
    var statusCode : Int,
    var message : String ,
    var data : ArrayList<NearByMeUsersData>
)

data class NearByMeUsersData(
    var _id : String,
    var userLoginType : NearByMeUserLoginType,
    var profileUrl : String,
    var distance : Double,
    var name : String
)

data class NearByMeUserLoginType(
    var name : String,
    var userType : String
)