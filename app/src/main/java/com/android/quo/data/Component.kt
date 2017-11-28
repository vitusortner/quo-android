package com.android.quo.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "component",
        foreignKeys = arrayOf(
            ForeignKey(entity = Picture::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("pictureId"),
                onDelete = ForeignKey.SET_NULL), //if picture gets deleted set value null ?
            ForeignKey(entity = Place::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("placeId"),
                onDelete = ForeignKey.CASCADE)))

data class Component (
    @PrimaryKey var id: Long,
    var pictureId: Long,
    var placeId: Long,
    var type: String,
    var position: Int,
    var text: String,
    var createdAt: Date
)