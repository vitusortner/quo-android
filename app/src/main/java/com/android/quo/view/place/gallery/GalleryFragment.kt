package com.android.quo.view.place.gallery

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.viewmodel.PlaceViewModel
import kotlinx.android.synthetic.main.fragment_place_gallery.recyclerView

/**
 * Created by vitusortner on 14.11.17.
 */
class GalleryFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_place_gallery, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val viewModel = ViewModelProviders.of(this).get(PlaceViewModel().javaClass)

        recyclerView.adapter = GalleryAdapter(activity, viewModel.imageList)
        recyclerView.layoutManager = GridLayoutManager(this.context, 3)
    }
}