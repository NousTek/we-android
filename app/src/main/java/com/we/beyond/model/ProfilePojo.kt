package com.we.beyond.model

data class ProfilePojo(
    var statusCode : Int,
    var message : String,
    var data : ProfileData
)


data class ProfileData(
    var location : HomeLocation,
    var enabled : Boolean,
    var kycProvided : Boolean,
    var kycStatus : String,
    var categories : ArrayList<ProfileCategories>,
    var firstName : String,
    var lastName : String,
    var _id : String,
    var organizationName : String,
    var email : String,
    var mobile : String,
    var city : String,
    var area : String,
    var profileUrl : String,
    var userLoginType : ProfileUserLoginType,
    var createdAt : String,
    var updatedAt : String,
    var userLoginTypes : ArrayList<ProfileUserLoginTypes>,
    var summary : Summary
)

data class HomeLocation(
    var coordinates : ArrayList<String>
)

data class ProfileUserLoginType(
    var _id : String,
    var name : String,
    var description : String,
    var userType : String
)

data class ProfileCategories(
    var _id : String,
    var name : String,
    var description : String
)


data class Summary(
    var issues : Int,
    var resolutions : Int,
    var badges : Int
)

data class UpdateProfilePojo(
    var statusCode: Int,
    var message: String,
    var data : UpdatedProfileData
)

data class UpdatedProfileData(
    var enabled : Boolean,
    var categories : ArrayList<String>,
    var _id : String,
    var firstName : String,
    var lastName : String,
    var city : String,
    var area : String,
    var profileUrl : String,
    var userLoginType : UpdatedUserLoginType,
    var updatedAt : String
)

data class UpdatedUserLoginType(
    var order : Int,
    var _id : String,
    var name : String,
    var description : String,
    var userType : String,
    var createdBy: String,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int

)

data class ProfileUserLoginTypes(
    var order : Int,
    var _id : String,
    var name : String,
    var description : String,
    var userType : String

)

data class UpdateProfilePic(
    var statusCode: Int,
    var message: String,
    var data : UpdateProfilePicData
)

data class UpdateProfilePicData(
    var enabled: Boolean,
    var _id: String,
    var profileUrl: String,
    var updatedAt: String
)

data class ChangePassword (
    var statusCode: Int,
    var  message: String,
    var data : ChangePasswordData
)

data class ChangePasswordData(
    var enabled: Boolean,
    var _id : String,
    var updatedAt: String
)

data class Logout (
    var statusCode: Int,
    var message: String
)