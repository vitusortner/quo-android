package com.android.quo.db.entity

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by vitusortner on 29.11.17.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Address(
        var street: String = "",

        var city: String = "",

        @ColumnInfo(name = "zip_code")
        var zipCode: Int = 0
) : Parcelable