package com.android.quo.db.entity

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
                        childColumns = arrayOf("placeId"),
                        onDelete = ForeignKey.CASCADE)
        ))
data class Component(
        @PrimaryKey
        val id: String,

        val picture: String? = null,

        val text: String? = null,

        val placeId: String,

        val position: Int
)