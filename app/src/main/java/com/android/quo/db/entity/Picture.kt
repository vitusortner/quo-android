package com.android.quo.db.entity

import android.annotation.SuppressLint
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@SuppressLint("ParcelCreator")
@Parcelize
// Disabled cascade delete otherwise cache doesn't work properly
// When getching places from API, we delete all places and insert them again, which leads
// to deletion of pictures (cascade delete)
@Entity(tableName = "picture")
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