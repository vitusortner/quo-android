package com.android.quo.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by FlorianSchlueter on 17.11.2017.
 */

@Entity (tableName =  "pictur")
data class Picture (
    @PrimaryKey
    val id: Int,

    @ColumnInfo (name = "src")
    val src: String,

    @ColumnInfo (name = "place")
    val place: Place,

    @ColumnInfo (name = "owner")
    val owner: User,

    @ColumnInfo (name = "visible")
    val visible: Boolean

)