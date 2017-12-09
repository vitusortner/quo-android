package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerPicture(
        @SerializedName("_id")
        val id: String,

        @SerializedName("owner")
        val ownerId: String,

        @SerializedName("place")
        val placeId: String,

        val src: String,

        @SerializedName("is_visible")
        val isVisible: Boolean,

        val timestamp: String
)