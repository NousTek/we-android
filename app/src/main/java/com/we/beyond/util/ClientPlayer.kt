package com.we.beyond.util

import android.os.Handler
import android.os.Looper

object ClientPlayer {

    /** Run on UI handler  */
    var UIHandler: Handler

    init {
        UIHandler = Handler(Looper.getMainLooper())
    }

    fun runOnUI(runnable: Runnable) {
        UIHandler.post(runnable)
    }
}

