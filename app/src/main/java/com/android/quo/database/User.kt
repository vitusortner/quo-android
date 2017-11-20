package com.android.quo.database


import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val id: Int,

    @ColumnInfo (name = "email")
    val email: String,

    @ColumnInfo (name = "password")
    val password: String,

    @ColumnInfo ( name = "visitedPlaces")
    val visitedPlaces: Array<Place>,

    @ColumnInfo ( name = "active")
    val active: Boolean

)