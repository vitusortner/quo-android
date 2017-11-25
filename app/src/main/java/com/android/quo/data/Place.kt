package com.android.quo.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "place"
//        foreignKeys = arrayOf(
//                ForeignKey(entity = Picture::class,
//                        parentColumns = arrayOf("id"),
//                        childColumns = arrayOf("titlePicture"),
//                        onDelete = ForeignKey.SET_NULL),
//                ForeignKey(entity = Picture::class,
//                        parentColumns = arrayOf("id"),
//                        childColumns = arrayOf("qrCode"),
//                        onDelete = ForeignKey.SET_NULL),
//                ForeignKey(entity = User::class,
//                        parentColumns = arrayOf("id"),
//                        childColumns = arrayOf("host"),
//                        onDelete = ForeignKey.NO_ACTION))
) //what happens if user gets deleted/deactivated?

data class Place(
        @PrimaryKey
        var id: Long,

        var host: Long,

        @SerializedName("title_picture")
        var titlePicture: Long,

        @SerializedName("qr_code")
        var qrCode: Long,

        var title: String
//        var startDate: Date,
//        var endDate: Date,
//        var lat: String,
//        var log: String
)