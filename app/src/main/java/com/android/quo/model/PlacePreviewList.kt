package com.android.quo.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 29.10.17.
 */
data class PlacePreviewList(@SerializedName("data") val list: List<PlacePreview>)