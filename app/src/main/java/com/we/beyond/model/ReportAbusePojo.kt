package com.we.beyond.model

data class ReportAbusePojo (
    var statusCode : Int,
    var message : String,
    var data : ReportAbuseData
)

data class ReportAbuseData(
    var _id : String,
    var type : String,
    var typeId : String,
    var data : String,
    var user : String,
    var createdAt : String,
    var updatedAt : String,
    var _v : Int
)