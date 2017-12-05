package com.android.quo.model
import com.google.gson.annotations.SerializedName

/**
 * Created by Jung on 05.12.17.
 */
data class ServerAddress(
        var street: String,

        var city: String,

        @SerializedName("zip_code")
        var zipCode: Int
)