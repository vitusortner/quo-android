package com.android.quo.view.place

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import kotlinx.android.synthetic.main.fragment_place.placeTabLayout
import kotlinx.android.synthetic.main.fragment_place.placeViewPager

/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_place, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        // TODO differentiate between home and my places recyclerview

//        val viewPagerAdapter = ViewPagerAdapter(fragmentManager)
//        viewPagerAdapter.fragmentList.add(PlaceOverviewFragment())
//        viewPagerAdapter.fragmentList.add(PlaceOverviewFragment())
//
//        viewPagerAdapter.fragmentTitleList.add("One")
//        viewPagerAdapter.fragmentTitleList.add("Two")
//
//        placeViewPager.adapter = viewPagerAdapter
//
        placeTabLayout.addTab(placeTabLayout.newTab().setText("hi"))
        placeTabLayout.addTab(placeTabLayout.newTab().setText("hi"))
        placeTabLayout.addTab(placeTabLayout.newTab().setText("hi"))

//        placeTabLayout.addOnTabSelectedListener()

        placeTabLayout.setupWithViewPager(placeViewPager)
    }
}