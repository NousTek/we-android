package com.we.beyond.model

data class CommentsPojo (
    var statusCode : Int,
    var message : String,
    var data : ArrayList<CommentsData>
)

data class CommentsData (
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var resolutionUsers : ArrayList<String>,
    var resolutionDate : String,
    var likesCount : Int,
    var abuseCount : Int,
    var delete : Boolean,
    var tags : ArrayList<String>,
    var tagNames : ArrayList<String>,
    var _id : String,
    var text : String,
    var commentType : String,
    var markAsFinal : Boolean,
    var user : UserCommentsDetails,
    var issue : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : String,
    var likeByUser : Boolean,
    var reply : CommentsReplyDetails,
    var resolutionUsersData : ArrayList<ResolutionUserData>

)

data class CommentDetailsPojo(
    var statusCode : Int,
    var message : String,
    var data : CommentsDetailsData
)


data class CommentsDetailsData (
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var resolutionUsers : ArrayList<String>,
    var resolutionDate : String,
    var tags : ArrayList<String>,
    var tagNames : ArrayList<String>,
    var likesCount : Int,
    var abuseCount : Int,
    var delete : Boolean,
    var _id : String,
    var text : String,
    var commentType : String,
    var markAsFinal : Boolean,
    var user : UserCommentsDetails,
    var issue : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : String,
    var likeByUser : Boolean,
    var resolutionUsersData : ArrayList<ResolutionUserData>,
    var reply : ArrayList<CommentsReplyDetails>

)

data class UserCommentsDetails(
    var _id : String,
    var firstName : String,
    var lastName : String,
    var organizationName : String,
    var profileUrl : String,
    var userLoginType : CommentsUserLoginTypeDetails
)

data class CommentsUserLoginTypeDetails(
    var _id : String,
    var name : String,
    var userType : String
)

data class CommentsReplyDetails(
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var likesCount : Int,
    var abuseCount : Int,
    var tags : ArrayList<String>,
    var tagNames : ArrayList<String>,
    var delete : Boolean,
    var _id : String,
    var text : String,
    var commentType : String,
    var user : UserCommentsDetails,
    var parent : String,
    var issue : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : String,
    var likeByUser : Boolean


)


data class CreateCommentPojo(
    var statusCode : Int,
    var message : String,
    var data : CreateCommentData
)

data class CreateCommentData(
    var imageUrls : ArrayList<String>,
    var videoUrls : ArrayList<String>,
    var likesCount : Int,
    var abuseCount : Int,
    var delete : Boolean,
    var _id : String,
    var text : String,
    var user : String,
    var issue : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int
)

data class DeleteCommentPojo(
    var statusCode: Int,
    var message: String,
    var data : DeleteCommentData
)

data class DeleteCommentData(
    var delete: Boolean,
    var _id: String,
    var user: String,
    var updatedAt: String
)

data class UpdateCommentPojo (
    var statusCode: Int,
    var message: String,
    var data : UpdateCommentData
)

data class UpdateCommentData(
    var delete: Boolean,
    var _id: String,
    var text : String,
    var user: String,
    var updatedAt: String
)