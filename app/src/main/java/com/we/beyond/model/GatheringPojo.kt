package com.we.beyond.model

data class GatheringPojo
    (
    var statusCode : Int ,var message : String, var fileUrl : String
)

data class GatheringListPojo(
    var statusCode : Int,
    var message : String,
    var data : ArrayList<GatheringData>

)

data class GatheringData(
    var location : LocationDetails,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var invites : ArrayList<String>,
    var campaigns : ArrayList<String>,
    var disabled : Boolean,
    var isGoing : Boolean,
    var isSubscribe : Boolean,
    var _id : String,
    var title : String,
    var description : String,
    var gatheringDate : String,
    var city : String,
    var comments : Int,
    var likes : Int,
    var address : String,
    var createdBy : String,
    var user : GatheringUserDetails,
    var issue : GatheringIssueDetails,
    var gatheringNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var _v : Int
)

data class GatheringIssueDetails(
    var resolved : Boolean,
    var _id : String
)


data class LocationDetails(
    var type : String,
    var coordinates : ArrayList<String>
)

data class GatheringUserDetails(
    var _id : String,
    var firstName : String,
    var lastName : String,
    var organizationName : String,
    var profileUrl : String,
    var userLoginType : GatheringUserLoginType
)

data class GatheringUserLoginType(
    var _id : String,
    var name : String,
    var userType : String
)


data class Campaigns(
    var location : LocationDetails,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var participants : ArrayList<String>,
    var comments: Int,
    var likes: Int,
    var disabled : Boolean,
    var cancel : Boolean,
    var _id : String,
    var title : String,
    var description : String,
    var campaignDate : String,
    var city : String,
    var address : String,
    var createdBy : String,
    var user : String,
    var gathering :String,
    var issue : String,
    var campaignNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int

)

data class GatheringDetails(
    var statusCode : Int,
    var message : String,
    var data : GatheringDetailsData
)


data class GatheringDetailsData(
    var location : LocationDetails,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var videoUrlThumbnails : ArrayList<String>,
    var invites : ArrayList<String>,
    var campaigns : ArrayList<Campaigns>,
    var isGoing: Boolean,
    var isSubscribe : Boolean,
    var disabled : Boolean,
    var _id : String,
    var title : String,
    var description : String,
    var gatheringDate : String,
    var city : String,
    var address : String,
    var createdBy : String,
    var user : GatheringUserDetails,
    var issue : IssueDetails,
    var gatheringNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var _v : Int,
    var goingListCount : Int,
    var goingList : ArrayList<GatheringGoingList>
)


data class GatheringGoingList(
    var _id : String,
    var user : GatheringGoingUserDetails
)

data class GatheringGoingUserDetails(
    var _id : String,
    var profileUrl : String,
    var name : String
)


data class IssueDetails(
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var tags : ArrayList<String>,
    var resolved : Boolean,
    var comments: Int,
    var likes: Int,
    var gatherings : ArrayList<String>,
    var _id : String,
    var title: String,
    var description : String,
    var coordinates : ArrayList<String>,
    var city : String,
    var address : String,
    var user : String,
    var category : String,
    var createdBy : String,
    var issueNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int

)

data class DeleteGatheringPojo (
    var statusCode : Int,
    var message : String,
    var data: DeleteData
)

data class DeleteData(
    var location : LocationDetails,
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


data class UpdateGatheringPojo(
    var statusCode: Int,
    var message: String,
    var data : UpdateGatheringData
)

data class UpdateGatheringData(
    var location : LocationDetails,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var campaigns : ArrayList<Campaigns>,
    var disabled : Boolean,
    var _id : String,
    var title : String,
    var description : String,
    var gatheringDate : String,
    var city : String,
    var address : String,
    var user : String,
    var updatedAt : String,
    var _v : Int
)

