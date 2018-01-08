package com.android.quo.network.model

/**
 * Created by vitusortner on 03.01.18.
 */
data class ServerLoginResponse(
        val token: String,
        val user: ServerUser
)