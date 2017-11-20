package com.android.quo.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
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
    @PrimaryKey val id: Int,
    val title: String,
    val startDate: Date,
    val endDate: Date,
    //maybe lat and long as one "position"
    val lat: String,
    val long: String,
    val components: Array<Component>//foreign key nedded?

)