package com.android.quo.db.entity

import android.arch.persistence.room.ColumnInfo

/**
 * Created by vitusortner on 29.11.17.
 */
data class Address(
        var street: String = "",

        var city: String = "",

        @ColumnInfo(name = "zip_code")
        var zipCode: Int = 0
)