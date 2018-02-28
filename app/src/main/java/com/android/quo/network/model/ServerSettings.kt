package com.android.quo.network.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 30.11.17.
 */
data class ServerSettings(
        @SerializedName("is_photo_upload_allowed")
        var isPhotoUploadAllowed: Boolean,
        @SerializedName("has_to_validate_gps")
        var hasToValidateGps: Boolean
)