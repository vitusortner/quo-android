package com.android.quo.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "component",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Place::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("place_id"),
                        onDelete = ForeignKey.CASCADE)
        )
)
data class Component(
        @PrimaryKey
        var id: String = "",

        var picture: String? = null,

        var text: String? = null,

        @ColumnInfo(name = "place_id")
        var placeId: String = "",

        var position: Int = 0
)