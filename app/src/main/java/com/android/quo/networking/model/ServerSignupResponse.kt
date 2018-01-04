package com.android.quo.networking.model

/**
 * Created by vitusortner on 03.01.18.
 */
data class ServerSignupResponse(
        val token: String,
        val user: ServerUser
)