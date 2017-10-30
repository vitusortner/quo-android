package com.android.quo.view.timeline

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.android.quo.R
import com.android.quo.viewmodel.PlacePreviewListViewModel
import kotlinx.android.synthetic.main.activity_main.placePreviewRecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val placePreviewListViewModel = ViewModelProviders.of(this).get(PlacePreviewListViewModel().javaClass)

        placePreviewListViewModel.getPlacePreviewList().observe(this, Observer { list ->
            list?.let {
                val placePreviewAdapter = PlacePreviewAdapter(list)
                placePreviewRecyclerView.adapter = placePreviewAdapter
                placePreviewRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        })
    }
}
