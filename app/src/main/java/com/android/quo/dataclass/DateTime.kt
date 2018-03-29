package com.android.quo.dataclass

import com.android.quo.util.Constants.Date.MONGO_DB_TIMESTAMP_FORMAT
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Jung on 12.12.17.
 */
data class DateTime(var date: String, var time: String) {

    fun toMongoTimestamp(): String =
        SimpleDateFormat(MONGO_DB_TIMESTAMP_FORMAT, Locale.getDefault()).format(toDate())

    fun toDate(): Date {
        val date = DATE_FORMAT.parse(date)
        val time = TIME_FORMAT.parse(time)
        return Date(date.time + time.time)
    }

    companion object {

        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun now(): DateTime =
            Date().let {
                val date = DATE_FORMAT.format(it)
                val time = TIME_FORMAT.format(it)
                DateTime(date, time)
            }
    }
}