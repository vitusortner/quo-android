package com.android.quo.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity (tableName = "component")
data class Component (
    @PrimaryKey
    val id: Int,

    @ColumnInfo (name = "type")
    val type: String,

    @ColumnInfo (name = "position")
    val position: Int,

    @ColumnInfo (name = "text")
    val text: String,

    @ColumnInfo (name = "picture")
    val picture: Picture
)