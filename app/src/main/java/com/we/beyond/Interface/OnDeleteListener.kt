package com.we.beyond.Interface

import com.google.gson.JsonObject

interface OnDeleteListener
{
    fun onDelete(jsonObject: JsonObject)
}