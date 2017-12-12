package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerComponent(
        @SerializedName("_id")
        val id: String? = null,

        val picture: String? = null,

        val text: String? = null,

        val position: Int? = null
)