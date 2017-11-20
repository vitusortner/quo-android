package com.android.quo.data


import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    val email: String,
    val password: String,
    val visitedPlaces: Array<Place>, //foreign key needed?
    val active: Boolean

)