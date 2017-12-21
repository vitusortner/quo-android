package com.android.quo.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

// TODO disabled cascade delete otherwise cache doesn't work properly
// When getching places from API, we delete all places and insert them again, which leads
// to deletion of components (cascade delete)
@Entity(tableName = "component")
data class Component(
        @PrimaryKey
        var id: String = "",

        var picture: String? = null,

        var text: String? = null,

        @ColumnInfo(name = "place_id")
        var placeId: String = "",

        var position: Int = 0
)