package com.we.beyond.model

data class LatLongPojo(
    var latitude:Double,
    var longitude:Double,
    var deviceFcmToken:String

){
    constructor() : this(0.0,0.0,"")
}


data class LatLongSelectedPojo(
    var latitude:Double,
    var longitude:Double
)

