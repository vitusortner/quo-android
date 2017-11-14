package com.android.quo.view.place

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by vitusortner on 12.11.17.
 */
class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PlaceOverviewFragment()
            1 -> PlaceOverviewFragment()
            2 -> PlaceOverviewFragment()
            else -> PlaceOverviewFragment()
        }
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "1"
            1 -> "2"
            2 -> "3"
            else -> "x"
        }
    }
}