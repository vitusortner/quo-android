package com.android.quo.db

import android.arch.persistence.room.TypeConverter
import java.util.Date

/**
 * Created by FlorianSchlueter on 21.11.2017.
 */
class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = if (value == null) null else Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}