package com.android.quo.view.place

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import kotlinx.android.synthetic.main.fragment_place_gallery.recyclerView

/**
 * Created by vitusortner on 14.11.17.
 */
class PlaceGalleryFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_place_gallery, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        recyclerView.adapter = PlaceGalleryAdapter()
//        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this.context, 3)
    }
}