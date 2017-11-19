package com.android.quo.view.place

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.android.quo.view.place.gallery.GalleryFragment
import com.android.quo.view.place.page.PageFragment

/**
 * Created by vitusortner on 12.11.17.
 */
class PlacePagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> PageFragment()
            1 -> GalleryFragment()
            else -> PageFragment()
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