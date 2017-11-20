package com.android.quo.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName =  "pictur",
        foreignKeys = arrayOf(
            ForeignKey(entity = User::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("owner"),
                onDelete = ForeignKey.CASCADE), //if user gets deleted all his picuturs too
            ForeignKey(entity = Place::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("placeId"),
                onDelete = ForeignKey.CASCADE))) //if place gets deleted all his picuturs too

data class Picture (
    @PrimaryKey val id: Int,
    val src: String,
    val visible: Boolean

)