package com.android.quo.util.extension

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData

fun <T> LiveData<T>.observeWithLifecycle(owner: LifecycleOwner, observer: (t: T?) -> Unit) =
    this.observe(owner, android.arch.lifecycle.Observer { observer(it) })

class NonNullMediatorLiveData<T> : MediatorLiveData<T>()

fun <T> LiveData<T>.filterNull(): NonNullMediatorLiveData<T> =
    NonNullMediatorLiveData<T>().apply {
        addSource(this@filterNull) { it?.let { this@apply.value = it } }
    }

fun <T> NonNullMediatorLiveData<T>.observeWithLifecycle(
    owner: LifecycleOwner,
    observer: (t: T) -> Unit
) =
    this.observe(owner, android.arch.lifecycle.Observer { it?.let(observer) })

