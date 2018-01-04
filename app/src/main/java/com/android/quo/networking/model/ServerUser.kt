package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerUser(
        @SerializedName("_id")
        val id: String,
        val email: String
)