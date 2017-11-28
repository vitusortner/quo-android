package com.android.quo.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.google.zxing.qrcode.encoder.QRCode
import java.util.Date

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "place",
        foreignKeys = arrayOf(
            ForeignKey(entity = User::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("host"),
                onDelete = ForeignKey.NO_ACTION)))

data class Place(
    @PrimaryKey var id: Long,
    var host: Long,
    var title: String,
    var startDate: Date,
    var endDate: Date,
    var lat: String,
    var log: String,
    var visitorPhotoSetting: Boolean,
    var validateGpsSetting: Boolean,
    var createdAt: Date
)