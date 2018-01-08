package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerAddress(
        var street: String,

        var city: String,

        @SerializedName("zip_code")
        var zipCode: Int,

        val name: String
)