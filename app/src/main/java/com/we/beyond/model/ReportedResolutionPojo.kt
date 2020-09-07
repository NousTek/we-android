package com.we.beyond.model


data class ReportedResolutionPojo (
    var statusCode : Int,
    var message : String,
    var data : ReportedResolutionData
)


data class ReportedResolutionData(
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var resolutionUsers : ArrayList<String>,
    var likes : Int,
    var abuses : Int,
    var delete : Boolean,
    var _id : String,
    var resolutionDate : String,
    var text : String,
    var commentType : String,
    var markAsFinal : Boolean,
    var user : String,
    var issue : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int

)

data class ReportedUserData(
    var statusCode : Int,
    var message : String,
    var data : ArrayList<UserList>
)


data class UserList(
    var _id : String,
    var profileUrl : String,
    var name : String
)


data class MarkAsResolvedPojo(
    var statusCode: Int,
    var message: String
)

data class ReportedResolutionDetailsPojo (
    var statusCode: Int,
    var message: String,
    var data : ReportedResolutionDetailsData
)


data class ReportedResolutionDetailsData(
    var issue: ResolutionIssueData,
    var resolutions : ArrayList<Resolution>
)

data class Resolution(
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var resolutionUsers : ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var likes : Int,
    var abuses : Int,
    var delete : Boolean,
    var _id : String,
    var resolutionDate : String,
    var text : String,
    var commentType : String,
    var markAsFinal : Boolean,
    var user : ResolutionUser,
    var issue : String,
    var issueCreatedBy : String,
    var createdAt : String,
    var updatedAt : String,
    var __v: Int,
    var resolutionUsersData : ArrayList<ResolutionUserData>

)

data class ResolutionUserData(
    var _id : String,
    var name : String
)

data class ResolutionIssueData(
    var _id: String,
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var tags: ArrayList<String>,
    var comments : Int,
    var likes : Int,
    var resolved: Boolean,
    var gatherings : ArrayList<String>,
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



data class ResolutionUser(
  var _id : String,
  var firstName : String,
  var lastName : String,
  var organizationName : String,
  var profileUrl : String,
  var userLoginType : ResolutionUserLoginType
)



data class ResolutionUserLoginType(

var _id: String,
var name: String,
var userType: String

)


data class DeleteReportedResolutionPojo (
    var statusCode: Int,
    var message: String,
    var data : DeleteReportedResolutionData
)

data class DeleteReportedResolutionData(
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var resolutionUsers : ArrayList<String>,
    var likes : Int,
    var abuses : Int,
    var delete : Boolean,
    var _id : String,
    var resolutionDate : String,
    var text : String,
    var commentType : String,
    var markAsFinal : Boolean,
    var user : String,
    var issue : String,
    var issueCreatedBy : String,
    var createdAt : String,
    var updatedAt : String,
    var __v: Int,
    var disabledRemark : String
)


data class UpdateReportResolutionPojo(
    var statusCode: Int,
    var message: String,
    var data : UpdateResolutionData
)

data class UpdateResolutionData(
    var imageUrls: ArrayList<String>,
    var videoUrls: ArrayList<String>,
    var delete : Boolean,
    var _id : String,
    var resolutionDate : String,
    var text : String,
    var commentType : String,
    var user : String,
    var issue : String
)