package com.we.beyond.model

data class LikePojo (
    var statusCode : Int,
    var message : String,
    var data : LikeData
)


data class LikeData(
    var _id : String,
    var type : String,
    var typeId : String,
    var user : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int
)