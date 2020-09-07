package com.we.beyond.Interface

import com.google.gson.JsonObject

interface OnCheckInListener
{
    fun onCheckIn(jsonObject: JsonObject)
}