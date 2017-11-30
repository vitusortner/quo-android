package com.android.quo.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "place")
data class Place(
        @PrimaryKey
        var id: String = "",

        @ColumnInfo(name = "is_host")
        var isHost: Boolean = false,

        var title: String = "",

        @ColumnInfo(name = "start_date")
        var startDate: Date = Date(),

        @ColumnInfo(name = "end_date")
        var endDate: Date? = null,

        var latitude: String = "",

        var longitude: String = "",

        @Embedded
        var address: Address? = null,

        @ColumnInfo(name = "is_photo_upload_allowed")
        var isPhotoUploadAllowed: Boolean = true,

        @ColumnInfo(name = "has_to_validate_gps")
        var hasToValidateGps: Boolean = true,

        @ColumnInfo(name = "title_picture")
        var titlePicture: String = "",

        @ColumnInfo(name = "qr_code_id")
        var qrCodeId: String = ""
)