package com.android.quo.data


import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "user")
data class User (
        @PrimaryKey
        val id: String
)