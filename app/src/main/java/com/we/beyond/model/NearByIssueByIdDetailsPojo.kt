package com.we.beyond.model

data class NearByIssueByIdDetailsPojo (
    var statusCode : Int,
    var message : String,
    var data: Data
)


data class Data(
    var _id: String,
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var tags: ArrayList<String>,
    var comments : Int,
    var likes : Int,
    var resolved: Boolean,
    var gatherings : ArrayList<Gatherings>,
    var title: String,
    var description: String,
    var coordinates: ArrayList<Double>,
    var city: String,
    var address: String,
    var user: UserDetails,
    var category: CategoryDetails,
    var issueNumber: Int,
    var createdAt: String,
    var updatedAt: String ,
    var distance: Double,
    var likeByUser : Boolean,
    var resolutionsCount : Int,
    var commentsCount : Int

)


data class UserDetails(
    var _id: String,
    var firstName: String,
    var lastName: String,
    var organizationName : String,
    var profileUrl: String,
    var userLoginType: UserLoginTypeDetails

)


data class UserLoginTypeDetails(
    var _id: String,
    var name: String,
    var userType: String
)

data class CategoryDetails(
    var _id: String,
    var name: String,
    var imageUrl: String
)

data class Gatherings(
    var location : GatheringLocation,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var invites : ArrayList<String>,
    var comments: Int,
    var likes: Int,
    var campaigns : ArrayList<String>,
    var disabled : Boolean,
    var _id : String,
    var title : String,
    var description : String,
    var gatheringDate : String,
    var city : String,
    var address : String,
    var createdBy : String,
    var user : String,
    var issue : String,
    var gatheringNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var _v : Int,
    var disabledRemark : String

)

data class GatheringLocation(
    var type : String,
    var coordinates : ArrayList<String>
)


data class DeleteNearByIssueByIdDetailsPojo (
    var statusCode : Int,
    var message : String,
    var data: DeleteIssueDetailsData
)


data class DeleteIssueDetailsData(
    var _id: String,
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var tags: ArrayList<String>,
    var comments : Int,
    var likes : Int,
    var resolved: Boolean,
    var gatherings : ArrayList<String>,
    var disabled: Boolean,
    var title: String,
    var description: String,
    var coordinates: ArrayList<Double>,
    var city: String,
    var address: String,
    var user: String,
    var category: String,
    var createdBy : String,
    var issueNumber: Int,
    var createdAt: String,
    var updatedAt: String ,
    var __v : Int,
    var disabledRemark : String

)


data class UpdateIssuePojo(
    var statusCode: Int,
    var message: String,
    var data : UpdateIssueData
)

data class UpdateIssueData(
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var tags: ArrayList<String>,
    var disabled: Boolean,
    var _id: String,
    var title: String,
    var description: String,
    var coordinates: ArrayList<Double>,
    var city: String,
    var address: String,
    var user: String,
    var category: String,
    var videoUrlThumbnails : ArrayList<String>,
    var updatedAt: String ,
    var __v : Int
)


