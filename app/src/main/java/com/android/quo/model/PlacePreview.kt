package com.android.quo.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 27.10.17.
 */
data class PlacePreview(
    val id: Int,

    val title: String,

    val description: String,

    @SerializedName("image_url")
    val imageUrl: String
)