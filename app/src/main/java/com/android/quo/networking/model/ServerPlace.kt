package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerPlace(
        @SerializedName("_id")
        var id: String? = null,

        var host: String,

        var title: String,

        var description: String? = null,

        @SerializedName("start_date")
        var startDate: String,

        @SerializedName("end_date")
        var endDate: String? = null,

        @SerializedName("lat")
        var latitude: String,

        @SerializedName("long")
        var longitude: String,

        val address: ServerAddress? = null,

        var settings: ServerSettings? = null,

        @SerializedName("title_picture")
        var titlePicture: String? = null,

        @SerializedName("qr_code_id")
        var qrCodeId: String? = null,

        var components: List<ServerComponent>? = null
)