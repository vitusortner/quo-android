package com.android.quo.view.timeline


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.quo.R
import com.android.quo.viewmodel.PlacePreviewListViewModel
import kotlinx.android.synthetic.main.fragment_home.placePreviewRecyclerView


/**
 * Created by Jung on 01.11.17.
 */

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val placePreviewListViewModel = ViewModelProviders.of(this)
                .get(PlacePreviewListViewModel().javaClass)

        placePreviewListViewModel.getPlacePreviewList().observe(this, Observer { list ->
            list?.let {
                val placePreviewAdapter = PlacePreviewAdapter(list)
                placePreviewRecyclerView.adapter = placePreviewAdapter
                placePreviewRecyclerView.layoutManager = LinearLayoutManager(this.context)
            }
        })

        return inflater?.inflate(R.layout.fragment_home, container, false)
    }
}