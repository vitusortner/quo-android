package com.android.quo.util.extension

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.android.quo.R
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

/**
 * Created by vitusortner on 25.02.18.
 */
@SuppressLint("CommitTransaction")
fun <T : Fragment> FragmentManager.createAndReplaceFragment(
    tag: String,
    klass: KClass<T>,
    bundle: Bundle? = null,
    addToBackStack: Boolean = false,
    animations: Pair<Int, Int>? = null,
    allowStateLoss: Boolean = false
) {
    // TODO check behaviour
//    val fragment = findFragmentByTag(tag) ?: klass.createInstace()
    val fragment = klass.createInstance()
    bundle?.let { fragment.arguments = it }
    beginTransaction()
        .apply { animations?.let { setCustomAnimations(animations.first, animations.second) } }
        .replace(R.id.content, fragment, tag)
        .apply { if (addToBackStack) addToBackStack(null) }
        .apply { if (allowStateLoss) commitAllowingStateLoss() else commit() }
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