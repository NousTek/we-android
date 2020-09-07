package com.we.beyond.model

data class BadgesPojo (
    var statusCode : Int,
    var message: String,
    var data : BadgesData
)

data class BadgesData(
    var enabled : Boolean,
    var kycProvided : String,
    var categories : ArrayList<BadgesCategories>,
    var _id : String,
    var firstName : String,
    var lastName : String,
    var organizationName : String,
    var email : String,
    var mobile : String,
    var city : String,
    var area : String,
    var profileUrl : String,
    var userLoginType : BadgesUserLoginType,
    var createdAt : String,
    var updatedAt : String,
    var summary : BadgesSummary,
    var badges : ArrayList<Badges>,
    var userBadges : ArrayList<UserBadges>


)

data class BadgesCategories(
    var _id : String,
    var name : String,
    var description : String
)


data class BadgesUserLoginType(
    var _id : String,
    var name : String,
    var description : String,
    var userType : String

)

data class BadgesSummary(
    var issues : Int,
    var resolutions : Int,
    var badges : Int
)

data class Badges(
    var _id : String,
    var name : String,
    var description : String,
    var selectedImageUrl : String,
    var deselectedImageUrl : String,
    var isSelected : Boolean,
    var date : String
)


data class UserBadges(
    var _id : String,
    var user : String,
    var badge : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int
)