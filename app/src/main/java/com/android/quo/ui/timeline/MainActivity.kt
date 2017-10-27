package com.android.quo.ui.timeline

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.android.quo.R
import com.android.quo.viewmodel.PlacePreviewViewModel
import kotlinx.android.synthetic.main.activity_main.placePreviewRecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val placePreviewViewModel = PlacePreviewViewModel()

        val placePreviewAdapter = PlacePreviewAdapter(placePreviewViewModel)
        placePreviewRecyclerView.adapter = placePreviewAdapter
    }
}
