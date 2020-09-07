package com.we.beyond.model

data class ConnectDetailsPojo(
    var statusCode: Int,
    var message: String,
    var data: ConnectDetailsData

)

data class ConnectDetailsData(
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var comments: Int,
    var likes: Int,
    var disabled: Boolean,
    var _id: String,
    var title: String,
    var description: String,
    var user: UserDetailsData,
    var connectCategory: ConnectData,
    var likeByUser: Boolean,
    var createdBy: String,
    var createdAt: String,
    var updatedAt: String,
    var __v : Int,
    var disabledBy : String
)

data class UserDetailsData(
    var _id: String,
    var firstName: String,
    var lastName: String,
    var organizationName: String,
    var profileUrl: String,
    var userLoginType: UserLoginTypesDetails

)

data class UserLoginTypesDetails(
    var _id: String,
    var name: String,
    var userType: String
)

data class ConnectData(
    var _id: String,
    var name: String
)


data class DeleteConnectDetailsPojo(
    var statusCode: Int,
    var message: String,
    var data: DeleteConnectDetailsData

)

data class DeleteConnectDetailsData(
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var comments: Int,
    var likes: Int,
    var disabled: Boolean,
    var _id: String,
    var title: String,
    var description: String,
    var user: String,
    var connectCategory: String,
    var likeByUser: Boolean,
    var createdBy: String,
    var createdAt: String,
    var updatedAt: String,
    var __v : Int,
    var disabledBy : String
)


data class UpdateConnectPojo (
    var statusCode: Int,
    var message: String,
    var data : UpdateConnectData
)


data class UpdateConnectData(
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var disabled: Boolean,
    var _id: String,
    var title: String,
    var description: String,
    var user: String,
    var connectCategory: String,
    var updatedAt: String
)

