package com.we.beyond.model

data class DashboardPojo(
    var statusCode : Int,
    var message : String,
    var data : DashboardData
)

data class DashboardData(
    var unresolvedIssueCount : Int,
    var resolvedIssueCount : Int,
    var upcomingGatheringCount : Int,
    var publishedConnectCount : Int,
    var allIssuesCount : Int,
    var user : DashboardUserData
)

data class DashboardUserData(
    var firstName : String,
    var lastName : String,
    var organizationName : String,
    var _id : String,
    var email : String,
    var mobile : String,
    var city : String,
    var area : String,
    var profileUrl : String,
    var userLoginType : DashboardUserLoginTypes,
    var categories : ArrayList<String>,
    var isNotficationReceived : Boolean
)

data class DashboardUserLoginTypes(
    var order : Int,
    var _id : String,
    var name : String,
    var description : String,
    var userType : String,
    var createdBy : String,
    var createdAt : String,
    var updatedAt : String,
    var __v : Int
)