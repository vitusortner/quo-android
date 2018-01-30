package com.android.quo.network.model

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