package com.we.beyond.util

import android.graphics.Typeface
import androidx.core.graphics.TypefaceCompatApi26Impl
import com.we.beyond.interceptor.ApplicationController

object ConstantFonts
{
    /** set fonts */
    var abys_regular : Typeface = Typeface.createFromAsset(
    ApplicationController.context.assets,
    "fonts/abys_regular.otf"
    )

    var raleway_semibold : Typeface = Typeface.createFromAsset(
        ApplicationController.context.assets,
        "fonts/poppins_semibold.ttf"
    )

    var raleway_regular : Typeface = Typeface.createFromAsset(
        ApplicationController.context.assets,
        "fonts/poppins_regular.ttf"
    )

    var raleway_medium : Typeface = Typeface.createFromAsset(
        ApplicationController.context.assets,
        "fonts/poppins_medium.ttf"
    )

    var number_semibold : Typeface = Typeface.createFromAsset(
        ApplicationController.context.assets,
        "fonts/sf_pro_rounded_semibold.otf"
    )
}