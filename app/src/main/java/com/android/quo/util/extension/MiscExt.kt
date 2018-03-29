package com.android.quo.util.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import com.android.quo.util.Logger
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 19.11.17.
 */
fun Context.getDisplayMetrics() = resources.displayMetrics

fun Float.toPx(context: Context) =
    (this * (context.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()

@SuppressLint("SimpleDateFormat")
fun String.toDate(pattern: String): Date? =
    try {
        SimpleDateFormat(pattern).parse(this)
    } catch (exception: ParseException) {
        Logger(javaClass).e("Error while parsing string to date", exception)
        null
    }

fun Context.permissionsGranted(vararg permissions: String): Boolean =
    permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

fun Date.now(pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)

fun async(block: () -> Unit) =
    Completable.complete().subscribeOn(Schedulers.newThread()).subscribe(block)