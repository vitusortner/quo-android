package com.android.quo.extensions

import android.content.Context
import android.util.DisplayMetrics
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 19.11.17.
 */
fun Context.getDisplayMetrics(): DisplayMetrics = this.resources.displayMetrics

fun Float.toPx(context: Context): Int {
    return this.toInt() * context.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT
}

fun String?.toDate(): Date? {
    this?.let {
        // 2017-12-18T21:40:03.451Z
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return simpleDateFormat.parse(it)
    } ?: run {
        return null
    }
}