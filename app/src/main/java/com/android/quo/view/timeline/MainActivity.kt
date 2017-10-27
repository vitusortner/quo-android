package com.android.quo.view.timeline

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.android.quo.R
import com.android.quo.viewmodel.PlacePreviewViewModel
import kotlinx.android.synthetic.main.activity_main.placePreviewRecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val placePreviewViewModel = ViewModelProviders.of(this).get(PlacePreviewViewModel().javaClass)

        val placePreviewAdapter = PlacePreviewAdapter(placePreviewViewModel)
        placePreviewRecyclerView.adapter = placePreviewAdapter
        placePreviewRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}
