package com.android.quo.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Jung on 05.12.17.
 */
data class ServerSettings(
        @SerializedName("is_photo_upload_allowed")
        var isPhotoUploadAllowed: Boolean,

        @SerializedName("has_to_validate_gps")
        var hasToValidateGps: Boolean
)