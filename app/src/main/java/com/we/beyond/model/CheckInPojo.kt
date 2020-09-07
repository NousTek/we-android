package com.we.beyond.model

data class CheckInPojo (
    var statusCode : Int,
    var message : String,
    var data : CheckInData
)

data class CheckInData(
    var _id : String,
    var type : String,
    var typeId : String,
    var user : String,
    var createdAt : String,
    var updatedAt : String,
    var _v : Int
)