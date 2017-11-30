package com.android.quo.db.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "picture",
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = User::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("ownerId"),
                        onDelete = ForeignKey.CASCADE), //if user gets deleted all his picuturs too
                ForeignKey(
                        entity = Place::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("placeId"),
                        onDelete = ForeignKey.CASCADE)
        )) //if place gets deleted all his picuturs too
data class Picture(
        @PrimaryKey
        val id: String,

        val ownerId: String,

        val placeId: String,

        val src: String,

        val isVisible: Boolean,

        val timestamp: Date
)