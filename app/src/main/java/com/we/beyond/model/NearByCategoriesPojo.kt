package com.we.beyond.model

data class NearByCategoriesPojo(
    var statusCode: Int,
    var message: String,
    var userCategories: ArrayList<UserCategories>,
    var categories: ArrayList<AllCategories>
)

data class UserCategories(

    var order: Int,
    var _id: String,
    var name: String,
    var description: String,
    var selectedImageUrl: String,
    var deselectedImageUrl: String

)


data class AllCategories(
    var order: Int,
    var _id: String,
    var name: String,
    var description: String,
    var selectedImageUrl: String,
    var deselectedImageUrl: String
)


