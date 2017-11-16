package com.android.quo.view.place

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PlacePageFragment()
            1 -> PlaceGalleryFragment()
            else -> PlacePageFragment()
        }
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "PAGE"
            1 -> "GALLERY"
            else -> "x"
        }
    }
}