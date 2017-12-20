package com.android.quo.view.place

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.android.quo.R
import com.android.quo.view.place.gallery.GalleryFragment
import com.android.quo.view.place.page.PageFragment

/**
 * Created by vitusortner on 12.11.17.
 */
class PlacePagerAdapter(
        fragmentManager: FragmentManager,
        private val context: Context,
        private val placeId: String?
) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment? {
        val bundle = Bundle()
        bundle.putString("placeId", placeId)

        return when (position) {
            0 -> {
                val fragment = PageFragment()
                fragment.arguments = bundle
                fragment
            }
            1 -> {
                val fragment = GalleryFragment()
                fragment.arguments = bundle
                fragment
            }
            else -> null
        }
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.resources.getString(R.string.place_tablayout_title_page)
            1 -> context.resources.getString(R.string.place_tablayout_title_gallery)
            else -> null
        }
    }
}