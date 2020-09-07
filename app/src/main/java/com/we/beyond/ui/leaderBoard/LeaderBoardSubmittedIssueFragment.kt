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
 * It shows the list of issue submitter on leader board
 */
class LeaderBoardSubmittedIssueFragment : Fragment(), OnLeaderBoardSelectedListener {

    /** init recycler view */
    var leaderBoardSubmittedIssueRecycler: RecyclerView? = null

    /** init leader board adapter */
    var leaderBoardDataAdapter: LeaderBoardAdapter? = null

    /** layout manager */
    var linearLayoutManager: LinearLayoutManager? = null

    /** init array list  */
    var leaderBoardList: ArrayList<DataList>? = null
    var leaderBoardTopList: ArrayList<DataList>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_leader_board_submitted_issue, container, false)

        /** array initialization */
        leaderBoardList = ArrayList()
        leaderBoardList!!.clear()

        leaderBoardTopList = ArrayList()
        leaderBoardTopList!!.clear()

        /** initialize ids of elements */
        initElementsWithIds(v)

        return v
    }

    /** ui initialization */
    private fun initElementsWithIds(v: View) {
        leaderBoardSubmittedIssueRecycler = v.findViewById(R.id.leaderBoardSubmittedIssueRecycler)
        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        leaderBoardSubmittedIssueRecycler!!.setHasFixedSize(true)
        leaderBoardSubmittedIssueRecycler!!.layoutManager = linearLayoutManager

    }

    /** It sets leader board data to adapter */
    override fun onSelected(leaderBoardData: LeaderBoardPojo) {

        println("data in submitted $leaderBoardData")

        if (leaderBoardData != null && leaderBoardData.data.dataList.isNotEmpty()) {
            leaderBoardSubmittedIssueRecycler!!.visibility = View.VISIBLE
            leaderBoardList!!.clear()
            leaderBoardList!!.addAll(leaderBoardData.data.dataList)

            leaderBoardList!!.clear()
            leaderBoardList!!.addAll(leaderBoardData.data.dataList)


            leaderBoardTopList!!.clear()
            leaderBoardTopList!!.addAll(leaderBoardData.data.topList)

            leaderBoardDataAdapter = LeaderBoardAdapter(context!!, leaderBoardList!!,leaderBoardTopList!!, "Submitted")
            leaderBoardSubmittedIssueRecycler!!.adapter = leaderBoardDataAdapter

        } else {
            leaderBoardList!!.clear()
            leaderBoardSubmittedIssueRecycler!!.visibility = View.GONE
        }

    }


}
