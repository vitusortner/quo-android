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
            ForeignKey(entity = Picture::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("titlePicture"),
                onDelete = ForeignKey.SET_NULL),
            ForeignKey(entity = Picture::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("qrCode"),
                onDelete = ForeignKey.SET_NULL),
            ForeignKey(entity = User::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("host"),
                onDelete = ForeignKey.NO_ACTION))) //what happens if user gets deleted/deactivated?

data class Place(
    @PrimaryKey var id: Long,
    var host: Long,
    var titlePicture: Long,
    var qrCode: Long,
    var title: String,
    var startDate: Date,
    var endDate: Date,
    var lat: String,
    var log: String
)