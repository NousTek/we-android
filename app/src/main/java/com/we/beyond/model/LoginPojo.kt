package com.we.beyond.model

data class LoginPojo
    (
    var accessToken :String,
    var userLoginType : String,
    var userCategories : Boolean
)

data class ForgotPasswordPojo(
    var statusCode : Int,
    var message : String,
    var data : String
)