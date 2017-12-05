package com.android.quo.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jung on 05.12.17.
 */

data class ServerPlace(
        var id: String,

        var host: String,

        var title: String,

        @SerializedName("start_date")
        var startDate: String,

        @SerializedName("end_date")
        var endDate: String,

        @SerializedName("lat")
        var latitude: String,

        @SerializedName("long")
        var longitude: String,

        var address: ServerAddress,

        var settings: ServerSettings,

        @SerializedName("title_picture")
        var titlePicture: String,

        @SerializedName("qr_code_id")
        var qrCodeId: String,

        var components: List<ServerComponent>
)