package com.we.beyond.ui.leaderBoard


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.we.beyond.adapter.LeaderBoardAdapter
import com.we.beyond.Interface.OnLeaderBoardSelectedListener

import com.we.beyond.R
import com.we.beyond.model.DataList
import com.we.beyond.model.LeaderBoardPojo

/**
 * It shows the list of gathering creators on leader board
 */
class LeaderBoardCreatedGatheringFragment : Fragment(),OnLeaderBoardSelectedListener {

    /** init recycler view */
    var leaderBoardCreatedGatheringRecycler : RecyclerView?=null

    /** init leader board adapter */
    var leaderBoardDataAdapter : LeaderBoardAdapter?=null

    /** layout manager */
    var linearLayoutManager: LinearLayoutManager? = null

    /** init array list  */
    var leaderBoardList : ArrayList<DataList>?=null
    var leaderBoardTopList : ArrayList<DataList>?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v=  inflater.inflate(R.layout.fragment_leader_board_created_gathering, container, false)

        /** array initialization */
        leaderBoardList = ArrayList()
        leaderBoardList!!.clear()

        leaderBoardTopList = ArrayList()
        leaderBoardTopList!!.clear()

        /** initialize ids of elements */
        initElementsWithIds(v)


        return  v
    }

    /** ui initialization */
    private fun initElementsWithIds(v: View)
    {
        leaderBoardCreatedGatheringRecycler = v.findViewById(R.id.leaderBoardCreatedGatheringRecycler)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        leaderBoardCreatedGatheringRecycler!!.setHasFixedSize(true)
        leaderBoardCreatedGatheringRecycler!!.layoutManager = linearLayoutManager

    }

    /** It sets leader board data to adapter */
    override fun onSelected(leaderBoardData: LeaderBoardPojo)
    {
        println("data in gathering $leaderBoardData")

        if(leaderBoardData!=null && leaderBoardData.data.dataList.isNotEmpty()) {
            leaderBoardCreatedGatheringRecycler!!.visibility = View.VISIBLE
            leaderBoardList!!.clear()
            leaderBoardList!!.addAll(leaderBoardData.data.dataList)

            leaderBoardTopList!!.clear()
            leaderBoardTopList!!.addAll(leaderBoardData.data.topList)

            leaderBoardDataAdapter = LeaderBoardAdapter(context!!, leaderBoardList!!,leaderBoardTopList!!, "Created")
            leaderBoardCreatedGatheringRecycler!!.adapter = leaderBoardDataAdapter
        }
        else {
            leaderBoardCreatedGatheringRecycler!!.visibility = View.GONE
            leaderBoardList!!.clear()
        }

    }


}
