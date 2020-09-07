package com.we.beyond.model

data class MyActivityPojo (
    var statusCode : Int,
    var message : String,
    var data : ArrayList<MyActivityData>
)

data class MyActivityData(
    var _id : String,
    var user : UserActivityData,
    var data : String,
    var createdAt : String,
    var displayText : String
)

data class UserActivityData(
    var  profileUrl : String
)