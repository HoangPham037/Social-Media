package com.example.socialmedia.ui.mainfragment

import androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fragmentManager: FragmentManager , behavior: Int): FragmentPagerAdapter(fragmentManager,behavior) {
    private var listFragment : ArrayList<Fragment> = arrayListOf()
    override fun getCount(): Int {
        return  listFragment.size
    }

    override fun getItem(position: Int): Fragment {
        return  listFragment.get(position)
    }
    fun setData(data: ArrayList<Fragment>){
        this.listFragment = data
        notifyDataSetChanged()
    }
}