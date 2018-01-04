package com.android.quo.networking

import android.util.Log
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 24.11.17.
 */
@Suppress("LeakingThis")
abstract class NetworkBoundResource<LocalType, RemoteType>(emitter: FlowableEmitter<LocalType>) {

    init {
        getLocal()
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .subscribe(emitter::onNext)

        getRemote()
                .subscribeOn(Schedulers.io())
                .subscribe({ remoteType ->
                    sync(remoteType)
                }, { error ->
                    Log.e("networking", "Fetching data from API failed with error: $error")
                })
    }

    abstract fun getRemote(): Single<RemoteType>

    abstract fun getLocal(): Flowable<LocalType>

    abstract fun sync(data: RemoteType)
}