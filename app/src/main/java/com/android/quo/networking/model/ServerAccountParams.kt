package com.android.quo.networking.model

import com.google.gson.annotations.SerializedName

/**
 * Created by vitusortner on 01.12.17.
 */
data class ServerLogin(
        val email: String,
        val password: String
)

data class ServerSignup(
        val email: String,
        val password: String
)

// TODO which fields are required?
data class ServerFacebookSignup(
        @SerializedName("fb_token")
        val fbToken: String
)

data class ServerPasswordChange(
        val email: String,
        val password: String
)

data class ServerPasswordReset(
        val email: String
)