package com.we.beyond.model

data class NotificationPojo (
    var statusCode : Int,
    var message : String,
    var data : ArrayList<NotificationData>
)

data class NotificationData(
    var _id : String,
    var type : String,
    var typeId : String,
    var activity : String,
    var issue: String,
    var connect : String,
    var gathering : String,
    var createdBy : CreatedByData,
    var data : String,
    var createdAt : String,
    var displayText : String
)

data class CreatedByData(
    var  profileUrl : String
)


data class NotifyServer(
    var statusCode: Int,
    var message: String,
    var data : Boolean
)
