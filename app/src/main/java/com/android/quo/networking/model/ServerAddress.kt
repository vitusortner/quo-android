package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerAddress(
        val street: String,

        val city: String,

        @SerializedName("zip_code")
        val zipCode: Int,

        val name: String
)