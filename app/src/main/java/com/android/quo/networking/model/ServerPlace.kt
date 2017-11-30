package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerPlace(
        val id: String,

        val host: String,

        val title: String,

        @SerializedName("start_date")
        val startDate: String,

        @SerializedName("end_date")
        val endDate: String,

        @SerializedName("lat")
        val latitude: String,

        @SerializedName("long")
        val longitude: String,

        val address: ServerAddress,

        val settings: ServerSettings,

        @SerializedName("title_picture")
        val titlePicture: String,

        @SerializedName("qr_code_id")
        val qrCodeId: String,

        val components: List<ServerComponent>
)