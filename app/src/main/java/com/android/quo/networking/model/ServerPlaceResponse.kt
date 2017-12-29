package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 21.12.17.
 */
data class ServerPlaceResponse(
        @SerializedName("place_id")
        val place: ServerPlace,

        val timestamp: String
)