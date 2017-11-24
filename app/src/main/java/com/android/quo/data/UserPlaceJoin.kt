package com.android.quo.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 24.11.2017.
 * Used for visitedPlaces from User
 * Many to many relationship
 */

@Entity(tableName = "user_place_join",
    primaryKeys = arrayOf("userId","placeId"),
    foreignKeys = arrayOf(
    ForeignKey(entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.SET_NULL),
    ForeignKey(entity = Place::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("placeId"),
        onDelete = ForeignKey.SET_NULL)))

data class UserPlaceJoin (
    var userId: Long,
    var placeId: Long
)