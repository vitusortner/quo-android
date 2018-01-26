package com.android.quo.view.createplace

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.android.quo.R

/**
 * Created by Jung on 27.11.17.
 */
class CreatePlacePagerAdapter(fragmentManager: FragmentManager, private val context: Context)
    : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> CreateEventFragment()
        1 -> CreatePageFragment()
        2 -> CreateSettingsFragment()
        else -> null
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> context.resources.getString(R.string.create_place_tablayout_title_event)
        1 -> context.resources.getString(R.string.create_place_tablayout_title_page)
        2 -> context.resources.getString(R.string.create_place_tablayout_title_settings)
        else -> null
    }
}