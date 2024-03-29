package com.android.quo.util.extension

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 25.02.18.
 */
fun <T> Single<T>.observeOnUi() = observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.subscribeOnIo() = subscribeOn(Schedulers.io())

fun <T> Flowable<T>.observeOnUi() = observeOn(AndroidSchedulers.mainThread())

fun <T> Flowable<T>.subscribeOnIo() = subscribeOn(Schedulers.io())

fun <T> Flowable<T>.subscribeOnComputation() = subscribeOn(Schedulers.computation())

fun <T, R> Single<T>.flatMapFlowable(mapper: (T) -> Flowable<R>) = toFlowable().flatMap(mapper)

fun <T> Flowable<List<T>>.filterNotEmpty() = this.filter { it.isNotEmpty() }