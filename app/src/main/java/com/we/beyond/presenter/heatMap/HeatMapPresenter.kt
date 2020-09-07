package com.we.beyond.presenter.heatMap

import android.content.Context
import com.we.beyond.model.HeatMapData

interface HeatMapPresenter
{
    /** this interface and functions are showing result on view */
    interface IHeatMapView
    {
        fun setLocationListAdapter (locationList : ArrayList<HeatMapData>)

    }

    /** this interface is working for calling respected apis */
    interface  IHeatMapPresenter
    {
        fun OnRequestHeatMapList (context: Context,latitude : String,longitude : String)

    }
}