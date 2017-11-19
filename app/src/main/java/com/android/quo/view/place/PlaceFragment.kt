package com.android.quo.view.place

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.jakewharton.rxbinding2.widget.RxToolbar
import kotlinx.android.synthetic.main.fragment_place.placeTabLayout
import kotlinx.android.synthetic.main.fragment_place.placeViewPager
import kotlinx.android.synthetic.main.fragment_place.toolbar

/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_place, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setupToolbar()

        placeViewPager.adapter = PlacePagerAdapter(childFragmentManager)

        placeTabLayout.setupWithViewPager(placeViewPager)
    }

    private fun setupToolbar() {
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)

        RxToolbar.navigationClicks(toolbar)
                .subscribe {
                    activity.onBackPressed()
                }
    }
}