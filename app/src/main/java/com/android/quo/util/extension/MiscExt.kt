package com.android.quo.util.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 19.11.17.
 */
fun Context.getDisplayMetrics(): DisplayMetrics = this.resources.displayMetrics

fun Float.toPx(context: Context): Int =
    (this * (context.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()

@SuppressLint("SimpleDateFormat")
fun String?.toDate(): Date? =
    this?.let {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        simpleDateFormat.parse(it)
    }

fun Context.permissionsGranted(vararg permissions: String): Boolean =
    permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }