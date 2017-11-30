package com.android.quo.networking

import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by vitusortner on 24.11.17.
 */
@Suppress("LeakingThis")
abstract class NetworkBoundSource<LocalType, RemoteType>(emitter: FlowableEmitter<LocalType>) {

    init {
        val firstDataDisposable = getLocal()
                .distinctUntilChanged()
                .subscribe(emitter::onNext)

        getRemote()
                .map(mapper())
                .subscribeOn(Schedulers.io())
                .subscribe { localTypeData ->
//                    firstDataDisposable.dispose()
                    saveCallResult(localTypeData)
//                    getLocal().subscribe(emitter::onNext)
                }
    }

    // TODO change return type, if needed
    abstract fun getRemote(): Single<RemoteType>

    abstract fun getLocal(): Flowable<LocalType>

    abstract fun saveCallResult(data: LocalType)

    abstract fun mapper(): (RemoteType) -> LocalType
}