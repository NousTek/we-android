package com.we.beyond.model

data class NearByIssuesPojo
    (
    var statusCode: Int,
    var message: String,
    var data: ArrayList<NearByIssues>

)

data class NearByIssues(
    var _id: String,
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var tags: ArrayList<String>,
    var resolved: Boolean,
    var title: String,
    var description: String,
    var coordinates: ArrayList<Double>,
    var city: String,
    var address: String,
    var user: User,
    var category: Category,
    var issueNumber: Int,
    var createdAt: String,
    var distance: Double,
    var likeByUser : Boolean,
    var resolutionsCount : Int,
    var myResolutionsCount : Int,
    var latestResolutionDate : String

)


data class User(
    var _id: String,
    var firstName: String,
    var lastName: String,
    var organizationName: String,
    var profileUrl: String,
    var userLoginType: UserLoginType

)


data class UserLoginType(
    var _id: String,
    var name: String,
    var userType: String
)

data class Category(
    var _id: String,
    var name: String
)