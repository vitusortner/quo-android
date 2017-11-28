package com.android.quo.data


import android.app.Notification
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity(tableName = "user")
data class User (
    @PrimaryKey var id: Long,
    var email: String,
    var password: String,
    var active: Boolean,
    var updateNotification: Boolean,
    var photoNotification: Boolean,
    var createdAt: Date

)