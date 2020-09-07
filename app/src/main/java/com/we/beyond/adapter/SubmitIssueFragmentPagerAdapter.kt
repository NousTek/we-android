package com.we.beyond.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class SubmitIssueFragmentPagerAdapter (var fm : FragmentManager): FragmentPagerAdapter(fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int {
        return  fragmentHolders.size
    }

    val fragmentHolders:ArrayList<FragmentHolder> = ArrayList()


    override  fun getItem(position:Int): Fragment {
        return fragmentHolders[position].fragment
    }
    override fun getPageTitle(position:Int):CharSequence {
        return fragmentHolders[position].title
    }
    fun addFragmentHolder(fragmentHolder:FragmentHolder) {
        fragmentHolders.add(fragmentHolder)
    }
    class FragmentHolder(fragment:Fragment, title:String) {
        var title:String
        var fragment:Fragment
        init{
            this.fragment = fragment
            this.title = title
        }
    }
}