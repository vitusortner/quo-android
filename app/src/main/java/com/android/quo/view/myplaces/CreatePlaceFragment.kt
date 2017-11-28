package com.android.quo.view.myplaces


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import kotlinx.android.synthetic.main.fragment_create_place.createPlaceViewPager
import kotlinx.android.synthetic.main.fragment_create_place.tabLayout
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_create_place.toolbar
import kotlinx.android.synthetic.main.fragment_create_place.view.toolbar


/**
 * Created by Jung on 27.11.17.
 */

class CreatePlaceFragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_place, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CreatePlacePagerAdapter(childFragmentManager)
        createPlaceViewPager.adapter = adapter
        tabLayout.setupWithViewPager(createPlaceViewPager)


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}