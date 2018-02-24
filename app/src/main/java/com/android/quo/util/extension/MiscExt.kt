package com.android.quo.util.extension

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.DisplayMetrics
import com.android.quo.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by vitusortner on 19.11.17.
 */
fun Context.getDisplayMetrics(): DisplayMetrics = this.resources.displayMetrics

fun Float.toPx(context: Context): Int {
    return (this * (context.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

@SuppressLint("SimpleDateFormat")
fun String?.toDate(): Date? {
    this?.let {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return simpleDateFormat.parse(it)
    } ?: return null
}

fun <T : Fragment> FragmentManager.createAndReplaceFragment(
    tag: String,
    clazz: Class<T>,
    bundle: Bundle? = null,
    addToBackStack: Boolean = false,
    animations: Pair<Int, Int>? = null
) {
    val fragment = findFragmentByTag(tag) ?: clazz.newInstance()
    bundle?.let { fragment.arguments = it }
    beginTransaction()
        .apply { animations?.let { setCustomAnimations(animations.first, animations.second) } }
        .replace(R.id.content, fragment, tag)
        .apply { if (addToBackStack) addToBackStack(null) }
        .commit()
}

fun FragmentManager.addFragment(
    fragment: Fragment,
    addToBackStack: Boolean,
    animations: Pair<Int, Int>? = null
) {
    beginTransaction()
        .apply { animations?.let { setCustomAnimations(animations.first, animations.second) } }
        .add(R.id.content, fragment)
        .apply { if (addToBackStack) addToBackStack(null) }
        .commit()
}