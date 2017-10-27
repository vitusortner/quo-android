package com.android.quo.viewmodel

import com.android.quo.model.PlacePreview

/**
 * Created by vitusortner on 27.10.17.
 */
class PlacePreviewViewModel {

    private val placePreview = PlacePreview("Foo", "Bar foo bar foo bar.")

    val title = placePreview.title
    val description = placePreview.description
}