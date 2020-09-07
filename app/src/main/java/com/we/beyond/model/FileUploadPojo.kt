package com.we.beyond.model

data class FileUploadPojo(
    var statusCode : Int,
    var message : String,
    var fileUrl : String,
    var fileMimeType : String,
    var data : String
)