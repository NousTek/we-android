package com.we.beyond.model

data class CampaignPojo (
    var statusCode : Int,
    var message : String,
    var data : CampaignData
)


data class CampaignPostPojo (
    var statusCode : Int,
    var message : String,
    var data : CampaignCreateDetails
)

data class CampaignCreateDetails(
    var location : CampaignLocationDetails,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var participants : ArrayList<String>,
    var comments : Int,
    var likes : Int,
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
    var gathering : String,
    var issue : String,
    var campaignNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var _v : Int



)
data class CampaignData(
    var location : CampaignLocationDetails,
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
    var user : CampaignUserDetails,
    var gathering : CampaignGatheringDetails,
    var issue : CampaignIssueDetails,
    var campaignNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var isCheckedIn : Boolean,
    var isGoing : Boolean,
    var _v : Int,
    var goingListCount : Int,
    var goingList : ArrayList<CamapignGoingList>
)


data class CamapignGoingList(
    var _id : String,
    var user : CamapignGoingUserDetails
)

data class CamapignGoingUserDetails(
    var _id : String,
    var profileUrl : String,
    var name : String
)

data class CampaignLocationDetails(
    var type : String,
    var coordinates : ArrayList<Double>
)

data class CampaignUserDetails(
    var _id : String,
    var firstName : String,
    var lastName : String,
    var organizationName : String,
    var profileUrl : String,
    var userLoginType : CampaignUserLoginType

)

data class CampaignUserLoginType(
    var _id : String,
    var name : String,
    var userType : String
)

data class CampaignGatheringDetails(
    var location : CampaignLocationDetails,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var invites : ArrayList<String>,
    var campaigns : ArrayList<String>,
    var disabled: Boolean,
    var _id: String,
    var title: String,
    var description: String,
    var gatheringDate : String,
    var city: String,
    var address: String,
    var createdBy: String,
    var user : String,
    var issue: String,
    var gatheringNumber: Int,
    var createdAt: String,
    var updatedAt: String,
    var _v: Int


)


data class CampaignIssueDetails(
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var tags : ArrayList<String>,
    var resolved : Boolean,
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


data class MyCampaignPojo(
    var statusCode: Int,
    var message: String,
    var data : ArrayList<MyCampaignData>
)


data class MyCampaignData(
    var location : CampaignLocationDetails,
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
    var isCheckedIn : Boolean,
    var isGoing : Boolean,
    var gathering : CampaignGatheringDetails,
    var issue : CampaignIssueDetails,
    var campaignNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var _v : Int
)

data class DeleteCampaignPojo(
    var statusCode: Int,
    var message: String,
    var data : DeleteCampaignData
)




data class DeleteCampaignData(
    var location : CampaignLocationDetails,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
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
    var gathering : String,
    var issue : String,
    var campaignNumber : Int,
    var createdAt : String,
    var updatedAt : String,
    var _v : Int,
    var disabledRemark : String
)


data class UpdateCampaignPojo(
    var statusCode : Int,
    var message : String,
    var data : UpdateCampaignData
)


data class UpdateCampaignData(
    var location : CampaignLocationDetails,
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var disabled : Boolean,
    var _id : String,
    var title : String,
    var description : String,
    var campaignDate : String,
    var city : String,
    var address : String,
    var user : String,
    var gathering : UpdateCampaignGatheringDetails
)

data class UpdateCampaignGatheringDetails(
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var invites : ArrayList<String>,
    var comments: Int,
    var likes: Int,
    var campaigns : ArrayList<String>,
    var disabled: Boolean,
    var _id: String,
    var title: String,
    var description: String,
    var gatheringDate : String,
    var city: String,
    var address: String,
    var createdBy: String,
    var user : String,
    var issue: String,
    var gatheringNumber: Int,
    var createdAt: String,
    var updatedAt: String,
    var _v: Int

)