package com.we.beyond.model

data class HeatMapPojo (
    var statusCode : Int,
    var message : String,
    var data : ArrayList<HeatMapData>
)

data class HeatMapData(
    var _id : String,
    var imageUrls : ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var resolved : Boolean,
    var title : String,
    var coordinates : ArrayList<Double>,
    var distance : Double

)