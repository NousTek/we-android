package com.we.beyond.model

data class SubscribePojo(
    var statusCode : Int,
    var message : String,
    var data : SubscribeData
)


data class SubscribeData(
    var _id : String,
    var type : String,
    var typeId : String,
    var user : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int
)

