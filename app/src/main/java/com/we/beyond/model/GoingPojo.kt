package com.we.beyond.model

data class GoingPojo (
    var statusCode : Int,
    var message : String,
    var data : GoingData
)


data class GoingData(
    var _id : String,
    var type : String,
    var typeId : String,
    var user : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int
)


data class GoingIdPojo(
    var statusCode: Int,
    var message: String,
    var data : ArrayList<GoingIdData>
)

data class GoingIdData(
    var _id: String,
    var user: GoingUserData
)

data class GoingUserData(
    var _id: String,
    var profileUrl : String,
    var name : String
)



