package com.we.beyond.presenter.leaderBoard

import android.content.Context
import com.we.beyond.model.LeaderBoardPojo

interface LeaderBoardPresenter
{
    /** this interface and functions are showing result on view */
    interface ILeaderBoardView
    {
        fun setLeaderBoardAdapter( leaderBoardDetails : LeaderBoardPojo)

    }

    /** this interface is working for calling respected apis */
    interface  ILeaderBoardPresenter
    {

        fun onLeaderBoard(context: Context, type : String,startDate : String,endDate : String)
        fun onLeaderBoardWithoutCriteria(context: Context, type : String)

    }
}