package com.android.quo.db.entity

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.android.quo.db.entity.Address
import java.util.*

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "place")
data class Place(
        @PrimaryKey
        val id: String,

        val isHost: Boolean,

        val title: String,

        val startDate: Date,

        val endDate: Date? = null,

        val lat: String,

        val long: String,

        @Embedded
        val address: Address? = null,

        val isPhotoUploadAllowed: Boolean = true,

        val hasToValidateGps: Boolean = true,

        val titlePicture: String,

        val qrCodeId: String
)