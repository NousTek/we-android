package com.we.beyond.Interface

interface OnLikeDislikeListener
{
    fun onLike(_id: String)
    fun onDislike(_id: String)
    fun summaryType(type : String)
}