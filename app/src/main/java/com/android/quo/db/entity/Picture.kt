package com.android.quo.db.entity

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@SuppressLint("ParcelCreator")
@Parcelize
@Entity(tableName = "picture",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Place::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("place_id"),
                        onDelete = ForeignKey.CASCADE)
        )
)
data class Picture(
        @PrimaryKey
        var id: String = "",

        @ColumnInfo(name = "owner_id")
        var ownerId: String = "",

        @ColumnInfo(name = "place_id")
        var placeId: String = "",

        var src: String = "",

        @ColumnInfo(name = "is_visible")
        var isVisible: Boolean = false,

        var timestamp: String = ""
) : Parcelable