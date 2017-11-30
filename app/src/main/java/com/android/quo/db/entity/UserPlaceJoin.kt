package com.android.quo.db.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import java.util.*

/**
 * Created by FlorianSchlueter on 24.11.2017.
 * Used for visitedPlaces from User
 * Many to many relationship
 */

@Entity(tableName = "user_place_join",
        primaryKeys = arrayOf("user_id", "place_id"),
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = User::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("user_id"),
                        onDelete = ForeignKey.SET_NULL),
                ForeignKey(
                        entity = Place::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("place_id"),
                        onDelete = ForeignKey.SET_NULL)
        ))
data class UserPlaceJoin(
        @ColumnInfo(name = "user_id")
        var userId: String = "",

        @ColumnInfo(name = "place_id")
        var placeId: String = "",

        var timestamp: Date = Date()
)