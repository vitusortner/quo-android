package com.android.quo.view.myplaces.createplace

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by Jung on 27.11.17.
 */

class CreatePlacePagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> CreateEventFragment()
        1 -> CreatePageFragment()
        2 -> CreateSettingsFragment()
        else -> null
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> "EVENT"
        1 -> "PAGE"
        2 -> "SETTINGS"
        else -> null
    }
}