package com.android.quo.util

import android.util.Log

/**
 * Created by vitusortner on 24.02.18.
 */
class Logger<in T>(clazz: Class<T>) {

    private val TAG = "${clazz.simpleName}::class"

    fun v(message: String, throwable: Throwable? = null) =
        Log.v(TAG, message, throwable)

    fun d(message: String, throwable: Throwable? = null) =
        Log.d(TAG, message, throwable)

    fun i(message: String, throwable: Throwable? = null) =
        Log.i(TAG, message, throwable)

    fun w(message: String, throwable: Throwable? = null) =
        Log.w(TAG, message, throwable)

    fun e(message: String, throwable: Throwable? = null) =
        Log.e(TAG, message, throwable)
}