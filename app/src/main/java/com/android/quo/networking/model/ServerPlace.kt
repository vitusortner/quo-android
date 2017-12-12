package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerPlace(
        @SerializedName("_id")
        val id: String? = null,

        val host: String,

        val title: String,

        val description: String? = null,

        @SerializedName("start_date")
        val startDate: String,

        @SerializedName("end_date")
        val endDate: String? = null,

        @SerializedName("lat")
        val latitude: String,

        @SerializedName("long")
        val longitude: String,

        val address: ServerAddress? = null,

        val settings: ServerSettings? = null,

        @SerializedName("title_picture")
        val titlePicture: String? = null,

        @SerializedName("qr_code_id")
        val qrCodeId: String? = null,

        val components: List<String>? = null,

        val pictues: List<String>? = null
)