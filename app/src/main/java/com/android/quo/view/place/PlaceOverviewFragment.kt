package com.android.quo.view.place

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R

/**
 * Created by vitusortner on 12.11.17.
 */
class PlaceOverviewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View =
            inflater.inflate(R.layout.fragment_place_overview, container, false)
}