package com.android.quo.util.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
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

fun Uri.getImagePath(context: Context): String? {
    var result: String? = null
    val mediaStoreData = arrayOf(MediaStore.Images.Media.DATA)
    context.contentResolver.query(this, mediaStoreData, null, null, null).apply {
        if (moveToFirst()) {
            val columnIndex = getColumnIndexOrThrow(mediaStoreData[0])
            result = getString(columnIndex)
        }
        close()
    }
    return result
}

fun FragmentActivity.hideKeyboard() {
    val view = (this.currentFocus ?: View(this)).apply { clearFocus() }
    (this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, 0)
}