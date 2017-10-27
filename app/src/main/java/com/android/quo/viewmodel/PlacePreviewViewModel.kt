package com.android.quo.viewmodel

import android.arch.lifecycle.ViewModel
import com.android.quo.model.PlacePreview

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewViewModel : ViewModel() {

    private var placePreviewList: Array<PlacePreview>? = null

    fun getPlacePreviews(): Array<PlacePreview>? {
        if (placePreviewList == null) {
            placePreviewList = loadPlacePreviews()
        }
        return placePreviewList
    }

    private fun loadPlacePreviews(): Array<PlacePreview> =
        arrayOf(
            PlacePreview("1", "Bar foo bar foo bar."),
            PlacePreview("2", "Bar foo bar foo bar."),
            PlacePreview("3", "Bar foo bar foo bar."),
            PlacePreview("4", "Bar foo bar foo bar."),
            PlacePreview("5", "Bar foo bar foo bar."),
            PlacePreview("6", "Bar foo bar foo bar."),
            PlacePreview("7", "Bar foo bar foo bar."),
            PlacePreview("8", "Bar foo bar foo bar."),
            PlacePreview("9", "Bar foo bar foo bar."),
            PlacePreview("10", "Bar foo bar foo bar."),
            PlacePreview("11", "Bar foo bar foo bar."),
            PlacePreview("12", "Bar foo bar foo bar."),
            PlacePreview("13", "Bar foo bar foo bar."),
            PlacePreview("14", "Bar foo bar foo bar."),
            PlacePreview("15", "Bar foo bar foo bar."),
            PlacePreview("16", "Bar foo bar foo bar."),
            PlacePreview("17", "Bar foo bar foo bar."),
            PlacePreview("18", "Bar foo bar foo bar."),
            PlacePreview("19", "Bar foo bar foo bar."),
            PlacePreview("20", "Bar foo bar foo bar."),
            PlacePreview("21", "Bar foo bar foo bar."),
            PlacePreview("22", "Bar foo bar foo bar."),
            PlacePreview("23", "Bar foo bar foo bar."),
            PlacePreview("24", "Bar foo bar foo bar."),
            PlacePreview("25", "Bar foo bar foo bar.")
        )

}