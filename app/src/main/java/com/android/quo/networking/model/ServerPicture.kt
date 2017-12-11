package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerPicture(
        @SerializedName("_id")
        var id: String,

        @SerializedName("owner")
        var ownerId: String,

        @SerializedName("place")
        var placeId: String,

        var src: String,

        @SerializedName("is_visible")
        var isVisible: Boolean,

        var timestamp: String
)