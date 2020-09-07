package com.we.beyond.model

data class CategoriesPojo (
    var statusCode : Int ,
    var message : String,
    var data : ArrayList<Categories>
)


data class Categories (
    var order : Int,
    var _id : String,
    var name : String,
    var description : String,
    var selectedImageUrl : String,
    var deselectedImageUrl : String,
    var alreadySelected : String
)


data class UpdateCategoriesPojo(
    var statusCode: Int,
    var message: String,
    var data : UpdateCategoryData
)

data class UpdateCategoryData(
    var enabled : Boolean,
    var categories: ArrayList<String>,
    var _id: String,
    var updatedAt : String,
    var _v : Int
)
