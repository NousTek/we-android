package com.we.beyond.model

data class LeaderBoardPojo (
    var statusCode : Int,
    var message : String,
    var data : LeaderBoardData
)

data class LeaderBoardData(
    var dataList : ArrayList<DataList>,
    var topList :  ArrayList<DataList>
)

data class DataList(
    var count : Int,
    var user : LeaderUserDetails
)

data class LeaderUserDetails(
    var _id : String,
    var firstName : String,
    var lastName : String,
    var organizationName : String,
    var profileUrl : String,
    var userLoginType : LeaderUserLoginType

)

data class LeaderUserLoginType(
    var name : String,
    var userType : String
)
