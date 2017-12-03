package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerUser(
        val id: String,

        // TODO place objects or ids?
        @SerializedName("visited_places")
        val visitedPlaces: List<ServerPlace>?,

        val token: String
)