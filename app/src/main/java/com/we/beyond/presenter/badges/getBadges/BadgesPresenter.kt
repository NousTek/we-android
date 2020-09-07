package com.we.beyond.presenter.badges.getBadges

import android.content.Context
import com.we.beyond.model.BadgesPojo

interface BadgesPresenter
{
    /** this interface and functions are showing result on view */
    interface IBadgesView {

        fun setBadgesAdapter(badgesDetails: BadgesPojo)

    }

    /** this interface is working for calling onGatheringCreated and onGatheringUpdated api respectively */
    interface IBadgesPresenter {

        fun onBadges(context: Context)
        fun onBadgesUser(context: Context, userId: String)

    }
}