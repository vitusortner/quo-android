package com.android.quo.util.extension

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.android.quo.R

/**
 * Created by vitusortner on 25.02.18.
 */

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