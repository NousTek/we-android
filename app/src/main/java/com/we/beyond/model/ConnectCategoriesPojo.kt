package com.we.beyond.model

data class ConnectCategoriesPojo (
    var statusCode : Int,
    var message : String,
    var data : ArrayList<ConnectCategories>
)

data class ConnectCategories(
    var order : Int,
    var _id : String,
    var name : String,
    var description : String,
    var imageUrl : String
)

data class ConnectCreatePojo(
    var statusCode : Int,
    var message : String,
    var fileUrl : String
)

data class ConnectPojo(
    var statusCode : Int,
    var message : String,
    var data : ArrayList<ConnectList>
)


data class ConnectList(
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var comments : Int,
    var likes : Int,
    var disabled : Boolean,
    var _id : String,
    var title : String,
    var description : String,
    var user : UserData,
    var connectCategory : ConnectCategoryData,
    var likeByUser : Boolean,
    var createdBy : String,
    var createdAt : String,
    var updatedAt : String
)


data class UserData(
    var _id : String,
    var firstName : String,
    var lastName : String,
    var organizationName : String,
    var profileUrl : String,
    var userLoginType : UserLoginTypes

)

data class UserLoginTypes(
    var _id : String,
    var name : String,
    var userType : String
)

data class ConnectCategoryData(
    var _id : String,
    var name : String
)

