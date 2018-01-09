package com.android.quo.network.model

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

        @SerializedName("start")
        var startDate: String,

        @SerializedName("end")
        var endDate: String? = null,

        @SerializedName("lat")
        var latitude: Double,

        @SerializedName("long")
        var longitude: Double,

        val address: ServerAddress? = null,

        var settings: ServerSettings? = null,

        @SerializedName("title_picture")
        var titlePicture: String? = null,

        @SerializedName("qr_code_id")
        var qrCodeId: String? = null,

        @SerializedName("qr_code")
        var qrCode: String? = null,

        val timestamp: String

)