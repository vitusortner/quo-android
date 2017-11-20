package com.android.quo.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.Date

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "place")
data class Place(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "picture")
    val titlePicture: Picture,

    @ColumnInfo(name = "startDate")
    val startDate: Date,

    @ColumnInfo(name = "endDate")
    val endDate: Date,

    @ColumnInfo(name = "lat")
    val lat: String,

    @ColumnInfo(name = "long")
    val long: String,

    @ColumnInfo(name = "host")
    val host: User,

    @ColumnInfo(name = "qrCode")
    val qrCode: Picture,

    @ColumnInfo(name = "components")
    val components: Array<Component>

)