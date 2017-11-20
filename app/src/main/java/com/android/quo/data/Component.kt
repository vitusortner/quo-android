package com.android.quo.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "component",
        foreignKeys = arrayOf(
            ForeignKey(entity = Picture::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("pictureId"),
                onDelete = ForeignKey.SET_NULL))) //if picture gets deleted set value null ?

data class Component (
    @PrimaryKey val id: Int,
    val type: String,
    val position: Int,
    val text: String
)