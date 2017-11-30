package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerPicture(
        val id: String,

        @SerializedName("owner_id")
        val ownerId: String,

        @SerializedName("place_od")
        val placeId: String,

        val src: String,

        @SerializedName("is_visible")
        val isVisible: Boolean,

        val timestamp: String
)