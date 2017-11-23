package com.android.quo.extensions

import android.content.Context
import android.util.DisplayMetrics

/**
 * Created by vitusortner on 19.11.17.
 */
fun Context.getDisplayMetrics(): DisplayMetrics = this.resources.displayMetrics

fun Float.toPx(context: Context): Int {
    return this.toInt() * context.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT
}