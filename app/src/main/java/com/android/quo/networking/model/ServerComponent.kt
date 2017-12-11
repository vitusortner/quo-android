package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerComponent(
        @SerializedName("_id")
        var id: String? = null,

        var picture: String? = null,

        var text: String? = null,

        var position: Int? = null
)